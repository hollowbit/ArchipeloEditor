package net.hollowbit.archipeloeditor.tools.editortools;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.changes.MapChange;
import net.hollowbit.archipeloeditor.world.MapElement;
import net.hollowbit.archipeloeditor.world.MapTile;
import net.hollowbit.archipeloeditor.world.worldrenderer.WorldRenderer;

public class Pencil extends Tool {

	protected int lastX, lastY;
	
	public Pencil(MainEditor editor, WorldRenderer worldRenderer) {
		super(editor, worldRenderer);
	}
	
	public void plot(int tileX, int tileY) {
		if (editor.getSelectedLayer() == MainEditor.TILE_LAYER)
    		editor.getMap().setTile(tileX, tileY, ((MapTile) editor.getSelectedItemValue()).id);
    	else
    		editor.getMap().setElement(tileX, tileY, ((MapElement) editor.getSelectedItemValue()).id);
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
		if (editor.getSelectedItemValue() == null)
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
	public void touchDown(float x, float y, int tileX, int tileY) {
		if(editor.getSelectedItemValue() != null) {
			editor.getChangeList().addChanges(new MapChange(editor.getMap()));
			editor.setJustSaved(false);
			
			if (worldRenderer.shiftPressed())
				drawLine(lastX, lastY, tileX, tileY);
			else
				plot(tileX, tileY);
				
			lastX = tileX;
			lastY = tileY;
		}
	}

	@Override
	public void touchUp(float x, float y, int tileX, int tileY) {}

	@Override
	public void touchDragged(float x, float y, int tileX, int tileY) {
		if(editor.getSelectedItemValue() != null) {
			drawLine(lastX, lastY, tileX, tileY);
			lastX = tileX;
			lastY = tileY;
		}
	}

}
