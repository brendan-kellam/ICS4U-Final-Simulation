package com.main.graphics;

/**
 * Handles all graphical models rendered to the screen
 */

public class Graphic {
	
	//width and height of the graphic
	private int width, height;
	
	//the pixel buffer related to the graphic
	private int[] graphic;
	
	public Graphic(int width, int height, int[] graphic){
		
		//set the with, height and pixel buffer
		this.width = width;
		this.height = height;
		this.graphic = graphic;
	}
	
	//return the width
	public int getWidth(){
		return width;
	}
	
	//return the height
	public int getHeight(){
		return height;
	}
	
	//return the pixel buffer
	public int[] getGraphic(){
		return graphic;
	}
		
}
