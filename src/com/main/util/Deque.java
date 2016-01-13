package com.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * The Deque ADT.
 * Implementation: Front [0, 2, 3, 4] Rear
 */

public class Deque {
	
	//list of items
	private List<Object> items;

	//constructor
	public Deque(){
		
		//create new items arraylist
		items = new ArrayList<Object>();
	}
	
	//returns if empty
	public boolean isEmpty(){
		return items.isEmpty();
	}
	
	//view the item in the front of the deque
	public Object peekFront(){
		return items.get(0);
	}
	
	//view the item in the back of the deque
	public Object peekRear(){
		return items.get(items.size()-1);
	}
	
	//add item to the front of the deque
	public void addFront(Object item){
		items.add(0, item);
	}
	
	//add item to the rear of the deque
	public void addRear(Object item){
		items.add(item);
	}
	
	//remove item from the front of the deque
	public Object removeFront(){
		return items.remove(0);
	}
	
	//remove from the rear
	public Object removeRear(){
		return items.remove(items.size()-1);
	}
	
	//checks if item is in the deque
	public boolean contains(Object item){
		return items.contains(item);
	}
	
	//clears the deque
	public void clear(){
		items.clear();
	}
	
	//returns the size
	public int size(){
		return items.size();
	}
	

}
