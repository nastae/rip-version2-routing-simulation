package com.protocol.rip.node;

import com.protocol.rip.node.messaging.Broadcast;
import com.protocol.rip.node.table.RoutingTable;
import com.protocol.rip.node.table.RoutingTableMessaging;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeDetails implements Broadcast, RoutingTableMessaging {

    protected boolean visited;
    protected Address address;
    protected List<NodeDetails> neightbours = new ArrayList<>();
    protected RoutingTable table;

    public Address getAddress() {
        return address;
    }

    public List<NodeDetails> getNeightbours() {
        return neightbours;
    }

    public void removeNeightbour(NodeDetails details) {
        neightbours.remove(details);
    }

    public void addNeightbour(NodeDetails neightbour) {
        neightbours.add(neightbour);
    }

    public void printLink(NodeDetails node) {
        System.out.println(String.format("%s-%s", address, node.getAddress()));
    }

    public RoutingTable getTable() {
        return table;
    }
}