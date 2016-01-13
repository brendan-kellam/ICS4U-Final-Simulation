package com.main.pathfinding;

import com.main.entity.Plane;
import com.main.util.Vector2i;

/**
 * Generates a tilemap used for pathfinding.
 * NOTE: the tilemaps are ONLY used for pathfinding
 **/
public class TileMapGenerator {
	
	//get the tileSize of a tile
	private int tileSize = Tile.size;
	
	//holds the vector passengers location
	private Vector2i location;
	
	//holds the initial minimums and maximums for the tilemp
	//NOTE: All extremities are multiples of the tileSize (4)
	private int xmin = 10, xmax = 1200;
	private int ymin = 160, ymax = 348;
	
	//create a new Tile array to hold the created tileMap
	private Tile[] tileMap;
	
	//the width and height of the tilemap.
	//NOTE: The width and height are in TILE precision
	private int width, height;
	
	
	//generates a new tileMap used for pathfining
	//accepts: the passenger coordinate position in integer format.
	public void generateMap(int passengerX, int passengerY){
		
		//create a new Vector to store the passenger's location
		location = new Vector2i(passengerX, passengerY);		
		
		//calculate the Extremities for the given passenger
		calculateExtremities();		
		
		//calculate the width and height of the tilemap.
		//NOTE: The width and height is in TILE precision, therefore is 1/4 actual scale.
		width = (xmax - xmin)/tileSize;
		height = (ymax - ymin)/tileSize;
								
		//create a new tile map array
		tileMap = new Tile[width * height];
		
		//loop from the minimum exterminate to the maximum, iterating by the tileSize.
		for(int y = ymin; y < ymax; y+=tileSize){
			
			//determine the y tile position
			//NOTE: This is in TILE precision
			int ya = (y - ymin)/tileSize;
		
			//loop in the x direction
			for(int x = xmin; x < xmax; x+=tileSize){
				
				//determine the x tile position
				int xa = (x - xmin)/tileSize;
			
				//create a new tile instance using absolute position for the tile
				//NOTE: This is in ABSOLUTE precision
				Tile tile = new Tile(x, y);
				
				//check if the tile is collidable
				tile.collidable = checkCollidable(x, y);
				
				//add the tile to the tilemap
				tileMap[xa + ya * width] = tile;
			}
		}
	}
	
	//determines if a given tile is collidable.
	//accepts a x and y position of the Tile in ABSOLUTE precision
	public boolean checkCollidable(int x, int y){
		
		//loop through the 16 pixels required to check a 4*4 tile. 
		for(int i = 0; i < 16; i++){
			
			//determine the x and y position on the tile
			//NOTE: The position is in ABSOLUTE precision and congruent with the screen coordinate system
			int xi = ((i % 4)) + x;
			int yi = ((i / 4)) + y;
			
			//create a new vector for the given pixel
			Vector2i pixel = new Vector2i(xi, yi);
			
			//if the pixel is in the collidable directory of the Plane, the tile is therefore marked as collidable
			if(Plane.collidablePixels.contains(pixel)){
				return true;
			}
		}
		
		//if the no pixel's where found to be collidable
		return false;
	}
	
	//calculate the extremities of the tilemap
	public void calculateExtremities(){
		
		//create vector representations of the minimum and maximum of the map 
		Vector2i min = new Vector2i(xmin, ymin);
		Vector2i max = new Vector2i(xmax, ymax);
		
		//fit the minimum and maximums to the passenger location
		min.fitToMultiple(location, 4);
		max.fitToMultiple(location, 4);
		
		//re-set the minimums and maximums
		xmin = min.getX();
		xmax = max.getX();
		ymin = min.getY();
		ymax = max.getY();
		
	}
	
	//returns the x-minimum of the tilemap
	public int getMinX(){
		return xmin;
	}
	
	//returns the x-maximum of the tilemap
	public int getMaxX(){
		return xmax;
	}
	
	//returns the y-minimum of the tilemap
	public int getMinY(){
		return ymin;
	}
	
	//returns the y-maximum of the tilemap
	public int getMaxY(){
		return ymax;
	}
	
	//return the generated tilemap
	public Tile[] getMap(){
		return tileMap;
	}
	
	//get the tilemap width
	public int getWidth(){
		return width;
	}
	
	//get the tilemap height
	public int getHeight(){
		return height;
	}
	
}
