package com.EvilNotch.Core.Load;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Api.BlockApi;
import com.EvilNotch.Core.Api.MCPMappings;
import com.EvilNotch.Core.Api.ReflectionUtil;
import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Line.ConfigBase;
import com.EvilNotch.Core.Util.Line.LineBase;
import com.EvilNotch.Core.Util.Line.LineDWNF;
import com.EvilNotch.Core.Util.Line.LineDynamic;
import com.EvilNotch.Core.Util.Line.LineDynamicLogic;
import com.EvilNotch.Core.Util.Line.LineEnhanced;
import com.EvilNotch.Core.Util.Line.LineItemStack;
import com.EvilNotch.Core.Util.Line.LineItemStackBase;
import com.EvilNotch.Core.Util.Util.BlockUtil;
import com.EvilNotch.Core.Util.Util.Registry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class LoadBaseConfig {
	private static ArrayList<String> ar_resistance = new ArrayList(5);
	private static ArrayList<String> ar_disableDrops = new ArrayList(5);
	private static ArrayList<String> ar_hardness = new ArrayList(5);
	private static ArrayList<String> ar_harvestLevel = new ArrayList(5);
	private static ArrayList<String> ar_lightLevel = new ArrayList(5);
	private static ArrayList<String> ar_quantityDropped = new ArrayList(5);
	private static ArrayList<String> ar_silk = new ArrayList(5);
	private static ArrayList<String> ar_slip = new ArrayList(5);
	private static ArrayList<String> ar_fire = new ArrayList(5);
	private static ArrayList<String> ar_lightOpacity = new ArrayList(5);
	private static ArrayList<String> ar_mat = new ArrayList(5);
	private static ArrayList<String> ar_mat_info = new ArrayList(5);
	
	public static ConfigBase blastResistance;
	public static ConfigBase disableDrops;
	public static ConfigBase hardness;
	public static ConfigBase harvestLevel;
	public static ConfigBase lightLevel;
	public static ConfigBase quantityDropped;
	public static ConfigBase silkTouchPickup;
	public static ConfigBase slipperiness;
	public static ConfigBase flame;
	public static ConfigBase lightOpacity;
	public static ConfigBase material;
	public static ConfigBase material_info;
	
	public static ArrayList<LineBase> harvestLvl_copy = new ArrayList();
	public static ArrayList<LineBase> disableDrops_list = new ArrayList();
	public static ArrayList<LineBase> quantityDroped_list = new ArrayList();
	public static ArrayList<LineBase> harvestLvl = new ArrayList();
	public static ArrayList<LineBase> silk_tocuh = new ArrayList();

	public static void cacheBlockProperties()
	{
	   blastResistance = new ConfigBase(new File(Config.blockproperties,"BlastResistance.txt"),ar_resistance);
	   disableDrops = new ConfigBase(new File(Config.blockproperties,"DisableDrops.txt"),ar_disableDrops);
	   hardness = new ConfigBase(new File(Config.blockproperties,"Hardness.txt"),ar_hardness);
	   harvestLevel = new ConfigBase(new File(Config.blockproperties,"HarvestLevel.txt"),ar_harvestLevel);
	   lightLevel = new ConfigBase(new File(Config.blockproperties,"LightLevel.txt"),ar_lightLevel);
	   quantityDropped = new ConfigBase(new File(Config.blockproperties,"QuantityBlockDropped.txt"),ar_quantityDropped);
	   silkTouchPickup = new ConfigBase(new File(Config.blockproperties,"CanSilkHarvest.txt"),ar_silk);
	   slipperiness = new ConfigBase(new File(Config.blockproperties,"Slipperiness.txt"),ar_slip);
	   flame = new ConfigBase(new File(Config.blockproperties,"Flammability.txt"),ar_fire);
	   lightOpacity = new ConfigBase(new File(Config.blockproperties,"LightOpacity.txt"),ar_lightOpacity);
	   material = new ConfigBase(new File(Config.blockproperties,"Material.txt"),ar_mat);
	   material_info = new ConfigBase(new File(Config.blockproperties,"MaterialInfo.txt"),ar_mat_info);
	   
	   modifyConfigs();
	}
	private static void modifyConfigs() 
	{
		if(hardness.first_launch)
		{
			hardness.appendLine(new LineEnhanced("\"" + "minecraft:end_portal_frame" + "\" = 5"));
			hardness.appendLine(new LineEnhanced("\"" + "minecraft:bedrock" + "\" = 665"));
			hardness.updateConfig();
		}
		if(harvestLevel.first_launch)
		{
			harvestLevel.appendLine(new LineEnhanced("\"" + "minecraft:end_portal_frame" + "\" = 3"));
			harvestLevel.appendLine(new LineEnhanced("\"" + "minecraft:bedrock" + "\" = 4"));
			harvestLevel.updateConfig();
		}
		if(silkTouchPickup.first_launch)
		{
			silkTouchPickup.appendLine(new LineItemStack("\"" + "minecraft:end_portal_frame" + "\"" + " = true"));
			silkTouchPickup.updateConfig();
		}
		
	}
	public static void setBlockProperties()
	{
		modifyConfigsPost();
		for(LineBase line : harvestLevel.lines)
			harvestLvl_copy.add(line);//Copys values in post init this call here
		
		for(LineBase line : silkTouchPickup.lines)
			silk_tocuh.add(line);//Copys values in post init this call here
		
		//Blast Resistance
		List<LineBase> br = blastResistance.lines;
		for(LineBase line : br)
		{
			Block b = GameRegistry.findBlock(line.modid, line.name);
			if(b != null)
				b.setResistance(((LineItemStack)line).head);
		}
		
		//Populate List for disable drops
		List<LineBase> sl = slipperiness.lines;
		for(LineBase line : sl)
		{
			Block b = GameRegistry.findBlock(line.modid, line.name);
			if(b != null)
				b.slipperiness = ((LineItemStack)line).fhead;
		}
		//Make Block flamable
		List<LineBase> fl = flame.lines;
		for(LineBase line : fl)
		{
			Block b = GameRegistry.findBlock(line.modid, line.name);
			LineDynamic dynamic = (LineDynamic)line;
			if(b != null)
				Blocks.fire.setFireInfo(b, dynamic.values[0], dynamic.values[1]);
		}
		
		//Make blocks transparent or not
		List<LineBase> op = lightOpacity.lines;
		for(LineBase line : op)
		{
			Block b = GameRegistry.findBlock(line.modid, line.name);
			if(b != null)
				b.setLightOpacity( ((LineItemStack)line).head);
		}
		//Make blocks transparent or not
		List<LineBase> mat = material.lines;
		for(LineBase line : mat)
		{
			Block b = GameRegistry.findBlock(line.modid, line.name);
			if(b != null)
			{
				Material material = BlockUtil.getMaterialFromString(((LineItemStack)line).strhead);
				if(material != null)
				{
					System.out.println("Web:" + Registry.materialtoname.get(material));
					BlockApi.setMaterial(b,material);
				}
			}
		}
		//Populate List for disable drops
		List<LineBase> dd = disableDrops.lines;
		for(LineBase line : dd)
		{
			Block b = GameRegistry.findBlock(line.modid, line.name);
			if(b != null)
				disableDrops_list.add(line);
		}
		
		//Hardness
		List<LineBase> hard = hardness.lines;
		for(LineBase line : hard)
		{
			Block b = GameRegistry.findBlock(line.modid, line.name);
			if(b != null)
				b.setHardness(((LineItemStack)line).head);
		}
		//Harvest level sets to 0 and fixes vanilla for the rest
		List<LineBase> hl = harvestLevel.lines;
		for(LineBase line : hl)
		{
			if(!(line instanceof LineDWNF))
			{
				Block b = GameRegistry.findBlock(line.modid, line.name);
				if(b != null)
				{
					harvestLvl.add(line);
					LineItemStack stack = (LineItemStack)line;
					if(stack.meta == -1)
					{
						for(int m=0;m<16;m++)
						{
							if(!BlockUtil.isBlockHarvestModified(harvestLvl_copy,b,m))
								b.setHarvestLevel(BlockUtil.getActualToolFromBlock(b,0),0,m);
							//else
								//System.out.println("modified:" + b.getUnlocalizedName() + " <" + m + ">");
						}
					}
					else
						b.setHarvestLevel(BlockUtil.getActualToolFromBlock(b,stack.meta), 0, stack.meta);//Override all other methods
				}
			}
			else{
				LineDWNF li = (LineDWNF)line;
				String toolclass = li.getLine(0).modid;
				LineItemStack base = (LineItemStack) li.getLine(1);
				Block b = GameRegistry.findBlock(base.modid, base.name);
				if(b != null)
				{
					harvestLvl.add(base);
					if(base.meta != -1)
						b.setHarvestLevel(toolclass, 0,base.meta);//Override all other methods
					else{
						for(int m=0;m<16;m++)
						{
							if(!BlockUtil.isBlockHarvestModified(harvestLvl_copy,b,m))
								b.setHarvestLevel(toolclass,0,m);
						//	else
							//	System.out.println("modified:" + b.getUnlocalizedName() + " " + m);
						}
					}
				}
			}
		}
		//Light Level
		List<LineBase> ll = lightLevel.lines;
		for(LineBase line : ll)
		{
			Block b = GameRegistry.findBlock(line.modid, line.name);
			if(b != null)
				b.setLightLevel(((LineItemStack)line).head);
		}
		//Quantity Dropped
		List<LineBase> qd = quantityDropped.lines;
		for(LineBase line : qd)
			quantityDroped_list.add(line);
		
	}
	
	
	public static void modifyConfigsPost() 
	{
		boolean infoupdate = false;
		Iterator <String> it = Registry.materialmap.keySet().iterator();
		while(it.hasNext())
		{
			String s = "\"" + it.next() + "\"";
			if(!material_info.containsStrLine(s))
			{
				material_info.appendString(s);
				infoupdate = true;
			}
		}
		if(infoupdate)
			material_info.updateConfig();
	}
	/**
	 * Do not use internal loading.
	 * Used for cacheing initial arraylists for configs
	 */
	protected static void cacheArrays()
	{
		ar_resistance.add("#Block Properties For Blast Resistance\r\n#The Parameters Are \"modid:block\" = int\r\n#To Make A Block BlastProof Set it To 6000000\r\n\r\n<Resistance>\r\n\r\n</Resistance>");
		ar_disableDrops.add("#Block Properties For Disable Drops\r\n#The Parameters Are \"modid:block\"\r\n\r\n<DisableDrops>\r\n\r\n</DisableDrops>");
		ar_hardness.add("#Block Properties For Hardness\r\n#The Parameters Are \"modid:block\" = int\r\n\r\n<Hardness>\r\n\r\n</Hardness>");
		ar_harvestLevel.add("#Block Properties For Harvest Level\r\n#The Parameters Are \"modid:block\" = int\r\n\r\n<HarvestLevel>\r\n\r\n</HarvestLevel>");
		ar_lightLevel.add("#Block Properties For Light Level\r\n#The Parameters Are \"modid:block\" = int\r\n#To Disable Lighting Set to 0 To Make really bright set to 1,Higher int = lower light\r\n\r\n<LightLevel>\r\n\r\n</LightLevel>");
		ar_quantityDropped.add("#Block Properties For Quantity Dropped\r\n#The Parameters Are \"modid:block\" = int\r\n\r\n<QuantityDropped>\r\n\r\n</QuantityDropped>");
		ar_silk.add("#Block Properties For SilkTouchDrops\r\n#The Parameters Are \"modid:block\" = boolean(either true or false)\r\n#Use SilkSpawners For SpawnerBlocks Only Intended on Vanilla Logic!\r\n\r\n<CanSilkHarvest>\r\n\r\n</CanSilkHarvest>");
		ar_slip.add("#Block Properties For Slipperiness\r\n#The Parameters Are \"modid:block\" = 0.8F\r\n\r\n<Slipperiness>\r\n\r\n</Slipperiness>");
		ar_fire.add("#Block Properties For Flammability\r\n#The Parameters Are \"modid:block\" = int(encouragement),int(flammibility)\r\n\r\n<Flammability>\r\n\r\n</Flammability>");
		ar_lightOpacity.add("#Block Properties For Light Opacity\r\n#The Parameters Are \"modid:block\" = int\r\n#Light Opacity: 0 lets all light through and 255 is default blocks light Opacity\r\n\r\n<LightOpacity>\r\n\r\n</LightOpacity>");
		ar_mat.add("#Block Properties For Material\r\n#The Parameters Are \"modid:block\" = \"materialname\" or \"modid:block\" = \"pathtofield\"\r\n#Example:\"modid:block\" = \"net.minecraft.block.material.Material.wood\"\r\n#Example2:\"modid:block\" = \"wood\"\r\n#Material Name List:[\"air\",\"grass\",\"ground\",\"wood\",\"rock\",\"iron\",\"anvil\",\"water\",\"lava\",\"leaves\",\"plants\",\"vine\",\"sponge\",\"cloth\",\"fire\",\"sand\",\"circuits\",\"carpet\",\"glass\",\"redstoneLight\",\"tnt\",\"coral\",\"ice\",\"packedIce\",\"snow\",\"craftedSnow\",\"cactus\",\"clay\",\"gourd\",\"dragonEgg\",\"portal\",\"cake\",\"web\"]\r\n\r\n<Material>\r\n\r\n</Material>");
		ar_mat_info.add("#Material Class Variables Info Use for Material.txt\r\n\r\n<MaterialInfo>\r\n\r\n</MaterialInfo>");
	}

}
