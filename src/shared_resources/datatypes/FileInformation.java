package shared_resources.datatypes;

//metadata of the files contained in the directory that is shared by the server
public class FileInformation {
	
	public final String fileName;
	public final String fileLength;
	
	public FileInformation(String fileName, String byteLength) {
		this.fileName = fileName;
		this.fileLength = byteLength;
	}
	
	@Override
	public String toString() {
		return fileName+", "+fileLength;	
	}
}
