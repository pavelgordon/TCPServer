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
        boolean alive = true;
        Record record = null;
        int state = 0;
        try {
//            outToClient.writeBytes();
            outToClient.write(HELLO_MESSAGE.getBytes());
            outToClient.flush();
            String command;
            while ((command = inFromClient.readLine()) != null) {
                String response = "wrong command";
                System.out.println("Got command " + command);
                switch (command) {
                    case "help":
                        response = "help";
                        break;
                    case "new":
                        record = new Record();
                        response = "Enter name";
                        state = 1;
                        break;
                    case "cancel":
                        response = "Cancelled current operation";
                        state = 0;
                        record = null;
                        break;
                    case "list":
                        response = Storage.prettyPrint();
                        break;
                    case "search":
                        response = Storage.findAll(command);
                        break;
                    case "exit": {
                        alive = false;
                        response = "Bye";
                        break;
                    }
                    default:
                        if (state == 1) {
                            record.setName(command);
                            state = 2;
                            response = "Enter surname";
                        } else if (state == 2) {
                            record.setSurname(command);
                            state = 3;
                            response = "Enter patronymic";
                        } else if (state == 3) {
                            record.setPatronymic(command);
                            state = 4;
                            response = "Enter position";
                        } else if (state == 4) {
                            record.setPosition(command);
                            boolean r = record.commit();
                            state = 0;
                            response = r? "Created successfully": "Not created due to storage issues";
                        } else
                            response = "Wrong command";

                        break;
                }
                outToClient.write(response.getBytes());
                outToClient.flush();
                if(!alive){
                    socket.close();
                    System.out.println(socket.isClosed());
                    System.out.println(socket.isConnected());
                    break;
                }
            }
            System.out.println("End of a thread");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
