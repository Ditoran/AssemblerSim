package de.assemblersim.application;
import java.util.ArrayList;
import java.util.List;


/**
 * The UndoRedoManager is an easy Class to manage undo's and redo's
 * of a any text component. It is nessesary to add a keylistener to 
 * the text component. Everytime the space key or return key is pressed,
 * the method addToQueue must be called.
 * 
 * @author Dominik Jahnke
 *
 */
public class UndoRedoManager{
	
	List<String> queue = new ArrayList<String>();
	int index = 0;
	
	public UndoRedoManager(){
		queue.add("");
		index++;
	}
	
	public String undo(){
		if(index>0){
			index--;
		}
		return queue.get(index);
	}
	
	public String redo(){
		if(index<queue.size()-1){
			index++;
		}
		return queue.get(index);
	}
	
	public void addToQueue(String input){
		queue.add(input);
		index++;
	}
	
	public void clearQueue(){
		queue.clear();
		queue.add("");
		index = 1;
	}
}
