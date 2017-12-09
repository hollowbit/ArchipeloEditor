package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Container;

import javax.swing.JFormattedTextField;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class DoublePropertyDefiner extends JPropertyDefinitionComponent<Double> {
	
	protected JFormattedTextField field;
	
	public DoublePropertyDefiner(Container container, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(container, label, name, x, y, required, editor);
		
		this.field = new JFormattedTextField(new Double(0.0d));
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
