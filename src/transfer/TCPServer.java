package transfer;

import java.io.*;
import java.net.*;

import server_threads.ServerServiceThread;

class TCPServer {

	//TODO 
    private final static String dir = "/home/donald/Downloads";

    public static void main(String args[]) {
       
        try (ServerSocket welcomeSocket = new ServerSocket(3456)){
            while(true) {
            	try {
            		(new ServerServiceThread(welcomeSocket.accept())).start();
            	} catch (Exception e) {
                	System.err.println("ServerServiceThread could not be initialized");
                	e.printStackTrace();
                	System.exit(1);
            	}
            }

        } catch (IOException e) {
        	System.err.println("server welcomeSocket could not be initialized");
        	e.printStackTrace();
        	System.exit(1);
        }
    }
}
