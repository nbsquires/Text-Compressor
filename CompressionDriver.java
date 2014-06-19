/*
 * CompressionDriver.java
 * Author: Natasha Squires
 * C Sc 361 Assignment #6
 */
import java.io.*;
import java.util.*;

public class CompressionDriver {

	public static void main(String[] args) throws IOException{		
		Scanner fileScan = new Scanner(new File("bin/test.txt"));
		StringBuilder textToEncode = new StringBuilder();
		while(fileScan.hasNext())
			textToEncode.append(fileScan.nextLine().toLowerCase()+"\n");
		
		HuffmanEncoding encode = new HuffmanEndcoding(textToEncode.toString());
		encode.compression();
	}

}
