package com.main;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;
import com.main.entity.Plane;
import com.main.graphics.Screen;
import com.main.graphics.gui.ControlPanel;

/**
 * The Main class to the simulation
 * All direct classes are instantiated from this super class 
 * @author Brendan kellam
 */

//Main class
public class Main extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	//set the width and height of the frame
	private static int width = 1150;
	private static int height = 720;
	
	//the scale of the frame
	private static int scale = 1;
	
	//holds if the program is running or not
	private static boolean running = false;
	
	//the main thread for the program
	private Thread thread; 
	
	//the main simulation frame
	private JFrame simulationFrame;
	
	//the time since the simulation began
	public static double simulationTime = 0.0;
	
	//Instantiate pixel buffers
	private static BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //handles each individual pixel
	private static int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData(); // gives a rectangular array of pixels that can be written to
	
	//the screen instance, used to draw to the frame (See: Screen.java)
	private Screen screen;
	
	//the main Plane instance. (See: Plane.java)
	private Plane plane;
	
	//the rate at which the simulation is run
	//NOTE: This value is directly manipulated by the controlpanel
	private int simulationSpeed = 60;
	
	//the time at which survival is viable. When the time increases beyond this limit, the simulation will end
	//NOTE: Directly manipulated via controlpanel
	private int survivalTime;
	
	//constructor
	public Main(){
		
		//create a new dimension
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size); //canvas method, sets the size of the canvas
		
		//create new screen instance
		screen = new Screen(width, height);
		simulationFrame = new JFrame();
		defineFrameProperties();
	
		//instantiate the control panel instance, passing in this instance of Main and the frame
		new ControlPanel(this, simulationFrame);
		
		//create a new plane object
		plane = new Plane(this);
	}
	
	//Runs the simulation, accepting all required initial values for the simulation
	//NOTE: This function is exclusively called from ControlPanel.java
	public void runSimulation(int passengerCount, double survivalChance, boolean accountForSurvival, 
			double gForce, int[] massIndex, boolean[] workingExits, boolean communication, boolean renderPath, int simulationSpeed, int survivalTime){
		
		//re-set the location of the frame
		simulationFrame.setLocationRelativeTo(null);
		
		//set the simulation speed
		this.simulationSpeed = simulationSpeed;
		
		//set the survival time
		this.survivalTime = survivalTime;
		
		//give the initial data to the Plane.java class
		plane.generateSimulation(passengerCount, survivalChance, accountForSurvival, gForce, massIndex, workingExits, communication, renderPath);
		
		//start the simulation
		start();	
	}
	
	//starts the main thread
	public synchronized void start(){ //(synchronized is used to prevent overlaps with threads)
		running = true;
		thread = new Thread(this, "Display"); //this thread will be attached to the main instance
		thread.start();
	}
	
	 //stops the simulation
	public synchronized void stop(){
		simulationFrame.setVisible(false);
		simulationFrame.dispose();
		System.exit(0);
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	//defines the JFrame properties
	private void defineFrameProperties(){
		simulationFrame.setResizable(false);
		simulationFrame.setTitle("Simulation");
		simulationFrame.add(this); //works because Main extends canvas
		simulationFrame.pack(); //sets size according to component
		simulationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulationFrame.setLocationRelativeTo(null);
		simulationFrame.setLocation(0, simulationFrame.getY());
		simulationFrame.setVisible(true);
	}
	
	//Main thread run object
	public void run() {
		
		//the lastTime in nano seconds and timer in current millis
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / simulationSpeed;
		double delta = 0;
		
		//set the frames and updates
		int frames = 0;
		int updates = 0;
		requestFocus();
	
		while(running){ //loop
			
			//current time
			long now = System.nanoTime();
			delta += (now-lastTime) / ns;
			lastTime = now;
			while (delta>=1){
				update(); //restricted to the simulationSpeed	
				updates++;
				delta--;
			}
			render(); // unrestricted
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				simulationFrame.setTitle("Simulation" + "  | " + updates + " ups, " + frames + " fps" + "  | T+ " + simulationTime);
				updates = 0;
				simulationTime += (simulationSpeed/60.0);
				
				//if the time is greater than the survivable time, then stop the program
				if(simulationTime > survivalTime){
					//display the stats
					plane.printStats();
					stop();
				}
				
				frames = 0;
			}
		}
		stop();
	}
	
	//update function
	public void update(){
		plane.update();
	}
	
	///render
	public void render(){
		BufferStrategy bs = getBufferStrategy(); //canvas comes with a buffer strategy
		if(bs == null){
			createBufferStrategy(3); //triple buffering
			return;
		}
		screen.clear();
		
		//render the plane
		plane.render(screen);
		
		//get the pixels from the screen
		for(int i = 0; i <pixels.length; i++){
			pixels[i] = screen.getGraphic()[i];
		}
		
		
		Graphics g = bs.getDrawGraphics(); //creating a link between graphics and the buffer
		g.drawImage(image, 0 , 0, getWidth(), getHeight(), null); //this is where all graphics will be displayed
		g.dispose(); //after every frame has been rendered, they have to be disposed
		bs.show(); //brings the next buffer available
	}
	
	//start of program
	public static void main(String[] args){
		System.out.println("running...");
		
		//create a new Main instance
		new Main();
	}
		
}
