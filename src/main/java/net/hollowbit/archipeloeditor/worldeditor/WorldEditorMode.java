package net.hollowbit.archipeloeditor.worldeditor;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.world.AssetManager;
import net.hollowbit.archipeloeditor.world.Map;

public abstract class WorldEditorMode<T> extends InputAdapter {
	
	protected Map map;
	protected WorldEditorModeListener<T> listener;
	protected WorldEditor worldEditor;
	protected String title;
	protected T object;
	
	public WorldEditorMode(String title, Map map, WorldEditorModeListener<T> listener) {
		this.title = title;
		this.map = map;
		this.listener = listener;
		this.object = null;
	}
	
	public WorldEditorMode(String title, T defaultObject, Map map, WorldEditorModeListener<T> listener) {
		this(title, map, listener);
		object = defaultObject;
	}
	
	public Map getMapToRender(MainEditor editor) {
		return map;
	}
	
	public void finishMode() {
		if (worldEditor != null)
			worldEditor.resetModeToDefault();
			
		if (listener != null)
			listener.valueReceived(object);
		
		this.listener = null;
		this.map = null;
	}
	
	public void cancelMode() {
		if (worldEditor != null)
			worldEditor.resetModeToDefault();
			
		if (listener != null)
			listener.canceled();
	}
	
	public void setWorldEditor(WorldEditor worldEditor) {
		this.worldEditor = worldEditor;
	}
	
	public void render(SpriteBatch batch, AssetManager assetManager) {
		
	}
	
	public String getTitle() {
		return title;
	}
	
	public interface WorldEditorModeListener<T> {
		
		public abstract void valueReceived(T object);
		public abstract void canceled();
		
	}
	
}
