package com.EvilNotch.Core.Gui;

import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Api.MCPMappings;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.world.World;

public class GuiRepairEnhanced extends GuiRepair{

	public GuiRepairEnhanced(InventoryPlayer i, World w, int x, int y,int z) 
	{
		super(i, w, x, y, z);
		this.inventorySlots = new ContainerRepairFixed(i, w, x, y, z, Minecraft.getMinecraft().thePlayer);
		try {
			ReflectionHelper.findField(GuiRepair.class,MCPMappings.getFeildName(MainCommonMod.isEclipse,  "field_147092_v")).set(this, this.inventorySlots);
		}catch(Exception e){e.printStackTrace();}
	}

}
