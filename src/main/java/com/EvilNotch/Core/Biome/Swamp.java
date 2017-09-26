package com.EvilNotch.Core.Biome;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;

import com.EvilNotch.Core.Config;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenSwamp;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class Swamp extends BiomeGenSwamp
{

	public Swamp(int p_i1988_1_) 
	{
		super(p_i1988_1_);
		this.waterColorMultiplier = Config.swampWaterColor;
	}
	
	 /**
     * Provides the basic grass color based on the biome temperature and rainfall
     */
    @SideOnly(Side.CLIENT)
    public int getBiomeGrassColor(int p_150558_1_, int p_150558_2_, int p_150558_3_)
    {
        double d0 = plantNoise.func_151601_a((double)p_150558_1_ * 0.0225D, (double)p_150558_3_ * 0.0225D);
        if(Config.regularSwamp && !Config.betaSwamp && !Config.releaseSwamp)
        	return d0 < -0.1D ? Config.swampColorbaseOne : Config.swampColorBaseTwo;
        //Swamp for mc 1.2.5
        if(Config.releaseSwamp)
        	return getOldBiomeGrassColor(p_150558_1_,p_150558_2_,p_150558_3_);
        if(Config.betaSwamp)
        {
            double d01 = (double)MathHelper.clamp_float(this.getFloatTemperature(p_150558_1_, p_150558_2_, p_150558_3_), 0.0F, 1.0F);
            double d2 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
            return getModdedBiomeGrassColor(ColorizerGrass.getGrassColor(d01, d2));

        }
        else
        	return Config.swampGrassColor;
    }
    /**
     * Returns int from old biome swamp grass mc 1.2.5
     */
    public int getOldBiomeGrassColor(int x, int y, int z)
    {
      double var1 = this.getFloatTemperature(x,y,z);
      double var3 = getFloatRainfall();
      return ((ColorizerGrass.getGrassColor(var1, var3) & 0xFEFEFE) + 5115470) / 2;
    }
    /**
     * Returns int from old biome swamp foilage mc 1.2.5
     */
    public int getOldBiomeFoliageColor(int x, int y, int z)
    {
      double var1 = getFloatTemperature(x,y,z);
      double var3 = getFloatRainfall();
      return ((ColorizerFoliage.getFoliageColor(var1, var3) & 0xFEFEFE) + 5115470) / 2;
    }


    /**
     * Provides the basic foliage color based on the biome temperature and rainfall
     */
    @SideOnly(Side.CLIENT)
    public int getBiomeFoliageColor(int p_150571_1_, int p_150571_2_, int p_150571_3_)
    {
    	if(!Config.betaSwamp && !Config.releaseSwamp)
    		return Config.swampColorFoilage;
    	if(Config.releaseSwamp)
        	return getOldBiomeFoliageColor(p_150571_1_,p_150571_2_,p_150571_3_); //mc 1.2.5
    	if(Config.betaSwamp)
    	{
    		 double d0 = (double)MathHelper.clamp_float(this.getFloatTemperature(p_150571_1_, p_150571_2_, p_150571_3_), 0.0F, 1.0F);
    	     double d1 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
    	     return getModdedBiomeFoliageColor(ColorizerFoliage.getFoliageColor(d0, d1));
    	}
    	return 0;
    }
   
}