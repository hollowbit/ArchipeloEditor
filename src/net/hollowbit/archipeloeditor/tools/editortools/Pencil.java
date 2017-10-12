package net.hollowbit.archipeloeditor.tools.editortools;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.WorldEditor;
import net.hollowbit.archipeloeditor.changes.MapChange;
import net.hollowbit.archipeloeditor.world.AssetManager;
import net.hollowbit.archipeloeditor.world.MapElement;
import net.hollowbit.archipeloeditor.world.MapTile;
import net.hollowbit.archipeloshared.ChunkData;

public class Pencil extends Tool {

	protected int lastX, lastY;
	protected int selectedLayer = 0;
	
	//Components
	JCheckBox checkBoxTilesVisible;
	JCheckBox checkBoxElementsVisible;
	JLabel lblListTitle;
	JTextField textFieldSearch;
	JList<Object> list;
	JLabel lblTileName;
	JComboBox<String> tilesetComboBox;
	
	Icon iconHoveredOver = null;
	
	MapChange change;
	
	public Pencil(MainEditor editor, WorldEditor worldRenderer) {
		super(editor, worldRenderer);
	}

	@Override
	public void render(SpriteBatch batch) {
		
	}
	
	
	public void plot(int tileX, int tileY) {
		if (selectedLayer == MainEditor.TILE_LAYER)
    		editor.getMap().setTile(tileX, tileY, ((MapTile) list.getSelectedValue()).id);
    	else
    		editor.getMap().setElement(tileX, tileY, ((MapElement) list.getSelectedValue()).id);
	}
	
