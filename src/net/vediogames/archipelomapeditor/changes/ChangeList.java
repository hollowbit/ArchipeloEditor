package net.vediogames.archipelomapeditor.changes;

import java.util.ArrayList;

import net.vediogames.archipelomapeditor.MainEditor;

public class ChangeList {
	
	public static ArrayList<Change[]> ChangeList = new ArrayList<Change[]>();
	
	private static int index = -1;
	
	public static void addChanges(Change... changes){
		if(MainEditor.map == null) return;
		MainEditor.justSaved = false;
		if(index < 0)
			ChangeList.removeAll(ChangeList);
		if(index == 0){
			Change[] change = ChangeList.get(0);
			ChangeList.removeAll(ChangeList);
			ChangeList.add(change);
		}else if(index < ChangeList.size() - 1)
			ChangeList = new ArrayList<Change[]>(ChangeList.subList(0, index));
		index++;
		ChangeList.add(changes);
	}
	
	public static void undo(){
		if(index < 0) return;
		for(Change change : ChangeList.get(index)){
			change.undoChange();
			if(change instanceof SettingsChange)
				MainEditor.list.repaint();
		}
		if(index > -1)
			index--;
	}
	
	public static void redo(){
		if(index >= ChangeList.size() - 1) return;
		index++;
		for(Change change : ChangeList.get(index))
			change.redoChanges();
	}
	
	public static void reset(){
		ChangeList.removeAll(ChangeList);
		index = -1;
	}
	
	public static void update(){
		if(index >= ChangeList.size())
			index = ChangeList.size() - 1;
		if(index < -1)
			index = -1;
	}
	
}
