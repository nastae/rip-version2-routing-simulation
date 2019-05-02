package com.protocol.rip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class RoutingService {

    private RoutingTable table;
    private HashMap<Address, Long> times;

    public RoutingService(RoutingTable table, HashMap<Address, Long> times) {
        this.table = table;
        this.times = times;
    }

    public List<List<Address>> findRoutesByPort(Address address) {
        List<List<Address>> group = new ArrayList<>();
        List<Address> ports = new ArrayList<>();

        final HashMap<Address, Node> nodes = Network.getNodes();
        final HashMap<Address, Boolean> isVisible = new HashMap<>();
        for (Address a : nodes.keySet()) {
            isVisible.put(a, false);
        }
        isVisible.put(address, true);
        ports.add(address);
        findRoutes(nodes.get(address), ports, group, isVisible);
        return group;
    }

    private void findRoutes(NodeDetails node, List<Address> route,
                            List<List<Address>> routeGroup, HashMap<Address, Boolean> isTraveled) {
        boolean isNodeTraveled = true;
        for (Boolean i : isTraveled.values()) {
            if (!i)
                isNodeTraveled = false;
        }
        if (isNodeTraveled) {
            routeGroup.add(route);
        } else {
            routeGroup.add(route);
            for (NodeDetails n : node.getNeightbours()) {
                if (!isTraveled.get(n.getAddress())) {
                    final List<Address> routeTemp = new ArrayList<>(route);
                    routeTemp.add(n.getAddress());
                    final HashMap<Address, Boolean> isTraveledTemp = new HashMap<>(isTraveled);
                    isTraveledTemp.put(n.getAddress(), true);
                    findRoutes(n, routeTemp, routeGroup, isTraveledTemp);
                }
            }
        }
    }

    public void populateRoutingTable(Address address) {
        for (List<Address> route : findRoutesByPort(address)) {
            final Address last = route.get(route.size()-1);
            final Optional<RoutingRow> row = table.findRoutingRow(last);
            times.put(last, System.currentTimeMillis());
            if (row.isPresent() && route.size() < 16) {
                if (route.size() <= row.get().getCount()) {
                    table.update(last, row.get(),
                            new RoutingRow(row.get().getAddress(), route.get(route.size() > 1 ? 1 : 0), route.size()-1));
                }
            } else {
                table.add(last, new RoutingRow(address,
                        route.get(route.size() > 1 ? 1 : 0), route.size() - 1));
            }
        }
    }
}
