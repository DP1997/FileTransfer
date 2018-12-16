package datatypes;

import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;

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
