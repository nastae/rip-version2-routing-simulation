package com.protocol.rip.node.table;

import com.protocol.rip.node.Address;
import com.protocol.rip.node.NodeDetails;

public interface RoutingTableMessaging {

    void request(NodeDetails node);

    void response(NodeDetails node);

    void receiveTable(Address nodeAddress, RoutingTable table);
}