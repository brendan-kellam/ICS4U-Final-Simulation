package com.main.graphics;

/**
 * Handles all interactions between the the graphical screen.
 */

public class Screen {

	//the width and height of the screen
	private int width, height;
	
	//the pixel buffer rendered on the screen
	private int[] pixels;
	
	public static final int NORENDERCOLOR = 0xff3000ff;
	
	//constructor
	public Screen(int width, int height){
		//set width and height
		this.width = width;
		this.height = height;
		
		//create pixel buffer
		pixels = new int[width * height];
		
	}
	
	//renders a graphic to the screen
	//Accepts: A graphic, absolute x and y coordinate point
	public void renderGraphic(Graphic graphic, int xpos, int ypos){
		
		//get the width and height of the graphic
		int w = graphic.getWidth(); 
		int h = graphic.getHeight();
		
		//get relative y position
		for(int y = 0; y < h; y++){
			int ya = y + ypos;
			
			//get relative x position
			for(int x = 0; x < w; x++){
				int xa = x + xpos;
				
				//prevents screen wrapping and array-index out of bound errors
				if (xa < -w || xa >= width || ya < 0 || ya >= height) break;
				if(xa < 0) xa = 0; //prevents screen overlapping
				int pixel = graphic.getGraphic()[x + y * w];
				
				//if the pixel is a non render, continue
				if(pixel == NORENDERCOLOR) continue;
				
				//render the data to the screen (using relative x and y positions)
				pixels[xa + ya * width] = pixel;
			}
		}
		
	}
	
	//clear the screen
	public void clear(){
		for(int i = 0; i< pixels.length; i++){
			pixels[i] = 0xffffff;
		}
	}

	//get the pixel buffer
	public int[] getGraphic(){
		return pixels;
	}
	
	//return the width
	public int getWidth(){
		return width;
	}
	
	//return the height
	public int getHeight(){
		return height;
	}
		
}
