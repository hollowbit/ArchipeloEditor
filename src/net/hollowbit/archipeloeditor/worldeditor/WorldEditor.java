package net.hollowbit.archipeloeditor.worldeditor;

import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.world.AssetManager;
import net.hollowbit.archipeloeditor.world.Map;
import net.hollowbit.archipeloeditor.world.worldrenderer.WorldRenderer;

public class WorldEditor extends WorldRenderer {
	
	protected static final WorldEditorMode<Object> DEFAULT_MODE = new WorldEditorMode<Object>("Edit map...", null, null) {
		
		@Override
		public Map getMapToRender(MainEditor editor) {
			return editor.getMap();
		}
		
	};
	
	protected boolean assetsLoaded = false;
	
	protected int lastX, lastY;
	protected float redoTimer = 0;
	protected float undoTimer = 0;
	
	protected boolean renderMapToFile = false;
	
	protected WorldEditorMode<?> mode = DEFAULT_MODE;
	protected JFrame previousWindow;
	
	public WorldEditor(MainEditor editor, AssetManager assetManager) {
		super(editor, assetManager);
	}
	
	@Override
	public void create() {
		super.create();
		editor.getWorldEditorWindow().setTitle(mode.getTitle());
	}
	
	@Override
	public void render() {
		if (!assetsLoaded) {
			assetManager.clear();
			assetManager.load();
			assetsLoaded = true;
			if (editor.getSelectedTool() != null)
				editor.getSelectedTool().reload(assetManager);
		}
		
		if (renderMapToFile) {
			SpriteBatch screenShotBatch = new SpriteBatch();
			FrameBuffer fbo = new FrameBuffer(Format.RGBA8888, editor.getMap().getWidth() * MainEditor.TILE_SIZE, editor.getMap().getWidth() * MainEditor.TILE_SIZE, true) {
		        @Override
		        protected Texture createColorTexture() {
		            PixmapTextureData data = new PixmapTextureData(new Pixmap(width, height, format), format, false, false);
		            Texture result = new Texture(data);
		            result.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		            result.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
		            return result;
		        }
		    };
			fbo.begin();
			Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			screenShotBatch.begin();
			editor.getMap().draw(assetManager, true, true, false, false, 0, 0, 0, null, screenShotBatch, editor.getMap().getMinTileX(), editor.getMap().getMinTileY(), editor.getMap().getWidth(), editor.getMap().getHeight());
			screenShotBatch.end();
			
			fbo.end();
			
			TextureData data = fbo.getColorBufferTexture().getTextureData();
			if (!data.isPrepared())
				data.prepare();
			Pixmap pixmap = data.consumePixmap();
			PixmapIO.writePNG(Gdx.files.absolute("C:/Users/Nathanael/Desktop/test.png"), pixmap);
			renderMapToFile = false;
		}
		
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		super.render(mode.getMapToRender(editor));
		
		if (Gdx.input.isKeyPressed(Keys.Z) && controlPressed() && !shiftPressed()) {
			undoTimer -= Gdx.graphics.getDeltaTime();
			if (undoTimer <= 0) {
				undoTimer = REDO_UNDO_TIMER + undoTimer;
				editor.undo();
			}
		} else
			undoTimer = 0;
		
		if (((Gdx.input.isKeyPressed(Keys.Z) && shiftPressed()) || Gdx.input.isKeyPressed(Keys.Y)) && controlPressed()) {
			redoTimer -= Gdx.graphics.getDeltaTime();
			if (redoTimer <= 0) {
				redoTimer = REDO_UNDO_TIMER - redoTimer;
				editor.redo();
			}
		} else
			redoTimer = 0;
		
		batch.begin();
		if (editor.getSelectedTool() != null && mode == DEFAULT_MODE)
			editor.getSelectedTool().render(batch);
		
		mode.render(batch, editor.getAssetManager());
		
		batch.end();
	}
	
