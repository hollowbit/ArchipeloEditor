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
		
		NumberFormat format2 = NumberFormat.getInstance();
		NumberFormatter formatter2 = new NumberFormatter(format2);
		formatter2.setValueClass(Double.class);
		formatter2.setMinimum(Double.MIN_VALUE);
		formatter2.setMaximum(Double.MAX_VALUE);
		formatter2.setAllowsInvalid(false);
		formatter2.setCommitsOnValidEdit(true);
		
		this.field = new JFormattedTextField(formatter2);
		field.setBounds(getValueModifierX(x), y, 300, 20);
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
	public String getValueAsString() {
		return field.getText();
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
