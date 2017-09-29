package com.EvilNotch.Core.Util.Util;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.Events.EventEnhancedVanilla;
import com.EvilNotch.Core.Events.TickHandler;
import com.EvilNotch.Core.Util.Line.LineBase;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;

public class Util {
	
	public static ArrayList toArray(RegistryNamespaced name)
	{
		ArrayList list = new ArrayList();
		Iterator it = name.iterator();
		while(it.hasNext())
			list.add(it.next());
		return list;
	}
	
	/**
	 * Gets a modname
	 */
	public static String getModName(String par1Str)
	 {
	     for (ModContainer mod : Loader.instance().getModList())
	     {
	         if (mod.getModId().equals(par1Str) )
	             return mod.getName();
	     }
	     return "Minecraft";
	 }
	
	/**
	 * Used for recalculating entity pos from one location to the other
	 * @param pos
	 * @param oldx
	 * @param newx
	 * @return
	 */
	public static double recalDouble(double pos, int oldx, int newx)
	{
		String str = String.valueOf(pos);
		String[] parts = str.split("\\.");
		String strEdit = parts[0];
		String strdeci = parts[1];
		
		double oldfront = Double.parseDouble(strEdit);
		double deci = Double.parseDouble(strdeci);
		double offset = (oldx - pos) * -1;

		return (double)(newx + offset);
	}
	
	public static String getEnumColor(EnumChatFormatting e)
	{
		return e + "";
	}

	 public static float getRenderFrame() {
	        return TickHandler.renderFrame;
	    }

	    public static double getRenderTime() {
	        return TickHandler.renderTime + getRenderFrame();
	    }
	    
     public static void changeTexture(ResourceLocation texture)
	 {
	     Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	 }
     /**
      * Removes instance from options.txt and then Adds all default packs except specified entry
      * @param name
      */
    public static void closeResourcePack(String name) 
  	{
 		List list = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntriesAll();
 		Iterator it = list.iterator();
 		while(it.hasNext())
 		{
 			ResourcePackRepository.Entry entry = (Entry) it.next();
 			entry.closeResourcePack();
 		}

  	}
    public static void setDefaultPack(String name) 
 	{
    	if(Minecraft.getMinecraft().gameSettings.resourcePacks.contains(name))
    		return;
    	ArrayList clear = new ArrayList();
		List toadd = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries();//cache already added resource packs
    	Minecraft.getMinecraft().getResourcePackRepository().func_148527_a(clear);//Clears current list from past edits
    	
 		Minecraft.getMinecraft().getResourcePackRepository().updateRepositoryEntriesAll();//Makes it launch first time
 		List list = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntriesAll();
 		Iterator it = list.iterator();
 		while(it.hasNext())
 		{
 			ResourcePackRepository.Entry entry = (Entry) it.next();
 			if(entry.getResourcePackName().equals(name))
 			{
 				ArrayList<Entry> li = new ArrayList();
 				li.addAll(toadd);
 				if(!Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries().contains(entry))
 					li.add(entry);
 				Minecraft.getMinecraft().getResourcePackRepository().func_148527_a(li);
 			}
 		}
 		
 	}

	public static boolean isLine(ArrayList<LineBase> lines, LineBase lineBase)
	{
		for(LineBase line : lines)
		{
//			System.out.println("Line1:" + line + " Line2:" + lineBase);
			if(line.equals(lineBase))
				return true;
		}
		return false;
	}
}
