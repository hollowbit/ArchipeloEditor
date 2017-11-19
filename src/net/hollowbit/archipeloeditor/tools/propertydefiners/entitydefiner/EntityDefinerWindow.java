package net.hollowbit.archipeloeditor.tools.propertydefiners.entitydefiner;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.entity.EntityType;
import net.hollowbit.archipeloeditor.tools.propertydefiners.BooleanPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.DirectionPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.DoublePropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.EntityDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.FloatPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.HealthPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.IntegerPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.PointPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.StringPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.StylePropertyDefiner;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.Point;
import net.hollowbit.archipeloshared.PropertyDefinition;

public class EntityDefinerWindow extends JFrame {
	
	private static final int SPACE_BETWEEN_ELEMENTS = 30;
	private static final int SPACE_FOR_DEFAULT_VALUES = 165;

	/**
	 * 
	 */
	private static final long serialVersionUID = -645128830564910563L;
	
	private Json json;
	
	public EntityDefinerWindow(String defaultValue, Container parentContainer, MainEditor editor, final EntityDefinerListener listener) {
		this(defaultValue, null, parentContainer, editor, listener);
	}
	
	public EntityDefinerWindow(String defaultValue, Point pos, Container parentContainer, MainEditor editor, final EntityDefinerListener listener) {
		//setAlwaysOnTop(true);
		this.requestFocus();
		this.setResizable(false);
		this.setTitle("Entity Definer");
		this.setBounds(100, 100, 490, 700);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage(MainEditor.ICON);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(null);
		this.setVisible(true);
		
		this.json = new Json();
		
		EntitySnapshot snapshot = null;
		try {
			snapshot = json.fromJson(EntitySnapshot.class, defaultValue);
			if (snapshot == null)
				snapshot = new EntitySnapshot();
		} catch (Exception e) {
			snapshot = new EntitySnapshot();
		}

		final ArrayList<SnapshotModifier> modifiers = new ArrayList<SnapshotModifier>();
		
				
		JLabel requiredLabel = new JLabel("* Required");
		requiredLabel.setBounds(this.getWidth() - 80, 10, 80, 30);
		this.getContentPane().add(requiredLabel);
				
		//ID
		JLabel idLabel = new JLabel("Unique ID*: ");
		idLabel.setBounds(10, 30, 200, 30);
		this.getContentPane().add(idLabel);
				
		JTextField idField = new JTextField(snapshot.name);
		idField.setBounds(100, 35, 200, 20);
		this.getContentPane().add(idField);
		
		//Type
		JLabel typeLabel = new JLabel("Entity Type (Changing this will erase everything)*:");
		typeLabel.setBounds(10, 60, 400, 30);
		this.add(typeLabel);

		JPanel propertiesPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(propertiesPanel);
		
		JComboBox<EntityType> entityTypeCboBox = new JComboBox<EntityType>();
		//entityType.setPreferredSize(new Dimension(194, 15));
		for (EntityType type : EntityType.values())//Add all entity types
			entityTypeCboBox.addItem(type);
		
		EntityType defaultType = EntityType.getById(snapshot.type);
		if (defaultType != null)
			entityTypeCboBox.setSelectedItem(defaultType);
		
		entityTypeCboBox.setBounds(10, 90, 290, 20);
		entityTypeCboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String posString = "";
				if (pos != null)
					posString = json.toJson(pos);
				
				regenerateProperties(null, scrollPane, propertiesPanel, (EntityType) entityTypeCboBox.getSelectedItem(), editor, posString, modifiers);
			}
		});
		this.add(entityTypeCboBox);
				
		//Properties
		JLabel defaultsLabel = new JLabel("Properties");
		defaultsLabel.setBounds(10, SPACE_FOR_DEFAULT_VALUES - 30, 200, 20);
		this.getContentPane().add(defaultsLabel);

		propertiesPanel.setLayout(null);
		propertiesPanel.setPreferredSize(new Dimension(440, this.getHeight() - SPACE_FOR_DEFAULT_VALUES - 80));
		//propertiesPanel.setBounds(0, 0, 470, this.getHeight() - SPACE_FOR_DEFAULT_VALUES - 80);
		
		scrollPane.setBounds(10, SPACE_FOR_DEFAULT_VALUES, 470, this.getHeight() - SPACE_FOR_DEFAULT_VALUES - 80);
		this.add(scrollPane);
		
		final JFrame frame = this;
		
		JButton confirm = new JButton("Finish");
		confirm.setBounds(this.getWidth() - 10 - 80, this.getHeight() - 10 - 60, 80, 30);
		confirm.addActionListener(new ActionListener() {
					
			@Override
			public void actionPerformed(ActionEvent e) {
				if (parentContainer != null)
					parentContainer.requestFocus();
				EntitySnapshot snapshot = new EntitySnapshot();
				snapshot.name = idField.getText();
				snapshot.type = ((EntityType) entityTypeCboBox.getSelectedItem()).getId();
				
				//TODO Add check to make sure all modifiers are valid before creating the object
				
				for (SnapshotModifier mod : modifiers)
					mod.modify(snapshot);
				
				listener.complete(snapshot);
				frame.setVisible(false);
			}
		});
		this.getContentPane().add(confirm);
		

		String posString = "";
		if (pos != null)
			posString = json.toJson(pos);
		this.regenerateProperties(snapshot.properties, scrollPane, propertiesPanel, (EntityType) entityTypeCboBox.getSelectedItem(), editor, posString, modifiers);
	}
	
	private void regenerateProperties(HashMap<String, String> defaultProperties, JScrollPane scrollPane, JPanel propertiesPanel, EntityType entityType, MainEditor editor, String posString, ArrayList<SnapshotModifier> modifiers) {
		//Clear old properties
		modifiers.clear();
		propertiesPanel.removeAll();
		
		//Add all properties
		ArrayList<PropertyDefinition> definitions = new ArrayList<PropertyDefinition>();
		for (PropertyDefinition def : entityType.getData().defaultProperties)
			definitions.add(def);
		definitions.addAll(entityType.getData().properties);
		
		int height = 10;
		
		for (int i = 0; i < definitions.size(); i++) {
			PropertyDefinition propertyDefinition = definitions.get(i);
			final String name = propertyDefinition.name;
			SnapshotModifier modifier = null;

			height += SPACE_BETWEEN_ELEMENTS;
			
			String defaultValue = null;
			if (defaultProperties != null)
				defaultValue = defaultProperties.get(name);
			
			switch (propertyDefinition.getType()) {
			case INTEGER:
				modifier = new IntegerPropertyDefiner(propertiesPanel, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + 10, defaultValue, propertyDefinition.required, editor);
				break;
			case FLOAT:
				modifier = new FloatPropertyDefiner(propertiesPanel, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + 10, defaultValue, propertyDefinition.required, editor);
				break;
			case DOUBLE:
				modifier = new DoublePropertyDefiner(propertiesPanel, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + 10, defaultValue, propertyDefinition.required, editor);
				break;
			case POINT:
				modifier = new PointPropertyDefiner(propertiesPanel, editor.getMap(), name, name, 10, i * SPACE_BETWEEN_ELEMENTS + 10, (posString == null || posString.equals("") ? defaultValue : posString), propertyDefinition.required, editor);
				break;
			case STYLE:
				modifier = new StylePropertyDefiner(entityType.getData().numberOfStyles, propertiesPanel, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + 10, defaultValue, propertyDefinition.required, editor);
				break;
			case DIRECTION:
				Direction[] allowedDirections = Direction.getAllowedDirectionsEntityMax(entityType.getData().maxDirections);
				modifier = new DirectionPropertyDefiner(allowedDirections, propertiesPanel, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + 10, (defaultValue == null ? allowedDirections[0].name(): defaultValue), propertyDefinition.required, editor);
				break;
			case HEALTH:
				modifier = new HealthPropertyDefiner(entityType.getData().maxHealth, propertiesPanel, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + 10, (defaultValue == null ? entityType.getData().maxHealth + "" : defaultValue), propertyDefinition.required, editor);
				break;
			case STRING:
				modifier = new StringPropertyDefiner(propertiesPanel, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + 10, defaultValue, propertyDefinition.required, editor);
				break;
			case BOOLEAN:
				modifier = new BooleanPropertyDefiner(propertiesPanel, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + 10, defaultValue, propertyDefinition.required, editor); 
				break;
			case ENTITY_SNAPSHOT:
				modifier = new EntityDefiner(propertiesPanel, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + 10, defaultValue, propertyDefinition.required, editor);
			default:
				modifier = null;
			}
			modifiers.add(modifier);
					
					/*switch (propertyDefinition.getType()) {
					case LOCATION:
						String json5 = "";
						
						modifiers.add(new SnapshotModifier() {
							@Override
							public void modify(EntitySnapshot snapshot) {
								snapshot.putString(name, json5); 
							}
						});
						break;
					case RECTANGLE:
						String json3 = "";
						
						modifiers.add(new SnapshotModifier() {
							@Override
							public void modify(EntitySnapshot snapshot) {
								snapshot.putString(name, json3); 
							}
						});
						break;
					case JSON:
						String json0 = "";
						
						modifiers.add(new SnapshotModifier() {
							@Override
							public void modify(EntitySnapshot snapshot) {
								snapshot.putString(name, json0);
							}
						});
						break;
					case ITEM:
						String json2 = "";
						
						modifiers.add(new SnapshotModifier() {
							@Override
							public void modify(EntitySnapshot snapshot) {
								snapshot.putString(name, json2);
							}
						});
						break;
					default:
						break;
					}*/
		}

		propertiesPanel.setPreferredSize(new Dimension(440, height));
		propertiesPanel.revalidate();
		propertiesPanel.repaint();
		
		scrollPane.revalidate();
		scrollPane.repaint();
	}
	
	public interface SnapshotModifier {
		
		public abstract void modify(EntitySnapshot snapshot);
		public abstract boolean isValid();
		
	}
	
	public interface EntityDefinerListener {
		
		public abstract void complete(EntitySnapshot snapshot);
		
	}

}
