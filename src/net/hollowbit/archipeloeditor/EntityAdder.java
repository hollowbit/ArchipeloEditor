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

	public EntityAdder (MainEditor editor, int tileX, int tileY) {
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("Entity Adder");
		setBounds(100, 100, 300, 140);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(MainEditor.ICON);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		JComboBox<EntityType> entityType = new JComboBox<EntityType>();
		for (EntityType type : EntityType.values())//Add all entity types
			entityType.addItem(type);
		entityType.setBounds(15, 15, 250, 30);
		getContentPane().add(entityType);
		
		JButton confirm = new JButton("Add");
		confirm.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				EntityDefiner entityDefiner = new EntityDefiner(editor, (EntityType) entityType.getSelectedItem(), tileX * MainEditor.TILE_SIZE, tileY * MainEditor.TILE_SIZE);
				entityDefiner.setVisible(true);
				
				//Remove
				editor.removeOpenWindow("entity-adder");
				setVisible(false);
				dispose();
				super.mouseReleased(e);
			}
			
		});
		confirm.setBounds(175, 65, 100, 30);
		getContentPane().add(confirm);
	}
	
}
