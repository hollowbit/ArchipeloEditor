package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Container;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class DoublePropertyDefiner extends JPropertyDefinitionComponent<Double> {
	
	protected JFormattedTextField field;
	
	public DoublePropertyDefiner(Container container, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(container, label, name, x, y, required, editor);
		
		NumberFormat format = NumberFormat.getInstance();
		format.setParseIntegerOnly(false);
		format.setMinimumFractionDigits(1);
		
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Double.class);
		formatter.setMinimum(Double.MIN_VALUE);
		formatter.setMaximum(Double.MAX_VALUE);
		formatter.setAllowsInvalid(true);
		formatter.setCommitsOnValidEdit(true);
		
		this.field = new JFormattedTextField(formatter);
		field.setBounds(getValueModifierX(x), y, 300, 20);
		field.setText("0.0");
		container.add(field);
		
		//Set default value
		this.setValueFromString(defaultValue);
	}

	@Override
	public void setValueFromString(String valueAsString) {
		try {
			field.setValue(new Double(Double.parseDouble(valueAsString)));
		} catch (Exception e) {}
	}

	@Override
	public boolean hasValue() {
		return field.isEditValid();
	}

	@Override
	public Double getValue() {
		return (Double) field.getValue();
	}

	@Override
	public void modify(EntitySnapshot snapshot) {
		snapshot.putDouble(name, ((Double) field.getValue()).doubleValue());
	}

	@Override
	public boolean isValid() {
		return hasValue();
	}

}
