package com.EvilNotch.Core.Interfaces;

import java.util.ArrayList;

public class InterfaceRegistry {
	
	public static ArrayList<IMobSpawnerRender> mobspanwnerRegistry = new ArrayList();
	
	public void registerMobSpawner(IMobSpawnerRender m){mobspanwnerRegistry.add(m);}
	
	

}
