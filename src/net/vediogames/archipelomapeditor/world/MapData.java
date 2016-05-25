package net.vediogames.archipelomapeditor.world;

import java.util.ArrayList;

import net.vediogames.archipelomapeditor.entity.EntitySnapshot;

public class MapData {
	
	public String displayName = "Island";
	public int type = 0;
	public int climat = 0;
	public String[][] tileData = new String[1][1];
	public String[][] elementData = new String[1][1];
	public ArrayList<EntitySnapshot> entitySnapshots = new ArrayList<EntitySnapshot>();
	
}
