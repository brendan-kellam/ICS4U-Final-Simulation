package com.main.entity;

import com.main.graphics.Screen;

/**
 * Entity.java is a abstracted layer for all entitys to inherit from
 */
public class Entity {

	//render location reletive to (0, 0)
	public double x, y; 
	
	//if the entity is still being updated/rendered
	protected boolean removed = false;

	
	//allows for a entity to be updated at 60ups
	public void update(){
		
	}
	
	//renders a entity to the screen
	public void render(Screen screen){
		
	}
	
	//removes a entity
	public void remove(){
		removed = true;
	}
	
	//returns if a entity has been removed
	public boolean isRemoved(){
		return removed;
	}
	
}
