package net.hollowbit.archipeloeditor.tools.propertydefiners;

import javax.swing.JFrame;
import javax.swing.JLabel;

import net.hollowbit.archipeloeditor.MainEditor;

public abstract class JPropertyDefinitionComponent<T> {
	
	protected static final int SPACE_OF_LABELS = 75;
	
	public static final int WIDTH = 315;
	public static final int HEIGHT = 40;
	
	protected String name;
	protected boolean required;
	
	protected JLabel nameLabel;
	
	protected MainEditor editor;
	
	public JPropertyDefinitionComponent(JFrame frame, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		
		if (name != null) {
			nameLabel = new JLabel(name + (required ? "*": "") + ":");
			nameLabel.setBounds(x, y, 75, 20);
			frame.add(nameLabel);
		}
		
		if (defaultValue != null && !defaultValue.trim().equals(""))
			this.setValueFromString(defaultValue);
	}
	
	public JPropertyDefinitionComponent(JFrame frame, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		this(frame, null, x, y, defaultValue, required, editor);
	}
	
	public abstract void setValueFromString(String valueAsString);
	public abstract boolean hasValue();
	public abstract String getValueAsString();
	
	public boolean hasNameLabel() {
		return nameLabel != null;
	}
	
	protected int getValueModifierX(int x) {
		return hasNameLabel() ? SPACE_OF_LABELS + x : x;
	}
	
}
