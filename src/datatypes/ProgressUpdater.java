package datatypes;

import application.FileTransferController;
import javafx.application.Platform;

public class ProgressUpdater extends Thread{
	
	boolean running = true;
	long counter = 0;
	public void run() {
		while(running) {
			double d = (double) ProgressStream.getBytesRead() / (double) 1700000;
            Platform.runLater(new Runnable() {
                @Override public void run() {
                }
            });
			System.out.println(ProgressStream.getBytesRead());
		}
	}
}