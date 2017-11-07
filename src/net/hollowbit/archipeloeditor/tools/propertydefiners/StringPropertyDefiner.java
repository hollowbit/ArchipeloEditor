package net.hollowbit.archipeloeditor.tools.propertydefiners;

import javax.swing.JFrame;
import javax.swing.JTextField;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class StringPropertyDefiner extends JPropertyDefinitionComponent<String> {

	protected JTextField field;
	
	public StringPropertyDefiner(JFrame frame, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(frame, label, name, x, y, defaultValue, required, editor);

		field = new JTextField();
		field.setBounds(getValueModifierX(x), y, 200, 20);
		frame.add(field);
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
