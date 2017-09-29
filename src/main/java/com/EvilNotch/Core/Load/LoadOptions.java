package com.EvilNotch.Core.Load;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Util.Java.Archive;
import com.EvilNotch.Core.Util.Java.ArchiveEntry;
import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Util.EntityUtil;
import com.EvilNotch.Core.Util.Util.NBTUtil;
import com.EvilNotch.Core.Util.Util.Util;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.nbt.NBTTagCompound;

public class LoadOptions {
	
	public static void loadOptions()
	{
		disableFullScreen();
		setOldResourcePacks();
	}
	
	public static void disableFullScreen() 
    {
		if(Config.disableFullScreen)
			return;
		try{
			Minecraft.getMinecraft().gameSettings.fullScreen = false;
			File dir = Config.configDir.getParentFile();
			File options = new File(dir,"options.txt");
			List<String> lines = Files.readAllLines(options.toPath());
			for(int i=0;i<lines.size();i++)
			{
				String s = lines.get(i);
				if(s.contains("fullscreen"))
					lines.set(i, "fullscreen:false");
			}
			Files.write(options.toPath(), lines); // You can add a charset and other options too
		}catch(Exception ee){ee.printStackTrace();}
		
	}
	
	@SuppressWarnings("unused")
	public static void setOldResourcePacks() 
    {
		generateResourcePackData();//Detects change from config
		if(!Config.oldMcResources || true)
			return;
		Util.closeResourcePack("Mc Textures 1.2.5.zip");//Removes instance of old resource pack to modify later
		Util.closeResourcePack("C418MissingMusic.zip");
		//Extract Files
		boolean flag = false;
		boolean flag2 = false;
		boolean firstLaunch = false;
    	File rpZip = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks(), "Mc Textures 1.2.5.zip");
    	File rpMusic = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks(), "C418MissingMusic.zip");
    	File temp = new File(rpZip.getParentFile(),"Mc Textures 1.2.5.zip.zip");
    	if(temp.exists())
    		temp.delete();//Delete corrupted exceptions
    	File rp_dir = Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks();
    	if(!rpMusic.exists())
    		JavaUtil.moveFileFromJar(MainCommonMod.class, "/assets/evil_notch_core/C418MissingMusic.zip", rpMusic);
    	if(!rpZip.exists())
    	{
    		JavaUtil.moveFileFromJar(MainCommonMod.class,"/assets/evil_notch_core/Mc Textures 1.2.5.zip",rpZip);
    		firstLaunch = true;
    	}
    	else
    		flag = true;
    	//Corrupt zip handling
    	if(flag)
    	{
    		try{
    			ZipFile tst = new ZipFile(rpZip);
    			tst.close();
    		}catch(Exception e){flag2 = true;}
    	}
    	//if does exist and has exception
    	if(flag2)
    	{
    		rpZip.delete();
    		JavaUtil.moveFileFromJar(MainCommonMod.class,"/assets/evil_notch_core/Mc Textures 1.2.5.zip",rpZip);
    		firstLaunch = true;
    	}
    	Archive arch = new Archive(rpZip);
    	String path = "assets/minecraft/textures/blocks/";
    	String path2 = "assets/minecraft/sounds/";
    	String a = "default";
    	if(Config.beta_gravel)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/beta_gravel.png"),new ZipEntry(path + "gravel.png") ));
    	else
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/gravel.png"),new ZipEntry(path + "gravel.png") ));
    	if(Config.beta_mob_spawner)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/beta_mob_spawner.png"),new ZipEntry(path + "mob_spawner.png") ));
    	else
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/mob_spawner.png"),new ZipEntry(path + "mob_spawner.png") ));
    	
    	if(Config.beta_hurt)
    	{
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/hit1.ogg"),new ZipEntry(path2 + "damage/hit1.ogg") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/hit2.ogg"),new ZipEntry(path2 + "damage/hit2.ogg") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/hit3.ogg"),new ZipEntry(path2 + "damage/hit3.ogg") ));
    	}
    	else
    	{
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/hit1.ogg"),new ZipEntry(path2 + "damage/hit1.ogg") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/hit2.ogg"),new ZipEntry(path2 + "damage/hit2.ogg") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/hit3.ogg"),new ZipEntry(path2 + "damage/hit3.ogg") ));	
    	}
    	
    	boolean flag_brick = false;
    	if(Config.beta_bricks)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/brick.png"),new ZipEntry(path + "brick.png") ));
    	if(Config.alpha_bricks)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("alpha/brick.png"),new ZipEntry(path + "brick.png") ));
    	
    	if(!Config.beta_bricks && !Config.alpha_bricks)	
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/brick.png"),new ZipEntry(path + "brick.png") ));
    		
    	if(Config.beta_cobblestone)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/cobblestone.png"),new ZipEntry(path + "cobblestone.png") ));
    	else
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/cobblestone.png"),new ZipEntry(path + "cobblestone.png") ));
    	if(Config.beta_cobblestone_mossy)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/cobblestone_mossy.png"),new ZipEntry(path + "cobblestone_mossy.png") ));
    	else
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/cobblestone_mossy.png"),new ZipEntry(path + "cobblestone_mossy.png") ));
  
    	if(Config.beta_diamond_block)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/diamond_block.png"),new ZipEntry(path + "diamond_block.png") ));
    	else
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/diamond_block.png"),new ZipEntry(path + "diamond_block.png") ));
    	if(Config.beta_end_stone)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/end_stone.png"),new ZipEntry(path + "end_stone.png") ));
    	else
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/end_stone.png"),new ZipEntry(path + "end_stone.png") ));
    	if(Config.beta_endrame)
    	{
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/endframe_side.png"),new ZipEntry(path + "endframe_side.png") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/endframe_top.png"),new ZipEntry(path + "endframe_top.png") ));
    	}
    	else{
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/endframe_side.png"),new ZipEntry(path + "endframe_side.png") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/endframe_top.png"),new ZipEntry(path + "endframe_top.png") ));
    	}
    	
    	if(Config.alpha_furnace_top)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("alpha/furnace_top.png"),new ZipEntry(path + "furnace_top.png") ));
    	else
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/furnace_top.png"),new ZipEntry(path + "furnace_top.png") ));
    	if(Config.beta_gold_block)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/gold_block.png"),new ZipEntry(path + "gold_block.png") ));
    	else
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/gold_block.png"),new ZipEntry(path + "gold_block.png") ));
    	if(Config.beta_iron_block)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/iron_block.png"),new ZipEntry(path + "iron_block.png") ));
    	else
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/iron_block.png"),new ZipEntry(path + "iron_block.png") ));
    	if(Config.beta_lapis_block)
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/lapis_block.png"),new ZipEntry(path + "lapis_block.png") ));
    	else
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/lapis_block.png"),new ZipEntry(path + "lapis_block.png") ));
    	if(Config.old_lava)
    	{
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/lava_flow.png"),new ZipEntry(path + "lava_flow.png") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/lava_flow.png.mcmeta"),new ZipEntry(path + "lava_flow.png.mcmeta") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/lava_still.png"),new ZipEntry(path + "lava_still.png") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/lava_still.png.mcmeta"),new ZipEntry(path + "lava_still.png.mcmeta") ));
    	}
    	else{
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/lava_flow.png"),new ZipEntry(path + "lava_flow.png") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/lava_flow.png.mcmeta"),new ZipEntry(path + "lava_flow.png.mcmeta") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/lava_still.png"),new ZipEntry(path + "lava_still.png") ));
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/lava_still.png.mcmeta"),new ZipEntry(path + "lava_still.png.mcmeta") ));
    	}
    	if(Config.old_water)
    	{
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/water_flow.png"),new ZipEntry(path + "water_flow.png") ));
        	arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/water_flow.png.mcmeta"),new ZipEntry(path + "water_flow.png.mcmeta") ));
        	arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/water_still.png"),new ZipEntry(path + "water_still.png") ));
        	arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("beta/water_still.png.mcmeta"),new ZipEntry(path + "water_still.png.mcmeta") ));
        }
    	else{
    		arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/water_flow.png"),new ZipEntry(path + "water_flow.png") ));
        	arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/water_flow.png.mcmeta"),new ZipEntry(path + "water_flow.png.mcmeta") ));
        	arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/water_still.png"),new ZipEntry(path + "water_still.png") ));
        	arch.addAndReplaceEntry(new ArchiveEntry(new ZipEntry("default/water_still.png.mcmeta"),new ZipEntry(path + "water_still.png.mcmeta") ));
        	
    	}
    	if(firstLaunch || Config.dyanmic_rp_changed)
    	{
    		for(int i=0;i<10;i++)
    			System.out.println("uuuuuuuuupppppppdating ARRRRRRRCHIVEEEEEEEEEEE!");
    		arch.updateArchive();//Takes 4-5 seconds but, writes updates to zip
    	}
    	if(Config.rp_C418)
    		Util.setDefaultPack("C418MissingMusic.zip");
    	if(Config.rp_Mc125)
    		Util.setDefaultPack("Mc Textures 1.2.5.zip");
	}

	private static void generateResourcePackData() 
	{
		try{
		File dir = new File(Config.folder,"/data");
		if(!dir.exists())
			dir.mkdirs();
		File file = new File(dir,"mc125.dat");
		if(!file.exists())
		{
			file.createNewFile();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("old_lava", true);
			nbt.setBoolean("old_water", true);
			
			nbt.setBoolean("alpha_bricks", false);
			nbt.setBoolean("alpha_furnace_top", false);
			
			nbt.setBoolean("beta_hurt", false);
			nbt.setBoolean("beta_bricks", false);
			nbt.setBoolean("beta_gravel", false);
			nbt.setBoolean("beta_mob_spawner", false);
			nbt.setBoolean("beta_cobblestone", false);
			nbt.setBoolean("beta_cobblestone_mossy", false);
			nbt.setBoolean("beta_diamond_block", false);
			nbt.setBoolean("beta_iron_block", false);
			nbt.setBoolean("beta_gold_block", false);
			nbt.setBoolean("beta_end_stone", false);
			nbt.setBoolean("beta_endrame", false);
			nbt.setBoolean("beta_lapis_block", false);
			NBTUtil.updateNBTFile(file, nbt);
		}
		NBTTagCompound nbt = NBTUtil.getFileNBT(file);
		boolean update = 
		!(nbt.getBoolean("old_lava") == Config.old_lava && 
		nbt.getBoolean("old_water") == Config.old_water && 
		nbt.getBoolean("alpha_bricks") == Config.alpha_bricks && 
		nbt.getBoolean("alpha_furnace_top") == Config.alpha_furnace_top && 
		nbt.getBoolean("beta_hurt") == Config.beta_hurt && 
		nbt.getBoolean("beta_bricks") == Config.beta_bricks && 
		nbt.getBoolean("beta_gravel") == Config.beta_gravel && 
		nbt.getBoolean("beta_mob_spawner") == Config.beta_mob_spawner && 
		nbt.getBoolean("beta_cobblestone") == Config.beta_cobblestone && 
		nbt.getBoolean("beta_cobblestone_mossy") == Config.beta_cobblestone_mossy &&
		nbt.getBoolean("beta_diamond_block") == Config.beta_diamond_block && 
		nbt.getBoolean("beta_iron_block") == Config.beta_iron_block && 
		nbt.getBoolean("beta_gold_block") == Config.beta_gold_block && 
		nbt.getBoolean("beta_end_stone") == Config.beta_end_stone && 
		nbt.getBoolean("beta_endrame") == Config.beta_endrame && 
		nbt.getBoolean("beta_lapis_block") == Config.beta_lapis_block);
		
		if(update)
		{
			Config.dyanmic_rp_changed = true;
			nbt.setBoolean("old_lava", Config.old_lava);
			nbt.setBoolean("old_water", Config.old_water);
			
			nbt.setBoolean("alpha_bricks", Config.alpha_bricks);
			nbt.setBoolean("alpha_furnace_top", Config.alpha_furnace_top);
			
			nbt.setBoolean("beta_hurt", Config.beta_hurt);
			nbt.setBoolean("beta_bricks", Config.beta_bricks);
			nbt.setBoolean("beta_gravel", Config.beta_gravel);
			nbt.setBoolean("beta_mob_spawner", Config.beta_mob_spawner);
			nbt.setBoolean("beta_cobblestone", Config.beta_cobblestone);
			nbt.setBoolean("beta_cobblestone_mossy", Config.beta_cobblestone_mossy);
			nbt.setBoolean("beta_diamond_block", Config.beta_diamond_block);
			nbt.setBoolean("beta_iron_block", Config.beta_iron_block);
			nbt.setBoolean("beta_gold_block", Config.beta_gold_block);
			nbt.setBoolean("beta_end_stone", Config.beta_end_stone);
			nbt.setBoolean("beta_endrame", Config.beta_endrame);
			nbt.setBoolean("beta_lapis_block", Config.beta_lapis_block);
			NBTUtil.updateNBTFile(file, nbt);
		}
		}catch(Exception e){e.printStackTrace();}
	}

	

}
