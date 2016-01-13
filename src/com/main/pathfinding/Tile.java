package com.main.pathfinding;

import java.util.Random;
import com.main.graphics.Graphic;
import com.main.graphics.Screen;


public class Tile {
	
	public static int size = 4;
	private int x, y;
	public  boolean collidable = false;
	private Graphic g;
	
	private int[] colours = {0xffe61e1e, 0xff1eff00, 0xff9856f3, 0xff19dd19, 0xff29330b};
	
	public Tile(int x, int y){
		this.x = x;
		this.y = y;
		generateGraphic();
	}
	
	private void generateGraphic(){
		Random rand = new Random();
		int col = colours[rand.nextInt(5)];
		
		int[] pixels = new int[size*size];
		for(int y = 0; y < size; y++){
			for(int x = 0; x < size; x++){
				pixels[x + y * size] = col;
			}
		}
		g = new Graphic(size, size, pixels);
	}

	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public boolean isCollidable(){
		return collidable;
	}
	
	public void render(Screen screen){
		if(!collidable) screen.renderGraphic(g, x, y);
	}
	
}
