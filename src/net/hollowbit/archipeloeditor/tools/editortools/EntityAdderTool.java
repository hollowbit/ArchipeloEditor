package net.hollowbit.archipeloeditor.tools.editortools;

import net.hollowbit.archipeloeditor.EntityAdder;
import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.world.worldrenderer.WorldRenderer;

public class EntityAdderTool extends Tool {

	public EntityAdderTool(MainEditor editor, WorldRenderer worldRenderer) {
		super(editor, worldRenderer);
	}

	@Override
	public void touchDown(float x, float y, int tileX, int tileY) {}

	@Override
	public void touchUp(float x, float y, int tileX, int tileY) {
		if (!editor.isWindowOpen("entity-adder")) {
			editor.addOpenWindow("entity-adder");
			EntityAdder entityAdder = new EntityAdder(editor, x, y);
			entityAdder.setVisible(true);
		}
	}

	@Override
	public void touchDragged(float x, float y, int tileX, int tileY) {}

}
