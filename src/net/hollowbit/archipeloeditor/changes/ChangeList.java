package net.hollowbit.archipeloeditor.changes;

import java.util.ArrayList;

import net.hollowbit.archipeloeditor.MainEditor;

public class ChangeList {
	
	private ArrayList<Change[]> changeList = new ArrayList<Change[]>();
	
	private int index = -1;
	private MainEditor editor;
	
	public ChangeList (MainEditor editor) {
		this.editor = editor;
		index = -1;
		changeList = new ArrayList<Change[]>();
	}
	
	public void addChanges (Change... changes) {
		if (editor.getMap() == null) return;
		if(index < 0)
			changeList.removeAll(changeList);
		if(index == 0){
			Change[] change = changeList.get(0);
			changeList.removeAll(changeList);
			changeList.add(change);
		}else if(index < changeList.size() - 1)
			changeList = new ArrayList<Change[]>(changeList.subList(0, index));
		index++;
		changeList.add(changes);
	}
	
	public void undo () {
		if(index < 0) return;
		for(Change change : changeList.get(index)){
			change.undoChange();
		}
		if(index > -1)
			index--;
	}
	
	public void redo () {
		if(index >= changeList.size() - 1) return;
		index++;
		for(Change change : changeList.get(index))
			change.redoChanges();
	}
	
	public void reset () {
		changeList.removeAll(changeList);
		index = -1;
	}
	
	public void update () {
		if(index >= changeList.size())
			index = changeList.size() - 1;
		if(index < -1)
			index = -1;
	}
	
}
