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

import utils.FileUtils;

public class FileTransferThread extends Thread{
	
	private Socket connection = null;
    private String sharePath = "/home/donald/Schreibtisch";

	
	public FileTransferThread(Socket sock) {
		super("FileTransferThread");
		this.connection = sock; 	
	}
	
	public void run() {
		System.out.println("Connection established");
		
		shareDirInformation();
		/*
		try (BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream())){
			
			if (bos != null) {
                File myFile = new File(sharePath);
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
		*/
	}
	
	public void shareDirInformation() {
		
		try (ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream())){
		
			if (oos != null) {
				ArrayList<String> fileNames = FileUtils.getFileNames(sharePath);
				oos.writeObject(fileNames);
				System.out.println("Server-Ordner:");
				for(String s : fileNames) {
					System.out.println(s);
				}
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Share-Ordner mitgeteilt.");
	}
}
