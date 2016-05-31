package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.world.Map;

public class SettingsChange extends Change {
	
	private String name = "";
	private int type;
	private int climat;
	
	private String redoName = "";
	private int redoType;
	private int redoClimat;
	
	Map map;
	
	public SettingsChange(Map map) {
		this.map = map;
		name = new String(map.getName());
		type = map.getType();
		climat = map.getClimat();
	}
	
	@Override
	public void undoChange() {
		redoName = new String(map.getName());
		redoType = new Integer(map.getType());
		redoClimat = new Integer(map.getClimat());
		map.setName(new String(name));
		map.setType(type);
		map.setClimat(climat);
	}
	
	@Override
	public void redoChanges() {
		map.setName(new String(redoName));
		map.setType(redoType);
		map.setClimat(redoClimat);
	}

}
