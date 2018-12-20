package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import static server.ServerApplicationController.showAlert;

class TCPServer extends Thread {

    static String sharedDir = null;
    private static int port = 0;
    private static ArrayList<ServerServiceThread> serviceThreads;
    
    public TCPServer(String sharedDir, int port) {
    	TCPServer.sharedDir = sharedDir;
    	TCPServer.port = port;
    	TCPServer.serviceThreads = new ArrayList<>();
    	System.out.println("server successfully initialized");
    }

    public void run() {
    	
    	try (ServerSocket welcomeSocket = new ServerSocket(port)){
            while(!isInterrupted()) {
            	try {
            		serviceThreads.add(new ServerServiceThread(welcomeSocket.accept()));
            		serviceThreads.get(serviceThreads.size()-1).start();
            		System.out.println("new ServerServiceThread successfully initialized");
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
        	showAlert("FEHLER!", "Der Server konnte nicht gestartet werden.", true);
        }
    }
    
    public void shutDown() {
    	for (ServerServiceThread sst : serviceThreads) {
    		sst.interrupt();
    		while(sst.isAlive()) {
	    		try {
					sst.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    	serviceThreads = null;
    	System.out.println("all ServerServiceThreads have been disbanded");
    }
}
