package datatypes;

import java.io.IOException;
import java.io.InputStream;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ProgressStream extends InputStream implements AutoCloseable {

    private static long bytesRead = 0 ;
    private static SimpleDoubleProperty sdp = null;

    private final InputStream stream ;

    public ProgressStream(InputStream stream) {
        this.stream = stream ;
    }

    @Override
    public int read() throws IOException {
        int result = stream.read() ;
        if (result != -1) {
            bytesRead++;
            System.out.println(bytesRead);
            if (bytesRead == 10000) {
                ProgressStream.bytesReadProperty().set(0.2);        	
            } 
            else if (bytesRead > 100000) {
                ProgressStream.bytesReadProperty().set(0.4); 
            }
        }
        return result ;
    }

    @Override
    public void close() throws IOException {
        super.close();
        stream.close();
    }

    public static DoubleProperty bytesReadProperty() {
    	if (sdp == null) {
		sdp = new SimpleDoubleProperty(0);
    	}
    	return sdp;	
    }
}