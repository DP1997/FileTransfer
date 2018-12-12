package transfer;

import java.io.*;
import java.io.ByteArrayOutputStream;
import java.net.*;

class TCPClient {

    private final static String serverIP = "10.0.188.22";
    private final static int serverPort = 3248;
    private final static String fileOutput = "C:\\Users\\Mirco\\Desktop\\testout.zip";

    public static void main(String args[]) {
        byte[] aByte = new byte[1];
        int bytesRead;

        Socket clientSocket = null;
        InputStream is = null;
        int byteCounter = 0;

        try {
            clientSocket = new Socket( serverIP , serverPort );
            System.out.println("Connection established");
            is = clientSocket.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (is != null) {

            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                fos = new FileOutputStream( fileOutput );
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
                */
                
                bos.write(baos.toByteArray());
               
                bos.flush();
                bos.close();              
                clientSocket.close();
                System.out.println("Übertragung fertig");
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}