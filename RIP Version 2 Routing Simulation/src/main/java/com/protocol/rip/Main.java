package com.protocol.rip;

import java.net.SocketException;
import java.util.Scanner;

public class Main {

    private static final String LOCALHOST = "127.0.0.1";

    public static void main(String[] args) {
        try {
            Network network = new Network();

            Node n1 = new Node(new Address(LOCALHOST, 100));
            Node n2 = new Node(new Address(LOCALHOST, 200));
            Node n3 = new Node(new Address(LOCALHOST, 300));
            network.addNewNode(n1);
            network.addNewNode(n2);
            network.addNode(n2, n1);
            network.addNewNode(n3, n2);

            while (true) {
                printMenu();

                Scanner scanner = new Scanner(System.in);
                int select = scanner.nextInt();

                String firstIp;
                int firstPort;
                String secondIp;
                int secondPort;
                String text;
                switch (select) {
                    case 1:
                        network.showNodes();
                        break;
                    case 2:
                        System.out.println("Enter new node IP: ");
                        firstIp = scanner.next();
                        System.out.println("Enter new node PORT: ");
                        firstPort = scanner.nextInt();
                        System.out.println("Enter first neighbour IP: ");
                        secondIp = scanner.next();
                        System.out.println("Enter first neighbour PORT: ");
                        secondPort = scanner.nextInt();

                        network.addNewNode(new Node(new Address(firstIp, firstPort)),
                                new Address(secondIp, secondPort));
                        break;
                    case 3:
                        System.out.println("Enter first node IP: ");
                        firstIp = scanner.next();
                        System.out.println("Enter first node PORT: ");
                        firstPort = scanner.nextInt();
                        System.out.println("Enter second node IP: ");
                        secondIp = scanner.next();
                        System.out.println("Enter second node PORT: ");
                        secondPort = scanner.nextInt();
                        network.linkNodes(new Address(firstIp, firstPort),
                                new Address(secondIp, secondPort));
                        break;
                    case 4:
                        System.out.println("Enter node IP: ");
                        firstIp = scanner.next();
                        System.out.println("Enter node PORT: ");
                        firstPort = scanner.nextInt();
                        network.removeNode(new Address(firstIp, firstPort));
                        break;
                    case 5:
                        System.out.println("Enter node IP: ");
                        firstIp = scanner.next();
                        System.out.println("Enter node PORT: ");
                        firstPort = scanner.nextInt();
                        System.out.println("Enter destination IP: ");
                        secondIp = scanner.next();
                        System.out.println("Enter destination PORT: ");
                        secondPort = scanner.nextInt();
                        System.out.println("Enter message text: ");
                        text = scanner.next();
                        network.getNode(new Address(firstIp, firstPort))
                                .send(new Message(new Address(secondIp, secondPort), text));
                        break;
                    case 0:
                        System.exit(0);
                    default:
                        break;
                }
            }
        } catch (SocketException e) {
            System.out.print("Error occured: " + e.getMessage());
        }
    }

    static void printMenu() {
        System.out.println("Commands:");
        System.out.println("1. View routing table");
        System.out.println("2. Add node");
        System.out.println("3. Link nodes");
        System.out.println("4. Remove node");
        System.out.println("5. Send message");
        System.out.println("0. Exit");
    }
}