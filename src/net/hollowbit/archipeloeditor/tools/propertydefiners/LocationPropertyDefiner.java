package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.SavedLocation;

public class LocationPropertyDefiner extends JPropertyDefinitionComponent<SavedLocation> {
	
	protected String jsonValue;
	protected JTextField field;
	protected JButton editBtn;
	protected JFrame frame2;
	
	public LocationPropertyDefiner(Container container, String label, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(container, label, name, x, y, required, editor);
		
		field = new JTextField();
		//field.setEnabled(false);
		field.setBounds(getValueModifierX(x), y, 260, 20);
		container.add(field);
		
		//Update field background on every update of this field
		field.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				refreshJsonField();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				refreshJsonField();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				refreshJsonField();
			}
		});
		
		editBtn = new JButton("...");
		editBtn.setBounds(getValueModifierX(x) + field.getWidth() + 5, y, 40, 20);
		container.add(editBtn);
		
		editBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame2 = new JFrame("Location Object Definer");
				frame2.requestFocus();
				frame2.setResizable(false);
				frame2.setBounds(0, 0, 325, 210);
				frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame2.setIconImage(MainEditor.ICON);
				frame2.setLocationRelativeTo(null);
				frame2.getContentPane().setLayout(null);
				frame2.setVisible(true);
				
				JLabel xLabel = new JLabel("X:");
				xLabel.setBounds(25, 15, 30, 20);
				frame2.getContentPane().add(xLabel);
				
				SpinnerModel xModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
				JSpinner xField = new JSpinner(xModel);
				xField.setBounds(90, 15, 200, 20);
				frame2.getContentPane().add(xField);
				
				JLabel yLabel = new JLabel("Y:");
				yLabel.setBounds(25, 50, 30, 20);
				frame2.getContentPane().add(yLabel);
				
				SpinnerModel yModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
				JSpinner yField = new JSpinner(yModel);
				yField.setBounds(90, 50, 200, 20);
				frame2.getContentPane().add(yField);
				
				JLabel mapLabel = new JLabel("Map:");
				mapLabel.setBounds(25, 85, 60, 20);
				frame2.getContentPane().add(mapLabel);
				
				JTextField mapField = new JTextField();
				mapField.setBounds(90, 85, 200, 20);
				frame2.getContentPane().add(mapField);
				
				DirectionPropertyDefiner directionDefiner = new DirectionPropertyDefiner(Direction.values(), frame2, "Direction", "direction", 25, 120, null, false, editor);
				
				/*JButton selectBtn = new JButton("Select");
				selectBtn.setBounds(15, 80, 75, 20);
				selectBtn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						int x = ((Integer) xField.getValue()).intValue();
						int y = ((Integer) yField.getValue()).intValue();
						editor.getWorldEditor().setMode(frame2, new PointWorldEditorMode(new Point(x, y), editor.getMap(), new WorldEditorModeListener<Point>() {
							
							@Override
							public void valueReceived(Point object) {
								xField.setValue((int) object.x);
								yField.setValue((int) object.y);
							};
							
							@Override
							public void canceled() {
							}
						}));
						super.mouseClicked(e);
					}
				});
				frame2.getContentPane().add(selectBtn);*/
				
				JButton confirmBtn = new JButton("Confirm");
				confirmBtn.setBounds(200, 150, 110, 20);
				confirmBtn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						frame2.setVisible(false);
						int x = ((Integer) xField.getValue()).intValue();
						int y = ((Integer) yField.getValue()).intValue();
						String map = mapField.getText();
						int direction = directionDefiner.getValue().intValue();
						field.setText(json.toJson(new SavedLocation(x, y, map, direction)));
						container.requestFocus();
						super.mouseClicked(e);
					}
				});
				frame2.getContentPane().add(confirmBtn);
				
				//Set value if text field json is good
				if (isJsonValid()) {
					SavedLocation loc = json.fromJson(SavedLocation.class, field.getText());
					xField.setValue((int) loc.x);
					yField.setValue((int) loc.y);
					mapField.setText(loc.getMap());
					directionDefiner.setValueFromString("" + loc.direction);
				}
			}
		});
		
		//Set default value
		this.setValueFromString(defaultValue);
	}

	@Override
	public void setValueFromString(String valueAsString) {
		//Only set field if it is valid json
		if (isJsonValid(valueAsString))
			field.setText(valueAsString);
	}

	@Override
	public boolean hasValue() {
		//If text field passes json parse test, than this definer has a value
		return this.isJsonValid();
	}
	
	protected void refreshJsonField() {
		if (isJsonValid())
			field.setBackground(Color.WHITE);
		else
			field.setBackground(Color.RED);
	}

	@Override
	public SavedLocation getValue() {
		return json.fromJson(SavedLocation.class, field.getText());
	}
	
	protected boolean isJsonValid() {
		return this.isJsonValid(field.getText());
	}
	
	protected boolean isJsonValid(String jsonText) {
		try {
			SavedLocation r = json.fromJson(SavedLocation.class, jsonText);
			return r != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void modify(EntitySnapshot snapshot) {
		snapshot.putString(name, field.getText());
	}

	@Override
	public boolean isValid() {
		return hasValue();
	}
	
}
