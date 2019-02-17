package net.hollowbit.archipeloeditor.entity;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.world.AssetManager;
import net.hollowbit.archipeloeditor.world.Map;
import net.hollowbit.archipeloeditor.world.RenderableGameWorldObject;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.Point;
import net.hollowbit.archipeloshared.SavedRectangle;

/**
 * Class that holds and stores some values of an entity taken from a snapshot.
 * @author vedi0boy
 *
 */
public class Entity implements RenderableGameWorldObject {
	
	private String name;
	private EntityType type;
	private int style;
	private Point pos;
	private CollisionRect rect;
	private Map map;
	private EntitySnapshot snapshot;
	
	private Color color;
	
	//Only used by spawners
	private SavedRectangle spawnerRectangle;
	
	public Entity(EntitySnapshot snapshot, Map map) {
		this.map = map;
		this.updateWithSnapshot(snapshot);
		
		Random r = new Random(name.hashCode());
		color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1);
	}
	
	public void updateWithSnapshot(EntitySnapshot snapshot) {
		this.snapshot = snapshot;
		this.name = snapshot.name;
		this.type = EntityType.getById(snapshot.type);
		this.style = snapshot.getInt("style", 0);
		this.pos = snapshot.getObject("pos", new Point(), Point.class);
		this.rect = new CollisionRect(pos.x, pos.y, getWidth(), getHeight());
		
		if (type == EntityType.SPAWNER)
			spawnerRectangle = snapshot.getObject("spawnRect", new SavedRectangle((int) pos.x, (int) pos.y, MainEditor.TILE_SIZE, MainEditor.TILE_SIZE), SavedRectangle.class);
	}
	
	public int getWidth() {
		if (spawnerRectangle != null)
			return spawnerRectangle.width;
					
		return type.getData().imgWidth;
	}
	
	public int getHeight() {
		if (spawnerRectangle != null)
			return spawnerRectangle.height;
		
		return type.getData().imgHeight;
	}

	public String getName() {
		return name;
	}

	public EntityType getType() {
		return type;
	}

	public int getStyle() {
		return style;
	}

	public Point getPos() {
		return pos;
	}

	public Map getMap() {
		return map;
	}
	
	public float getX() {
		if (spawnerRectangle != null)
			return spawnerRectangle.x;
		
		return pos.x;
	}
	
	public float getY() {
		if (spawnerRectangle != null)
			return spawnerRectangle.y;
		
		return pos.y;
	}

	public EntitySnapshot getSnapshot() {
		return snapshot;
	}
	
	/**
	 * Unique generated color based on this entity's name
	 * @return
	 */
	public Color getUniqueColor() {
		return color;
	}

	@Override
	public float getRenderY() {
		if (spawnerRectangle != null)
			return spawnerRectangle.y;
		
		return type.getDrawOrderY(pos.y);
	}

	@Override
	public CollisionRect getViewRect() {
		return rect;
	}

	@Override
	public void renderObject(AssetManager assetManager, SpriteBatch batch) {
		if (type == EntityType.SPAWNER) {
			batch.setColor(getUniqueColor().r, getUniqueColor().g, getUniqueColor().b, 0.5f);
			batch.draw(assetManager.getBlank(), spawnerRectangle.getX(), spawnerRectangle.getY(), spawnerRectangle.getWidth(), spawnerRectangle.getHeight());
			batch.setColor(1, 1, 1, 1);
		} else
			batch.draw(type.getEditorTexture(style), pos.x, pos.y, getWidth(), getHeight());
	}
	
}
