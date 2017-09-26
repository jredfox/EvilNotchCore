package com.EvilNotch.Core.Blocks;

import java.util.Random;

import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Load.LoadRegister;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSand;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;

public class BlockOldMushroom extends BlockMushroom {
	
	 	public BlockOldMushroom()
	    {
	        float f = 0.2F;
	        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
	        this.setTickRandomly(true);
	    }
	 	
	
	 /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
	@Override
    public boolean canBlockStay(World w, int x, int y, int z)
    {
		Block block = w.getBlock(x, y-1, z);
		boolean k = block instanceof BlockGrass || block instanceof BlockDirt || block instanceof BlockSand;
    	if(!k)
    		return super.canBlockStay(w, x, y, z);
		
		return true;
    }
	@Override
	public boolean func_149884_c(World p_149884_1_, int p_149884_2_, int p_149884_3_, int p_149884_4_, Random p_149884_5_)
    {
        int l = p_149884_1_.getBlockMetadata(p_149884_2_, p_149884_3_, p_149884_4_);
        p_149884_1_.setBlockToAir(p_149884_2_, p_149884_3_, p_149884_4_);
        WorldGenBigMushroom worldgenbigmushroom = null;

        if (this == LoadRegister.brown_mushroom)
        {
            worldgenbigmushroom = new WorldGenBigMushroom(0);
        }
        else if (this == LoadRegister.red_mushroom)
        {
            worldgenbigmushroom = new WorldGenBigMushroom(1);
        }

        if (worldgenbigmushroom != null && worldgenbigmushroom.generate(p_149884_1_, p_149884_5_, p_149884_2_, p_149884_3_, p_149884_4_))
        {
            return true;
        }
        else
        {
            p_149884_1_.setBlock(p_149884_2_, p_149884_3_, p_149884_4_, this, l, 3);
            return false;
        }
    }

}
