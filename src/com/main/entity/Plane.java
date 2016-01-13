package com.main.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.main.Main;
import com.main.graphics.Graphic;
import com.main.graphics.ImageLoader;
import com.main.graphics.Screen;
import com.main.pathfinding.TileMapGenerator;
import com.main.util.Vector2i;

/**
 * Plane.java handles interaction between the user and the plane logic.
 * 
 * <br>
 * <h2>A list of all equations applied to the aircraft</h2>
 * <ul>
 * 
 * <h4>Determine deceleration time</h4>
 * <li>t(A)= (-1 * 2^(0.059*A) + 40 )/6.5 </li>
 * t = time (s) <br>
 * A = Angle of Crash (degrees)
 * <br>
 * 
 * <h4>Survival percent</h4>
 * <li>s(g) = (1.0 - (g/DEATHLYGFORCE))*100</li>
 * s = survival percentage (%)<br>
 * g = # of g forces applied on the passenger
 * <br>
 * 
 * <h4>Movement speed</h4>
 * <li>s(g, m) = (1.0 - (g/DEATHLYGFORCE)) - (m/BASEMASS-1);</li>
 * s = speed of passenger (px)<br>
 * g = # of g forces applied on the passenger <br>
 * m = mass of passenger (kg)
 * 
 * </ul>
 */

public class Plane extends Entity{	
	
	//the x and y position of the Plane
	public double x = 0, y = 160;
	
	//the graphic for the plane
	private Graphic graphic;
	
	//the main instance
	private Main main;
	
	//graphic path
	private String gp = "/plane.png"; 
	
	private static final int COLLIDABLECODE = 0xff000000;
	
	//the amount of souls on board. max =132
	private int passengerCount;
	
	//stores the survival percentage for a given passenger
	private double survivalChance;
	
	//holds if the survivalChance will be applied or not
	private boolean accountForSurvival;

	//the inflicted gFroce on impact
	private double gForce;
	
	//number of exits on the airplane. Note: Exit-count is constant and cannot be changed
	public static final int EXITCOUNT = 8;
	
	//the mass of the airplane with no passengers
	public final static int PLANEMASS = 48500;
	
	//holds all exits, with a boolean value representing if a exit works
	private boolean[] workingExits;
	
	//the mass for each given passenger
	private int[] massIndex;
	
	//deterimes if the passengers should communicate or not
	private boolean communicate = false;
	
	//determines if a passengers path should be rendered
	private boolean renderPath;
	
	//holds each passenger on the plane
	private Passenger[] passengers;
	
	//holds each exit door on the plane
	private Exit[] exits = new Exit[EXITCOUNT];
	
	//Holds all collidablePixels on the plane graphic
	public static List<Vector2i> collidablePixels = new ArrayList<Vector2i>();
	
	//the root tilemap for all passengers 
	private TileMapGenerator rootTilemap;
	
	//create a new random instance
	private Random rand = new Random();
	
	//the number of passengers who escaped
	public int escaped = 0;

	//constructor
	public Plane(Main main){
		
		//set main
		this.main = main;
		
		//set the graphic
		graphic = ImageLoader.getImage(gp);
	}
	
	//generate and start the simulation.
	//NOTE: This function is called in the main function, but originates from ControlPanel.java
	public void generateSimulation(int passengerCount, double survivalChance, boolean accountForSurvival, 
			double gForce, int[] massIndex, boolean[] workingExits, boolean communicate, boolean renderPath){
		
		//set all incoming variables 
		this.passengerCount = passengerCount;
		this.survivalChance = survivalChance;
		this.accountForSurvival = accountForSurvival;
		this.gForce = gForce;
		this.massIndex = massIndex;
		this.workingExits = workingExits;
		this.communicate = communicate;
		this.renderPath = renderPath;
		
		//sets the passengers
		passengers = new Passenger[passengerCount];
		
		//generate the list of collidable pixels
		generateCollision();
		
		//load the exits into the plane
		loadExits();
		
		//load the passengers into the plane
		loadPassengers();
				
	}
	
	//interprets the graphic to place the required emergency exits
	//NOTE: Exits are located using the colour code: 0xffffae00
	private void loadExits(){
		
		//get graphic data
		int w = graphic.getWidth();
		int h = graphic.getHeight();
		int[] pixels = graphic.getGraphic();
		
		//set a iterator value
		int iterator = 0;
		
		//loop through the contents of the y axis
		for(int y = 0; y < h; y++){
			int ya = y + (int) this.y;
			
			//loop through the contents of the x axis
			for(int x = 0; x < w; x++){
				int xa = x + (int) this.x;
				
				//get the current pixel
				int pixel = pixels[x + y * w];
				
				if(pixel == 0xffffae00){
					
					//get the id for the given exit
					int id = iterator;
										
					//generate a new exit
					Exit exit = new Exit(xa, ya, id, workingExits[iterator], this);
					exits[iterator] = exit;
					
					//increment the iterator
					iterator++;
				}
			}
		}	
	}
	
