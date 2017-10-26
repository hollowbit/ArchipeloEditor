package net.hollowbit.archipeloeditor.tools.propertydefiners;

import java.awt.Color;
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

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditorMode.WorldEditorModeListener;
import net.hollowbit.archipeloeditor.worldeditor.modes.PointWorldEditorMode;
import net.hollowbit.archipeloshared.Point;

public class PointPropertyDefiner extends JPropertyDefinitionComponent<Point> {

	protected String jsonValue;
	protected JTextField field;
	protected JButton editBtn;
	protected JFrame frame2;
	protected Json json;
	
	public PointPropertyDefiner(JFrame frame, String name, int x, int y, String defaultValue, boolean required, MainEditor editor) {
		super(frame, name, x, y, defaultValue, required, editor);
		System.out.println("PointPropertyDefiner:  HEYYYYY!");
		json = new Json();
		
		field = new JTextField();
		//field.setEnabled(false);
		field.setBounds(getValueModifierX(x), y, 190, 20);
		frame.add(field);
		
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
		editBtn.setBounds(getValueModifierX(x) + field.getWidth(), y, 40, 20);
		frame.add(editBtn);
		
		editBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame2 = new JFrame("Point Object Definer");
				frame2.requestFocus();
				frame2.setResizable(false);
				frame2.setBounds(0, 0, 325, 200);
				frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame2.setIconImage(MainEditor.ICON);
				frame2.setLocationRelativeTo(null);
				frame2.getContentPane().setLayout(null);
				frame2.setVisible(true);
				
				JLabel xLabel = new JLabel("X:");
				xLabel.setBounds(2, 40, 40, 20);
				frame2.getContentPane().add(xLabel);
				
				SpinnerModel xModel = new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
				JSpinner xField = new JSpinner(xModel);
				xField.setBounds(60, 40, 200, 20);
				frame2.getContentPane().add(xField);
				
				JLabel yLabel = new JLabel("Y:");
				yLabel.setBounds(2, 70, 40, 20);
				frame2.getContentPane().add(yLabel);
				
				SpinnerModel yModel = new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
				JSpinner yField = new JSpinner(yModel);
				yField.setBounds(60, 70, 200, 20);
				frame2.getContentPane().add(yField);
				
				JButton selectBtn = new JButton("Select");
				selectBtn.setBounds(15, 90, 150, 20);
				selectBtn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						int x = ((Integer) xField.getValue()).intValue();
						int y = ((Integer) yField.getValue()).intValue();
						editor.getWorldEditor().setMode(frame2, new PointWorldEditorMode(new Point(x, y), editor.getMap(), new WorldEditorModeListener<Point>() {
							
							@Override
							public void valueReceived(Point object) {
								xField.setValue(object.x);
								yField.setValue(object.y);
							};
							
							@Override
							public void canceled() {
							}
						}));
						super.mouseClicked(e);
					}
				});
				frame2.getContentPane().add(selectBtn);
				
				JButton confirmBtn = new JButton("Confirm");
				confirmBtn.setBounds(200, 90, 150, 20);
				confirmBtn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						frame.setVisible(false);
						
						int x = ((Integer) xField.getValue()).intValue();
						int y = ((Integer) yField.getValue()).intValue();
						field.setText(json.toJson(new Point(x, y)));
						super.mouseClicked(e);
					}
				});
				frame2.getContentPane().add(confirmBtn);
				
				//Set value if text field json is good
				if (isJsonValid()) {
					Point point = json.fromJson(Point.class, field.getText());
					xField.setValue(point.x);
					yField.setValue(point.y);
				}
			}
		});
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
	public String getValueAsString() {
		return field.getText();
	}
	
	protected boolean isJsonValid() {
		return this.isJsonValid(field.getText());
	}
	
	protected boolean isJsonValid(String jsonText) {
		try {
			json.fromJson(Point.class, jsonText);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
