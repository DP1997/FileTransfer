package datatypes;

import java.io.IOException;
import java.io.InputStream;

public class ProgressStream extends InputStream implements AutoCloseable {

    private long bytesRead = 0 ;

    private final InputStream stream ;

    public ProgressStream(InputStream stream) {
        this.stream = stream ;
    }

    @Override
    public int read() throws IOException {
        int result = stream.read() ;
        if (result != -1) {
            bytesRead++;
        }
        return result ;
    }

    @Override
    public void close() throws IOException {
        super.close();
        stream.close();
    }

    public long getBytesRead() {
        return bytesRead ;
    }
}