	public void setMode(JFrame previousWindow, WorldEditorMode<?> mode) {
		this.previousWindow = previousWindow;
		this.mode = mode;
		inputMultiplexer.addProcessor(mode);
		editor.getWorldEditorWindow().requestFocus();
		mode.setWorldEditor(this);
		editor.getWorldEditorWindow().setTitle(mode.getTitle());
	}
	
	public void resetModeToDefault() {
		inputMultiplexer.removeProcessor(mode);
		this.mode = DEFAULT_MODE;
		previousWindow.requestFocus();
		previousWindow = null;
		editor.getWorldEditorWindow().setTitle(mode.getTitle());
	}
	
	public void reloadAssets() {
		assetsLoaded = false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (editor.getMap() == null)
			return false;
			
		Vector2 mouseLocation = cam.unproject(new Vector2(screenX, screenY));
		
		int tileX = (int) (mouseLocation.x / MainEditor.TILE_SIZE);
		int tileY = (int) (mouseLocation.y / MainEditor.TILE_SIZE);
		
		if (editor.getSelectedTool() != null && mode == DEFAULT_MODE)
			editor.getSelectedTool().touchDown(mouseLocation.x, mouseLocation.y, tileX, tileY, button);
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector2 mouseLocation = cam.unproject(new Vector2(screenX, screenY));
		
		int tileX = (int) (mouseLocation.x / MainEditor.TILE_SIZE);
		int tileY = (int) (mouseLocation.y / MainEditor.TILE_SIZE);
	
		if (editor.getSelectedTool() != null && mode == DEFAULT_MODE)
			editor.getSelectedTool().touchUp(mouseLocation.x, mouseLocation.y, tileX, tileY, button);
		return super.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (editor.getMap() == null)
			return false;
			
		Vector2 mouseLocation = cam.unproject(new Vector2(screenX, screenY));
		
		int tileX = (int) (mouseLocation.x / MainEditor.TILE_SIZE);
		int tileY = (int) (mouseLocation.y / MainEditor.TILE_SIZE);
		
		if (!Gdx.input.isKeyPressed(Keys.SPACE)) {
			if (editor.getSelectedTool() != null && mode == DEFAULT_MODE)
				editor.getSelectedTool().touchDragged(mouseLocation.x, mouseLocation.y, tileX, tileY);
		}
		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return super.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(int amount) {
		System.out.println("WorldEditor.java  TEST!!");
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT))
			cam.zoom(amount * ZOOM_SCALE, Gdx.input.getX(), Gdx.input.getY());
		else {
			if (editor.getSelectedTool() != null && mode == DEFAULT_MODE)
				editor.getSelectedTool().mouseScrolled(amount);
		}
		return super.scrolled(amount);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
		case Keys.G:
			if (!controlPressed() && !shiftPressed())
				editor.setShowGrid(!editor.showGrid());
			break;
		case Keys.T:
			if (!controlPressed() && !shiftPressed())
				editor.setShowTiles(!editor.showTiles());
			break;
		case Keys.E:
			if (!controlPressed() && !shiftPressed())
				editor.setShowElements(!editor.showMapElements());
			break;
		/*case Keys.Z:
			if (controlPressed()) {
				if (shiftPressed())
					editor.redo();
				else
					editor.undo();
			}
			break;
		case Keys.Y:
			if (controlPressed())
				editor.redo();
			break;*/
		case Keys.S:
			if (controlPressed())
				editor.save();
			break;
		case Keys.F5:
			this.reloadAssets();
			break;
		case Keys.F2:
			renderMapToFile = true;
			break;
		case Keys.C:
			if (!controlPressed() && !shiftPressed())
				editor.setShowCollisionMap(!editor.showCollisionMap());
			break;
		case Keys.F6:
			if (editor.getMap() != null)
				editor.getMap().regenerateCollisionMaps(editor.getAssetManager());
			break;
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		return super.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		return super.keyTyped(character);
	}

}
