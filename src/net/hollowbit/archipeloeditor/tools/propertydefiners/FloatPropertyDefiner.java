package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.text.NumberFormatter;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class FloatPropertyDefiner extends JPropertyDefinitionComponent<Float> {
	
	protected JFormattedTextField field;
	
	public FloatPropertyDefiner(JFrame frame, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(frame, label, name, x, y, defaultValue, required, editor);
		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Float.class);
		formatter.setMinimum(Float.MIN_VALUE);
		formatter.setMaximum(Float.MAX_VALUE);
		formatter.setAllowsInvalid(false);
		formatter.setCommitsOnValidEdit(true);
		field = new JFormattedTextField(formatter);
		field.setBounds(getValueModifierX(x), y, 230, 20);
		frame.add(field);
	}

	@Override
	public void setValueFromString(String valueAsString) {
		field.setValue(new Float(Float.parseFloat(valueAsString)));
	}

	@Override
	public boolean hasValue() {
		return field.isEditValid();
	}

	@Override
	public String getValueAsString() {
		return field.getText();
	}

	@Override
	public void modify(EntitySnapshot snapshot) {
		snapshot.putFloat(name, ((Float) field.getValue()).floatValue());
	}

	@Override
	public boolean isValid() {
		return hasValue();
	}

}
