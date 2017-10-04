package com.EvilNotch.Core.Interfaces;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class BasicSpawnerEntry {
	
	public NBTTagList list;
	public ArrayList<Entity> ents;
	public double field_98287_c;
	public double field_98284_d;
	public IMobSpawnerRender spawner;
	
	public BasicSpawnerEntry(NBTTagList list,ArrayList<Entity> ents,int delay,IMobSpawnerRender spawner)
	{
		this.list = list;
		this.ents = ents;
		this.spawner = spawner;
	}
	public void updateDelay(int delay)
	{
		 this.field_98284_d = this.field_98287_c;
         this.field_98287_c = (this.field_98287_c + (double)(1000.0F / ((float)delay + 200.0F))) % 360.0D;
	}

}
