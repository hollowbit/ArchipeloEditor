package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.world.Map;

public class SettingsChange extends Change {
	
	private String name;
	private String displayName;
	private String music;
	
	private String redoName;
	private String redoDisplayName;
	private String redoMusic;
	
	Map map;
	
	//Saves a state of settings before new ones are applied
	public SettingsChange(Map map) {
		this.map = map;
		name = map.getName();
		displayName = map.getDisplayName();
		music = map.getMusic();
	}
	
	//Saves settings changed and reverts to old ones
	@Override
	public void undoChange() {
		redoName = map.getName();
		redoDisplayName = map.getDisplayName();
		redoMusic = map.getMusic();
		map.setName(name);
		map.setDisplayName(displayName);
		map.setMusic(music);
	}
	
	//Redoes change to settings
	@Override
	public void redoChanges() {
		map.setName(new String(redoName));
		map.setDisplayName(new String(redoDisplayName));
		map.setMusic(redoMusic);
	}

}
