package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Container;

import javax.swing.JTextField;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class StringPropertyDefiner extends JPropertyDefinitionComponent<String> {

	protected JTextField field;
	
	public StringPropertyDefiner(Container container, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(container, label, name, x, y, required, editor);

		field = new JTextField();
		field.setBounds(getValueModifierX(x), y, 300, 20);
		container.add(field);
		
		//Set default value
		this.setValueFromString(defaultValue);
	}

	@Override
	public void setValueFromString(String valueAsString) {
		field.setText(valueAsString);
	}

	@Override
	public boolean hasValue() {
		return !(field.getText() == null || field.getText().equals(""));
	}

	@Override
	public String getValueAsString() {
		return field.getText();
	}

	@Override
	public void modify(EntitySnapshot snapshot) {
		snapshot.putString(name, field.getText());
	}

	@Override
	public boolean isValid() {
		return hasValue();
	}

}
