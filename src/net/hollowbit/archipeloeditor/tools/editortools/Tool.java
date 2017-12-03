package net.hollowbit.archipeloeditor.tools.editortools;

import javax.swing.JPanel;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.changes.ChangeList;
import net.hollowbit.archipeloeditor.world.AssetManager;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditor;

public abstract class Tool {
	
	protected MainEditor editor;
	protected WorldEditor worldRenderer;
	
	public Tool(MainEditor editor, WorldEditor worldRenderer) {
		this.editor = editor;
		this.worldRenderer = worldRenderer;
	}
	
	public void reload(AssetManager assetManager){}
	
	public abstract void addComponents(JPanel panel);
	public abstract void render(SpriteBatch batch);
	public void updateVisibilities(boolean tilesVisible, boolean elementsVisible, boolean gridVisible, boolean collisionMapVisible){};
	
	public abstract void touchDown(float x, float y, int tileX, int tileY, int button);
	public abstract void touchUp(float x, float y, int tileX, int tileY, int button);
	public abstract void touchDragged(float x, float y, int tileX, int tileY);
	public abstract void mouseScrolled(int amount);
	
	public Object getSelectedItem() {
		return null;
	}
	
	public int getSelectedLayer() {
		return -1;
	}
	
	public ChangeList getChangeList() {
		return editor.getMapChangeList();
	}
	
}
