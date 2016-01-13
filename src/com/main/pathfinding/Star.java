package com.main.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.main.util.Node;
import com.main.util.Vector2i;

/**
 * Handles the A* pathfinding algorithm. 
 */

public class Star {
	
	//the imported tilemap required for pathfinding
	private Tile[] tileMap;
	
	//the width and height of the tilemap
	private int width, height;
	
	//the tileMapGenerator instance (See: TileMapGenerator.java)
	private TileMapGenerator generator;

	private Comparator<Node> nodeSorter = new Comparator<Node>(){ //takes in two node opjects, and compares them
		public int compare(Node n0, Node n1) {
			if(n1.fCost < n0.fCost) return +1; //move n1 up in index
			if(n1.fCost > n0.fCost) return -1; //move n1 down in index
			return 0;							//if the nodes are the same
		}
	};
	
	//constructor
	//Accepts: TileMapGenerator instance
	public Star(TileMapGenerator generator){
		
		//set the required variables
		tileMap = generator.getMap();
		width = generator.getWidth();
		height = generator.getHeight();
		this.generator = generator;
	}
	
	//Finds a new path given a start and finish
	//Returns a ArrayList of Nodes
	public List<Node> findPath(Vector2i start, Vector2i goal){
		
		List<Node> openList = new ArrayList<Node>(); //every tile that is being considered
		List<Node> closedList = new ArrayList<Node>(); //after a tile has been processed, it will be added here
		
		//the current node
		Node current = new Node(start, null, 0, start.getDistance(goal));
		openList.add(current);
		
		//if the start is already at the goal, simply return the openList
		if(start.equals(goal)){
			return openList;
		}
		
		//while the openList is still occupied
		while(openList.size() > 0){
			
			Collections.sort(openList, nodeSorter); //sorts our openList from lowest fCost to highest
			current = openList.get(0); //gets the starting node, the one with the lowest cost, determined by the comparator
			
			//if the current tile is the goal
			if(current.tile.equals(goal)){
				List<Node> path = new ArrayList<Node>();
				while(current.parent != null){ //retrases steps from the finish to the start
					path.add(current); //adds each node back into the path arraylist
					
					//re-sets the parent
					current = current.parent;
				}
				
				//clears the open and closed list
				openList.clear();
				closedList.clear();
				return path;
			}
			
			openList.remove(current); //remove the current node from the open list and add it to the closed
			closedList.add(current);
			for(int i = 0; i < 9; i++){ //checks all nodes, 4 is the middle
				if(i == 4) continue;
				
				//gets the x and y coordinate of the given tile
				int x = (int)current.tile.getX();
				int y = (int)current.tile.getY();
				
				//produces a quadrant-checking like system, starting from the top left, reaching the bottom right
				int xi = ((i % 3) - 1)*Tile.size;
				int yi = ((i / 3) - 1)*Tile.size;

				//gets the absolute position accounting for the quadrent offset
				Tile at = getTile(x + xi, y + yi);
				
				//if no tile exists here, continue
				if(at == null) continue;
				
				//if the tile is collidable, also continue
				if(at.isCollidable()) continue;
	
				
				Vector2i a = new Vector2i(x + xi, y + yi); //tile in vector form
				double gCost = current.gCost + (current.tile.getDistance(a) == 1 ? 1 : 0.95); //gets the distance from the middle
				double hCost = a.getDistance(goal); //determines the h cost
				
				//creates a new node using this data
				Node node = new Node(a, current, gCost, hCost);
				
				//if the node is not worth adding to the openlist due to it being both in the closed list and having a high gcost
				if(vecInList(closedList, a) && gCost >= node.gCost) continue;
				
				//if the node is not in the closed list and has a lower gcost, add it to the openlist
				if(!vecInList(openList, a) || gCost < node.gCost) openList.add(node);
			}
		}
		
		//if no path is found, clear the closed list and return null
		closedList.clear();
		return null;
		
	}
	
	//returns a tile at a specific location
	private Tile getTile(int x, int y){
		
		//if the tiles location exceeds bounds, return null
		if(x < generator.getMinX() || x > generator.getMaxX() || y < generator.getMinY() || y > generator.getMaxY()){
			return null;
		}
		
		//get the tile x and y. 
		//NOTE: These components are in TILE format
		x = (x-generator.getMinX()) / Tile.size;
		y = (y-generator.getMinY()) / Tile.size;
		
		//return the tile at the location
		return tileMap[x + y * width];

	}
	
	//determines if a vector is in a list
	private boolean vecInList(List<Node> list, Vector2i vector){
		
		//loop the list
		for(Node n : list){
			
			//return true if the vector is contained
			if(n.tile.equals(vector)) return true;
		}
		//return false if not
		return false;
	}

	
}
