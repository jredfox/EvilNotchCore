package com.EvilNotch.Core.Util.Util;

import com.EvilNotch.Core.Api.FieldAcess;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenMutated;

public class BiomeUtil {

	public static int getFirstId() 
	{
		for(int i=0;i<FieldAcess.biomeList.length;i++)
		{
			BiomeGenBase b = FieldAcess.biomeList[i];
			if(b == null)
				return i;
		}
		return -1;
	}
	public static BiomeGenBase createMutation(int id,BiomeGenBase b)
    {
        return new BiomeGenMutated(id + 128, b);
    }

}
