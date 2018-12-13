package transfer;

import java.io.*;
import java.net.*;

import server_threads.ServerServiceThread;

class TCPServer {

    private final static String dir = "/home/donald/Downloads";

    public static void main(String args[]) {
       
        try (ServerSocket welcomeSocket = new ServerSocket(3248)){
            while(true) {
            	(new ServerServiceThread(welcomeSocket.accept())).start();
            }

        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}
