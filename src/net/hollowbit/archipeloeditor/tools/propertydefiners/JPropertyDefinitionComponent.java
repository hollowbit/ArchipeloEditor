package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Container;

import javax.swing.JLabel;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.tools.propertydefiners.entitydefiner.EntityDefinerWindow.SnapshotModifier;

public abstract class JPropertyDefinitionComponent<T> implements SnapshotModifier {
	
	private static final int SPACE_OF_LABELS = 120;
	
	public static final int WIDTH = 315;
	public static final int HEIGHT = 40;
	
	protected String name;
	protected String label;
	protected boolean required;
	
	protected JLabel nameLabel;
	
	protected MainEditor editor;
	protected Json json;
	
	public JPropertyDefinitionComponent(Container container, String label, String name, int x, int y, boolean required, MainEditor editor) {
		this.name = name;
		this.label = label;
		this.required = required;
		this.json = new Json();
		
		if (label != null) {
			nameLabel = new JLabel(label + (required ? "*": "") + ":");
			nameLabel.setBounds(x, y, SPACE_OF_LABELS, 20);
			container.add(nameLabel);
		}
	}
	
	public abstract void setValueFromString(String valueAsString);
	public abstract boolean hasValue();
	public abstract T getValue();
	
	public boolean hasNameLabel() {
		return nameLabel != null;
	}
	
	protected int getValueModifierX(int x) {
		return hasNameLabel() ? SPACE_OF_LABELS + x : x;
	}
	
	@Override
	public boolean isRequired() {
		return required;
	}
	
	@Override
	public String getName() {	
		return label == null ? name : label;
	}
	
}
