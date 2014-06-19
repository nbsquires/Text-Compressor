/*
 * HuffmanEncoding.java
 * Author: Natasha Squires
 * C Sc 361 Assignment #6
 */
import java.util.*;
import java.io.*;
import java.text.*;

public class HuffmanEndcoding {
	//instance variables
	private String textToEncode, stripString, anagram;
	private StringBuilder encodedText, decodedText;
	private String[] encoding;
	private int iteration;
	private ArrayList<Node> frequencies;
	private ArrayList<Node> huffmanTree, decompressedHuffman;
	private byte[] byteStream, fileData;
	private int byteIterator;
	private byte buffer;
	
	//Huffman encoding with a simple string
	public HuffmanEndcoding(String text)
	{
		setTextToEncode(text);
		huffmanTree = new ArrayList<Node>();
		frequencies = new ArrayList<Node>();	
		iteration=0;
	}

	public void setTextToEncode(String textToEncode) {
		this.textToEncode = textToEncode;
	}
	
	public void compression(){
		computeFrequencies();
		buildHuffmanTree();
		decode(encodedText.toString());
		compress();
		computeCompressionRatio();
		try{
			deCompress();
		}catch(IOException ioe){}
		
		System.out.println();
		printEncodedText("testing");
	}
	
	private void computeCompressionRatio()
	{
		double uncompressedLength = textToEncode.length()*8;
		double compressedLength = encodedText.toString().length();
		NumberFormat fmt = NumberFormat.getPercentInstance();
		double compressionRatio = compressedLength / uncompressedLength;
		System.out.println("Compression ratio (minus tree): " +fmt.format(compressionRatio));
	}
	//Computes the number of occurrences of a particular character
	private void computeFrequencies()
	{
		stripString = stripString(textToEncode);
		
		encoding = new String[stripString.length()];
		
		for(int i = 0; i < stripString.length(); i++)
		{
			int count = 1;
			for(int j=i; j < textToEncode.length(); j++)
			{
				if(stripString.charAt(i)==textToEncode.charAt(j))
				{
					StringBuilder nodeName = new StringBuilder();
					nodeName.append(stripString.charAt(i));
					String newNode = nodeName.toString();
					frequencies.add(i,new Node(newNode,count++));
				}
			}
		}
	}
	
	/*removes duplicate occurrences of characters in the text to encode*/
	private String stripString(String text)
	{
		StringBuilder stripString = new StringBuilder();
		
		for(int i=0; i < text.length(); i++)
		{
			String stripped = text.substring(i,i+1);
			if(stripString.indexOf(stripped)==-1)
				stripString.append(stripped);
			
		}
		
		return stripString.toString();
	}	
	
	public void buildHuffmanTree()
	{
		//sort frequencies 
		quickSort(frequencies, 0, stripString.length()-1);
		/*remove unnecessary frequency elements */
		for(int i=frequencies.size()-1; i >= stripString.length(); i--)
			frequencies.remove(i);
		
		//Add each computed frequency node as leaf nodes 
		Node lChild=null, rChild=null;
		for(int i=0; i < frequencies.size(); i++)
			huffmanTree.add(i,new Node(frequencies.get(i).getNodeName(), frequencies.get(i).getSum(), lChild, rChild));
		
				
		//size of the frequency list
		int listSize = stripString.length();
				
		/*the last element should be the root node of our completed tree, i.e. the frequency list should only have one remaining element*/
		while(listSize>1)
		{
			Node leftChild = null, rightChild = null, newNode = null;
			/*grab the first two nodes in our sorted list. They are the elements of the tree with the lowest sum value*/
			leftChild = frequencies.get(0);
			rightChild = frequencies.get(1);
					
			/*add them together to get the new node's sum (which will be LC and RC's root)*/
			int sum = leftChild.getSum()+rightChild.getSum();
			/*make the node name a concatenation of the LC and RC's node names*/
			String newNodeName = leftChild.getNodeName()+rightChild.getNodeName();
			/*instantiate the new node, set its children as LC and RC, and add to the huffmanTree list*/
			newNode = new Node(newNodeName, sum);
			
			/* since sorting the frequency arraylist messes up the huffman tree's references to added nodes,
			 * a search must be performed in order to set the correct child/parent references */
			for(int i=0; i < huffmanTree.size(); i++)
			{
				if(leftChild.getNodeName().equalsIgnoreCase(huffmanTree.get(i).getNodeName()))
				{
					huffmanTree.get(i).setParent(newNode);
					huffmanTree.get(i).setAsLeftChild(true);
					newNode.setLeftChild(huffmanTree.get(i));
				}
				if(rightChild.getNodeName().equalsIgnoreCase(huffmanTree.get(i).getNodeName()))
				{
					huffmanTree.get(i).setParent(newNode);
					huffmanTree.get(i).setAsRightChild(true);
					newNode.setRightChild(huffmanTree.get(i));
				}
			}
			huffmanTree.add(newNode);
					
			/*now LC and RC must be removed from the frequency list, since they are now represented by the new node (their root) 
			  -> we must add the root to the frequency list*/
			frequencies.remove(leftChild);
			frequencies.remove(rightChild);
			/*add the new node to the front of the list (arbitrary)*/
			frequencies.add(0, new Node(newNode.getNodeName(), newNode.getSum(), newNode.getLeftChild(), newNode.getRightChild()));

			/*sort the frequency list*/
			quickSort(frequencies, 0, frequencies.size()-1);
			
			/*increment listSize by 1 since we removed 2 nodes and added 1*/
			listSize--;
		}	
		
		/* Traverse the tree to find each letter in the text to encode and set its codeword value*/
		findLeaves(huffmanTree);
		encode(encoding, huffmanTree.get(huffmanTree.size()-1), "");
		
		printEncodedText(textToEncode);
	}
	
