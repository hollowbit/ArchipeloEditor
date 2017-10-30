package net.hollowbit.archipeloeditor.tools.propertydefiners;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.Direction;

public class DirectionPropertyDefiner extends JPropertyDefinitionComponent<Direction> {

	protected JComboBox<Direction> comboBox;
	
	public DirectionPropertyDefiner(JFrame frame, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(frame, name, x, y, defaultValue, required, editor);
		
		comboBox = new JComboBox<Direction>();
		for (Direction direction : Direction.values())
			comboBox.addItem(direction);
		comboBox.setSelectedIndex(0);
		comboBox.setBounds(getValueModifierX(x), y, 230, 20);
		frame.add(comboBox);
	}

	@Override
	public void setValueFromString(String valueAsString) {
		Direction d = Direction.getDirectionByName(valueAsString);
		if (d != null)
			comboBox.setSelectedItem(d);
	}

	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public String getValueAsString() {
		return ((Direction) comboBox.getSelectedItem()).name();
	}

}
