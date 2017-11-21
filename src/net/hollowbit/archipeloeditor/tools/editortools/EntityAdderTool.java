package net.hollowbit.archipeloeditor.tools.editortools;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.entity.EntityType;
import net.hollowbit.archipeloeditor.tools.propertydefiners.entitydefiner.EntityDefinerWindow;
import net.hollowbit.archipeloeditor.tools.propertydefiners.entitydefiner.EntityDefinerWindow.EntityDefinerListener;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.Point;

public class EntityAdderTool extends Tool {
	
	JComboBox<EntityType> entityType;
	JPanel panel;
	boolean componentsAdded = false;
	
	private JTextArea jsonEditingArea;
	private JButton removeBtn;
	private JButton addUpdateBtn;
	private JButton editBtn;
	
	private boolean upToDate = false;
	private String updatedJson;
	
	private Json json;
	
	public EntityAdderTool(MainEditor editor, WorldEditor worldRenderer) {
		super(editor, worldRenderer);
		json = new Json();
		
		json.setOutputType(OutputType.javascript);
	}

	@Override
	public void addComponents(JPanel panel) {
		this.panel = panel;
	}

	@Override
	public void render(SpriteBatch batch) {
		
	}

	@Override
	public void touchDown(float x, float y, int tileX, int tileY, int button) {}

	@Override
	public void touchUp(float x, float y, int tileX, int tileY, int button) {
		if (editor.getMap() == null || tileX >= editor.getMap().getMaxTileX() || tileY >= editor.getMap().getMaxTileY() || tileX < editor.getMap().getMinTileX() || tileY < editor.getMap().getMinTileY())
			return;
		
		ArrayList<EntitySnapshot> entitiesTouched = new ArrayList<EntitySnapshot>();
		if (entitiesTouched.isEmpty()) {//Add new entity
			EntitySnapshot newSnapshot = new EntitySnapshot();
			newSnapshot.name = "entity-name";
			newSnapshot.type = EntityType.WIZARD.getId();
			newSnapshot.putObject("pos", new Point(Math.round(x), Math.round(y)));
			initializeEditingComponents(newSnapshot, x, y, true);
		} else {
			initializeEditingComponents(entitiesTouched.get(0), x, y, false);
		}
	}
	
	private void initializeEditingComponents(EntitySnapshot entity, float x, float y, boolean isNew) {
		if (!componentsAdded) {
			removeBtn = new JButton("Remove");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 0, 5, 5);
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 4;
			panel.add(removeBtn, gbc);
			
			removeBtn.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
						removeBtn.setEnabled(false);
						refreshJsonField();
						editor.getMap().removeEntity(json.fromJson(EntitySnapshot.class, jsonEditingArea.getText()).name);
					super.mouseClicked(e);
				}
			});
			
			addUpdateBtn = new JButton("Add");
			gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 0, 5, 5);
			gbc.gridx = 5;
			gbc.gridy = 3;
			gbc.gridwidth = 2;
			panel.add(addUpdateBtn, gbc);
			
			addUpdateBtn.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (isJsonValid()) {
						//TODO validate type and name of entity before adding
						removeBtn.setEnabled(true);
						updatedJson = jsonEditingArea.getText();
						refreshJsonField();
						editor.getMap().addUpdateEntity(json.fromJson(EntitySnapshot.class, jsonEditingArea.getText()));
					}
					super.mouseClicked(e);
				}
			});
			
			
			jsonEditingArea = new JTextArea(50, 30);
			jsonEditingArea.setLineWrap(true);
			jsonEditingArea.setTabSize(1);
			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.gridx = 0;
			gbc.gridy = 4;
			gbc.gridwidth = 8;
			gbc.gridheight = 10;
			gbc.fill = GridBagConstraints.BOTH;
			panel.add(jsonEditingArea, gbc);
			
			jsonEditingArea.getDocument().addDocumentListener(new DocumentListener() {
				
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
			
			editBtn = new JButton("Edit");
			gbc = new GridBagConstraints();
			gbc.insets = new Insets(10, 10, 10, 10);
			gbc.gridx = 6;
			gbc.gridy = 25;
			gbc.gridwidth = 2;
			panel.add(editBtn, gbc);
			
			editBtn.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					EntitySnapshot snapshot = json.fromJson(EntitySnapshot.class, jsonEditingArea.getText());
					Point pos = snapshot.getObject("pos", new Point(Math.round(x), Math.round(y)), Point.class);
					new EntityDefinerWindow(jsonEditingArea.getText(), pos, editor.getMainWindow(), editor, new EntityDefinerListener() {
						
						@Override
						public void complete(EntitySnapshot snapshot) {
							jsonEditingArea.setText(json.prettyPrint(snapshot));
						}
					});
					
					super.mouseClicked(e);
				}
			});
			
			componentsAdded = true;
		}
		
		jsonEditingArea.setText(json.prettyPrint(entity));
		removeBtn.setEnabled(false);
		updatedJson = jsonEditingArea.getText();
		refreshJsonField();
	}
	
	private void refreshJsonField() {
		upToDate = jsonEditingArea.getText().equals(updatedJson);
		
		boolean isJsonValid = isJsonValid();

		addUpdateBtn.setEnabled(!upToDate || isAddMode());
		
		if (isAddMode())
			addUpdateBtn.setText("Add");
		else
			addUpdateBtn.setText("Update");
		
		if (isJsonValid) {
			jsonEditingArea.setBackground(Color.WHITE);
			editBtn.setEnabled(true);
		} else {
			jsonEditingArea.setBackground(Color.RED);
			editBtn.setEnabled(false);
			addUpdateBtn.setEnabled(false);
		}
	}
	
	private boolean isAddMode() {
		return !removeBtn.isEnabled();
	}
	
	private boolean isJsonValid() {
		try {
			EntitySnapshot snapshot = json.fromJson(EntitySnapshot.class, jsonEditingArea.getText());
			return snapshot != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void touchDragged(float x, float y, int tileX, int tileY) {}

	@Override
	public void mouseScrolled(int amount) {}

}
