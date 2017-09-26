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
	
	public NBTTagCompound getEntityIdNBT(NBTTagCompound nbt,World w);
	public NBTTagList getMountsNBT(NBTTagCompound nbt,World w);
	public ArrayList<Entity> getAllEntities(NBTTagCompound nbt,World w);
	public Block getBlock();
	public boolean hasNEIRender();
	public boolean hasNEIItemRender();
}
