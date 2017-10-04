package com.EvilNotch.Core.Interfaces;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class InterfaceRegistry {
	
	public static ArrayList<IMobSpawnerRender> mobspanwnerRegistry = new ArrayList();
	
	public static void registerMobSpawner(IMobSpawnerRender m){mobspanwnerRegistry.add(m);}
	
	public static IMobSpawnerRender getRenderForBlock(Block b,int meta)
	{
		IMobSpawnerRender render = null;
		for(int i=0;i<mobspanwnerRegistry.size();i++)
		{
			IMobSpawnerRender spawner = mobspanwnerRegistry.get(i);
//			System.out.println("a:" + spawner.getBlock().getClass());
			if(b == spawner.getBlock())
			{
				if(!spawner.isMetaSpecific())
					return spawner;
				if(spawner.isMetaSpecific() && spawner.getMeta() == meta)
					return spawner;
				render = spawner;
			}
		}
		return render;
	}
	
	

}
