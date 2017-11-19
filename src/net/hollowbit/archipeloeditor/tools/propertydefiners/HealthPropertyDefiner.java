package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Container;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class HealthPropertyDefiner extends JPropertyDefinitionComponent<Integer> {

	protected JSpinner spinner;
	
	public HealthPropertyDefiner(int maxHealth, Container container, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(container, label, name, x, y, required, editor);
		
		SpinnerModel model = new SpinnerNumberModel(maxHealth, 1, maxHealth, 1);
		spinner = new JSpinner(model);
		spinner.setBounds(getValueModifierX(x), y, 300, 20);
		container.add(spinner);
		
		//Set default value
		this.setValueFromString(defaultValue);
	}

	@Override
	public boolean hasValue() {
		return spinner.getValue() != null;
	}

	@Override
	public Integer getValue() {
		return (Integer) spinner.getValue();
	}

	@Override
	public void setValueFromString(String valueAsString) {
		try {
			spinner.setValue(new Integer(Integer.parseInt(valueAsString)));
		} catch (Exception e) {}
	}

	@Override
	public void modify(EntitySnapshot snapshot) {
		snapshot.putInt(name, ((Integer) spinner.getValue()).intValue());
	}

	@Override
	public boolean isValid() {
		return hasValue();
	}

}
