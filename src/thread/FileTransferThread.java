package thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

import datatypes.FileInformation;
import utils.FileUtils;

public class FileTransferThread extends Thread{
	
	private Socket connection = null;
    private String sharePath = "C:\\Users\\Mirco\\Desktop\\testordner";
    private String fileChosenPath = "";

	
	public FileTransferThread(Socket sock) {
		super("FileTransferThread");
		this.connection = sock; 	
	}
	
	public void run() {
		shareDirInformation();
		//...
		try (BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream())){
			
			if (bos != null) {
				System.out.println("Connection established");
				sendFile(bos);
			}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}
	
	public void sendFile(BufferedOutputStream bos) throws IOException {
		fileChosenPath = FileUtils.getChosenFileName(sharePath);
		// ausgewählte Datei des Clients
	    File myFile = new File(fileChosenPath);
	    
	    byte[] mybytearray = new byte[(int) myFile.length()];
	
	    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile))) {
	    	bis.read(mybytearray, 0, mybytearray.length);
	        bos.write(mybytearray, 0, mybytearray.length);
	        bos.flush();
	        connection.close();
	        return;
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }	
	}
	
	public void shareDirInformation() {
		
		try (ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream())){
		
			if (oos != null) {
				ArrayList<String> fileNames = FileUtils.getFileNames(sharePath);
				ArrayList<Long> fileLengths = FileUtils.getFileLengths(sharePath);
				oos.writeObject(fileNames);
				oos.writeObject(fileLengths);
				System.out.println("Server-Ordner:");
				for (int i = 0; i < fileNames.size(); i++) {
					System.out.print(fileNames.get(i) + " " + fileLengths.get(i) + " Bytes");
				}
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Share-Ordner mitgeteilt.");
	}
}
