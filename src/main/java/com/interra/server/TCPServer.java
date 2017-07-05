package com.interra.server;

/**
 * Created by pgordon on 22.06.2017.
 */


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

class TCPServer {
    private static final Logger logger = LogManager.getLogger();
    private static final int MAX_CLIENTS = 2;
    private static final int PORT = 6789;

    private static HashMap<String, ClientThread> clients = new HashMap<String, ClientThread>();


    public static void main(String argv[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(PORT);
        logger.info("Server started");
        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            String connectionName = createClientName(connectionSocket);
            logger.info("Accepted connection from socket " + connectionName);
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            if (clients.size() >= MAX_CLIENTS) {
                logger.info("Exceeded user limit");
                outToClient.write("Exceeded user limit".getBytes());
                outToClient.flush();
                connectionSocket.close();
            } else if (!clients.containsKey(connectionName)) {
                ClientThread clientThread = new ClientThread(connectionSocket, connectionName);
                clientThread.start();
                clients.put(createClientName(connectionSocket), clientThread);
            }
        }
    }

    private static String createClientName(Socket socket) {
        return socket.getInetAddress() + ":" + socket.getPort();
    }


}
