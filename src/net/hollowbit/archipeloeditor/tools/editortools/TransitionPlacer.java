package net.hollowbit.archipeloeditor.tools.editortools;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.changes.MapChange;
import net.hollowbit.archipeloeditor.world.MapTile;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditor;
import net.hollowbit.archipeloshared.ChunkData;

public class TransitionPlacer extends Tool {
	

	MapChange change;
	
	JCheckBox cleanupCheckBox;
	
	public TransitionPlacer(MainEditor editor, WorldEditor worldRenderer) {
		super(editor, worldRenderer);
	}

	@Override
	public void addComponents(JPanel panel) {
		cleanupCheckBox = new JCheckBox("Clean up?");

		GridBagConstraints gbc_cleanupCheckBox = new GridBagConstraints();
		gbc_cleanupCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_cleanupCheckBox.gridx = 0;
		gbc_cleanupCheckBox.gridy = 1;
		panel.add(cleanupCheckBox, gbc_cleanupCheckBox);
	}

	@Override
	public void render(SpriteBatch batch) {}

	@Override
	public void touchDown(float x, float y, int tileX, int tileY, int button) {
		if (button == Buttons.LEFT && !Gdx.input.isKeyPressed(Keys.SPACE)) {
			change = new MapChange(editor.getMap());
			int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
			int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
			change.addChunk(editor.getMap().getChunk(chunkX, chunkY));
			
			editor.getMapChangeList().addChanges(change);
			editor.setJustSaved(false);
			generateTransitions(tileX, tileY);
		}
	}

	@Override
	public void touchUp(float x, float y, int tileX, int tileY, int button) {
	}

	@Override
	public void touchDragged(float x, float y, int tileX, int tileY) {
	}

	@Override
	public void mouseScrolled(int amount) {
	}
	
	private void generateTransitions(int tileX, int tileY) {
		boolean[][] processedTiles = new boolean[ChunkData.SIZE][ChunkData.SIZE];
		
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
		
		int xWithinChunk = Math.abs(tileX) % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - xWithinChunk;
		int yWithinChunk = Math.abs(tileY) % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - yWithinChunk;
		
		MapTile targetTile = editor.getAssetManager().getTileByID(editor.getMap().getTile(tileX, tileY));
		
		if (targetTile != null) {
			String target = null;
			if (!targetTile.transitionInner.equals(""))
				target = targetTile.transitionInner;
			else
				target = targetTile.id;
			search(target, processedTiles, chunkX, chunkY, xWithinChunk, yWithinChunk, tileX, tileY);
		}
	}
	
	//Recursion search and edit algorithm
	private void search(String target, boolean[][] processedTiles, int chunkX, int chunkY, int xWithinChunk, int yWithinChunk, int tileX, int tileY){
		if(xWithinChunk >= ChunkData.SIZE) return;
		if(yWithinChunk >= ChunkData.SIZE) return;
		if(xWithinChunk < 0) return;
		if(yWithinChunk < 0) return;
		
		if(processedTiles[yWithinChunk][xWithinChunk])
			return;
		
		String tileId = editor.getMap().getTile(chunkX, chunkY, xWithinChunk, yWithinChunk);
		if(tileId == null) {
			if (target != null)
				return;
		} else {
			if (target == null)
				return;
			
			if(!tileId.equals(target)) {
				MapTile tile = editor.getAssetManager().getTileByID(tileId);
				if (tile == null || tile.transitionInner == null || !tile.transitionInner.equals(target)) {
					return;
				}
			}
		}
		
		processedTiles[yWithinChunk][xWithinChunk] = true;
		
		processTile(target, tileX, tileY);
		
		search(target, processedTiles, chunkX, chunkY, xWithinChunk + 1, yWithinChunk, tileX + 1, tileY);
		search(target, processedTiles, chunkX, chunkY, xWithinChunk - 1, yWithinChunk, tileX - 1, tileY);
		search(target, processedTiles, chunkX, chunkY, xWithinChunk, yWithinChunk + 1, tileX, tileY + 1);
		search(target, processedTiles, chunkX, chunkY, xWithinChunk, yWithinChunk - 1, tileX, tileY - 1);
	}
	
