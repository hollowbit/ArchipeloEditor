package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Container;

import javax.swing.JComboBox;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class DirectionPropertyDefiner extends JPropertyDefinitionComponent<Integer> {

	protected JComboBox<Direction> comboBox;
	
	public DirectionPropertyDefiner(Direction[] allowedDirections, Container container, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(container, label, name, x, y, required, editor);
		
		comboBox = new JComboBox<Direction>();
		for (Direction direction : allowedDirections)
			comboBox.addItem(direction);
		comboBox.setSelectedIndex(0);
		comboBox.setBounds(getValueModifierX(x), y, 100, 20);
		container.add(comboBox);
		
		//Set default value
		this.setValueFromString(defaultValue);
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
	public Integer getValue() {
		return ((Direction) comboBox.getSelectedItem()).ordinal();
	}

	@Override
	public void modify(EntitySnapshot snapshot) {
		snapshot.putInt(name, ((Direction) comboBox.getSelectedItem()).ordinal());
	}

	@Override
	public boolean isValid() {
		return hasValue();
	}

}
