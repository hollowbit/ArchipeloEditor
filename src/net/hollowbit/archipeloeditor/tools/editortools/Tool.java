package net.hollowbit.archipeloeditor.tools.editortools;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.world.worldrenderer.WorldRenderer;

public abstract class Tool {
	
	protected MainEditor editor;
	protected WorldRenderer worldRenderer;
	
	public Tool(MainEditor editor, WorldRenderer worldRenderer) {
		this.editor = editor;
		this.worldRenderer = worldRenderer;
	}
	
	public abstract void touchDown(float x, float y, int tileX, int tileY);
	public abstract void touchUp(float x, float y, int tileX, int tileY);
	public abstract void touchDragged(float x, float y, int tileX, int tileY);
	
}
