package com.protocol.rip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class RoutingTable {

    private List<RoutingRow> rows;
    private HashMap<Address, RoutingRow> mapRows;

    public RoutingTable() {
        rows = new ArrayList<>();
        mapRows = new HashMap<>();
    }

    public RoutingTable(List<RoutingRow> rows) {
        this.rows = rows;
    }

    public List<Address> getNextHop(Address address) {
        return mapRows.get(address) != null
                ? mapRows.get(address).getNextHop()
                : new ArrayList<>();
    }

    public List<RoutingRow> getRoutingRows() {
        return rows;
    }

    public Optional<RoutingRow> findRoutingRow(Address address) {
        return Optional.ofNullable(mapRows.get(address));
    }

    public void add(Address address, RoutingRow row) {
        rows.add(row);
        mapRows.put(address, row);
    }

    public void update(Address address, RoutingRow oldRow, RoutingRow newRow) {
        rows.set(rows.indexOf(oldRow), newRow);
        mapRows.put(address, newRow);
    }

    public Optional<NodeDetails> getNextHopNode(List<NodeDetails> neightbours, Address address) {
        return neightbours
                .stream()
                .filter(n -> n.getAddress().equals(getNextHop(address).size() >= 1 ? getNextHop(address).get(0) : ""))
                .findFirst();
    }
}
