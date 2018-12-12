package thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;

public class FileTransferThread extends Thread{
	
	private Socket connection = null;
    private String dataToSend = "/home/donald/Downloads";

	
	public FileTransferThread(Socket sock) {
		super("FileTransferThread");
		this.connection = sock; 	
	}
	
	public void run() {
		
		try (BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream())){
			
			if (bos != null) {
                File myFile = new File(dataToSend);
                byte[] mybytearray = new byte[(int) myFile.length()];

                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile))) {
                	bis.read(mybytearray, 0, mybytearray.length);
                    bos.write(mybytearray, 0, mybytearray.length);
                    bos.flush();
                    connection.close();

                    // File sent -> exit run()
                    return;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
}
