package net.hollowbit.archipeloeditor.tools.propertydefiners;

import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import net.hollowbit.archipeloeditor.MainEditor;

public class HealthPropertyDefiner extends JPropertyDefinitionComponent<Integer> {

	protected JSpinner spinner;
	
	public HealthPropertyDefiner(int maxHealth, JFrame frame, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(frame, name, x, y, defaultValue, required, editor);
		
		SpinnerModel model = new SpinnerNumberModel(maxHealth, 1, maxHealth, 1);
		spinner = new JSpinner(model);
		spinner.setBounds(getValueModifierX(x), y, 230, 20);
		frame.add(spinner);
	}

	@Override
	public boolean hasValue() {
		return spinner.getValue() != null;
	}

	@Override
	public String getValueAsString() {
		return ((Integer) spinner.getValue()).intValue() + "";
	}

	@Override
	public void setValueFromString(String valueAsString) {
		spinner.setValue(new Integer(Integer.parseInt(valueAsString)));
	}

}
