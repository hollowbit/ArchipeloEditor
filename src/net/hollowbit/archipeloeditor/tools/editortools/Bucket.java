package net.hollowbit.archipeloeditor.tools.editortools;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JCheckBox;
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
import net.hollowbit.archipeloeditor.changes.MapChange;
import net.hollowbit.archipeloeditor.world.AssetManager;
import net.hollowbit.archipeloeditor.world.MapElement;
import net.hollowbit.archipeloeditor.world.MapTile;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditor;
import net.hollowbit.archipeloshared.ChunkData;

public class Bucket extends Tool {
	
	protected int selectedLayer = 0;
	
	//Components
	JCheckBox checkBoxTilesVisible;
	JCheckBox checkBoxElementsVisible;
	JLabel lblListTitle;
	JTextField textFieldSearch;
	JList<Object> list;
	JLabel lblTileName;
	
	Icon iconHoveredOver = null;
	
	MapChange change;
	
	public Bucket(MainEditor editor, WorldEditor worldRenderer) {
		super(editor, worldRenderer);
	}

	@Override
	public void render(SpriteBatch batch) {
		
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
				change = new MapChange(editor.getMap());
				int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
				int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
				change.addChunk(editor.getMap().getChunk(chunkX, chunkY));
				
				editor.getChangeList().addChanges(change);
				editor.setJustSaved(false);
				startFill(tileX, tileY);
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
			
			startFill(tileX, tileY);
		}
	}
	
	protected void startFill(int tileX, int tileY) {
		boolean[][] filledTiles = new boolean[ChunkData.SIZE][ChunkData.SIZE];
		
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
		
		int xWithinChunk = Math.abs(tileX) % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - xWithinChunk;
		int yWithinChunk = Math.abs(tileY) % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - yWithinChunk;
		
		if (selectedLayer == MainEditor.TILE_LAYER) {
			String replaceTile = editor.getMap().getTile(tileX, tileY);
			bucketFillTiles(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk);
		} else if (selectedLayer == MainEditor.ELEMENT_LAYER) {
			String replaceTile = editor.getMap().getElement(tileX, tileY);
			bucketFillElements(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk);
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
		checkBoxTilesVisible.setSelected(true);
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
					reloadLists();
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
		
		checkBoxElementsVisible.setSelected(true);
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
					reloadLists();
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
				reloadLists();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				reloadLists();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				reloadLists();
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
		
		lblListTitle = new JLabel("Tiles:  ");
		GridBagConstraints gbc_lblListTitle = new GridBagConstraints();
		gbc_lblListTitle.weightx = 40;
		gbc_lblListTitle.anchor = GridBagConstraints.WEST;
		gbc_lblListTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblListTitle.gridx = 0;
		gbc_lblListTitle.gridy = 8;
		panel.add(lblListTitle, gbc_lblListTitle);
		
		lblTileName = new JLabel("");
		lblTileName.setPreferredSize(new Dimension(194, 15));
		GridBagConstraints gbc_lblTileName = new GridBagConstraints();
		gbc_lblTileName.anchor = GridBagConstraints.WEST;
		gbc_lblTileName.fill = GridBagConstraints.NONE;
		gbc_lblTileName.gridwidth = 4;
		gbc_lblTileName.insets = new Insets(0, 0, 5, 5);
		gbc_lblTileName.gridx = 1;
		gbc_lblTileName.gridy = 8;
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
		gbc_list.gridy = 9;
		panel.add(list, gbc_list);
	}
	
	//Reloads the lists depending on the selected layer
	public void reloadLists () {
		ArrayList<Icon> iconList = new ArrayList<Icon>();
		switch (selectedLayer) {
		case 0:
			for (MapTile tile : editor.getAssetManager().getMapTiles()){
				if(tile.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
					iconList.add(tile);
			}
			break;
		case 1:
			for (MapElement element : editor.getAssetManager().getMapElements()){
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

	//Recursion bucket fill algorithm for tiles
	public void bucketFillTiles(String replaceTile, boolean[][] filledTiles, int chunkX, int chunkY, int xWithinChunk, int yWithinChunk){
		if(xWithinChunk >= ChunkData.SIZE) return;
		if(yWithinChunk >= ChunkData.SIZE) return;
		if(xWithinChunk < 0) return;
		if(yWithinChunk < 0) return;
		
		if(filledTiles[yWithinChunk][xWithinChunk]) return;
		if(editor.getMap().getTile(chunkX, chunkY, xWithinChunk, yWithinChunk) == null) {
			if (replaceTile != null)
				return;
		} else {
			if (replaceTile == null)
				return;
			
			if(!editor.getMap().getTile(chunkX, chunkY, xWithinChunk, yWithinChunk).equals(replaceTile))
				return;
		}
		
		filledTiles[yWithinChunk][xWithinChunk] = true;
		
		editor.getMap().setTile(chunkX, chunkY, xWithinChunk, yWithinChunk, ((MapTile) list.getSelectedValue()).id);
		bucketFillTiles(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk + 1, yWithinChunk);
		bucketFillTiles(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk - 1, yWithinChunk);
		bucketFillTiles(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk + 1);
		bucketFillTiles(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk - 1);
	}

	//Recursion bucket fill algorithm for elements
	public void bucketFillElements(String replaceTile, boolean[][] filledTiles, int chunkX, int chunkY, int xWithinChunk, int yWithinChunk){
		if(xWithinChunk >= ChunkData.SIZE) return;
		if(yWithinChunk >= ChunkData.SIZE) return;
		if(xWithinChunk < 0) return;
		if(yWithinChunk < 0) return;
		
		if(filledTiles[yWithinChunk][xWithinChunk]) return;
		if(editor.getMap().getElement(chunkX, chunkY, xWithinChunk, yWithinChunk) == null) {
			if (replaceTile != null)
				return;
		} else {
			if (replaceTile == null)
				return;
			
			if(!editor.getMap().getElement(chunkX, chunkY, xWithinChunk, yWithinChunk).equals(replaceTile))
				return;
		}		filledTiles[yWithinChunk][xWithinChunk] = true;
		
		editor.getMap().setElement(chunkX, chunkY, xWithinChunk, yWithinChunk, ((MapElement) list.getSelectedValue()).id);
		bucketFillElements(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk + 1, yWithinChunk);
		bucketFillElements(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk - 1, yWithinChunk);
		bucketFillElements(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk + 1);
		bucketFillElements(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk - 1);
	}
	
	@Override
	public void reload(AssetManager assetManager) {
		reloadLists();
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
