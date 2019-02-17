package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.entity.Entity;
import net.hollowbit.archipeloeditor.world.Map;

public class EntityAddUpdateChange extends Change {
	
	private Map map;
	private Entity oldEntity;
	private Entity newEntity;
	
	public EntityAddUpdateChange(Map map, Entity oldEntity, Entity newEntity) {
		this.map = map;
		this.oldEntity = oldEntity;
		this.newEntity = newEntity;
	}
	
	@Override
	public void undoChange() {
		map.removeEntity(newEntity);
		if (oldEntity != null)
			map.addEntity(oldEntity);
	}

	@Override
	public void redoChanges() {
		if (oldEntity != null)
			map.removeEntity(oldEntity);
		map.addEntity(newEntity);
	}

}