	/*
	 * simply marks whether or not a node is a leaf
	 */
	private void findLeaves(ArrayList<Node> tempTree)
	{
		for(Node node : tempTree)
		{
			if(node.getLeftChild()==null && node.getRightChild()==null)
			{
				node.setAsLeaf(true);
			}
		}
	}
	
	/*
	 * Creates a symbolic representation of each character's code word
	 */
	private void encode(String[] encode, Node root, String code)
	{
		if(!root.isLeaf())//if it is not a leaf
		{
			encode(encode, root.getLeftChild(), code + "0"); //append 0 
			encode(encode, root.getRightChild(), code + "1"); //append 1
		}
		else //if it is a leaf, then it is a single character -> set that character's encoding
		{
			encode[iteration] = code;
		    root.setEncoding(encode[iteration]);
			iteration++;
		}		
	}
	
	/*
	 * prints serialized huffmanTree and binary representations of the codewords to a file test.compressed
	 */
	private void compressToFile() throws IOException
	{
		File file = new File("bin/test.compressed");
		FileOutputStream out = new FileOutputStream(file);
//		PrintWriter p_out = new PrintWriter(new BufferedWriter(new FileWriter("bin/test.compressed"))	);
	//	ObjectOutputStream obj_out = new ObjectOutputStream(out);
		
	//	obj_out.writeObject(huffmanTree);
	//	p_out.println("Tree End");
		for(int i=0; i < byteIterator; i++)
			out.write(byteStream[i]);
		
	//	obj_out.close();
		out.close();			
	}
	
	/*
	 * Actual binary representation of the encoded text
	 */
	private void compress()
	{
		String s = encodedText.toString();
		byte[] test = new byte[s.length()];
		//byteStream = new byte[s.length()];
		buffer=0x00; //8 bit buffer
		int bits=0;
		for(int i=0; i < s.length(); i++)
		{
			if(s.charAt(i)=='0')
			{
				buffer <<= 1;//append 0
				bits++;
			}
			else if(s.charAt(i)=='1')
			{
				buffer <<= 1;//append 0
				buffer |= 1;//bitwise or appends 1
				bits++;
			}
			
			if(bits==8)
			{
				test[byteIterator] = buffer;
				byteIterator++;
				buffer=0x00;
				bits=0;
			}
		}
		
		byteStream = new byte[byteIterator];
		for(int i=0; i < byteIterator; i++)
		{
			byteStream[i] = test[i];
		}
		
		try{
			compressToFile();
		}catch(IOException ioe){}
	}
	
