package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Container;

import javax.swing.JCheckBox;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class BooleanPropertyDefiner extends JPropertyDefinitionComponent<Boolean> {

	protected JCheckBox checkBox;
	
	public BooleanPropertyDefiner(Container container, String label, String name, int x, int y, String defaultValue, boolean required,
			MainEditor editor) {
		super(container, label, name, x, y, required, editor);

		checkBox = new JCheckBox();
		checkBox.setBounds(getValueModifierX(x), y, 300, 20);
		container.add(checkBox);
		
		//Set default value
		this.setValueFromString(defaultValue);
	}

	@Override
	public void setValueFromString(String valueAsString) {
		try {
			checkBox.setSelected(Boolean.parseBoolean(valueAsString));
		} catch (Exception e) {}
	}

	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public String getValueAsString() {
		return "" + checkBox.isSelected();
	}

	@Override
	public void modify(EntitySnapshot snapshot) {
		snapshot.putBoolean(name, checkBox.isSelected());
	}

	@Override
	public boolean isValid() {
		return hasValue();
	}

}
