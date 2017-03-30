package net.hollowbit.archipeloeditor.entity;

import java.util.ArrayList;

import net.hollowbit.archipeloeditor.tools.FileReader;
import net.hollowbit.archipeloeditor.tools.StaticTools;
import net.hollowbit.archipeloshared.EntityTypeData;
import net.hollowbit.archipeloshared.PropertyDefinition;

public enum EntityType {
	
	PLAYER ("player"),
	TELEPORTER ("teleporter"),
	DOOR ("door"),
	DOOR_LOCKED ("door-locked"),
	SIGN ("sign"),
	BLOBBY_GRAVE ("blobby-grave"),
	COMPUTER ("computer"),
	WIZARD ("wizard");
	
	private String id;
	private EntityTypeData data;
	
	private EntityType (String id) {
		this.id = id;
		
		String dataString = FileReader.readFileIntoString("/shared/entities/" + id + ".json");
		this.data = StaticTools.getJson().fromJson(EntityTypeData.class, dataString);
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return id;
	}

	public EntityTypeData getData() {
		return data;
	}
	
	public PropertyDefinition[] getDefaultProperties () {
		return data.defaultProperties;
	}
	
	public ArrayList<PropertyDefinition> getProperties () {
		return data.properties;
	}
	
}
