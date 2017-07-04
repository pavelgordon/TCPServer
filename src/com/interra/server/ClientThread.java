package com.interra.server;

import com.interra.Message;
import com.interra.Record;
import com.interra.Storage;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.TreeMap;


import static com.interra.server.ClientThread.State.*;

/**
 * Created by pgordon on 23.06.2017.
 */
public class ClientThread extends Thread {

    private TreeMap<String, String> commands = new TreeMap<>();
    private Socket socket;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;

    ClientThread(Socket connectionSocket) throws IOException {
        System.out.println("New client initialized");
        this.socket = connectionSocket;
        this.inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        this.outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        commands.put("help", "print all commands");
        commands.put("exit", "close the connection");
        commands.put("cancel", "interrupt current operation without closing connection");
        commands.put("new", "create new record");
        commands.put("search", "search through records");
    }

    public InetAddress getAddress() {
        return socket.getInetAddress();
    }


    @Override
    public void run() {
        System.out.println("New client started");
        boolean alive = true;
        Record record = null;
        State state = State.WAITING_FOR_COMMAND;
        try {
            outToClient.write(Message.HELLO_MESSAGE.getBytes());
            outToClient.flush();
            String command;
            while ((command = inFromClient.readLine()) != null) {
                String response = "wrong command";
                System.out.println(Message.GOT_COMMAND + command);
                switch (command) {
                    case "help":
                        response = getHelp();
                        break;
                    case "new":
                        record = new Record();
                        response = "Enter name";
                        state = State.WAITING_FOR_NAME;
                        break;
                    case "cancel":
                        response = "Cancelled current operation";
                        state = State.WAITING_FOR_COMMAND;
                        record = null;
                        break;
                    case "list":
                        response = Storage.prettyPrint();
                        break;
                    case "search":
                        response = "Enter query";
                        state = State.WAITING_FOR_SEARCH;
                        break;
                    case "exit": {
                        alive = false;
                        response = "Bye";
                        break;
                    }
                    default:
                        if (state == State.WAITING_FOR_NAME) {
                            record.setName(command);
                            state = State.WAITING_FOR_SURNAME;
                            response = "Enter surname";
                        } else if (state == State.WAITING_FOR_SURNAME) {
                            record.setSurname(command);
                            state = WAITING_FOR_PATRONYMIC;
                            response = "Enter patronymic";
                        } else if (state == WAITING_FOR_PATRONYMIC) {
                            record.setPatronymic(command);
                            state = WAITING_FOR_POSITION;
                            response = "Enter position";
                        } else if (state == WAITING_FOR_POSITION) {
                            record.setPosition(command);
                            boolean r = record.commit();
                            state = State.WAITING_FOR_COMMAND;
                            response = r ? "Created successfully" : "Not created due to storage issues";
                        } else if (state == WAITING_FOR_SEARCH) {
                            response = Storage.findAll(command);
                            state = WAITING_FOR_COMMAND;
                        } else
                            response = "Wrong command";

                        break;
                }
                outToClient.write(response.getBytes());
                outToClient.flush();
                if (!alive) {
                    socket.close();
                    break;
                }
            }
            System.out.println("End of a thread");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum State {
        WAITING_FOR_COMMAND,
        WAITING_FOR_NAME,
        WAITING_FOR_SURNAME,
        WAITING_FOR_PATRONYMIC,
        WAITING_FOR_POSITION,
        WAITING_FOR_SEARCH
    }

    public String getHelp() {
        StringBuilder response = new StringBuilder();
        //TODO String.format
        commands.forEach((k, v) -> {
            response.append(k);
            response.append(":");
            response.append(v);
        });
        return response.toString();
    }

}
