package com.main.entity;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.main.Main;
import com.main.graphics.Graphic;
import com.main.graphics.ImageLoader;
import com.main.graphics.Screen;
import com.main.pathfinding.Star;
import com.main.pathfinding.Tile;
import com.main.pathfinding.TileMapGenerator;
import com.main.util.Node;
import com.main.util.Vector2i;

/**
 * Handles all movement and AI characteristics of a passenger. <br>
 * The Passenger has these given characteristics:
 * <li> Mass <br>
 * <li> Survival percentage <br>
 * <li> AI pathfinding <br>
 * <li> Communication techniques
 */

public class Passenger extends Entity{

	//x, y position of the passenger
	private double x, y;
	
	//the vector position of the passenger
	private Vector2i position;
	
	//Instance of the Plane.java class
	private Plane plane;
	
	//holds the array index of where this instance is held in the passenger array
	private int id;
	
	//speed at which a passenger can move
	private double speed;
	
	//the mass of the given passenger
	private int mass;

	//passenger graphic
	private Graphic graphic;
	
	//width and height of the passenger square
	private int width = 10, height = 10;
	
	//holds the deathGraphic
	private Graphic deathGraphic; 
	
	//creates a new bounding collision Rectangle
	private Rectangle collision;
		
	//holds if the passenger is alive or not
	private boolean alive = true;
		
	//the initial response for a passenger
	private double initialResponse = 0.0;
			
	//holds if the passenger is stationary
	private boolean stationary = false;
	
	//the distence a given passenger can shout to another
	private double shoutRadius;
	
	//generator object used to generate a tile map
	private TileMapGenerator generator;
		
	//array to hold the tilemap for the given passenger
	private Tile[] tileMap;
	
	//instance of Star.java, giving passengers path-finding abilities
	private Star aStar;
	
	//the calculated path for the passenger to take
	List<Tile> path = new ArrayList<Tile>();
	
	//the current player tile target
	private Tile target = null;
	
	//holds the possible exits a given passenger can use to escape
	private ArrayList<Exit> possibleExits;
		
	//the current exit the Passenger is focused on
	private Exit currentExit;
	
	//holds if the passenger is currently exiting the aircraft
	private boolean exiting = false;

	//holds all exits the passenger will shout to others
	private List<Exit> instructedExits = new ArrayList<Exit>();
	
	//if the passenger is waiting in a door Queue
	private boolean inExitQueue = false;
	
	//the maximum gForce a passenger can take
	public static final double DEATHLYGFORCE = 10.2;
	
	//the minimum speed a passenger can travel
	public static final double MINSPEED = 0.05;
	
	//the base mass (or average) for a passenger.
	//NOTE: Measured in kg
	public static final double BASEMASS = 68;
	
	//the minimum and maximum mass for a given passenger
	public static final int MASSMIN = 36, MASSMAX = 140;
	
	//5 different colours to represent and differentiate the passengers
	private int[] colours = {0xffe61e1e, 0xff1eff00, 0xff9856f3, 0xff19dd19, 0xff29330b};
	
	//create new random instance
	private Random rand = new Random();
	
	
	//constructor for passenger
	public Passenger(double x, double y, int mass, boolean alive, Plane plane, int id){
		
		//set the x and y position
		this.x = x;
		this.y = y;
		
		//set the mass of the player
		this.mass = mass;
		
		//set if the passenger is initially alive
		this.alive = alive;
				
		//set the plane instance
		this.plane = plane;
		
		//set the passenger id
		this.id = id;
		
		//Instantiate the vector position
		position = new Vector2i((int) x, (int) y);	
		
		//create a new collision box
		this.collision = new Rectangle((int)x, (int)y, width, height);
		
		//generate the passenger graphic
		generateGraphic();
		
		//generate a new pathfinding tilemap. Following tileMap generation, a new instance of Star.java is created
		generateTileMap();	
						
		//generate a new A* pathfinding instance
		aStar = new Star(generator);
		
		//calculate the speed of the passenger
		speed = calculateSpeed();
		
		//set the minimum and maximums for a shout, and then generate the shout
		int min = 10, max = 20;
		shoutRadius = rand.nextInt((max - min) + 1) + min;
		
		//set the initial response time of the passenger
		initialResponse = calculateInitialResponse();
		
		//Initially set the possibleExits to all cached exits
		this.possibleExits = new ArrayList<Exit>(Arrays.asList(plane.getExits()));
		
		//generate a exit target
		generateExitTarget();
				
		findPath(currentExit.getPosition());
	}
	
	
	//Develops a path for the passenger to take, using A*. Accepts: destination vector2i
	//See: (Star.java)
	private void findPath(Vector2i dest){
		
		//intitially clear the previous path
		path.clear();
		
		//fits the destination to the same multiple as the Passenger
		position.fitToMultiple(dest, 4);
			
		//find a new path from the current passenger position to the destination
		List<Node> path = aStar.findPath(position, dest);
		
		//if no path could be found, continue
		if(path == null){
			
			//state a path could not be found ( :{ ), and then kill the passenger
			System.out.println("------path cannot be found---------");
			alive = false;
			return;
		}
		
		//loop the path size
		for(int i = 0; i < path.size(); i++){
			
			//get each node at the specified index
			Node node = path.get(i);
			
			//get the Vector2i related to the given node
			Vector2i tilePath = node.tile;
			
			//create a new tile with this data and add the tile to the path list
			Tile tile = new Tile(tilePath.getX(), tilePath.getY());
			this.path.add(tile);
		}
	}
	
