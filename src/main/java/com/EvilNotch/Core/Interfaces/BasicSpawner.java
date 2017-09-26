package com.EvilNotch.Core.Interfaces;

import java.util.ArrayList;

import com.EvilNotch.Core.TileEntity.MobSpawnerVLogic;
import com.EvilNotch.Core.Util.Util.EntityUtil;
import com.EvilNotch.Core.Util.Util.NBTUtil;
import com.EvilNotch.Core.Util.Util.TileEntityUtil;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BasicSpawner implements IMobSpawnerRender{
	
	public Block block;
	public boolean isItem;
	public boolean nei;
	public boolean neiItem;
	
	public BasicSpawner(Block b,boolean nei,boolean item_nei,boolean isItem)
	{
		this.block = b;
		this.isItem = isItem;
		this.nei = nei;
		this.neiItem = item_nei;
	}

	public NBTTagCompound getEntityIdNBT(NBTTagCompound nbt,World w) 
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("id", nbt.getString("EntityId"));
		if(EntityUtil.hasSpawnData(tag))
			tag.setTag("EntityNBT", nbt.getTag("SpawnData"));
		return tag;
	}
	public NBTTagList getMountsNBT(NBTTagCompound nbt,World w)
	{
		NBTTagList list = nbt.getTagList("mounts", 10);
		for(int i=0;i<list.tagCount();i++)
			list.appendTag(list.getCompoundTagAt(i).copy());
		
		return list;
	}
	public ArrayList<Entity> getAllEntities(NBTTagCompound nbt,World w)
	{	
		NBTTagCompound base = getEntityIdNBT(nbt,w);
		NBTTagList mounts = getMountsNBT(nbt,w);
		NBTTagList list = new NBTTagList();
		if(base != null)
			list.appendTag(base);
		for(int i=0;i<mounts.tagCount();i++)
			list.appendTag(mounts.getCompoundTagAt(i));
		
		return cacheEntities(list,w);
	}
	@Override
	public Block getBlock() {
		return this.block;
	}
	@Override
	public boolean hasNEIRender() {
		return this.nei;
	}
	@Override
	public boolean hasNEIItemRender() {
		return this.neiItem;
	}
	public ArrayList<Entity> cacheEntities(NBTTagList list,World w) 
	{
		ArrayList<Entity> ents = new ArrayList();
		for(int i=0;i<list.tagCount();i++)
		{
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			Entity ent = EntityUtil.createBasicEntity(w, nbt,0,0,0);
			if(nbt.getTag("EntityNBT") == null)
				ent = MobSpawnerVLogic.getDisplayEnt(w, ent, false);
			if(ent != null)
				ents.add(ent);
		}
		EntityUtil.mountEntities(ents);
		return ents;
	}


}
