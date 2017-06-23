package com.interra;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Created by pgordon on 23.06.2017.
 */
public class ClientThread extends Thread {
    private static final String HELLO_MESSAGE = "Welcome to test server!  Enter help for list of available commands.";
    private TreeMap<String, String> commands = new TreeMap<>();
    private Socket socket;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;

    public ClientThread(BufferedReader inFromClient, DataOutputStream outToClient) {
        System.out.println("New client initialized");
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
    }

    ClientThread(Socket connectionSocket) throws IOException {
        System.out.println("New client initialized");
        this.socket = connectionSocket;
        this.inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        this.outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        commands.put("help", "print all commands");
        commands.put("exit", "close the connection");
        commands.put("cancel", "interrupt current operation without closing connection");
        commands.put("new", "create new record");
    }

    InetAddress getAddress() {
        return socket.getInetAddress();
    }


    @Override
    public void run() {
        System.out.println("New client started");

        try {
            outToClient.writeBytes(HELLO_MESSAGE + "\n");
            String command;
            while ((command = inFromClient.readLine()) != null) {
                String response = "wrong command";
                System.out.println("Got command " + command);
                switch (command) {
                    case "help":
                        response = "help";
                        break;
                    case "new":
                        response = "new";
                        break;
                    case "cancel":
                        response = "Cancelled current operation";
                        break;
                    case "exit": {
                        outToClient.writeBytes("Bye" + "\n");
                        outToClient.close();
                        break;
                    }
                    default:
                        response = "wrong command";
                        break;
                }
                outToClient.writeBytes(response + "\n");

            }
            System.out.println("End of a thread");
        } catch (IOException e) {
            System.err.println("Got exception " + e);
        }
    }
}
