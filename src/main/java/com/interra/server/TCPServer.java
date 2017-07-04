package com.interra.server;

/**
 * Created by pgordon on 22.06.2017.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;

class TCPServer {
    private static final int MAX_CLIENTS = 1;
    private static final int PORT = 6789;

    static ClientThread[] clients = new ClientThread[MAX_CLIENTS];
    static int currentClientAmount = 0;

    public static void main(String argv[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(PORT);


        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println(connectionSocket);
            System.out.println(connectionSocket.getInetAddress());
            BufferedReader inFromClient =
                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            if (currentClientAmount == 0) {
                clients[currentClientAmount] = new ClientThread(connectionSocket);
                clients[currentClientAmount].start();
                currentClientAmount++;
            }
            if (connectionSocket.getInetAddress().equals(clients[0].getAddress())) {
                System.out.println("Old client");
            }
        }
    }


}
