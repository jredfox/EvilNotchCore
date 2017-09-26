package com.EvilNotch.Core.Proxy;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.Events.GuiEvent;
import com.EvilNotch.Core.Items.Render.ItemMobSpawnerRender;
import com.EvilNotch.Core.Load.LoadOptions;
import com.EvilNotch.Core.Load.LoadRegister;
import com.EvilNotch.Core.TileEntity.TileVMobSpawner;
import com.EvilNotch.Core.TileEntity.Render.TileVMobSpawnerRender;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ServerProxy{
	
	@Override
	public void preinit()
	{
		LoadOptions.loadOptions();
	}
	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(new GuiEvent());//Client Events only register on clients side else server crashes
		ClientRegistry.bindTileEntitySpecialRenderer(TileVMobSpawner.class, new TileVMobSpawnerRender());
	    // ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMobSpawner.class, new TileVMobSpawnerRender());
	}
	@Override
	public void postinit()
	{
		if(Config.spawnerNEI_Models)
		{
		   MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(LoadRegister.mob_spawner), new ItemMobSpawnerRender(LoadRegister.mob_spawner));
		   MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.mob_spawner), new ItemMobSpawnerRender(Blocks.mob_spawner));
		}
	}

}
