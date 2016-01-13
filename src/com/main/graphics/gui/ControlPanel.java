package com.main.graphics.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import com.main.Main;
import com.main.entity.Passenger;
import com.main.entity.Plane;
import com.main.util.Frame;

/**
 * Handles all functionalities related to the side control panel
 * Extends: Frame.java; Location: com.main.util
 * See formulae in Plane.java
 */

public class ControlPanel extends Frame{

	
	//Frame variables
	private JFrame window = new JFrame();
	private Dimension size = new Dimension(200, 780);

	//main instance
	private Main main;
	
	//Instantiate a new collision angle graphic
	private CollisionAngleGraphic collisionAngleGraphic;
	
	//Sliders:
	private JSlider collisionAngle;
	private JSlider simulationSpeed;
	
	//Buttons:
	private JButton collisionAngleExpand;
	private JButton runSimulation;
	
	
	//text fields
	JTextField inputDecelTime;
	JTextField inputDecel;
	JTextField inputImpactForce;
	JTextField inputVelocity;
	JTextField inputGForce;	
	JTextField inputPassengerCount;
	JTextField inputPassMassMin;
	JTextField inputPassMassMax;
	JTextField inputSurvival;
	JTextField inputSurvivalTime;
	
	
	//checkboxes
	JCheckBox accountForSurvival;
	JCheckBox renderPath;
	JCheckBox communication;
	JCheckBox door_0;
	JCheckBox door_1;
	JCheckBox door_2;
	JCheckBox door_3;
	JCheckBox door_4;
	JCheckBox door_5;
	JCheckBox door_6;
	JCheckBox door_7;
	
	//-----variables-----
	
	//Holds the angle at which the plane is going to crash at 
	private double angle;
	
	//The velocity of the airplane on impack
	private double velocity = 246;
	
	//time required for the plane to decelerate completely
	private double decelerationTime;
	
	//the deceleration of the plane on impact
	private double deceleration;
	
	//the gForces applied on impact
	private double gForce;
	
	//the aircraft mass
	private double planeMass = Plane.PLANEMASS;
	
	//the impact force in (N) on impact
	private double impactForce;
	
	//the percentage chance of a passenger surviving
	private double survivalChance;
	
	//a boolean list to store if a exit is set to functioning or not
	private boolean[] workingExits = new boolean[Plane.EXITCOUNT];
	
	//the mass index of all passengers on the aircraft
	private int[] massIndex;
	
	//the passenger count
	private int passengerCount = 0;
	
	//the survival time (initially set to 100)
	private int survivalTime = 100;
		
	//random instance
	Random rand = new Random();
	
	//construtor. Accepts: instance of Main.java, instance of the simulation JFrame
	public ControlPanel(Main main, JFrame simulationFrame){
		
		//set the main instance
		this.main = main;
		
		//--configure the JFrame--
		window.setTitle("Controll");
		window.setSize(size);
		
		//calculates the frame position
		int framex = simulationFrame.getWidth()+simulationFrame.getX();
		int framey = simulationFrame.getY();
		
		window.setLocation(framex, framey);
		window.setResizable(false);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.setLayout(null);
		initWindow(window);
		
		//initialize the components
		initComponents();
		window.getContentPane().setBackground(Color.LIGHT_GRAY);
		
		//initialize the action handlers
		action();
	}
		
