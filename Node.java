/*
 * Node.java
 * Author: Natasha Squires
 * C Sc 361 Assignment #6
 */
import java.io.Serializable;


public class Node implements Serializable {
	private String nodeName;
	private  transient int sum;
	private Node parent, leftChild, rightChild;
	private boolean isLeftChild, isRightChild, isLeaf;
	private transient String encoding;
	
	public Node(){}
	
	public Node(String nodeName, int sum, Node leftChild, Node rightChild)
	{
		this.nodeName = nodeName;
		this.sum = sum;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		isLeaf = false;
	}
	
	
	public Node(String nodeName, int sum)
	{
		this.nodeName = nodeName;
		this.sum = sum;
	}
	
	public Node(int sum)
	{
		this.sum = sum;
	}
	
	public void setLeftChild(Node leftChild)
	{
		this.leftChild = leftChild;
	}
	
	public Node getLeftChild()
	{
		return leftChild;
	}
	
	public void setRightChild(Node rightChild)
	{
		this.rightChild = rightChild;
	}
	
	public Node getRightChild()
	{
		return rightChild;
	}
	
	public void setParent(Node parent)
	{
		this.parent = parent;
	}
	public Node getParent()
	{
		return parent;
	}
	
	public void setNodeName(String nodeName)
	{
		this.nodeName = nodeName;
	}
	
	public String getNodeName()
	{
		return nodeName;
	}
	
	public void setSum(int sum)
	{
		this.sum = sum;
	}
	
	public int getSum()
	{
		return sum;
	}
	
	public void setAsLeftChild(boolean isLeftChild)
	{
		this.isLeftChild = isLeftChild;
	}
	
	public boolean isLeftChild()
	{
		return isLeftChild;
	}
	
	public void setAsRightChild(boolean isRightChild)
	{
		this.isRightChild = isRightChild;
	}
	
	public boolean isRightChild()
	{
		return isRightChild;
	}
	
	public void setAsLeaf(boolean isLeaf)
	{
		this.isLeaf = isLeaf;
	}
	
	public boolean isLeaf()
	{
		return isLeaf;
	}
	
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}
	
	public String getEncoding()
	{
		return encoding;
	}
	
	public String toString()
	{
		return nodeName+": "+sum; 
	}
	
	public String allInfo()
	{
		return "Node: "+nodeName+": "+sum+"\nParent: "+getParent()+"\nLeft Child: "+getLeftChild()+"\nRight Child: "+getRightChild();
	}
}
