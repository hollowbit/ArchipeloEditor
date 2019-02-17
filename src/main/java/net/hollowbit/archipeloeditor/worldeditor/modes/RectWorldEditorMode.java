package net.hollowbit.archipeloeditor.worldeditor.modes;

import net.hollowbit.archipeloeditor.world.Map;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditorMode;
import net.hollowbit.archipeloshared.Rectangle;

public class RectWorldEditorMode extends WorldEditorMode<Rectangle> {
	
	public RectWorldEditorMode(Rectangle defaultRect, Map map, WorldEditorMode.WorldEditorModeListener<Rectangle> listener) {
		super("Select a Rect on the map. Press enter to continue or ESC to cancel.", defaultRect, map, listener);
	}

}
