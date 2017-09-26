package com.EvilNotch.Core.Load;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Util.Util.EntityUtil;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public class LoadCommon {
	
	public static void preinit(FMLPreInitializationEvent event)
	{
		Config.loadConfig(event); //Loads General Configs and use for asm?
		MainCommonMod.proxy.preinit();
		LoadBaseConfig.cacheArrays();
		LoadBaseConfig.cacheBlockProperties();
	}
	public static void init()
	{
		LoadRegister.load();
		MainCommonMod.proxy.init();
	}
	public static void post()
	{
		EntityUtil.cacheWorldNeedyEnts();//Cache entities that need the world
		EntityUtil.cacheEntities(MainCommonMod.world);//Cache All Entities
		LoadRegister.loadpost();
		MainCommonMod.proxy.postinit();
		LoadBaseConfig.setBlockProperties();
	}

}