	//handles initializing the components to the JFrame
	//NOTE: Commenting is thin on this section due to the codes repetitive nature and simplicity
	private void initComponents(){
		
		label(5, 15, 200, 16, "Collision angle");
		
		//add the expand button
		collisionAngleExpand = button(120, 12, 40, 25, "+");
		window.add(collisionAngleExpand);
		
		//add the collision angle slider
		collisionAngle = slider(5, 30, 180, 50, JSlider.HORIZONTAL, 0, 90, 45);
		collisionAngle.setMinorTickSpacing(5);
	    collisionAngle.setMajorTickSpacing(15);
	    collisionAngle.setPaintTicks(true);
	    collisionAngle.setPaintLabels(true);	    
		window.add(collisionAngle);
		
		
		//deceleration time:
		label(5, 100, 200, 16, "Decel time (s): ");
		
		inputDecelTime = txtfield(130, 94, 50, 30);
		inputDecelTime.setEnabled(false);
		window.add(inputDecelTime);
		
		
		//deceleration:
		label(5, 130, 200, 16, "Decel (m/s^2): ");
		
		inputDecel = txtfield(130, 124, 60, 30);
		inputDecel.setEnabled(false);
		window.add(inputDecel);
		

		//impact force:
		label(5, 160, 200, 16, "Impact F (kN): ");
		
		inputImpactForce = txtfield(130, 154, 70, 30);
		inputImpactForce.setEnabled(false);
		window.add(inputImpactForce);
		
		
		//velocity:
		label(5, 190, 200, 16, "Velocity (m/s):");
		
		inputVelocity = txtfield(130, 184, 60, 30);
		inputVelocity.setText(Double.toString(velocity));
		window.add(inputVelocity);
		
		//GForce: 
		label(5, 220, 200, 16, "G Force: ");
		
		inputGForce = txtfield(130, 214, 60, 30);
		inputGForce.setEnabled(false);
		window.add(inputGForce);
		
		//---passenger information----
		label(5, 265, 200, 16, "--Passenger Information--");
		
		//# of passengers
		label(5, 295, 200, 16, "# of Passengers:");
		inputPassengerCount = txtfield(130, 289, 50, 30);
		inputPassengerCount.setText("132");
		window.add(inputPassengerCount);
		
		
		//min and max mass:
		label(5, 325, 100, 16, "Mass min (kg):");
		inputPassMassMin = txtfield(130, 319, 40, 30);
		inputPassMassMin.setText("36");
		window.add(inputPassMassMin);
		
		label(5, 355, 100, 16, "Mass max (kg): ");
		inputPassMassMax = txtfield(130, 349, 40, 30);
		inputPassMassMax.setText("100");
		window.add(inputPassMassMax);
		
		
		//survival %:
		label(5, 385, 150, 16, "Survival percent: ");
		
		inputSurvival = txtfield(130, 379, 60, 30);
		inputSurvival.setEnabled(false);
		window.add(inputSurvival);
		
		
		//include fatalities:
		label(5, 415, 140, 16, "Include fatalities:");
		accountForSurvival = checkBox(130, 411, 23, 23);
		accountForSurvival.setSelected(true);
		window.add(accountForSurvival);
		
		
		//render path:
		label(5, 445, 140, 16, "Render path: ");
		renderPath = checkBox(130, 441, 23, 23);
		window.add(renderPath);
			
		//passenger communication:
		label(5, 475, 140, 16, "Passenger Com:");
		communication = checkBox(130, 471, 23, 23);
		communication.setSelected(true);
		window.add(communication);
		
		//survival time:
		label(5, 505, 140, 16, "Survival Time: (s)");
		inputSurvivalTime = txtfield(130, 499, 40, 30);
		inputSurvivalTime.setText("100");
		window.add(inputSurvivalTime);
		
		//---Exit Information---
		label(25, 555, 150, 16, "--Exit Information--");
		
		label(5, 580, 150, 16, "Functioning Exits: ");
	
		
		//Add all doors to the frame
		door_0 = checkBox(5, 595, 23, 23);
		door_0.setSelected(true);
		window.add(door_0);
		
		door_1 = checkBox(70, 595, 23, 23);
		door_1.setSelected(true);
		window.add(door_1);

		door_2 = checkBox(100, 595, 23, 23);
		door_2.setSelected(true);
		window.add(door_2);

		door_3 = checkBox(165, 595, 23, 23);
		door_3.setSelected(true);
		window.add(door_3);
		
		door_4 = checkBox(5, 620, 23, 23);
		door_4.setSelected(true);
		window.add(door_4);
		
		door_5 = checkBox(70, 620, 23, 23);
		door_5.setSelected(true);
		window.add(door_5);
		
		door_6 = checkBox(100, 620, 23, 23);
		door_6.setSelected(true);
		window.add(door_6);
		
		door_7 = checkBox(165, 620, 23, 23);
		door_7.setSelected(true);
		window.add(door_7);
		
		
		//simulation settings
		label(20, 640, 240, 16, "--Simulation Settings--");
		
		label(5, 660, 200, 16, "Updates /s:");
		
		//simulation speed slider:
		simulationSpeed = slider(5, 680, 180, 50, JSlider.HORIZONTAL, 0, 120, 60);
		simulationSpeed.setMinorTickSpacing(10);
		simulationSpeed.setMajorTickSpacing(20);
		simulationSpeed.setPaintTicks(true);
		simulationSpeed.setPaintLabels(true);	    
		window.add(simulationSpeed);
		
		//runbutton
		runSimulation = button(80, 730, 50, 25, "Run");
		window.add(runSimulation);
		
		//after adding all components, do a initial calculation
		calculate();
	}
	
