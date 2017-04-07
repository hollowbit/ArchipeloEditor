package net.hollowbit.archipeloeditor;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.hollowbit.archipeloeditor.entity.EntityType;

public class EntityDefiner extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EntityDefiner (MainEditor editor, EntityType entityType, float x, float y) {
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("Entity Definer");
		setBounds(100, 100, 325, 500);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(MainEditor.ICON);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		//TODO Add base entity settings
		//TODO Add default properties
		//TODO Add other properties
		
		JLabel requiredLabel = new JLabel("* Required");
		requiredLabel.setBounds(10, 10, 200, 30);
		getContentPane().add(requiredLabel);
		
		//ID
		JLabel idLabel = new JLabel("Unique ID*: ");
		idLabel.setBounds(10, 30, 200, 30);
		getContentPane().add(idLabel);
		
		JTextField idField = new JTextField();
		idField.setBounds(100, 35, 200, 20);
		getContentPane().add(idField);
		
		//Animation
		JLabel animLabel = new JLabel("Animation: ");
		animLabel.setBounds(10, 60, 200, 30);
		getContentPane().add(animLabel);
		
		JTextField animField = new JTextField();
		animField.setBounds(100, 65, 200, 20);
		getContentPane().add(animField);
		
		//Properties
		
		
	}

}
