<<<<<<< HEAD
=======
package transfer;

import java.awt.SecondaryLoop;
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
        
        // Verbindungsaufbau
        try {
            clientSocket = new Socket( serverIP , serverPort );
            is = clientSocket.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Connection established");
        // Empfange DirInformation
        receiveDirInformation(clientSocket);
        
        Scanner sc = new Scanner(System.in);
        while((fileName = sc.nextLine()) != null) {
        	System.out.println("Anfrage gesendet");
        	// teile Server den angeforderten Dateinamen mit
            contactServer(clientSocket);
            
            //lade Datei herunter
            downloadFileFromServer(is);
            System.out.println("Download erfolgreich");
        }
        clientSocket.close();
        
		
    }
    public static void contactServer(Socket clientSocket){
    	try(OutputStreamWriter bos = new OutputStreamWriter(clientSocket.getOutputStream())){
    		bos.write(fileName, 0, fileName.length());
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

                System.out.println("Die Datei wurde übertragen");
                System.out.println("Dateigröße: " + byteCounter);
                                
                bos.write(baos.toByteArray());
               
                bos.flush();
                bos.close();              
                
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
>>>>>>> branch 'master' of https://github.com/roninshowdown/eva_ws18.git
