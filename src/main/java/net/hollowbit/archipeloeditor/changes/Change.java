package net.hollowbit.archipeloeditor.changes;

public abstract class Change {
	
	public abstract void undoChange();
	public abstract void redoChanges();
	
}
