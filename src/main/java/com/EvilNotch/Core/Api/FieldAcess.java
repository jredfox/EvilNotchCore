package com.EvilNotch.Core.Api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Util.Line.LineBase;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemArmor;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

public class FieldAcess {
		
		//MCPMAPPINGS API hashmaps cached here only on pre-init
		public static HashMap<String,String> fieldToOb = new HashMap();
		public static HashMap<String,String> obToField = new HashMap();
		
		public static HashMap<String,String> methodToOb = new HashMap();
		public static HashMap<String,String> obToMethod = new HashMap();
		
		public static HashMap<String,String> paramToOb = new HashMap();
		public static HashMap<String,String> obToParam = new HashMap();
		
		public static HashMap<String,Class> entity_stringToClassMapping = new HashMap();
		public static HashMap<Class,String> entity_classToStringMapping = new HashMap();
		public static HashMap<Class,Integer> entity_classToIDMapping = new HashMap();
		
		public static void CachePrivateFeilds()
		{
			entity_stringToClassMapping = (HashMap<String, Class>) EntityList.stringToClassMapping;
			entity_classToStringMapping = (HashMap<Class, String>) ReflectionUtil.getObject(null, EntityList.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "classToStringMapping"));
			entity_classToIDMapping = (HashMap<Class, Integer>)ReflectionUtil.getObject(null, EntityList.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "classToIDMapping"));
		}
		public static void CacheMCP()
		{
			try{
				InputStream in = MainCommonMod.class.getResourceAsStream("/assets/evil_notch_core/MCP/" + MinecraftForge.MC_VERSION + "/fields.csv"); 
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String strline;
				 while ((strline = reader.readLine()) != null) 
				 {
					 String[] parts = LineBase.getParts(strline, ",");
					 fieldToOb.put(parts[1], parts[0]);
					 obToField.put(parts[0], parts[1]);
				 }
				 
				 //Cache methods
				 strline = null;
				 in = MainCommonMod.class.getResourceAsStream("/assets/evil_notch_core/MCP/"+ MinecraftForge.MC_VERSION + "/methods.csv"); 
				 reader = new BufferedReader(new InputStreamReader(in));
				 while ((strline = reader.readLine()) != null) 
				 {
					 String[] parts = LineBase.getParts(strline, ",");
					 methodToOb.put(parts[1], parts[0]);
					 obToMethod.put(parts[0], parts[1]);
				 }
				 
				 strline = null;
				 in = MainCommonMod.class.getResourceAsStream("/assets/evil_notch_core/MCP/" + MinecraftForge.MC_VERSION + "/params.csv"); 
				 reader = new BufferedReader(new InputStreamReader(in));
				 while ((strline = reader.readLine()) != null) 
				 {
					 String[] parts = LineBase.getParts(strline, ",");
					 paramToOb.put(parts[1], parts[0]);
					 obToParam.put(parts[0], parts[1]);
				 }
				}catch(Exception e){e.printStackTrace();}
		}
		public static boolean isTessellatorDrawing(Tessellator te) 
		{
			try{
				return ReflectionHelper.findField(Tessellator.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "isDrawing")).getBoolean(te);
			}catch(Throwable t){t.printStackTrace();}
			return true;
		}

}
