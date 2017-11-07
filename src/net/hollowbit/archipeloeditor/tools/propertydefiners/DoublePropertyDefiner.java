package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.text.NumberFormatter;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class DoublePropertyDefiner extends JPropertyDefinitionComponent<Double> {
	
	protected JFormattedTextField field;
	
	public DoublePropertyDefiner(JFrame frame, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(frame, label, name, x, y, defaultValue, required, editor);
		
		NumberFormat format2 = NumberFormat.getInstance();
		NumberFormatter formatter2 = new NumberFormatter(format2);
		formatter2.setValueClass(Double.class);
		formatter2.setMinimum(Double.MIN_VALUE);
		formatter2.setMaximum(Double.MAX_VALUE);
		formatter2.setAllowsInvalid(false);
		formatter2.setCommitsOnValidEdit(true);
		
		this.field = new JFormattedTextField(formatter2);
		field.setBounds(getValueModifierX(x), y, 230, 20);
		frame.add(field);
	}

	@Override
	public void setValueFromString(String valueAsString) {
		field.setValue(new Double(Double.parseDouble(valueAsString)));
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
