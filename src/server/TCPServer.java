package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import static server.ServerApplicationController.showAlert;

class TCPServer extends Thread {

    static String sharedDir = null;
    private static int port = 0;
    private static ArrayList<ServerServiceThread> serviceThreads;
    public static ServerSocket welcomeSocket;
    
    public TCPServer(String sharedDir, int port) {
    	TCPServer.sharedDir = sharedDir;
    	TCPServer.port = port;
    	TCPServer.serviceThreads = new ArrayList<>();
    	System.out.println("server successfully initialized");
    }

    public void run() {
    	
    	try {
    		welcomeSocket = new ServerSocket(port);
            while(!isInterrupted() && !welcomeSocket.isClosed()) {
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
        	showAlert("Fehler!", "Der Server konnte nicht gestartet werden.", true);
        }
    }
    
    public void shutDown() {
    	System.out.println("	terminating threads...");
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
    	System.out.println("all ServerServiceThreads have been disbanded");
    	
    }
}