	private void processTile(String inner, int tileX, int tileY) {
		MapTile tile = editor.getAssetManager().getTileByID(editor.getMap().getTile(tileX, tileY + 1));
		boolean north = tile == null || tile.id.equals(inner) || tile.transitionInner.equals(inner);
		tile = editor.getAssetManager().getTileByID(editor.getMap().getTile(tileX - 1, tileY));
		boolean west = tile == null || tile.id.equals(inner) || tile.transitionInner.equals(inner);
		tile = editor.getAssetManager().getTileByID(editor.getMap().getTile(tileX, tileY - 1));
		boolean south = tile == null || tile.id.equals(inner) || tile.transitionInner.equals(inner);
		tile = editor.getAssetManager().getTileByID(editor.getMap().getTile(tileX + 1, tileY));
		boolean east = tile == null || tile.id.equals(inner) || tile.transitionInner.equals(inner);
		tile = editor.getAssetManager().getTileByID(editor.getMap().getTile(tileX + 1, tileY + 1));
		boolean northEast = tile == null || tile.id.equals(inner) || tile.transitionInner.equals(inner);
		tile = editor.getAssetManager().getTileByID(editor.getMap().getTile(tileX - 1, tileY + 1));
		boolean northWest = tile == null || tile.id.equals(inner) || tile.transitionInner.equals(inner);
		tile = editor.getAssetManager().getTileByID(editor.getMap().getTile(tileX + 1, tileY - 1));
		boolean southEast = tile == null || tile.id.equals(inner) || tile.transitionInner.equals(inner);
		tile = editor.getAssetManager().getTileByID(editor.getMap().getTile(tileX - 1, tileY - 1));
		boolean southWest = tile == null || tile.id.equals(inner) || tile.transitionInner.equals(inner);
		
		boolean placed = false;
		
		boolean cleanup = cleanupCheckBox.isSelected();
		
		//Sides
		if (north && south) {
			if (west) {
				placed = placeEast(inner, tileX, tileY, cleanup);
			} else if (east) {
				placed = placeWest(inner, tileX, tileY, cleanup);
			}
		} else if (east && west) {
			if (north) {
				placed = placeSouth(inner, tileX, tileY, cleanup);
			} else if (south) {
				placed = placeNorth(inner, tileX, tileY, cleanup);
			}
		}
		
		//Inners
		if (!placed && north && west && !northWest)
			placed = placeNorthWestInner(inner, tileX, tileY, cleanup);
		
		if (!placed && north && east && !northEast)
			placed = placeNorthEastInner(inner, tileX, tileY, cleanup);
		
		if (!placed && south && west && !southWest)
			placed = placeSouthWestInner(inner, tileX, tileY, cleanup);
		
		if (!placed && south && east && !southEast)
			placed = placeSouthEastInner(inner, tileX, tileY, cleanup);
			
		//Outers
		if (!placed && north && west && northWest)
			placed = placeSouthEastOuter(inner, tileX, tileY, cleanup);
		
		if (!placed && north && east &&northEast)
				placed = placeSouthWestOuter(inner, tileX, tileY, cleanup);
		
		if (!placed && south && west && southWest)
				placed = placeNorthEastOuter(inner, tileX, tileY, cleanup);
		
		if (!placed && south && east && southEast)
				placed = placeNorthWestOuter(inner, tileX, tileY, cleanup);
		
		if (!placed) {
			if (cleanup)
				cleanup(inner, tileX, tileY);
		}
	}
	
	private void cleanup(String inner, int tileX, int tileY) {
		HashMap<String, Integer> countTracker = new HashMap<String, Integer>();
		for (int r = -1; r <= 1; r++) {
			for (int c = -1; c <= 1; c++) {
				MapTile tile = editor.getAssetManager().getTileByID(editor.getMap().getTile(tileX + c, tileY + r));
				
				if (tile != null) {
					String tileId = null;
					
					if (!tile.transitionInner.equals(""))
						tileId = tile.transitionInner;
					else
						tileId = tile.id;
					
					Integer count = countTracker.get(tileId);
					if (count == null)
						count = new Integer(0);
					
					countTracker.put(tileId, count.intValue() + 1);
				}
			}
		}
		
		int highest = 0;
		String highestId = null;
		for (Entry<String, Integer> count : countTracker.entrySet()) {
			if (count.getValue().intValue() > highest) {
				highest = count.getValue().intValue();
				highestId = count.getKey();
			}
		}
		
		//Once highest found, set tile
		editor.getMap().setTile(tileX, tileY, highestId);
	}
	
	private boolean placeEast(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX + 1, tileY);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 3);
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}
	
	private boolean placeWest(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX - 1, tileY);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 0);
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}
	
	private boolean placeNorth(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX, tileY + 1);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 1);
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}
	
	private boolean placeSouth(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX, tileY - 1);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 2);
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}
	
	private boolean placeNorthEastOuter(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX, tileY + 1);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 4);
		
		if (tile == null) {
			outer = editor.getMap().getTile(tileX + 1, tileY);
			tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 4);
		}
		
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}
	
	private boolean placeNorthWestOuter(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX, tileY + 1);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 5);
		
		if (tile == null) {
			outer = editor.getMap().getTile(tileX - 1, tileY);
			tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 5);
		}
		
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}
	
	private boolean placeSouthEastOuter(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX, tileY -1);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 6);
		
		if (tile == null) {
			outer = editor.getMap().getTile(tileX + 1, tileY);
			tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 6);
		}
		
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}
	
	private boolean placeSouthWestOuter(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX, tileY - 1);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 7);
		
		if (tile == null) {
			outer = editor.getMap().getTile(tileX - 1, tileY);
			tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 7);
		}
		
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}
	
	private boolean placeNorthEastInner(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX + 1, tileY + 1);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 11);
		
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}
	
	private boolean placeNorthWestInner(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX - 1, tileY + 1);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 10);
		
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}
	
	private boolean placeSouthEastInner(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX + 1, tileY - 1);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 9);
		
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}
	
	private boolean placeSouthWestInner(String inner, int tileX, int tileY, boolean cleanup) {
		String outer = editor.getMap().getTile(tileX - 1, tileY - 1);
		MapTile tile = editor.getAssetManager().getTransitionTile(inner, outer, (byte) 8);
		
		if (tile != null) {
			if (!cleanup)
				editor.getMap().setTile(tileX, tileY, tile.id);
			return true;
		}
		return false;
	}

}
