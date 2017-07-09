package com.interra.server;

/**
 * Created by pgordon on 22.06.2017.
 */


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.LimitExceededException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

class TCPServer implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    private static final int PORT = 6789;

    private static HashMap<String, ClientThread> clients = new HashMap<String, ClientThread>();


    public static void main(String argv[]) {
        new TCPServer().run();
    }

    private String createClientName(Socket socket) {
        return socket.getInetAddress() + ":" + socket.getPort();
    }


    @Override
    public void run() {
        try {
            ServerSocket welcomeSocket = new ServerSocket(PORT);
            logger.info("Server started");
            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                logger.info("Accepted connection from socket " + connectionSocket.getInetAddress() + ":" + connectionSocket.getPort());

                ClientThread clientThread = new ClientThread(connectionSocket);
                try {
                    ThreadManager.getInstance().registerThread(clientThread);
                    clientThread.start();
                } catch (LimitExceededException e) {
                    logger.info("Too many clients");
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    outToClient.write("Exceeded user limit\r\n".getBytes());
                    outToClient.flush();
                    connectionSocket.close();
                }
            }
        } catch (IOException e) {
            logger.error("Connection problems", e);
        }

    }
}
