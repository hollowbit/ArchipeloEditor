package net.hollowbit.archipeloeditor.tools.propertydefiners;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class BooleanPropertyDefiner extends JPropertyDefinitionComponent<Boolean> {

	protected JCheckBox checkBox;
	
	public BooleanPropertyDefiner(JFrame frame, String label, String name, int x, int y, String defaultValue, boolean required,
			MainEditor editor) {
		super(frame, label, name, x, y, defaultValue, required, editor);

		checkBox = new JCheckBox();
		checkBox.setBounds(getValueModifierX(x), y, 230, 20);
		frame.add(checkBox);
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
