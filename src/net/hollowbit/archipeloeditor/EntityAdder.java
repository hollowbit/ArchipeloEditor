package net.hollowbit.archipeloeditor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import net.hollowbit.archipeloeditor.entity.EntityType;

public class EntityAdder extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntityAdder (MainEditor editor) {
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("Entity Adder");
		setBounds(100, 100, 200, 125);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(MainEditor.ICON);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		JComboBox<EntityType> entityType = new JComboBox<EntityType>();
		for (EntityType type : EntityType.values())//Add all entity types
			entityType.addItem(type);
		entityType.setBounds(30, 30, 100, 30);
		getContentPane().add(entityType);
		
		JButton confirm = new JButton("Add");
		confirm.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				//TODO Open entity definer
				super.mouseReleased(e);
			}
			
		});
		confirm.setBounds(70, 75, 50, 30);
		getContentPane().add(confirm);
	}
	
}
