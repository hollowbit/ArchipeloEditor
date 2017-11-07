package net.hollowbit.archipeloeditor.objectdefiners;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.entity.EntityType;
import net.hollowbit.archipeloeditor.tools.propertydefiners.BooleanPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.DirectionPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.DoublePropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.FloatPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.HealthPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.IntegerPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.JPropertyDefinitionComponent;
import net.hollowbit.archipeloeditor.tools.propertydefiners.PointPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.StringPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.StylePropertyDefiner;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.PropertyDefinition;

public class EntityDefiner extends JPropertyDefinitionComponent<EntitySnapshot> {
	
	private static final int SPACE_BETWEEN_ELEMENTS = 30;
	private static final int SPACE_FOR_DEFAULT_VALUES = 150;
	
	private Json json;
	
	protected JTextField field;
	
	protected EntityType entityType;
	
	public EntityDefiner (EntityType entityType, boolean bigVersion, JFrame frame, String labelName, String uniqueName, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(frame, labelName, uniqueName, x, y, defaultValue, required, editor);
		json = new Json();
		this.entityType = entityType;
		
		field = new JTextField();
		field.setBounds(getValueModifierX(x), y, 230, 20);
		frame.add(field);
		
		//Update field background on every update of this field
		field.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				refreshJsonField();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				refreshJsonField();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				refreshJsonField();
			}
		});
	}
	
	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}
	
	protected void startEditorWindow() {
		//Construct editor frame
		JFrame frame2 = new JFrame();
		//setAlwaysOnTop(true);
		frame2.requestFocus();
		frame2.setResizable(false);
		frame2.setTitle("Entity Definer");
		frame2.setBounds(100, 100, 325, 500);
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.setIconImage(MainEditor.ICON);
		frame2.setLocationRelativeTo(null);
		frame2.getContentPane().setLayout(null);
				
		JLabel requiredLabel = new JLabel("* Required");
		requiredLabel.setBounds(10, 10, 200, 30);
		frame2.getContentPane().add(requiredLabel);
				
		//ID
		JLabel idLabel = new JLabel("Unique ID*: ");
		idLabel.setBounds(10, 30, 200, 30);
		frame2.getContentPane().add(idLabel);
				
		JTextField idField = new JTextField();
		idField.setBounds(100, 35, 200, 20);
		frame2.getContentPane().add(idField);
				
		//Animation
		JLabel animLabel = new JLabel("Animation: ");
		animLabel.setBounds(10, 60, 200, 30);
		frame2.getContentPane().add(animLabel);
				
		JTextField animField = new JTextField();
		animField.setBounds(100, 65, 200, 20);
		frame2.getContentPane().add(animField);
				
		//Properties
		JLabel defaultsLabel = new JLabel("Properties");
		defaultsLabel.setBounds(10, SPACE_FOR_DEFAULT_VALUES - 30, 200, 20);
		frame2.getContentPane().add(defaultsLabel);
				
		//Add all properties
		ArrayList<PropertyDefinition> definitions = new ArrayList<PropertyDefinition>();
		for (PropertyDefinition def : entityType.getData().defaultProperties)
			definitions.add(def);
		definitions.addAll(entityType.getData().properties);
		
		final ArrayList<SnapshotModifier> modifiers = new ArrayList<SnapshotModifier>();
		
		for (int i = 0; i < definitions.size(); i++) {
			PropertyDefinition propertyDefinition = definitions.get(i);
			final String name = propertyDefinition.name;
			
			SnapshotModifier modifier = null;
			
			switch (propertyDefinition.getType()) {
			case INTEGER:
				modifier = new IntegerPropertyDefiner(frame2, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case FLOAT:
				modifier = new FloatPropertyDefiner(frame2, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case DOUBLE:
				modifier = new DoublePropertyDefiner(frame2, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case POINT:
				modifier = new PointPropertyDefiner(frame2, editor.getMap(), name, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case STYLE:
				modifier = new StylePropertyDefiner(entityType.getData().numberOfStyles, frame2, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case DIRECTION:
				modifier = new DirectionPropertyDefiner(frame2, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case HEALTH:
				modifier = new HealthPropertyDefiner(entityType.getData().maxHealth, frame2, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case STRING:
				modifier = new StringPropertyDefiner(frame2, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case BOOLEAN:
				modifier = new BooleanPropertyDefiner(frame2, name, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "false", propertyDefinition.required, editor); 
				break;
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
					case ENTITY_SNAPSHOT:
						break;
					default:
						break;
					}*/
		}
				
		JButton confirm = new JButton("Create");
		confirm.setBounds(frame2.getBounds().width - 10 - 80, frame2.getBounds().height - 10 - 20, 80, 20);
		confirm.addActionListener(new ActionListener() {
					
			@Override
			public void actionPerformed(ActionEvent e) {
				EntitySnapshot snapshot = new EntitySnapshot();
				snapshot.name = idField.getText();
				snapshot.putString("anim", animField.getText());
				
				//TODO Add check to make sure all modifiers are valid before creating the object
				
				for (SnapshotModifier mod : modifiers)
					mod.modify(snapshot);
			}
		});
		frame2.getContentPane().add(confirm);
	}
	
	public interface SnapshotModifier {
		
		public abstract void modify(EntitySnapshot snapshot);
		public abstract boolean isValid();
		
	}

	@Override
	public void setValueFromString(String valueAsString) {
		//Only set field if it is valid json
		if (isJsonValid(valueAsString))
			field.setText(valueAsString);
	}

	@Override
	public boolean hasValue() {
		//If text field passes json parse test, than this definer has a value
		return this.isJsonValid();
	}
	
	protected void refreshJsonField() {
		if (isJsonValid())
			field.setBackground(Color.WHITE);
		else
			field.setBackground(Color.RED);
	}

	@Override
	public String getValueAsString() {
		return field.getText();
	}
	
	protected boolean isJsonValid() {
		return this.isJsonValid(field.getText());
	}
	
	protected boolean isJsonValid(String jsonText) {
		try {
			EntitySnapshot es = json.fromJson(EntitySnapshot.class, jsonText);
			return es != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void modify(EntitySnapshot snapshot) {
		snapshot.putString(this.name, field.getText());
	}

	@Override
	public boolean isValid() {
		return hasValue();
	}

}
