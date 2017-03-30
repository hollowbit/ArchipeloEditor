package net.hollowbit.archipeloeditor;

import javax.swing.JFrame;

import net.hollowbit.archipeloeditor.entity.EntityType;

public class EntityDefiner extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private float x, y;
	private EntityType entityType;
	private MainEditor editor;
	
	public EntityDefiner (MainEditor editor, EntityType entityType, float x, float y) {
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("Entity Definer");
		setBounds(100, 100, 500, 700);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(MainEditor.ICON);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		this.editor = editor;
		this.entityType = entityType;
		this.x = x;
		this.y = y;
		
		//TODO Add base entity settings
		//TODO Add default properties
		//TODO Add other properties
	}

}
