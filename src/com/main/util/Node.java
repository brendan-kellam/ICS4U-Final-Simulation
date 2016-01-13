package com.main.util;

public class Node {

	public Vector2i tile;
	public Node parent; 
	public double fCost, gCost, hCost; //three calculations of the cost, gCost is the total node-to-node cost, or the total distance from the start
	//The hCost is the distance from the given node to the finish
	//The fCost is the total cost for a given node
	
	public Node(Vector2i tile, Node parent, double gCost, double hCost){
		this.tile = tile;
		this.parent = parent;
		this.gCost = gCost;
		this.hCost = hCost;
		this.fCost = this.gCost + this.hCost;
	}
	
}
