package com.protocol.rip;

import java.net.SocketException;
import java.util.*;

public class Network {

    private static final String LOCALHOST = "127.0.0.1";

    private static Node initialNode;

    static {
        try {
            initialNode = new Node(new Address(LOCALHOST, 64));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<Address, Thread> executions = new HashMap<>();
    private static HashMap<Address, Node> nodes = new HashMap<>();

    public Network() throws SocketException {
        nodes.put(initialNode.getAddress(), initialNode);
        new Thread(initialNode).start();
    }

    public void addNode(NodeDetails one, NodeDetails two) {
        two.addNeightbour((NodeDetails)one);
        one.addNeightbour((NodeDetails)two);
    }

    public void addNewNode(Node node) throws SocketException {
        addNewNode(node, initialNode);
    }

    public void addNewNode(Node one, Node two) {
        nodes.put(one.getAddress(), one);
        addNode(one, two);
        final Thread thread = new Thread(one);
        executions.put(one.getAddress(), thread);
        thread.start();
    }

    public void addNewNode(Node one, Address two) {
        nodes.put(one.getAddress(), one);
        addNode(one, nodes.get(two));
        final Thread thread = new Thread(one);
        executions.put(one.getAddress(), thread);
        thread.start();
    }

    public static void removeNode(Address address) {
        if (address.getIp().equals(initialNode.getAddress().getIp()) &&
                address.getPort() == initialNode.getAddress().getPort()) {
            System.out.println("Initial node cannot be removed");
            return;
        }
        final Node node = nodes.get(address);
        final List<NodeDetails> details = node.getNeightbours();
        for (int i = details.size()-1; i >= 0; i--) {
            details.get(i).removeNeightbour(node);
            node.removeNeightbour(details.get(i));
        }
        executions.get(address).stop();
        executions.remove(address);
    }

    public void showNodes() {
        HashMap<String, Boolean> isPair = new HashMap<>();

        for (Node n1 : nodes.values()) {
            for (NodeDetails n2 : n1.getNeightbours()) {
                final String pair = n1.getAddress().toString()+n2.getAddress().toString();
                final String reversePair = n2.getAddress().toString()+n1.getAddress().toString();
                if (isPair.get(pair) == null || !isPair.get(pair)) {
                    isPair.put(pair, true);
                    isPair.put(reversePair, true);
                    n1.printLink(n2);
                }
            }
        }
    }

    public void linkNodes(Address first, Address second) {
        addNode(nodes.get(first), nodes.get(second));
    }

    public Node getNode(Address address) {
        return nodes.get(address);
    }

    public static HashMap<Address, Node> getNodes() {
        return nodes;
    }
}