	/*
	 * prints symbolic representation of the encoded text, and any text in which its letters can be represented
	 * by nodes in the given huffman tree
	 */
	private void printEncodedText(String text)
	{
		anagram = text;
		System.out.println("String: "+anagram);
		encodedText = new StringBuilder();
		for(int i=0; i < anagram.length(); i++)
		{
			char c = anagram.charAt(i);
			
			for(Node node: huffmanTree)
			{
				if(node.isLeaf())
				{
					char[] character = node.getNodeName().toCharArray();
					if(c == character[0])
					{
						encodedText.append(node.getEncoding());
					}
				}
			}
		}
		
		System.out.println("Encoded Text: " + encodedText.toString());
	}
	
	private void decode(String text)
	{
		
		decodeEncodedText(huffmanTree.get(huffmanTree.size()-1), text);
		
		System.out.println("Decoded Text: " + decodedText.toString());
		decodedText = null;
	}
	/*
	 * Decodes the huffman encoded text back to the original readable representation
	 */
	private void decodeEncodedText(Node root, String text) 
	{
		System.out.println("Decoding... ");
		decodedText = new StringBuilder();
		
		//for each bit in the encoding
		for(int i=0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			if(c=='0')
			{
				if(!root.getLeftChild().isLeaf())
				{
					root = root.getLeftChild();
				}
				else
				{
					decodedText.append(root.getLeftChild().getNodeName());
					root = huffmanTree.get(huffmanTree.size()-1);
				}
			}
			else if(c=='1')
			{
				if(!root.getRightChild().isLeaf())
				{
					root = root.getRightChild();
				}
				else
				{
					decodedText.append(root.getRightChild().getNodeName());
					root = huffmanTree.get(huffmanTree.size()-1);
				}
			}
		}
	}
	
	/*
	 * reads compressed file (doesn't fully work, couldn't figure out how to read binary files in time)
	 */
	private void deCompress() throws IOException
	{
	/*	System.out.println("1");
		File file = new File("bin/test.compressed");
		fileData = new byte[(int)file.length()];
		System.out.println("2");
		try{
			DataInputStream is = new DataInputStream(new FileInputStream(file));
			System.out.println("3");
			//BufferedInputStream in = new BufferedInputStream(is);
			
			System.out.println("4");
			
			is.readFully(fileData);
			System.out.println("5");
			is.close();
		}catch(Exception e){}*/
		
		

		//in.readFully(fileData);
		//in.close();
		
		System.out.println("begin expansion");
		readBinary();
		System.out.println("End expansion");
	}	
	
	private void readBinary()
	{
		StringBuilder expand = new StringBuilder();
		int bits=8;
		
		for(int i=0; i < /*fileData.length*/byteStream.length; i++)
		{
			bits--;
			buffer = byteStream[i];//fileData[i];
			if(((buffer >> bits) & 1)==1)
			{
				expand.append('1');
			}
			else
				expand.append('0');
			
			if(bits==0)
				bits=8;
		}
		
		System.out.println("Expanded: " + expand.toString());
		decodeEncodedText(huffmanTree.get(huffmanTree.size()-1), expand.toString());
		System.out.println("Decoded: " + decodedText.toString());
	}
	/*********************************************
		Quick Sort
	***********************************************/
	private void quickSort(ArrayList<Node> tempList, int first, int last)
	{
		if(first < last)
		{
			int middle;
			middle = partition(tempList, first, last);
			quickSort(tempList, first, middle-1);
			quickSort(tempList, middle+1, last);
		}
	}
	
	private int partition(ArrayList<Node> tempList, int first, int last)
	{
		int key;
		key = tempList.get(first).getSum();
		int partition = first;
		
		for(int k = first+1; k <= last; k++)
		{
			if(tempList.get(k).getSum() < key)
			{
				partition++;
				int temp = tempList.get(partition).getSum();
				tempList.get(partition).setSum(tempList.get(k).getSum());
				tempList.get(k).setSum(temp);	
				String tempNodeName = tempList.get(partition).getNodeName();
				tempList.get(partition).setNodeName(tempList.get(k).getNodeName());
				tempList.get(k).setNodeName(tempNodeName);
			}
		}
		
		int temp = tempList.get(first).getSum();
		tempList.get(first).setSum(tempList.get(partition).getSum());;
		tempList.get(partition).setSum(temp);
		String tempNodeName = tempList.get(first).getNodeName();
		tempList.get(first).setNodeName(tempList.get(partition).getNodeName());
		tempList.get(partition).setNodeName(tempNodeName);
		
		return partition;
	}
}
