package com.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract data type Queue
 **/
public class Queue {

	//a list of items
	private List<Object> items;
	
	//constructor
	public Queue(){
		
		//create new arraylist
		items = new ArrayList<Object>();
	}

	//returns if the queue is empty
	public boolean isEmpty(){
		return items.size() == 0;
	}
	
	//add items to the queue
	public void enqueue(Object item){
		items.add(item);
	}
	
	//remove items from the queue
	public Object dequeue(){
		return items.remove(0);
	}
	
}
