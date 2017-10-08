package com.EvilNotch.Core.Events;

import com.EvilNotch.Core.Gui.GuiRepairEnhanced;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;

public class GuiEvent {
	
	//Disables GUI BUTTONS
	@SubscribeEvent
	public void test(GuiOpenEvent e)
	{
		if(e.gui == null || e.gui.getClass() == null)
			return;
		if(!e.gui.getClass().equals(GuiRepair.class))
			return;
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		//e.gui = new GuiRepairEnhanced(p.inventory,p.worldObj,(int)p.posX,(int)p.posY,(int)p.posZ);
	}

}
