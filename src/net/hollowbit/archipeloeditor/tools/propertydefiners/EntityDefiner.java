package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.tools.propertydefiners.entitydefiner.EntityDefinerWindow;
import net.hollowbit.archipeloeditor.tools.propertydefiners.entitydefiner.EntityDefinerWindow.EntityDefinerListener;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class EntityDefiner extends JPropertyDefinitionComponent<EntitySnapshot> {
	
	private Json json;
	
	protected JTextComponent field;
	protected JButton editBtn;
	
	public EntityDefiner (Container container, String labelName, String uniqueName, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(container, labelName, uniqueName, x, y, required, editor);
		json = new Json();
		
		field = new JTextField();
		field.setBounds(getValueModifierX(x), y, 260, 20);
		editBtn = new JButton("...");
		editBtn.setBounds(getValueModifierX(x) + field.getWidth() + 5, y, 40, 20);
		container.add(field);
		container.add(editBtn);
		
		editBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new EntityDefinerWindow(field.getText(), container, editor, new EntityDefinerListener() {
					
					@Override
					public void complete(EntitySnapshot snapshot) {
						field.setText(json.toJson(snapshot));
					}
					
				});
				super.mouseClicked(e);
			}
		});
		
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
		
		//Set default value
		this.setValueFromString(defaultValue);
	}
	
	public JTextComponent getTextComponent() {
		return field;
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
