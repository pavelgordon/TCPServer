package com.interra.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * This class is a simple program i used for testing before switching to telnet
 * Created by pgordon on 22.06.2017.
 */

class TCPClient {
    public static void main(String argv[]) throws Exception {
        String command;
        String response;

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket("localhost", 6789);

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        TODO implement better transferring option?
        char [] c = new char[1020];
        while ( inFromServer.read(c) !=-1) {
            response = new String(c);
            System.out.println(response);
            if( response.equals("Bye")){
                break;
            }
            command = inFromUser.readLine();
            outToServer.writeBytes(command + '\n');
            c = new char[1020];
        }
        clientSocket.close();
    }
}
