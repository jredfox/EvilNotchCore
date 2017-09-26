package com.EvilNotch.Core.Api;

import java.lang.reflect.Method;

import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Util.Util.BlockUtil;
import com.EvilNotch.Core.Util.Util.ItemUtil;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;


public class BlockApi {
	
	public static float GetBlockHardness(Block b)
	{
		try {
			return ReflectionHelper.findField(Block.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "blockHardness")).getFloat(b);
		} catch(Exception e){e.printStackTrace();}
		return 0.0F;
	}
	public static void setStepSound(Block b,SoundType type)
	{
		try {
			ReflectionHelper.findField(Block.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "blockSoundType")).set(b, type);
		} catch (Exception e){e.printStackTrace();}
	}
	public static void setTransLucent(Block b, boolean boole)
	{
		try {
			ReflectionHelper.findField(Block.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "translucent")).set(b, boole);
		} catch (Exception e){e.printStackTrace();}
	}
	public static void setBlasResistence(Block b, float f)
	{
		try {
			ReflectionHelper.findField(Block.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "blockResistance")).set(b, f);
		} catch (Exception e){e.printStackTrace();}
	}
	public static void setEnableStats(Block b, boolean boole)
	{
		try {
			ReflectionHelper.findField(Block.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "enableStats")).set(b, boole);
		} catch (Exception e){e.printStackTrace();}
	}
	public static void setIsBlockContainer(Block b, boolean boole)
	{
		try {
			ReflectionHelper.findField(Block.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "isBlockContainer")).set(b, boole);
		} catch (Exception e){e.printStackTrace();}
	}
	public static void setMaterial(Block b, Material m)
	{
		try {
			ReflectionHelper.findField(Block.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "blockMaterial")).set(b, m);
		} catch (Exception e){e.printStackTrace();}
	}
	public static String getBlockString(Block b)
	{
		return BlockUtil.getBlockString(b);
	}
	public static String getItemString(Item item)
	{
		return ItemUtil.getItemString(item);
	}
	
	
}
