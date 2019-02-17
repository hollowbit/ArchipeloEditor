package net.hollowbit.archipeloeditor.changes;

import java.util.ArrayList;

import net.hollowbit.archipeloeditor.MainEditor;

public class ChangeList {
	
	private ArrayList<Change[]> changeList = new ArrayList<Change[]>();
	
	private int index = -1;
	private MainEditor editor;
	private ArrayList<ChangeListener> listeners;
	
	public ChangeList (MainEditor editor) {
		this.editor = editor;
		index = -1;
		changeList = new ArrayList<Change[]>();
		
		listeners = new ArrayList<ChangeListener>();
	}
	
	//Adds a change to the list, makes sure index is good as well
	//Also overrides redo changes
	public void addChanges (Change... changes) {
		if (editor.getMap() == null) return;
		if (index < 0)
			changeList.removeAll(changeList);
		if (index == 0) {
			Change[] change = changeList.get(0);
			changeList.removeAll(changeList);
			changeList.add(change);
		} else if (index < changeList.size() - 1)
			changeList = new ArrayList<Change[]>(changeList.subList(0, index));
		index++;
		changeList.add(changes);
	}
	
	//Undo the last change
	public void undo () {
		if (index < 0) return;
		for (Change change : changeList.get(index)) {
			change.undoChange();
		}
		if(index > -1)
			index--;
		
		for (ChangeListener listener : listeners)
			listener.undoOccured();
	}
	
	//redo a previously undone change
	public void redo () {
		if (index >= changeList.size() - 1) return;
		index++;
		for(Change change : changeList.get(index))
			change.redoChanges();
		
		for (ChangeListener listener : listeners)
			listener.redoOccured();
	}
	
	//Clear all changes
	public void reset () {
		changeList.removeAll(changeList);
		index = -1;
	}
	
	//Makes sure index isn't out of bounds
	public void update () {
		if (index >= changeList.size())
			index = changeList.size() - 1;
		if (index < -1)
			index = -1;
	}
	
	public void addListener(ChangeListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ChangeListener listener) {
		this.listeners.remove(listener);
	}
	
	public interface ChangeListener {
		
		public abstract void undoOccured();
		public abstract void redoOccured();
		
	}
	
}
