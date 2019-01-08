package server_console;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.InvalidPathException;


public class Main {
	
	public static void main(String[] args) {
		//check for length of args
		if(args.length < 1) {
			System.err.println("usage: java -jar -ea <filename.jar> <port>");
			return;
		}		
		//check for internet connection
        try { 
            URL url = new URL("https://www.google.de/"); 
            URLConnection connection = url.openConnection(); 
            connection.connect();
            System.out.println("internet connection available");
            
        } 
        catch (Exception e) { 
            System.err.println("no internet connection available - the server can only be accessed locally.");
        } 
        
   	 	String sharedDir;
   	 	Integer port;
   	 	
        //check for a valid path
		try {
			File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
			sharedDir = jarDir.getAbsolutePath();
			String os = System.getProperty("os.name").toLowerCase();
			// windows
			if(os.contains("win")) {
				System.out.println("windows detected");
				sharedDir = sharedDir.replace("\\","\\\\") + "\\\\";
			}
			else if(os.contains("nix") || os.contains("nux")) {
				System.out.println("linux detected");
				sharedDir = sharedDir + "/";
			}
			System.out.println("Path: "+sharedDir);
		} catch (InvalidPathException | NullPointerException ex) {
			ex.printStackTrace();
			System.err.println("invalid path - start the application with a specific path.");
			return;
		} 
		
		//check for a valid port
		try {
			port = Integer.parseInt(args[0]);
			assert(1024 <= port  && port <= 65535);
			System.out.println("Port: "+port);
		} catch (NumberFormatException | NullPointerException | AssertionError ex) {
				ex.printStackTrace();
				System.err.println("invalid port - specifiy a port between 1024 and 65535.");
				return;
		}
		
		TCPServer server = new TCPServer(sharedDir, port);
		server.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		        server.shutDown();
		    }
		}));
	}

}
