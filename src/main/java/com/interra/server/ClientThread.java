package com.interra.server;

import com.interra.server.storage.Record;
import com.interra.server.storage.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.TreeMap;


import static com.interra.server.ClientThreadState.*;

/**
 * Created by pgordon on 23.06.2017.
 */
public class ClientThread extends Thread {
    private static final Logger logger = LogManager.getLogger();

    private TreeMap<String, String> commands = new TreeMap<>();
    private Socket socket;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    private boolean alive;
    private Record record;
    private ClientThreadState currentState;

    ClientThread(Socket connectionSocket, String connectionName) throws IOException {
        logger.info("New client initialized");
        this.socket = connectionSocket;
        this.inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        this.outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        commands.put("help", "print all commands");
        commands.put("exit", "close the connection");
        commands.put("cancel", "interrupt current operation without closing connection");
        commands.put("new", "create new record");
        commands.put("search", "search through records");
        alive = true;
        currentState = WAITING_FOR_COMMAND;

    }

    InetAddress getAddress() {
        return socket.getInetAddress();
    }


    @Override
    public void run() {
        logger.info("New client started");
        try {
            outToClient.write(Message.HELLO_MESSAGE.getBytes());
            outToClient.flush();
            String command;
            while ((command = inFromClient.readLine()) != null) {
                System.out.println(Message.GOT_COMMAND + command);
                String response = handleCommand(command);
                outToClient.write(response.getBytes());
                outToClient.flush();
                if (!alive) {
                    socket.close();
                    break;
                }
            }
            logger.info("New client started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleCommand(String command) {
        switch (command) {
            case "help":
                return buildHelp();
            case "new":
                record = new Record();
                currentState = WAITING_FOR_NAME;
                return "Enter name";
            case "cancel":
                currentState = WAITING_FOR_COMMAND;
                record = null;
                return "Cancelled current operation";
            case "list":
                return Storage.prettyPrint();
            case "search":
                currentState = WAITING_FOR_SEARCH;
                return "Enter query";
            case "exit": {
                alive = false;
                return "Bye";
            }
            default:
                return handleTextByState(command);
        }
    }

    private String handleTextByState(String command) {
        switch (currentState) {
            case WAITING_FOR_NAME:
                if (Record.isValidName(command)) {
                    record.setName(command);
                    currentState = WAITING_FOR_SURNAME;
                    return "Enter surname";
                } else {
                    return "Validation failed. Enter valid name";
                }
            case WAITING_FOR_SURNAME:
                if (Record.isValidSurname(command)) {
                    record.setSurname(command);
                    currentState = WAITING_FOR_PATRONYMIC;
                    return "Enter patronymic";
                } else {
                    return "Validation failed. Enter valid surname";
                }

            case WAITING_FOR_PATRONYMIC:
                if (Record.isValidPatronymic(command)) {
                    record.setPatronymic(command);
                    currentState = WAITING_FOR_POSITION;
                    return "Enter position";
                } else {
                    return "Validation failed. Enter valid patronymic";
                }
            case WAITING_FOR_POSITION:
                if (Record.isValidPosition(command)) {
                    record.setPosition(command);
                    record.commit();
                    currentState = WAITING_FOR_COMMAND;
                    return "Created successfully";
                } else {
                    return "Validation failed. Enter valid position";
                }
            case WAITING_FOR_SEARCH:
                currentState = WAITING_FOR_COMMAND;
                return Storage.findAll(command);
            default:
                return "Wrong command";
        }
    }


    private String buildHelp() {
        StringBuilder response = new StringBuilder();
        //TODO String.format
        commands.forEach((k, v) -> {
            response.append(k);
            response.append(" : ");
            response.append(v);
            response.append("\n");
        });
        return response.toString();
    }

}
