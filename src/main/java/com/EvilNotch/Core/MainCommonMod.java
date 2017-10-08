package com.EvilNotch.Core;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import com.EvilNotch.Core.Api.FieldAcess;
import com.EvilNotch.Core.Api.MCPMappings;
import com.EvilNotch.Core.Api.ReflectionUtil;
import com.EvilNotch.Core.Biome.Swamp;
import com.EvilNotch.Core.Blocks.BlockOldMushroom;
import com.EvilNotch.Core.Events.EventBlock;
import com.EvilNotch.Core.Events.EventEnhancedVanilla;
import com.EvilNotch.Core.Items.EmptyMap;
import com.EvilNotch.Core.Items.ItemLeatherArmor;
import com.EvilNotch.Core.Items.Render.ItemMobSpawnerRender;
import com.EvilNotch.Core.Load.LoadBaseConfig;
import com.EvilNotch.Core.Load.LoadCommon;
import com.EvilNotch.Core.Load.LoadOptions;
import com.EvilNotch.Core.Load.LoadRegister;
import com.EvilNotch.Core.Proxy.ModReference;
import com.EvilNotch.Core.Proxy.ServerProxy;
import com.EvilNotch.Core.TileEntity.TileVMobSpawner;
import com.EvilNotch.Core.TileEntity.TileVFurnace;
import com.EvilNotch.Core.TileEntity.TileVMobSpawner;
import com.EvilNotch.Core.TileEntity.Render.InterfacialSpawnerRender;
import com.EvilNotch.Core.TileEntity.Render.InterfacialSpawnerRender;
import com.EvilNotch.Core.Util.FakeWorld;
import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Line.LineBase;
import com.EvilNotch.Core.Util.Line.LineItemStack;
import com.EvilNotch.Core.Util.Util.BlockUtil;
import com.EvilNotch.Core.Util.Util.EntityUtil;
import com.EvilNotch.Core.Util.Util.Registry;
import com.EvilNotch.Core.Util.Util.Util;
import com.sun.jmx.snmp.Timestamp;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;



@Mod(modid = ModReference.MODID, name = ModReference.NAME, version = ModReference.VERSION)
public class MainCommonMod
{
    public static boolean isEclipse = false;
	public static boolean resoucePackOld = false;//if 1.2.5 texture pack is loaded
	public static World world = null;//Fake world to create valid entities in post init
	@SidedProxy(clientSide = ModReference.CLIENT_PROXY_CLASS, serverSide = ModReference.Server_PROXY_CLASS)
	public static ServerProxy proxy;
	@Mod.Instance(ModReference.MODID)
	public static MainCommonMod INSTANCE;
	
	@EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
		world = new FakeWorld();//Galacticraft fix for some reason ASM every world class and caused a crash
		new ForgeHooks(); //Bug Fixes for block hardness/harvest level properties
	   	FieldAcess.CacheMCP();
	   	isEclipse = isDeObfucscated();
	   	FieldAcess.CachePrivateFeilds();
		LoadCommon.preinit(event);
    }
	
	@EventHandler
    public void init(FMLInitializationEvent event)
    {
		new ForgeHooks(); //Bug Fixes for block hardness/harvest level properties
	   	FieldAcess.CachePrivateFeilds();//Cache private fields and update them
        LoadCommon.init();
    }
	
	@EventHandler
    public void postinit(FMLPostInitializationEvent event)
    {
	   	FieldAcess.CachePrivateFeilds();//Cache private fields and update them
	   	LoadCommon.post();
    }
	@EventHandler
    public void finished(FMLLoadCompleteEvent event)
    {
		for(int i=0;i<10;i++)
		{
			
		}
		if(Config.Debug)
		{
			System.out.println(EntityUtil.ents_worldneedy);
			for(String s : EntityUtil.end_ents)
				System.out.println("EndEnt:" + s);
		}
    }
    
	public static boolean isDeObfucscated()
    {
    	try{
    		ReflectionHelper.findField(Block.class, FieldAcess.fieldToOb.get("blockHardness"));
    		return false;
    	}
    	catch(Exception e){return true;}
    }
	
}