	//interprets the graphic to place the required passengers.
	//NOTE: Passengers are placed based of a 0xff0000ff pixel located at their given seat. See plane graphic for details
	private void loadPassengers(){
		
		//get graphic data
		int w = graphic.getWidth();
		int h = graphic.getHeight();
		int[] pixels = graphic.getGraphic();
		
		//A Vector2i buffer used to initially hold the passengers found from the graphic
		List<Vector2i> passBuffer = new ArrayList<Vector2i>();
	
		//NOTE: When a passenger is found, it is initially added to a buffer
		//loop through the y contents of the screen
		for(int y = 0; y < h; y++){
			int ya = y + (int) this.y;
			
			//loop through the x contents of the screen
			for(int x = 0; x < w; x++){
				int xa = x + (int) this.x;
				
				//get the current pixel
				int pixel = pixels[x + y * w];
				
				//if the pixel is equal to the passenger color
				if(pixel == 0xff0000ff){
					
					//add the new found passenger to the passengerBuffer
					passBuffer.add(new Vector2i(xa, ya));
				}
			}
		}
		
		//if the passengerCount is bellow the maximum, some of the passengers must be randomly removed
		if(passengerCount != 132){
			
			//calculate the number of passengers that must be removed
			int diff = 132 - passengerCount;
			
			//loop through the difference
			for(int i = 0; i < diff; i++){
				
				//get the size of the buffer
				int size = passBuffer.size();
				
				//generate a random index and remove said index from the passBuffer
				int remove = rand.nextInt((size - 1));
				passBuffer.remove(remove);
			}
			
		}
		
		//loop the passengerCount
		for(int i = 0; i < passengerCount; i++){
			
			//get the vector at the given index
			Vector2i vect = passBuffer.get(i);
			
			//get the x and y components of the vector
			int x = vect.getX();
			int y = vect.getY();
			
			//set the survive attribute to initially true
			boolean survive = true;
			
			//if the simulation must take into avount survival statistics, continue
			if(accountForSurvival && survivalChance != 100){
				
				//generate a random number between 0 and 100 s
				int random = rand.nextInt(100)+1;
				
				//if the the random number is greater than the survival chance, set survive to false
				if(random > survivalChance) survive = false;
			}
			
			//create a new passenger in the passengers array with the found data
			passengers[i] = new Passenger(x, y, massIndex[i], survive, this, i);
			
		}
		
	
				
	}

	//Generates a list of pixels found on the plane graphic that are collidable
	private void generateCollision(){
		
		//get the width and height of the plane graphic
		int w = graphic.getWidth();
		int h = graphic.getHeight();
		
		//get the pixel array from the graphic
		int[] pixels = graphic.getGraphic();
		
		//loop through the y component of the graphic
		for(int y = 0; y < h; y++){
			
			//calculate the absolute position of the pixel
			int ya = y + (int) this.y;
			
			//loop through the x component of the graphic
			for(int x = 0; x < w; x++){
				
				//Absolute position
				int xa = x + (int) this.x;
				
				//get the given pixel from the graphic
				int pixel = pixels[x + y * w];
				
				//if the pixel is
				if(pixel == COLLIDABLECODE){
					
					//create a vector with the absolute position data and add said vector to the collidable pixels list
					Vector2i pos = new Vector2i(xa, ya);
					collidablePixels.add(pos);
				}
			}
		}
	}
	
	//the update function
	public void update(){
		
		//Loop through all passengers
		for(Passenger p : passengers){
			
			//if the passenger is not removed, then update
			if(!p.isRemoved()) p.update();
		}

		//Update each exit
		for(Exit e : exits){
			e.update();
		}
		
		//if all passengers have escpaed
		if(escaped >= passengerCount){
			
			//print the final stats
			printStats();
			
			//stop the simulation
			main.stop();
		}
	}
	
	//print the final stats 
	public void printStats(){
		
		int perished = passengerCount - escaped;
		
		System.out.println("Passengers survived: " + escaped);
		System.out.println("Passengers perished: " + perished);
		System.out.println("Time elapsed: " + Main.simulationTime);
	}
	
	//the render function
	public void render(Screen screen){
		
		//render the plane graphic to the screen
		screen.renderGraphic(graphic, (int) x, (int) y);
		
		//loop the passenger list
		for(Passenger p : passengers){
			
			//if a given passenger is not removed, continue
			if(!p.isRemoved()){
				p.render(screen);
				
				//render the path if specified
				if(renderPath) p.renderPath(screen);
			}
		}
	}
	
	//return communcation boolean
	public boolean communicate(){
		return communicate;
	}
	
	//set the root tileMap (Accepts: TileMapGenerator instance)
	public void setRoot(TileMapGenerator tileMap){
		rootTilemap = tileMap;
	}
	
	//gets the root tilemap
	public TileMapGenerator getRoot(){
		return rootTilemap;
	}
	
	//returns the gForce on impact
	public double getGForce(){
		return gForce;
	}
	
	
	//returns the passenger list
	public Passenger[] getPassengers(){
		return passengers;
	}
	
	//returns the emergency exit list
	public Exit[] getExits(){		
		return exits;
	}
	
}
