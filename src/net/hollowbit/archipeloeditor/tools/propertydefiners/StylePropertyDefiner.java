package net.hollowbit.archipeloeditor.tools.propertydefiners;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class StylePropertyDefiner extends JPropertyDefinitionComponent<Integer> {
	
	protected int numberOfStyles;
	protected JComboBox<Integer> comboBox;
	
	public StylePropertyDefiner(int numberOfStyles, JFrame frame, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(frame, label, name, x, y, defaultValue, required, editor);
		this.numberOfStyles = numberOfStyles;
		
		comboBox = new JComboBox<Integer>();
		for (int u = 0; u < numberOfStyles; u++)
			comboBox.addItem(u);
		comboBox.setSelectedIndex(0);
		comboBox.setBounds(getValueModifierX(x), y, 230, 20);
		frame.add(comboBox);
	}

	@Override
	public void setValueFromString(String valueAsString) {
		try {
			int index = Integer.parseInt(valueAsString);
			if (index > 0 && index < numberOfStyles)
				comboBox.setSelectedIndex(index);
		} catch (Exception e) {}
	}

	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public String getValueAsString() {
		return "" + comboBox.getSelectedIndex();
	}

	@Override
	public void modify(EntitySnapshot snapshot) {
		snapshot.putInt(name, comboBox.getSelectedIndex());
	}

	@Override
	public boolean isValid() {
		return hasValue();
	}

}
