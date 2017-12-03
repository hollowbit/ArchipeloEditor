package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.entity.Entity;
import net.hollowbit.archipeloeditor.world.Map;

public class EntityRemoveChange extends Change {

	private Map map;
	private Entity removedEntity;
	
	public EntityRemoveChange(Map map, Entity removedEntity) {
		this.map = map;
		this.removedEntity = removedEntity;
	}
	
	@Override
	public void undoChange() {
		map.addEntity(removedEntity);
	}

	@Override
	public void redoChanges() {
		map.removeEntity(removedEntity);
	}

}
