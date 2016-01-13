package com.main.graphics.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * CollisionAngleGraphic.java handles the plane angle graphic representation
 * Directly Evoked from: ControlPanel.java
 */

public class CollisionAngleGraphic{
	
	//create new JFrame to hold panel
	private JFrame window = new JFrame();
	
	//Instantiates a new JPanel graphic
	private AngleGraphic graphic = new AngleGraphic();

	//holds the angle of crash (in radians)
	private double angle = 0.0;
	
	//constructor
	public CollisionAngleGraphic(){
		
		//configure the JFrame
		window.setTitle("Col Angle");
		window.setResizable(false);
		window.add(graphic);
		window.pack();		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	//sets the angle on the graphic representation.
	//NOTE: Val is the inputed angle, in degrees
	public void setAngle(int val){
		
		//convert the angle into radians
		this.angle = Math.toRadians(val);
		
		//get the hypotenuse of the graphic 
		double hypot = (double) graphic.getLength();
		
		//calculate the opposite and adjacent sides
		double op = Math.sin(angle) * hypot;
		double adj = Math.cos(angle) * hypot;
		
		//determine the x and y points for the line to draw
		int x2 = (int) adj;
		int y2 = (int) (150 - op);
		
		//set the line with teh calculated data
		graphic.setLine(x2, y2, val);
	}
	
	/**
	 * Handles the graphic jpanel applied to the jframe
	 */
	public class AngleGraphic extends JPanel {
		private static final long serialVersionUID = 1L;
		
		//the length of the line created
		private int length = 150;
		
		//the vertices that connect to the origin (0, 150)
		private int x2 = 0, y2 = 0;
		
		//the given angle (in degrees)
		private int angle;
		
		//the graphics object used for drawing to the screen
		Graphics g;
		
		//set the preferred dimensions (150x150)
        public Dimension getPreferredSize() {
            return new Dimension(150, 150);
        }
        
        //set the pain component
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            this.g = g;
            draw();
        }
        
        //draws the information to the jpannel
        private void draw(){
        	
        	//set the color to black and draw the line
        	g.setColor(Color.BLACK);
        	g.drawLine(0, 150, x2, y2);
        	
        	//set the color to grey for the angle integer
	    	g.setColor(Color.LIGHT_GRAY);
	    	 
	    	//determine the render x and y, dependent on the angle
	    	int renderX = (angle < 25) ? 20 : 40;
	    	int renderY = (angle < 25) ? 130 : 140;
	    	
	    	//draw the angle to the screen
	    	g.drawString(Integer.toString(angle), renderX, renderY);
	    	 
	    	//change back to black and draw a angle circle
	    	g.setColor(Color.BLACK);
	    	g.drawOval(-15, 140, 30, 30);
        }
        
        //draw the line to the screen
        private void setLine(int x2, int y2, int angle){
        	this.x2 = x2;
        	this.y2 = y2;
        	this.angle = angle;
        	repaint();
        }
       
        //get the length of the line
        public int getLength(){
        	return length;
        }
    }

}