	/**
	 * Draws a line using the selected element or tile given the limits of the line.
	 * The line is 1 pixel wide.
	 * Will simply not do anything if there is no selected tile or element.
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void drawLine(int x1, int y1, int x2, int y2) {
		if (list.getSelectedValue() == null)
			return;
		
		if (x1 == x2 && y1 == y2) {
			plot(x1, y1);
			return;
		}
		
		int signumY = (int) Math.signum(y2 - y1);
		int deltaX = Math.abs(x2 - x1) + 1;
		
		if (deltaX == 1) {
			if (y1 < y2) {
				for (int y = y1; y < y2; y++)
					plot(x1, y);
			} else {
				for (int y = y2; y < y1; y++)
					plot(x1, y);
			}
			return;
		}
		
		int deltaY = Math.abs(y2 - y1) + 1;
		
		if (deltaY == 1) {
			if (x1 < x2) {
				for (int x = x1; x <= x2; x++)
					plot(x, y1);
			} else {
				for (int x = x2; x <= x1; x++)
					plot(x, y1);
			}
			return;
		}
		
		float deltaError = Math.abs((float) deltaY / deltaX);
		
		float error = 0;
		int y = y1;
		boolean ySatisfied = false;
		
		if (x1 < x2) {
			for (int x = x1; x <= x2; x++) {
				//Plot point
				plot(x, y);
				error += deltaError;
				
				while(error >= 1) {
					//Plot point
					plot(x, y);
					
					//Update error
					y += signumY;
					error -= 1;
					
					//Determine if y has reached its end
					if (signumY > 0 ? y > y2 : y < y2) {
						ySatisfied = true;
						break;
					}
				}
				
				if (ySatisfied)
					break;
			}
		} else {
			for (int x = x1; x >= x2; x--) {
				//Plot point
				plot(x, y);
				error += deltaError;
				
				while(error >= 1) {
					//Plot point
					plot(x, y);
					
					//Update error
					y += signumY;
					error -= 1;
					
					//Determine if y has reached its end
					if (signumY > 0 ? y > y2 : y < y2) {
						ySatisfied = true;
						break;
					}
				}
				
				if (ySatisfied)
					break;
			}
		}
	}

	@Override
	public void touchDown(float x, float y, int tileX, int tileY, int button) {
		if (tileX >= editor.getMap().getMaxTileX() || tileY >= editor.getMap().getMaxTileY() || tileX < editor.getMap().getMinTileX() || tileY < editor.getMap().getMinTileY())
			return;
		
		if (button == Buttons.RIGHT) {
			switch (selectedLayer) {
			case MainEditor.TILE_LAYER:
				list.setSelectedValue(editor.getAssetManager().getTileByID(editor.getMap().getTile(tileX, tileY)), true);
				break;
			case MainEditor.ELEMENT_LAYER:
				list.setSelectedValue(editor.getAssetManager().getElementByID(editor.getMap().getElement(tileX, tileY)), true);
				break;
			}
		} else if (button == Buttons.LEFT && !Gdx.input.isKeyPressed(Keys.SPACE)) {
			if(list.getSelectedValue() != null) {
				int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
				int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
				change = new MapChange(editor.getMap());
				change.addChunk(editor.getMap().getChunk(chunkX, chunkY));
				editor.getChangeList().addChanges(change);
				editor.setJustSaved(false);
				
				if (worldRenderer.shiftPressed())
					drawLine(lastX, lastY, tileX, tileY);
				else
					plot(tileX, tileY);
					
				lastX = tileX;
				lastY = tileY;
			}
		}
	}

	@Override
	public void touchUp(float x, float y, int tileX, int tileY, int button) {
		change = null;
	}

	@Override
	public void touchDragged(float x, float y, int tileX, int tileY) {
		if (tileX >= editor.getMap().getMaxTileX() || tileY >= editor.getMap().getMaxTileY() || tileX < editor.getMap().getMinTileX() || tileY < editor.getMap().getMinTileY())
			return;
		
		if(list.getSelectedValue() != null) {
			int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
			int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
			change.addChunk(editor.getMap().getChunk(chunkX, chunkY));
			
			drawLine(lastX, lastY, tileX, tileY);
			lastX = tileX;
			lastY = tileY;
		}
	}

	@Override
	public void mouseScrolled(int amount) {
		if (list.getSelectedValue() != null)
			list.setSelectedIndex(list.getSelectedIndex() + amount);
		else {
			list.setSelectedIndex(0);
			list.setSelectedIndex(list.getSelectedIndex() + amount);
		}
	}

	@Override
	public void addComponents(JPanel panel) {
		JLabel lblVisibility = new JLabel("Visibility:");
		GridBagConstraints gbc_lblVisibility = new GridBagConstraints();
		gbc_lblVisibility.insets = new Insets(0, 0, 5, 5);
		gbc_lblVisibility.gridx = 0;
		gbc_lblVisibility.gridy = 3;
		panel.add(lblVisibility, gbc_lblVisibility);
		
		JLabel lblEditingLayer = new JLabel("Selected Layer:");
		GridBagConstraints gbc_lblEditingLayer = new GridBagConstraints();
		gbc_lblEditingLayer.anchor = GridBagConstraints.WEST;
		gbc_lblEditingLayer.insets = new Insets(0, 0, 5, 5);
		gbc_lblEditingLayer.gridx = 1;
		gbc_lblEditingLayer.gridy = 3;
		panel.add(lblEditingLayer, gbc_lblEditingLayer);
		
		checkBoxTilesVisible = new JCheckBox("");
		checkBoxTilesVisible.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				editor.setShowTiles(checkBoxTilesVisible.isSelected());
			}
		});
		checkBoxTilesVisible.setSelected(editor.showTiles());
		GridBagConstraints gbc_checkBoxTilesVisible = new GridBagConstraints();
		gbc_checkBoxTilesVisible.insets = new Insets(0, 0, 5, 5);
		gbc_checkBoxTilesVisible.gridx = 0;
		gbc_checkBoxTilesVisible.gridy = 4;
		panel.add(checkBoxTilesVisible, gbc_checkBoxTilesVisible);
		
		final JRadioButton rdbtnElements = new JRadioButton("Elements");
		
		final JRadioButton rdbtnTiles = new JRadioButton("Tiles");
		rdbtnTiles.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (selectedLayer != 0) {
					selectedLayer = 0;
					lblListTitle.setText("Tiles:");
					rdbtnElements.setSelected(false);
					rdbtnTiles.setSelected(true);
					reload(editor.getAssetManager());
				}
			}
		});
		
		rdbtnTiles.setSelected(true);
		GridBagConstraints gbc_rdbtnTiles = new GridBagConstraints();
		gbc_rdbtnTiles.gridwidth = 4;
		gbc_rdbtnTiles.anchor = GridBagConstraints.WEST;
		gbc_rdbtnTiles.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnTiles.gridx = 1;
		gbc_rdbtnTiles.gridy = 4;
		panel.add(rdbtnTiles, gbc_rdbtnTiles);
		
		checkBoxElementsVisible = new JCheckBox("");
		checkBoxElementsVisible.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				editor.setShowElements(checkBoxElementsVisible.isSelected());
			}
		});
		
		checkBoxElementsVisible.setSelected(editor.showMapElements());
		GridBagConstraints gbc_checkBoxElementsVisible = new GridBagConstraints();
		gbc_checkBoxElementsVisible.insets = new Insets(0, 0, 5, 5);
		gbc_checkBoxElementsVisible.gridx = 0;
		gbc_checkBoxElementsVisible.gridy = 5;
		panel.add(checkBoxElementsVisible, gbc_checkBoxElementsVisible);
		
		
		GridBagConstraints gbc_rdbtnElements = new GridBagConstraints();
		gbc_rdbtnElements.gridwidth = 4;
		gbc_rdbtnElements.anchor = GridBagConstraints.WEST;
		gbc_rdbtnElements.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnElements.gridx = 1;
		gbc_rdbtnElements.gridy = 5;
		rdbtnElements.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (selectedLayer != 1) {
					selectedLayer = 1;
					lblListTitle.setText("Elements:");
					rdbtnTiles.setSelected(false);
					rdbtnElements.setSelected(true);
					reload(editor.getAssetManager());
				}
			}
			
		});
		panel.add(rdbtnElements, gbc_rdbtnElements);
		
		JLabel lblSearch = new JLabel("Search:");
		GridBagConstraints gbc_lblSearch = new GridBagConstraints();
		gbc_lblSearch.gridwidth = 5;
		gbc_lblSearch.anchor = GridBagConstraints.WEST;
		gbc_lblSearch.insets = new Insets(0, 0, 5, 0);
		gbc_lblSearch.gridx = 0;
		gbc_lblSearch.gridy = 6;
		panel.add(lblSearch, gbc_lblSearch);
		
		textFieldSearch = new JTextField();
		textFieldSearch.getDocument().addDocumentListener(new DocumentListener() {//If search field is changed, update list accordingly
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				reload(editor.getAssetManager());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				reload(editor.getAssetManager());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				reload(editor.getAssetManager());
			}
		});
		
		GridBagConstraints gbc_textFieldSearch = new GridBagConstraints();
		gbc_textFieldSearch.gridwidth = 5;
		gbc_textFieldSearch.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldSearch.gridx = 0;
		gbc_textFieldSearch.gridy = 7;
		panel.add(textFieldSearch, gbc_textFieldSearch);
		textFieldSearch.setColumns(10);
		
		tilesetComboBox = new JComboBox<String>();
		tilesetComboBox.setPreferredSize(new Dimension(300, 15));
		
		for (String category : editor.getAssetManager().getCategoryTiles().keySet())
			tilesetComboBox.addItem(category);
		tilesetComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tilesetComboBox.getSelectedItem() != null)
					reloadLists(tilesetComboBox.getSelectedItem().toString());
			}
			
		});
		
		GridBagConstraints gbc_tilesetComboBox = new GridBagConstraints();
		gbc_tilesetComboBox.gridwidth = 5;
		gbc_tilesetComboBox.weightx = 40;
		gbc_tilesetComboBox.anchor = GridBagConstraints.WEST;
		gbc_tilesetComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_tilesetComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_tilesetComboBox.gridx = 0;
		gbc_tilesetComboBox.gridy = 8;
		panel.add(tilesetComboBox, gbc_tilesetComboBox);
		
		lblListTitle = new JLabel("Tiles:  ");
		GridBagConstraints gbc_lblListTitle = new GridBagConstraints();
		gbc_lblListTitle.weightx = 40;
		gbc_lblListTitle.anchor = GridBagConstraints.WEST;
		gbc_lblListTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblListTitle.gridx = 0;
		gbc_lblListTitle.gridy = 9;
		panel.add(lblListTitle, gbc_lblListTitle);
		
		lblTileName = new JLabel("");
		lblTileName.setPreferredSize(new Dimension(194, 15));
		GridBagConstraints gbc_lblTileName = new GridBagConstraints();
		gbc_lblTileName.anchor = GridBagConstraints.WEST;
		gbc_lblTileName.fill = GridBagConstraints.NONE;
		gbc_lblTileName.gridwidth = 4;
		gbc_lblTileName.insets = new Insets(0, 0, 5, 5);
		gbc_lblTileName.gridx = 1;
		gbc_lblTileName.gridy = 9;
		panel.add(lblTileName, gbc_lblTileName);
		
		//Load items to list
		list = new JList<Object>(editor.getAssetManager().getMapTiles().toArray());
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent event) {
				if(list.getSelectedValue() != null){
					if(selectedLayer == 0)
						lblTileName.setText(((MapTile) list.getSelectedValue()).name);
					else if(selectedLayer == 1)
						lblTileName.setText(((MapElement) list.getSelectedValue()).name);
				}
			}
			
		});
		
		//Display tile/element info when right-clicked
		list.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					ListModel<Object> model = list.getModel();
					int index = list.locationToIndex(e.getPoint());
					if (index > -1) {
						list.setToolTipText(null);
						if (selectedLayer == 0) {
							MapTile tile = (MapTile) model.getElementAt(index);
							iconHoveredOver = tile;
							String text = "<html>"
						    		+ "ID: " + tile.id + "<br>"
						    		+ "Name: " + tile.name + "<br>"
						    		+ "Swimmable: " + tile.swimmable + "<br>"
						    		+ "Damage: " + tile.damage + "<br>"
						    		+ "Time(s) Between damage: " + tile.damageSpeed + "<br>"
						    		+ "Speed Multiplier: " + tile.speedMultiplier + "<br>";
							if (tile.animated) {
								text += "# of Animation Frames: " + tile.numberOfFrames + "<br>"
									    + "Time(s) Between Frames: " + tile.animationSpeed + "<br>";
							}
							text += "Collision Table:<br>";
							
							for(int i = 0; i < tile.collisionTable.length; i++){
								for(int u = 0; u < tile.collisionTable[0].length; u++){
									text += tile.collisionTable[i][u] ? 1 : 0;
								}
								text += "<br>";
							}
						    text += "</html>";
							list.setToolTipText(text);
						} else if(selectedLayer == 1) {
							MapElement element = (MapElement) model.getElementAt(index);
							iconHoveredOver = element;
							String text = "<html>"
						    		+ "ID: " + element.id + "<br>"
						    		+ "Name: " + element.name + "<br>"
						    		+ "Width: " + element.width + "<br>"
						    		+ "Height: " + element.height + "<br>"
						    		+ "OffsetX: " + element.offsetX + "<br>"
						    		+ "OffsetY: " + element.offsetY + "<br>";
							if (element.animated) {
								text += "# of Animation Frames: " + element.numberOfFrames + "<br>"
									    + "Time(s) Between Frames: " + element.animationSpeed + "<br>";
							}
							text += "Collision Table:<br>";
							
							for(int i = 0; i < element.height; i++){
								for(int u = 0; u < element.width; u++){
									text += element.collisionTable[i][u] ? 1 : 0;
								}
								text += "<br>";
							}
						    text += "</html>";
							list.setToolTipText(text);
						}
					    ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(list, 0, 0, 0, e.getX(), e.getY(), 0, false));
					    ToolTipManager.sharedInstance().setInitialDelay(0);
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		
		list.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {}

			@Override
			public void mouseMoved(MouseEvent e) {//Remove info if no longer hover on tile/element
				if (list.locationToIndex(e.getPoint()) != -1) {
					if (list.getModel().getElementAt(list.locationToIndex(e.getPoint())) != iconHoveredOver) {
						list.setToolTipText(null);
						iconHoveredOver = null;
					}
				}
			}
			
		});
		
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.gridheight = 7;
		gbc_list.gridwidth = 5;
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 10;
		panel.add(list, gbc_list);
	}
	
	//Reloads the lists depending on the selected layer
	public void reloadLists (String category) {
		ArrayList<Icon> iconList = new ArrayList<Icon>();
		switch (selectedLayer) {
		case 0:
			for (MapTile tile : editor.getAssetManager().getCategoryTiles().get(category)){
				if(tile.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
					iconList.add(tile);
			}
			break;
		case 1:
			for (MapElement element : editor.getAssetManager().getCategoryElements().get(category)){
				if (element.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
					iconList.add(element);
			}
			break;
		}

		lblTileName.setText("");
		list.clearSelection();
		list.setListData(iconList.toArray());
	}

	@Override
	public void updateVisibilities(boolean tilesVisible, boolean elementsVisible, boolean gridVisible, boolean collisionMapVisible) {
		if (checkBoxTilesVisible != null)
			checkBoxTilesVisible.setSelected(tilesVisible);
		if (checkBoxElementsVisible != null)
			checkBoxElementsVisible.setSelected(elementsVisible);
	}
	
	@Override
	public void reload(AssetManager assetManager) {
		tilesetComboBox.removeAllItems();
		if (selectedLayer == 0) {
			for (String category : editor.getAssetManager().getCategoryTiles().keySet())
				tilesetComboBox.addItem(category);
		} else if (selectedLayer == 1) {
			for (String category : editor.getAssetManager().getCategoryElements().keySet())
				tilesetComboBox.addItem(category);
		}
		
		reloadLists(tilesetComboBox.getSelectedItem().toString());
		
		super.reload(assetManager);
	}
	
	@Override
	public Object getSelectedItem() {
		return list.getSelectedValue();
	}
	
	@Override
	public int getSelectedLayer() {
		return selectedLayer;
	}

}
