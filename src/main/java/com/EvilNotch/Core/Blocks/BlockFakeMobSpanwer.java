package com.EvilNotch.Core.Blocks;

import com.EvilNotch.Core.TileEntity.TileVMobSpawner;
import com.EvilNotch.Core.TileEntity.TileVMobSpawner;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class BlockFakeMobSpanwer extends BlockMobSpawner{
	
	 /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
       return new TileVMobSpawner();//return new TileVMobSpawner();
    }

}
