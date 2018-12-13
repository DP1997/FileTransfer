package transfer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import utils.FileUtils;

class TCPClient {

    private final static String serverIP = "127.0.0.1";
    private final static int serverPort = 3248;
    private final static String sharePath = "C:\\Users\\Mirco\\Desktop\\testordner";
    private final static String fileChosen = "";
    
    public static void main(String args[]) {
    	
        byte[] aByte = new byte[1];
        int bytesRead;

        Socket clientSocket = null;
        InputStream is = null;
        int byteCounter = 0;
        
        		
        // Verbindungsaufbau
        try {
            clientSocket = new Socket( serverIP , serverPort );
            is = clientSocket.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Connection established");
        try ( ObjectInputStream objectInput = new ObjectInputStream(clientSocket.getInputStream())){
        	Object object = objectInput.readObject();
        	String [] fileNames = (String[]) object;
        	for(String s : fileNames) {
        		System.out.println(s);
        		System.out.println("Client");
        	}
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        /*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (is != null) {

            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                fos = new FileOutputStream( sharePath );
                bos = new BufferedOutputStream(fos);
                bytesRead = is.read(aByte, 0, aByte.length);

                do {
                		byteCounter++;
                        baos.write(aByte);
                        bytesRead = is.read(aByte);
                } while (bytesRead != -1);

                System.out.println("Die Datei wurde übertragen");
                System.out.println("Dateigröße: " + byteCounter);
                
                //Test
                /*
                UnzipUtil.startUnzipping(fileOutput, baos);
                
                
                bos.write(baos.toByteArray());
               
                bos.flush();
                bos.close();              
                clientSocket.close();
                System.out.println("Übertragung fertig");
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		*/
		
    }
}