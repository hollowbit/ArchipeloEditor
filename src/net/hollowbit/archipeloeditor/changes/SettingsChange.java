package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.world.Map;

public class SettingsChange extends Change {
	
	private String name;
	private String displayName;
	private int type;
	private int climat;
	
	private String redoName;
	private String redoDisplayName;
	private int redoType;
	private int redoClimat;
	
	Map map;
	
	//Saves a state of settings before new ones are applied
	public SettingsChange(Map map) {
		this.map = map;
		name = new String(map.getName());
		displayName = new String(map.getDisplayName());
		type = map.getType();
		climat = map.getClimat();
	}
	
	//Saves settings changed and reverts to old ones
	@Override
	public void undoChange() {
		redoName = new String(map.getName());
		redoDisplayName = new String(map.getDisplayName());
		redoType = new Integer(map.getType());
		redoClimat = new Integer(map.getClimat());
		map.setName(new String(name));
		map.setDisplayName(new String(displayName));
		map.setType(type);
		map.setClimat(climat);
	}
	
	//Redoes change to settings
	@Override
	public void redoChanges() {
		map.setName(new String(redoName));
		map.setDisplayName(new String(redoDisplayName));
		map.setType(redoType);
		map.setClimat(redoClimat);
	}

}
