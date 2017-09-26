package com.EvilNotch.Core.Util.Util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityUtil {
	
	/**
	 * Gets Tile Entity NBT
	 * @param tile
	 * @return
	 */
	public static NBTTagCompound getTileNBT(TileEntity tile)
	{
		if(tile == null)
			return null;
		NBTTagCompound nbt = new NBTTagCompound();
		tile.writeToNBT(nbt);
		return nbt;
	}
	public static void setTileEntity(World w,TileEntity tile,int x, int y, int z)
	{
		w.setTileEntity(x, y, z, tile);
		updateTileEntity(w,tile,x,y,z);
	}
	public static void updateTileEntity(World w,TileEntity tile,int x, int y, int z) 
	{
		tile.markDirty();
		w.markBlockForUpdate(x, y, z);
	}

}