	//Congregates all the data gathered from the input frame, making the required calculations
	//returns true if all calculations are successfully complete
	private boolean calculate(){
		
		//get the angle of impact
		angle = collisionAngle.getValue();
		
		//create a new decimal formatter
		DecimalFormat f = new DecimalFormat("##.00");
		
		//velocity input
		try{
			
			//get the inputed string
			String input = inputVelocity.getText();
			
			//if said string is empty, ignore
			if(!input.equals("")){
				
				//gets the value in double format
				double temp = Double.parseDouble(inputVelocity.getText());
				
				//if it is less than zero, throw a new exception
				if(temp < 0) throw new NumberFormatException();
				velocity = temp;
			}
			
		}catch(NumberFormatException e){
			dialogBox("Invalid velocity value");
			return false;
		}
		
		
		//set passenger count to zero
		passengerCount = 0;
		try{
			
			//get the input
			String input = inputPassengerCount.getText();
			
			//ignore if null
			if(!input.equals("")){
				
				//parse to integer
				passengerCount = Integer.parseInt(inputPassengerCount.getText());
				
				//if out of bounds, throw a new exception
				if(passengerCount < 0 || passengerCount > 132){
					throw new NumberFormatException();
				}
			
			}
			
		}catch(NumberFormatException e){
			dialogBox("Invalid range for passenger count");
			return false;
		}
		
		//set the mass minimums and maximums
		int massMin = Passenger.MASSMIN;
		int massMax = Passenger.MASSMAX;
		
		try{
			
			//get the inputed data
			String inputMin = inputPassMassMin.getText();
			String inputMax = inputPassMassMax.getText();
			
			//ignore if null
			if(!inputMin.equals("") && !inputMax.equals("")){
				
				//parse to intger
				massMin = Integer.parseInt(inputMin);
				massMax = Integer.parseInt(inputMax);
				
				//if the masses are out of range, throw a new exception
				if(massMin > massMax) throw new NumberFormatException();
				
				if(massMin <= 0 || massMax <= 0) throw new NumberFormatException();
			}
			
		}catch(NumberFormatException e){
			dialogBox("Invalid range for passenger mass");
			return false;
		}
		
		//survival time:
		try{
			
			//get the input
			String input = inputSurvivalTime.getText();
			
			//if null ignore
			if(!input.equals("")){
				
				//parse to integer
				survivalTime = Integer.parseInt(input);
				
				//if out of range, throw new exception
				if(survivalTime <= 0){
					throw new NumberFormatException();
				}
			}
			
		}catch(NumberFormatException e){
			dialogBox("Invalid range for survival time");
			return false;
		}
		
		
		//create a temporary total mass
		int totalMass = 0;
		
		//Instantiate the massIndex
		massIndex = new int[passengerCount];
		
		//loop the passenger count
		for(int i = 0; i < passengerCount; i++){
			
			//randomly generate a new mass in bounds
			int mass = rand.nextInt((massMax - massMin) + 1) + massMin;
			
			//set the mass in the index and add on to the total
			massIndex[i] = mass;
			totalMass+=mass;
		}
		
		//calculate the deceleration time
		decelerationTime = calculateDecelTime(angle);		
		inputDecelTime.setText(f.format(decelerationTime)); 
		
		//calculate the deceleration
		deceleration = velocity / decelerationTime;
		inputDecel.setText(f.format(deceleration));
		
		//calculate the GForce
		gForce = deceleration/9.81;
		inputGForce.setText(f.format(gForce));
		
		//calculate the total mass and impactForce
		totalMass+=planeMass;
		impactForce = totalMass * deceleration;
		inputImpactForce.setText(f.format(impactForce/1000));
		
		//if the gForce is less than the deadly gForce, then continue
		if(gForce <= Passenger.DEATHLYGFORCE){
			
			//calculate survival chance
			survivalChance = (1.0 - (gForce/Passenger.DEATHLYGFORCE))*100;
		}else{
			
			//if the gForce is higher, then set the survival rate to 0
			survivalChance = 0.0;
		}
		
		inputSurvival.setText(f.format(survivalChance) + "%");
		
		//get the door selections
		workingExits[0] = door_0.isSelected();
		workingExits[1] = door_1.isSelected();
		workingExits[2] = door_2.isSelected();
		workingExits[3] = door_3.isSelected();
		workingExits[4] = door_4.isSelected();
		workingExits[5] = door_5.isSelected();
		workingExits[6] = door_6.isSelected();
		workingExits[7] = door_7.isSelected();
		
		//return true if all calculations are complete
		return true;
	}

