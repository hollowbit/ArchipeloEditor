package net.hollowbit.archipeloeditor.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloeditor.world.Map;
import net.hollowbit.archipeloeditor.world.RenderableGameWorldObject;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.Point;

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
	
	public Entity(EntitySnapshot snapshot, Map map) {
		this.map = map;
		this.updateWithSnapshot(snapshot);
	}
	
	public void updateWithSnapshot(EntitySnapshot snapshot) {
		this.snapshot = snapshot;
		this.name = snapshot.name;
		this.type = EntityType.getById(snapshot.type);
		this.style = snapshot.getInt("style", 0);
		this.pos = snapshot.getObject("pos", new Point(), Point.class);
		this.rect = new CollisionRect(pos.x, pos.y, getWidth(), getHeight());
	}
	
	public int getWidth() {
		//TODO add exceptions for entities like slimes, etc
		return type.getData().imgWidth;
	}
	
	public int getHeight() {
		//TODO add exceptions for entities like slimes, etc
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

	public EntitySnapshot getSnapshot() {
		return snapshot;
	}

	@Override
	public float getRenderY() {
		return type.getDrawOrderY(pos.y);
	}

	@Override
	public CollisionRect getViewRect() {
		return rect;
	}

	@Override
	public void renderObject(SpriteBatch batch) {
		batch.draw(type.getEditorTexture(style), pos.x, pos.y, getWidth(), getHeight());
	}
	
}
