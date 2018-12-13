package transfer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import datatypes.FileInformation;
import utils.FileUtils;

class TCPClient {

    private final static String serverIP = "192.168.0.7";
    private final static int serverPort = 3248;
    
    private final static String sharePath = "C:\\Users\\Mirco\\Desktop\\testordner";
    private static String fileName = "";
    
    public static void main(String args[]) throws IOException {
    	
        Socket clientSocket = null;
        InputStream is = null;
        
        try {
            // Verbindungsaufbau
            clientSocket = new Socket( serverIP , serverPort );
            is = clientSocket.getInputStream();
            
            System.out.println("Connection established");
            // Empfange DirInformation
            receiveDirInformation(clientSocket);
            
            Scanner sc = new Scanner(System.in);
            fileName = sc.nextLine();
        	// teile Server den angeforderten Dateinamen mit
            contactServer(clientSocket);
            
            //lade Datei herunter
            downloadFileFromServer(is);
            System.out.println("Download erfolgreich");
        } catch (IOException ex) {
            ex.printStackTrace();
        }		
    }
    public static void contactServer(Socket clientSocket){
    	try{
    		// auto-flush
    		PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
    		pw.println((fileName));
    		System.out.println("Suche nach File: " + fileName);
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
    }
    public static void downloadFileFromServer(InputStream is) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] aByte = new byte[1];
        int bytesRead;
        int byteCounter = 0;
        
        if (is != null) {

            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
            	String filePath = sharePath + "\\" + fileName;
                fos = new FileOutputStream(filePath);
                bos = new BufferedOutputStream(fos);
                bytesRead = is.read(aByte, 0, aByte.length);

                do {
                		byteCounter++;
                        baos.write(aByte);
                        bytesRead = is.read(aByte);
                } while (bytesRead != -1);

                System.out.println("Die Datei wurde empfangen");
                System.out.println("Dateigröße: " + byteCounter);
                                
                bos.write(baos.toByteArray());
               
                bos.flush();             
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void receiveDirInformation(Socket clientSocket) {
        // empfange Share-Ordner Informationen
        try {
        	ObjectInputStream objectInput = new ObjectInputStream(clientSocket.getInputStream());
        	Object fileNamesObj = objectInput.readObject();
        	Object fileLengthsObj = objectInput.readObject();
        	ArrayList<String> fileNames = (ArrayList<String>) fileNamesObj;
        	ArrayList<Long> fileLengths  = (ArrayList<Long>) fileLengthsObj;
        	System.out.println("Ordner des Servers:");
			for (int i = 0; i < fileNames.size(); i++) {
				System.out.println(fileNames.get(i) + " " + fileLengths.get(i) + " Bytes");
			}
			System.out.println("Share-Ordner empfangen.");
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    }
}

