package com.EvilNotch.Core.Interfaces;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IMobSpawnerRender {
	
	public NBTTagCompound getEntityIdNBT(NBTTagCompound nbt,World w,boolean isItem,int meta);
	public NBTTagList getMountsNBT(NBTTagCompound nbt,World w,boolean isItem,int meta);
	public NBTTagList getAllEntitiesNBT(NBTTagCompound nbt,World w,boolean isItem,int meta);
	public ArrayList<Entity> getAllEntities(NBTTagCompound nbt,World w,boolean isItem,int meta);
	public ArrayList<Boolean> getFlagFire(ArrayList<Entity> list);
	public Block getBlock();
	public boolean hasMountRender(boolean item);
	public boolean hasNEIItemRender();
	public boolean hasDelayTag(NBTTagCompound nbt);
	public boolean isMetaSpecific();
	public int getDelay(NBTTagCompound nbt);
	public int getMeta();
}
