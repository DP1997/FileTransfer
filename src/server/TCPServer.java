package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import static server.ServerApplicationController.showAlert;

class TCPServer extends Thread {

	//the directory that will be shared with the clients
    static String sharedDir = null;
    private static int port = 0;
    //all server threads can be referenced here
    private static ArrayList<ServerServiceThread> serviceThreads;
    //initial socket
    public static ServerSocket welcomeSocket;
    
    //constructor: initialization of class variables
    public TCPServer(String sharedDir, int port) {
    	TCPServer.sharedDir = sharedDir;
    	TCPServer.port = port;
    	TCPServer.serviceThreads = new ArrayList<>();
    	System.out.println("server successfully initialized");
    }

    public void run() {
    	try {
    		//start the server
    		welcomeSocket = new ServerSocket(port);
    		//check whether the server is supposed to go offline
            while(!isInterrupted() && !welcomeSocket.isClosed()) {
            	try {
            		//accept incoming tcp connections
            		serviceThreads.add(new ServerServiceThread(welcomeSocket.accept()));
            		//start a new thread for each connection
            		serviceThreads.get(serviceThreads.size()-1).start();
            		System.out.println("new ServerServiceThread successfully initialized");
            	} catch (SocketException e) {
                	e.printStackTrace();
                	showAlert("Server offline!", "Der Server wurde heruntergefahren.", false);
            	} catch (Exception e) {
                	System.err.println("ServerServiceThread could not be initialized");
                	e.printStackTrace();
                	showAlert("Verbindungsfehler!", "Eingehende Client-Verbindung konnte nicht entgegengenommen werden.", false);
            	}
            }
            System.err.println("server has been interrupted");

        } catch (IOException e) {
        	System.err.println("server welcomeSocket could not be initialized");
        	e.printStackTrace();
        	showAlert("Fehler!", "Der Server konnte nicht gestartet werden.", true);
        }
    }
    
    //terminate the server
    public void shutDown() {
    	System.out.println("	terminating threads...");
    	//for all threads -> close their socket and join them
    	for (ServerServiceThread sst : serviceThreads) {
    		try {
				sst.connection.close();
			} catch (Exception e1) {
				e1.printStackTrace();
				sst.connection = null;
			}
    		while(sst.isAlive()) {
	    		try {
					System.out.println("	joining thread " + sst.getId()+"...");
					sst.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    	serviceThreads = null;
    	System.out.println("	...all ServerServiceThreads have been disbanded");
    	
    }
}
