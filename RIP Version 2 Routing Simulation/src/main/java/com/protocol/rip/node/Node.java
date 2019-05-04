package com.protocol.rip.node;

import com.protocol.rip.network.Network;
import com.protocol.rip.node.messaging.Message;
import com.protocol.rip.node.table.RoutingRow;
import com.protocol.rip.node.table.RoutingTable;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Node extends NodeDetails implements Runnable {

    private HashMap<Address, Long> times = new HashMap<>();

    public Node(Address address, List<NodeDetails> neightbours, RoutingTable table) throws SocketException {
        this.address = address;
        this.neightbours = neightbours;
        this.table = table;
    }

    public Node(Address address) throws SocketException {
        this.address = address;
        this.neightbours = new ArrayList<>();
        this.table = new RoutingTable();
    }

    @Override
    public void run() {
        new Thread(this::updateRoutingTable).start();
        new Thread(this::markOrRemoveNode).start();
    }

    private void updateRoutingTable() {
        while (true) {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mergeRoutingTablesOfNeightbours();
            for (NodeDetails node : getNeightbours()) {
                times.put(node.getAddress(), System.currentTimeMillis());
                request(node);
            }
        }
    }

    private void mergeRoutingTablesOfNeightbours() {
        if (this.table.getRoutingRows().stream().noneMatch(r -> r.getAddress().equals(this.getAddress()))) {
            table.add(this.getAddress(), new RoutingRow(this.getAddress(), this.getAddress(), 0));
        }
        for (NodeDetails node : getNeightbours()) {
            if (this.table.getRoutingRows().stream().noneMatch(r -> r.getAddress().equals(node.getAddress()))) {
                table.add(node.getAddress(), new RoutingRow(node.getAddress(), node.getAddress(), 1));
            }
        }
    }

    private void markOrRemoveNode() {
        while (true) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (NodeDetails node : getNeightbours()) {
                if (!node.address.equals(this.address) && node.getAddress() != null && times.get(node.getAddress()) != null) {
                 if (System.currentTimeMillis() - times.get(node.getAddress()) >= 180000) {
                     Network.removeRoutingRows(node.getAddress());
                 }
                 if (System.currentTimeMillis() - times.get(node.getAddress()) >= 240000) {
                     Network.removeNode(node.getAddress());
                     times.remove(node.getAddress());
                 }
                }
            }
        }
    }

    @Override
    public void send(Message message) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Optional<NodeDetails> details = table.getNextHopNode(neightbours, message.getDestAddr());
        if (details.isPresent()) {
            table.findRoutingRow(message.getDestAddr())
                    .ifPresent(x -> printMessageSending(message, x.getNextHop().get(0)));
            if (address.equals(details.get().getAddress()))
                receive(message);
            else
                details.get().receive(message);
        } else {
            System.out.println("Failed to send message " + message.getText() + " from " + address.toString() + " to " + message.getDestAddr().toString());
        }

    }

    @Override
    public void receive(Message message) {
        if (message.getDestAddr().equals(address)) {
            printReceivedMessage(message);
        } else {
            send(message);
        }
    }

    private void printMessageSending(Message message, Address nextHop) {
        System.out.println(String.format("Sending message from node %s to %s", address.toString(), nextHop.toString()));
        System.out.println(String.format("Message %s\n", message.getText()));
    }

    private void printReceivedMessage(Message message) {
        System.out.println(String.format("Delivered message to node %s", address.toString()));
        System.out.println(String.format("Message %s\n", message.getText()));
    }

    @Override
    public void request(NodeDetails node) {
        if (isReachableNode(node))
            node.response(this);
    }

    @Override
    public void response(NodeDetails node) {
        if (isReachableNode(node)) {
            node.receiveTable(this.getAddress(), this.table);
        }
    }

    private synchronized boolean isReachableNode(NodeDetails node) {
        return this.table.getRoutingRows()
                .stream()
                .noneMatch(t -> t.getAddress()
                        .equals(node.getAddress())
                        && t.getCount() >= 16);
    }

    @Override
    public void receiveTable(Address nodeAddress, RoutingTable table) {
        mergeTable(nodeAddress, table);
    }

    private synchronized void mergeTable(Address nodeAddress, RoutingTable table) {
        for (int i = 0; i < table.getRoutingRows().size(); i++) {
            final RoutingRow r1 = table.getRoutingRows().get(i);
            if (this.table.getRoutingRows().stream().noneMatch(r -> r.getAddress().equals(r1.getAddress()))) {
                this.table.add(r1.getAddress(), new RoutingRow(r1.getAddress(), nodeAddress, r1.getCount()+1));
            } else {
                for (RoutingRow r2 : this.table.getRoutingRows()) {
                    if (r1.getAddress().equals(r2.getAddress()) && r1.getCount()+1 < r2.getCount()) {
                        this.table.update(r1.getAddress(), r2, new RoutingRow(r1.getAddress(), nodeAddress, r1.getCount()+1));
                    }
                }
            }
        }
    }
}