package net.hollowbit.archipeloeditor.objectdefiners;

import javax.swing.JFrame;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.world.AssetManager;

public abstract class ObjectDefiner<T> extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8429136770915792405L;
	
	protected T object;
	protected DefinitionCompleteListener<T> listener;
	protected MainEditor editor;
	protected AssetManager assetManager;
	
	public ObjectDefiner(MainEditor editor, AssetManager assetManager, DefinitionCompleteListener<T> listener) {
		this.editor = editor;
		this.assetManager = assetManager;
		this.listener = listener;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(MainEditor.ICON);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
	}
	
	public void open(T object) {
		if (object != null)
			this.object = object;
		else
			this.object = createObject();
		this.setVisible(true);
	}
	
	/**
	 * Method to create a new object of the proper type.
	 */
	protected abstract T createObject();
	
	protected void complete() {
		this.setVisible(false);
		this.listener.objectComplete(object);
	}
	
	public interface DefinitionCompleteListener<T> {
		
		public abstract void objectComplete(T object);
		
	}
	
}
