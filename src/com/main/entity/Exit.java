package com.main.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.main.Main;
import com.main.graphics.Screen;
import com.main.util.Deque;
import com.main.util.Vector2i;

/**
 * Exit.java represents the emergency exists located on the plane. 
 * Note: Exit count = 8 
 * 
 * The door functions by setting up a set radius (that increases in size as passengers enter the radius). <br>
 * When a passenger enters, they will be added to a double ended queue. <br>
 * The first passenger to enter will trigger the opening time, a randomly generated period before the passengers can either:
 * <ol>
 * 	<li> Exit the aircraft with a <b>functioning door</b> <br>
 * 	(In this event, each passenger will be removed from the front of the deque I.E the passenger who last entered the queue will be the last to exit)<br><br>
 * 
 * 	<li> React and re-path find with a <b>non-functioning door</b><br>
 * 	(In this event, each passenger will be removed from the back of the deque I.E the passenger who entered first will leave the broken exit last)<br>
 * </ol>
 * 
 * 
 */

public class Exit extends Entity{

	//the vector position of the door
	private Vector2i pos;
	
	//a boolean that stores if the door is working or not
	private boolean functioning;
	
	//a boolean that stores if the time to open has expired
	private boolean timeEnded = false;
	
	//the door id (or index in the main exits arraylist) [see: Plane.java]
	private int id;

	//instance of the plane class
	private Plane plane;
	
	//the inital radius of the plane door
	private double radius = 5.0;
	
	//the time required for the door to be opened
	private int timeToOpen;
	
	//the minimum and maximum time extremities for the opening of the door
	private int tMin = 5, tMax = 15;
	
	//the current passenger interacting with the door
	private Passenger interactingPassenger;
	
	//a double-ended queue with all passengers withing the radius of the door
	private Deque passengers = new Deque();
	
	private List<Passenger> visited = new ArrayList<Passenger>();
	
	//time 1 and time two
	private double t1 = 0.0, t2 = 0.0;	

	//a new random object
	private Random rand = new Random();
	
	//constructor
	public Exit(int x, int y, int id, boolean functioning, Plane plane){
		
		//set the incoming varibles
		this.id = id;
		this.functioning = functioning;
		this.plane = plane;
		
		
		//randomly generate a given time for the door to be opened
		timeToOpen = rand.nextInt((tMax - tMin) + 1) + tMin;
				
		//set the position of the door
		pos = new Vector2i(x, y);
	}

	//update function
	public void update(){
	
		//test if any passengers have entered the exits radius
		testForPassengers();
		
		//if the time difference
		if(t2 - t1 > timeToOpen){
			
			//timeEnded becomes true when the timer ends
			timeEnded = true;
			
			//sets the radius back to 5.0
			radius = 5.0;
			
			
			//if there are no passengers in the deque, return out
			if(passengers.isEmpty())return;
			
			//set a new passenger object
			Passenger p;				
			
			//if the door is functioning 
			if(functioning){
				
				//remove the passenger in the FRONT of the Deque
				p = (Passenger) passengers.removeFront();
				
				//handle a functioning exit
				p.handleFunctioningExit();
			
			//handle a non-functioning door
			}else{
				
				//remove the passenger in the REAR of the Deque
				p = (Passenger) passengers.removeRear();
				
				//handle a broken door
				p.handleBrokenExit();
			}
						
		}			
		
		//if there is a interacting passenger, reset the t2 every update
		if(interactingPassenger != null) t2 = Main.simulationTime;
	}
	
	//test if passengers are in the radius of the exit
	private void testForPassengers(){
		
		//loop through each passengers
		for(Passenger p : plane.getPassengers()){
			
			//if the passenger is within range of the radius, continue
			if(p.getPos().getDistance(pos) <= radius){
								
				//if a passenger enters when the door has been tested and is not functioning, 
				//they will imediatly be notified it is broken
				if(timeEnded){
					
					//if the passenger has yet to visit
					if(!visited.contains(p)){
						
						//add the passenger to the rear of the queue
						passengers.addRear(p);
						
						//set them as visited
						visited.add(p);
					}
					
					//will always continue out if the time has ended
					//NOTE: This is used to prevent the code below from running and adding two occurences of the same passenger
					continue;
				}
				
				//if the passenger is not already within the cahched passengers arraylist, continue
				if(!passengers.contains((Object) p)){
					
					//add the passenger to visited and run addPassenger
					visited.add(p);
					addPassenger(p);
					
				}
			}
		}
		
	}
	
	//add the passenger to the queue
	private void addPassenger(Passenger p){
		
		//if the passengers arraylist is empty
		if(passengers.isEmpty()){
			
			//set the currently interacting passenger to the current passenger
			interactingPassenger = p;
			
			//set the time 1 to the current time, effectively starting the count-down timer
			t1 = Main.simulationTime;
		}
		
		//add the passenger to the rear of the deque (end of the line)
		passengers.addRear(p);
		
		//notify passenger they have entered the exit queue.
		p.enterExitQueue();
		
		//set the extremities of the radius
		int min = 5;
		int max = (int) radius + passengers.size()*4;
		
		//expand the radius for each passenger added
		radius = rand.nextInt((max - min) + 1) + min;
	}
	
	//no need to render Exits 
	public void render(Screen screen){}
	
	//get the position of the Exit
	public Vector2i getPosition(){
		return pos;
	}
	
	//get the ID of the given exit
	public int getId(){
		return id;
	}
	
}
