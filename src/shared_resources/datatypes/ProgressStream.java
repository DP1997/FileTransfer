package shared_resources.datatypes;
import java.io.IOException;
import java.io.InputStream;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

//necessary for the progressbar implemented in the client application
public class ProgressStream extends InputStream implements AutoCloseable {

    public static long bytesRead = 0 ;
    public static double fileLength = 0;
    public static int progress = 0;
    private static SimpleDoubleProperty sdp = new SimpleDoubleProperty();

    private final InputStream stream ;

    public ProgressStream(InputStream stream) {
        this.stream = stream ;
    }
    @Override
    public int read() throws IOException {
        int result = stream.read() ;
        if (result != -1) {
            bytesRead++;
            progress++;
            // update progress bar no more than 100 times in one download
            if(progress >= (int)fileLength / 100) {
            	progress = 0;
            	setProgress(ProgressStream.bytesRead);	
            }
        }
        else setProgress(ProgressStream.bytesRead);	
        return result;
    }

    @Override
    public void close() throws IOException {
        super.close();
        stream.close();
    }
    public static void setProgress(double value) {
    	sdp.set(value/ fileLength);
    }
    public static double getProgress() {
    	return sdp.get();
    }
    public static long getBytesRead() {
    	return bytesRead;
    }
    public static DoubleProperty bytesReadProperty() {
    	return sdp;
    }
    public static void resetProgressBar() {
    	ProgressStream.progress = 0;
		ProgressStream.bytesRead = 0;
		ProgressStream.setProgress(0);
    }
	public static void setFileLength(int fileLength) {
		ProgressStream.fileLength = fileLength;
		
	}
}