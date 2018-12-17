package utils;

public class MarshallingUtils {

	public static byte[] marshalling(int fileLength) {
		byte[] b = new byte[4];
		int shift = 0;
		for (int i=3; i>=0; i--) {
			b[i] = (byte) (fileLength >> shift);
			shift += 8;
		}
		return b;
	}
	
    public static int unmarshalling(byte[] buffer) {
    	int n = 0;
    	for(int i = 0; i < 4; i++) {
    		n<<=8;
    		n |= (int) buffer[i] & 0xFF;
    	}
    	return n;
    }
}
