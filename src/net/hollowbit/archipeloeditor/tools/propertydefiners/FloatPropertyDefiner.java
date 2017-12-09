package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Container;

import javax.swing.JFormattedTextField;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class FloatPropertyDefiner extends JPropertyDefinitionComponent<Float> {
	
	protected JFormattedTextField field;
	
	public FloatPropertyDefiner(Container container, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(container, label, name, x, y, required, editor);
		
		field = new JFormattedTextField(new Float(0.0f));
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
	public Float getValue() {
		return (Float) field.getValue();
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