	//creates a sorted list of possible exit targets, from closest in distance to furthest away.
	private void generateExitTarget(){
				
		//sort the exits from greatest to least in distance from the player
		Collections.sort(possibleExits, exitSorter);
		
		if(possibleExits.isEmpty()){
			return;
		}
		
		//set the current exit to the closest exit
		currentExit = possibleExits.get(0);

	}
	
	//calculates the speed of the passenger, using the speed formula (See: Master comment in Plane.java)
	private double calculateSpeed(){
		
		//set the speed of the player
		double speed = (1.0 - (plane.getGForce()/DEATHLYGFORCE)) - (mass/BASEMASS-1);
		
		//if the calculated speed is less the minimum, then re-set the speed to be the min
		if(speed <= MINSPEED){
			speed = MINSPEED;
		}
		
		//return the calculated speed
		return speed;
	}
	
	//calculates the random intial response time of the passenger
	private double calculateInitialResponse(){
		
		//set the response to the gForce * 1.5 initially
		double response = plane.getGForce()*1.5;
		int min = -5, max = 5;
		
		//randomly offset this response time
		int offset = rand.nextInt((max - min) + 1) - min;
		
		//return the sum of the response and offset
		return response + offset;
	}
	
	//generates a random graphic for each passenger.
	private void generateGraphic(){
		
		//generate a random colour
		Random rand = new Random();
		int colour = colours[rand.nextInt(5)];
		
		//create a new pixel array buffer
		int[] pixels = new int[width*height];
		
		//loop through the width and height
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				
				//append the randomized colour at a given index
				pixels[x + y * width] = colour;
			}
		}
		
		//create a new graphic with the generated data
		graphic = new Graphic(width, height, pixels);
		
		//get the death graphic
		deathGraphic = ImageLoader.getImage("/death.png");
	}
	
	//generates a new tile map
	//NOTE: The tilemap is solely used for pathfinding
	private void generateTileMap(){
		
		//if not the root passenger, a tilemap can be inherited
		if(id != 0){
			
			//get the root passenger (i.e: the passenger with id = 0)
			Passenger root = plane.getPassengers()[0];
			
			//fit the passengers vecorized position to the root passenger
			position.fitToMultiple(root.getPos(), 4);
			
			//reset the x and y
			this.x = position.getX();
			this.y = position.getY();
			
			//get the root tileMap and return out of the function
			generator = plane.getRoot();
			tileMap = generator.getMap();
			
			return;
		}
		
		//-- if root passenger:
		
		//create a new TileMapGenerator object
		generator = new TileMapGenerator();
		
		//generate the map and pull tile data plus width and height
		generator.generateMap((int) x, (int) y);
		tileMap = generator.getMap();
		
		//set the root tilemap in the Plane.java class (See: Plane.java)
		plane.setRoot(generator);
	}
	
	//sorts the cached exits from closest to farthest from the passenger
	private Comparator<Exit> exitSorter = new Comparator<Exit>(){

		//a comparator function
		public int compare(Exit e1, Exit e2) {
			
			//get the distance from the player to the exit
			double d1 = e1.getPosition().getDistance(position);
			double d2 = e2.getPosition().getDistance(position);
			
			//compare these distances to determine the sorting requirements
			if(d2 < d1) return +1; //move e2 up in the index
			if(d2 > d1) return -1; //move e2 down in the index
			return 0; //if the distances are the same, do nothing
		}
	};
	
	//define new collisionCounter, holding the number of collisions that have occured
	private int collisionCount = 0;
	//handles players movement. Accepts a target, (xa, ya).
	//Note: THE TARGET IS A SPECIFIC COORDINATE POINT, NOT A TRANSLATIONAL SHIFT.
	private void move(int xa, int ya){
		
		
		//calculates the translational shift applied.
		//NOTE: No absolute is applied to difference, therefore xdiff and ydiff are vector values, with magnitude and direction.
		double xdiff = xa - x;
		double ydiff = ya - y;
		
		//if the player is not at the target coordinate
		if(x != xa || y != ya){
			
			//set the x and y move to the distance
			//NOTE: Also holds the translational shift
			double xmove = xdiff;
			double ymove = ydiff;
			
			//if the difference is greater than the speed, set the move to the speed. Prevents the player from over traveling. 
			//NOTE: The direction is applied through the use of sign(xdiff), therefore no handling of direction must be applied when translating
			if(Math.abs(xdiff) >= speed) xmove = speed * sign(xdiff);
			if(Math.abs(ydiff) >= speed) ymove = speed * sign(ydiff);
	
			//if the collisionCount is greater than the randomly generated integer, ignore collision.
			//NOTE: This system is used to allow passengers to essentially "push" past eachother
			if(collisionCount > rand.nextInt(60)){
				
				//move the passenger normally, ignoring collision
				x+=xmove;
				y+=ymove;
				collisionCount = 0;
				return;
			}
			
			//---check collision on the two separate axisis---
			
			//if no collison is found, move in the x axis
			if(!collision(xmove, 0)) x+=xmove;
			//if collision is found, add to the collision counter
			else collisionCount++;
			
			//repeat for y component
			if(!collision(0, ymove)) y+=ymove;
			else collisionCount++;
		}
		
	}
	
	//Handles collision between two passenger bodies
	//Accepts: the translational shifts that would be applied if the passenger where to move
	private boolean collision(double xa, double ya){
		
		//determine the absolute x and y by adding the shifts
		int rectX = (int) (x + xa), rectY = (int) (y + ya); 
		
		//set col to the location calculated
		Rectangle col = collision;
		col.setLocation(rectX, rectY);
		
		//get the result from the testCollision function
		boolean result = testCollision(col);
		
		//if no collision has occurred, set the collision box to the shifted coordinate points
		if(!result){
			collision.setLocation(rectX, rectY);
		}
		
		//return the result from the collision
		return result;
		
	}

	//Handles the specific collision between two bodies by checking if their collision boxes overlap
	private boolean testCollision(Rectangle collision){
		
		//loop through all passengers
		for(Passenger passenger : plane.getPassengers()){
			
			//if the current passenger is this, then continue
			if(passenger.getID() == getID()) continue;
			
			//if the two passengers colide, then return a boolean true for collision
			if(passenger.getCollision().intersects(collision)){
				return true;
			}
			
		}
		
		//return false for no collision
		return false;
	}
		

	//handles moving along the pre-calculated A* path.
	//See: Star.java
	private void pathMove(){
		
		//if the path is empty, then return out of the function
		if(path.isEmpty()) return;

		//if the current target is null continue
		if(target == null){
			
			//set the target to the closest node to the passenger
			target = path.remove(path.size()-1);
		}else{
			
			//if the current vector passenger position is equal to the target, then set the target
			//to null to allow for a new one to be cached
			if(position.getX() == target.getX() && position.getY() == target.getY()){
				target = null;
				return;
			}
			
			//move the passenger to the specified target
			move(target.getX(), target.getY());
			
		}
	
	}
	
	
	//handles the event of a functioning exit
	//NOTE: This function is called  from Exit.java in the event the passenger is at a functioning exit
	public void handleFunctioningExit(){
		
		//set being in the queue back to false, allowing for the passenger
		//to continue to pathfind
		inExitQueue = false;
		
		//set the exiting boolean to true, allowing the passenger to exit
		exiting = true;
	}
	
	//handles the event of a non-functioning exit
	//NOTE: This function is called from Exit.java in the event the passenger reachign a non-functioning exit
	public void handleBrokenExit(){
		
		//add the broken exit to the instructed list to allow the given passenger to tell others 
		instructedExits.add(currentExit);
		
		//remove the current exit from the total list
		possibleExits.remove(currentExit);
		
		//generate a new exit target and find a new path
		generateExitTarget();
		findPath(currentExit.getPosition());
		
		//set being in a exit queue to false
		inExitQueue = false;
	}
	
	//handles when another passenger instructs a specific exit is not functioning
	//NOTE: This function is exclusively called from other passengers
	public void instructedOfBrokenExit(Exit exit){
		
		//if the path size is down to one, then ignore the shouts
		if(path.size() == 1) return;
		
		//if the passenger has already been instructed about the exit, ignore
		if(!instructedExits.contains(exit)){
			
			//remove the exit from the possible exits
			possibleExits.remove(exit);
			
			//generate a new path 
			generateExitTarget();
			findPath(currentExit.getPosition());
			
			//add the broken exit to the passengers instructedExits, allowing them in-tern to tell others
			instructedExits.add(exit);
		}
		
	}

	//handles instructing other passengers of a broken exit
	//NOTE: This function is called withing a instance of Passenger.java
	private void instruct(){
		
		//if the instructedExits is not empty
		if(!instructedExits.isEmpty()){
			
			//loop through all passengers
			for(Passenger p : plane.getPassengers()){
				
				//if the current passenger is itself, then continue looping
				if(p == this) continue;
				
				//if the passenger is within range, continue
				if(getPos().getDistance(p.getPos()) <= shoutRadius){
					
					//instruct the passenger of all the broken exits
					for(Exit e : instructedExits){
						p.instructedOfBrokenExit(e);
					}
				}
				
				
			}
		}
	}
	
	//the update function
	public void update(){
		
		
		//if the passenger is not alive, then do not update
		if(!alive) return;
		
		//the passenger cannot function until the initial response (or shock) time is
		//completely depleted
		if(Main.simulationTime <= initialResponse) return;
		
		//if the passenger is not in a queue, then move
		if(!inExitQueue) pathMove();
		
		//if the passenger is not in a exit queue, path is empty and has been flagged for exiting, the passenger is removed
		if(!inExitQueue && path.isEmpty() && exiting){
			plane.escaped++;
			remove();
		}
		
		//if communication is turned on, instruct other passengers on broken exits
		if(plane.communicate()) instruct();
		
		//re-set the vector position
		position.set((int) x, (int) y);
		collision.setLocation((int) x, (int) y); 
		
	}

	//the render function
	public void render(Screen screen){
		
		//render the passenger
		screen.renderGraphic(graphic, (int) x, (int) y);
		
		//if the passenger is not alive, render the dead graphic
		if(!alive) screen.renderGraphic(deathGraphic, (int) x, (int) y);
	}
	
	//renders the A* path to the screen
	//NOTE: This function is called in Plane.java and is dependent on the state set by ControlPanel.java
	public void renderPath(Screen screen){
		for(int i = 0; i < path.size(); i++){
			path.get(i).render(screen);
		}
	}
	
	//gets the sign of a number (ex: sign(42) returns 1 while sign(-42) returns -1)
	private int sign(double value){
		if(value < 0) return -1;
		return 1;
	}
	
	//enters the passenger into a exit queue
	//NOTE: This is called exclusivly from Exit.java
	public void enterExitQueue(){
		inExitQueue = true;
	}

	//returns if the passenger is stationary
	public boolean isStationary(){
		return stationary;
	}
	
	//returns if the passenger is in a exit queue
	public boolean isInExitQueue(){
		return inExitQueue;
	}
	
	//returns the passenger speed
	public double getSpeed(){
		return speed;
	}

	//returns if the passenger is alive
	public boolean isAlive(){
		return alive;
	}
	
	//returns the collision box
	public Rectangle getCollision(){
		return collision;
	}
	
	//return the x-point
	public double getX(){
		return x;
	}
	
	//return the y-point
	public double getY(){
		return y;
	}
	
	//returns the id of the passenger
	public int getID(){
		return id;
	}
	
	//get the vector position
	public Vector2i getPos(){
		return position;
	}
	
	//returns the tileMap related to the given passenger
	public TileMapGenerator getTileMap(){
		return generator;
	}
	
}
