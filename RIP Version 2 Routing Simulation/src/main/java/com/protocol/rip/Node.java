package com.protocol.rip;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Node extends NodeDetails implements Runnable {

    private final DatagramSocket socket;
    private RoutingService routingService;
    private HashMap<Address, Boolean> isReachable = new HashMap<>();
    private HashMap<Address, Long> times = new HashMap<>();

    public Node(Address address, List<NodeDetails> neightbours, RoutingTable table) throws SocketException {
        this.address = address;
        this.neightbours = neightbours;
        this.table = table;
        this.routingService = new RoutingService(table, times);
        socket = new DatagramSocket(address.getPort());
    }

    public Node(Address address) throws SocketException {
        this.address = address;
        this.neightbours = new ArrayList<>();
        this.table = new RoutingTable();
        this.routingService = new RoutingService(table, times);
        socket = new DatagramSocket(address.getPort());
    }

    @Override
    public void run() {
        new Thread(this::updateRoutingTable).start();
        new Thread(this::markOrRemoveNode).start();
    }

    private void updateRoutingTable() {
        while (true) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            routingService.populateRoutingTable(address);
        }
    }

    private void markOrRemoveNode() {
        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (NodeDetails node : Network.getNodes().values()) {
                if (node.getAddress() != null && times.get(node.getAddress()) != null) {
                 if (System.currentTimeMillis() - times.get(node.getAddress()) >= 180000) {
                    for (RoutingRow row : node.table.getRoutingRows()) {
                        if (node.getAddress().equals(row.getNextHop().get(0))) {
                            node.table.update(node.getAddress(), row,
                                    new RoutingRow(row.getAddress(), row.getNextHop(), 16));
                        }
                    }
                 } else if (System.currentTimeMillis() - times.get(node.getAddress()) >= 240000) {
                    Network.removeNode(node.getAddress());
                 }
                }
            }
        }
    }

    @Override
    public void send(Message message) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Optional<NodeDetails> details = table.getNextHopNode(neightbours, message.getDestAddr());
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
}