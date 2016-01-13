package com.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract data type of a Stack
 */

public class Stack {

	//list of items
	private List<Object> items;
	
	//constructor
	public Stack(){
		
		//set items to new ArrayList
		items = new ArrayList<Object>();
	}
	
	//returns if empty
	public boolean isEmpty(){
		return items.isEmpty();
	}
	
	//add item to stack
	public void push(Object item){
		items.add(item);
	}
	
	//remove item from stack
	public Object pop(){
		return items.remove(size()-1);
	}
	
	//check top item in stack
	public Object peek(){
		return items.get(size()-1);
	}
	
	//veiw size of stack
	public int size(){
		return items.size();
	}
	
}