	//calculate the deceleration time.
	//Accepts: The angle of impact
	private double calculateDecelTime(double angle){
		double decelerationTime = (-1 * Math.pow(2, (0.059*angle)) + 40 )/6.5;		
		return decelerationTime;
	}
	
	//Runs the simulation
	//NOTE: This function is only called when the run button is pressed
	private void runSimulation(){
		
		//if the passengerCount is less than zero, throw a error
		if(passengerCount <= 0){
			dialogBox("There must be atleast 1 passenger onboard");
			return;
		}
		
		//get the speed
		int speed = simulationSpeed.getValue();
		
		//if the speed is less than zero, throw error
		if(speed <= 0){
			dialogBox("The update per second must be greater than 0");
			return;
		}
				
		//close the Jframe
		window.setVisible(false);
		window.dispose();
		
		//call-back on Main.java with the initial data
		main.runSimulation(passengerCount, survivalChance, accountForSurvival.isSelected(), 
				gForce, massIndex, workingExits, communication.isSelected(), renderPath.isSelected(), speed, survivalTime);
	}
	
	//the action listener function
	private void action(){ 
		
		//collision angle expand button listener
		collisionAngleExpand.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) {
						collisionAngleGraphic = new CollisionAngleGraphic();
					}
				}
		);
		
		//Collision angle slider listener
		collisionAngle.addChangeListener(
				new javax.swing.event.ChangeListener(){
					public void stateChanged(ChangeEvent e) {
						if(collisionAngleGraphic != null){
							collisionAngleGraphic.setAngle(collisionAngle.getValue());
						} 
						calculate();
					}
				}
		);
		
		//run simulation button listener 
		runSimulation.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent event) {
						
						//if the calculation passes, then run the simulation
						if(calculate())
							runSimulation();
					}
				}
		);
				
		
		//All JTextFields field listener
		inputVelocity.addActionListener(new FieldListener());
		inputPassengerCount.addActionListener(new FieldListener());
		inputPassMassMin.addActionListener(new FieldListener());
		inputPassMassMax.addActionListener(new FieldListener());
		inputSurvivalTime.addActionListener(new FieldListener());
	}
	
	//creates a new JTexField action listener
	class FieldListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			calculate();
		}
	}

}

