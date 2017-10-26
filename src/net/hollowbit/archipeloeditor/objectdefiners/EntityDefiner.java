package net.hollowbit.archipeloeditor.objectdefiners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.entity.EntityType;
import net.hollowbit.archipeloeditor.tools.ObjectCapsule;
import net.hollowbit.archipeloeditor.tools.propertydefiners.DoublePropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.FloatPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.IntegerPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.JPropertyDefinitionComponent;
import net.hollowbit.archipeloeditor.tools.propertydefiners.PointPropertyDefiner;
import net.hollowbit.archipeloeditor.tools.propertydefiners.StylePropertyDefiner;
import net.hollowbit.archipeloeditor.world.AssetManager;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditorMode.WorldEditorModeListener;
import net.hollowbit.archipeloeditor.worldeditor.modes.PointWorldEditorMode;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.Point;
import net.hollowbit.archipeloshared.PropertyDefinition;

public class EntityDefiner extends ObjectDefiner<EntitySnapshot> {
	
	private static final int SPACE_BETWEEN_ELEMENTS = 30;
	private static final int SPACE_FOR_DEFAULT_VALUES = 150;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Json json;
	
	public EntityDefiner (MainEditor editor, AssetManager assetManager, EntityType entityType, float x, float y, DefinitionCompleteListener<EntitySnapshot> listener) {
		super(editor, assetManager, listener);
		json = new Json();
		
		//setAlwaysOnTop(true);
		requestFocus();
		setResizable(false);
		setTitle("Entity Definer");
		setBounds(100, 100, 325, 500);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(MainEditor.ICON);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
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
		JLabel defaultsLabel = new JLabel("Properties");
		defaultsLabel.setBounds(10, SPACE_FOR_DEFAULT_VALUES - 30, 200, 20);
		getContentPane().add(defaultsLabel);
		
		//Add all properties
		ArrayList<PropertyDefinition> definitions = new ArrayList<PropertyDefinition>();
		for (PropertyDefinition def : entityType.getData().defaultProperties)
			definitions.add(def);
		definitions.addAll(entityType.getData().properties);

		JFrame entityDefiner = this;
		
		final ArrayList<SnapshotModifier> modifiers = new ArrayList<SnapshotModifier>();
		
		for (int i = 0; i < definitions.size(); i++) {
			PropertyDefinition propertyDefinition = definitions.get(i);
			final String name = propertyDefinition.name;
			
			JPropertyDefinitionComponent<?> component = null;
			
			switch (propertyDefinition.getType()) {
			case INTEGER:
				component = new IntegerPropertyDefiner(this, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case FLOAT:
				component = new FloatPropertyDefiner(this, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case DOUBLE:
				component = new DoublePropertyDefiner(this, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case POINT:
				component = new PointPropertyDefiner(this, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			case STYLE:
				component = new StylePropertyDefiner(entityType.getData().numberOfStyles, this, name, 10, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, "", propertyDefinition.required, editor);
				break;
			default:
				component = null;
			}
			
			/*switch (propertyDefinition.getType()) {
			
			case DOUBLE:
				
				break;
			case POINT:
				
				break;
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
			case DIRECTION:
				JComboBox<Direction> comboBox = new JComboBox<Direction>();
				for (Direction direction : Direction.values())
					comboBox.addItem(direction);
				comboBox.setSelectedIndex(0);
				comboBox.setBounds(SPACE_OF_LABELS, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, 200, 20);
				getContentPane().add(comboBox);
				
				modifiers.add(new SnapshotModifier() {
					@Override
					public void modify(EntitySnapshot snapshot) {
						snapshot.putInt(name, ((Direction) comboBox.getSelectedItem()).ordinal()); 
					}
				});
				break;
			case STRING:
				JTextField field3 = new JTextField();
				field3.setBounds(SPACE_OF_LABELS, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, 200, 20);
				getContentPane().add(field3);
				
				modifiers.add(new SnapshotModifier() {
					@Override
					public void modify(EntitySnapshot snapshot) {
						snapshot.putString(name, field3.getText()); 
					}
				});
				break;
			case BOOLEAN:
				JCheckBox checkBox = new JCheckBox();
				checkBox.setBounds(SPACE_OF_LABELS, i * SPACE_BETWEEN_ELEMENTS + SPACE_FOR_DEFAULT_VALUES, 200, 20);
				getContentPane().add(checkBox);
				
				modifiers.add(new SnapshotModifier() {
					@Override
					public void modify(EntitySnapshot snapshot) {
						snapshot.putBoolean(name, checkBox.isSelected()); 
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
			case STYLE:
				
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
		confirm.setBounds(this.getBounds().width - 10 - 80, this.getBounds().height - 10 - 20, 80, 20);
		confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				object.name = idField.getText();
				object.putString("anim", animField.getText());
				
				for (SnapshotModifier mod : modifiers)
					mod.modify(object);
			}
		});
		getContentPane().add(confirm);
		
	}
	
	private interface SnapshotModifier {
		
		public abstract void modify(EntitySnapshot snapshot);
		
	}

	@Override
	protected EntitySnapshot createObject() {
		return new EntitySnapshot();
	}

}
