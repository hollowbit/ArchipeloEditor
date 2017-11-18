package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Container;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class FloatPropertyDefiner extends JPropertyDefinitionComponent<Float> {
	
	protected JFormattedTextField field;
	
	public FloatPropertyDefiner(Container container, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(container, label, name, x, y, required, editor);
		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Float.class);
		formatter.setMinimum(Float.MIN_VALUE);
		formatter.setMaximum(Float.MAX_VALUE);
		formatter.setAllowsInvalid(false);
		formatter.setCommitsOnValidEdit(true);
		field = new JFormattedTextField(formatter);
		field.setBounds(getValueModifierX(x), y, 300, 20);
		container.add(field);
		
		//Set default value
		this.setValueFromString(defaultValue);
	}

	@Override
	public void setValueFromString(String valueAsString) {
		try {
			field.setValue(new Float(Float.parseFloat(valueAsString)));
		} catch (Exception e) {}
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
