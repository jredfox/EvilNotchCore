package com.EvilNotch.Core.Events;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.ZipFile;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Items.Render.ItemMobSpawnerRender;
import com.EvilNotch.Core.Util.FakeWorld;
import com.EvilNotch.Core.Util.SilentTeleport;
import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Util.EntityUtil;
import com.EvilNotch.Core.Util.Util.NBTUtil;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TickHandler {
	public static int renderTime;
	public static float renderFrame;
	public static File dir = null;
	
	public String player = null;
	public boolean errored = false;
	public NBTTagCompound cached = null;
	public boolean client = false;
	public boolean server = false;
	
	public static int tick_item = 0;
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void tick(TickEvent.PlayerTickEvent e) {
		 if(e.player.worldObj.isRemote)
				return;
		 //Made to clear entity cache NBTTagList,Entity tied to NBTTagList unique to that stack's EntityData
		if(tick_item == 1200 * Config.spawnerClearMinuets)
		{
			ItemMobSpawnerRender.itemstacks.clear();
			ItemMobSpawnerRender.itemstacks = new HashMap();
			tick_item = 0;
		}
		else
			tick_item++;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void inventoryFixer(TickEvent.PlayerTickEvent e) {
		if(MainCommonMod.world instanceof FakeWorld)
			return;//Determines if the world as actually loaded since you don't want it trying to fix invenotry and having that not work because the world isn't loaded yet
	    World worldIn = e.player.getEntityWorld();
	    final EntityPlayer playerIn = e.player;
	    final NBTTagCompound playerNBT = cached;//EntityUtil.JsonToNBT("{Spawns:[],SleepTimer:0s,Attributes:[0:{Base:20.0d,Name:\"generic.maxHealth\",},1:{Base:0.0d,Name:\"generic.knockbackResistance\",},2:{Base:0.10000000149011612d,Name:\"generic.movementSpeed\",},3:{Base:1.0d,Name:\"generic.attackDamage\",},],Invulnerable:0b,PortalCooldown:0,AbsorptionAmount:0.0f,abilities:{invulnerable:1b,mayfly:1b,instabuild:1b,walkSpeed:0.1f,mayBuild:1b,flying:1b,flySpeed:0.05f,},FallDistance:0.0f,DeathTime:0s,HealF:20.0f,XpTotal:0,playerGameType:1,UUID:\"4223a2b1-ed37-4577-adb2-d18eca6411f6\",Motion:[0:0.0d,1:0.0d,2:0.0d,],Health:20s,foodSaturationLevel:0.0f,Air:300s,OnGround:0b,Dimension:1,Rotation:[0:45.001957f,1:55.19992f,],XpLevel:0,Score:0,Sleeping:0b,Pos:[0:100.0d,1:59.0d,2:0.0d,],Fire:-20s,XpP:0.0f,EnderItems:[],foodLevel:20,foodExhaustionLevel:1.9452982f,HurtTime:0s,SelectedItemSlot:1,AttackTime:0s,Inventory:[{id:86,Damage:0,Count:12},{id:2,Damage:0,Count:1,Slot:2}],foodTickTimer:0,}");
	    if (!playerIn.isDead && errored) 
	    {
	        int currentDimension = playerIn.dimension;
	        int dimensionIn = playerNBT.getInteger("Dimension");

	        if (currentDimension != dimensionIn) {
	            if (!worldIn.isRemote) {
	                EntityPlayerMP playerMP = (EntityPlayerMP) playerIn;
	                
	                SilentTeleport teleporter = new SilentTeleport(DimensionManager.getWorld(playerMP.getServerForPlayer().provider.dimensionId)) {
	                    @Override
	                    public boolean placeInExistingPortal(Entity entityIn,double x, double y, double z, float rotationYaw) {
	                        super.placeInExistingPortal(entityIn,entityIn.posX,entityIn.posY,entityIn.posZ, rotationYaw);
	                        playerIn.readFromNBT(playerNBT);
	                        return false;
	                    }
	                };

	                playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, dimensionIn, teleporter);
	            }

	        } else {
	            playerIn.readFromNBT(playerNBT);
	        }
	        
	        if(e.side == Side.CLIENT)
				client = true;
			if(e.side == Side.SERVER)
				server = true;
			if(server && client)
			{
				errored = false;
				cached = null;
				player = null;
				client = false;
				server = false;
			}
			return;//if it is erroed don't do the cache process until is patched!
	    }
	    
	    if(e.player.worldObj.isRemote)
			return;
	    String display = e.player.getCommandSenderName();
		dir = new File(e.player.worldObj.getSaveHandler().getWorldDirectory(),"playerdata/names");
		if(!dir.exists())
			dir.mkdirs();
			
		generateFiles(e.player.worldObj,e.player);//Creates initial cache from player
					
		NBTTagCompound cached_nbt = (NBTTagCompound) EntityUtil.getPlayerFileNBT(display).copy();
		NBTTagCompound nbt = (NBTTagCompound) EntityUtil.getEntityNBT(e.player).copy();
		nbt.setString("UUID", e.player.getUniqueID().toString());

		//Update player if uuid changed with last cached one
		if(hasPlayerUUIDChanged(cached_nbt,nbt))//&& MainCommonMod.isEclispe //Can't fix bug in debug cause uuid changes all the time?
		{
			cached_nbt.setLong("UUIDLeast", nbt.getLong("UUIDLeast"));
			cached_nbt.setLong("UUIDMost", nbt.getLong("UUIDMost"));
			
			System.out.println("Corrupted PlayerData Patching User: " + display);
			System.out.println("NBT:" + cached_nbt);
			EntityUtil.updatePlayerFile(new File(dir,display + ".dat"), nbt);//For Debugging to ensure it works
			errored = true;
			cached = cached_nbt;
			player = display;
		}
		//if(!errored)
		//EntityUtil.updatePlayerFile(new File(dir,display + ".dat"), nbt); actual cache per tick update
	}
	public static boolean hasPlayerUUIDChanged(NBTTagCompound cached,NBTTagCompound nbt) 
	{
		String uuid = cached.getString("UUID");
		String cached_uuid = nbt.getString("UUID");
		if(!uuid.equals(cached_uuid))
			return true;
		return false;
	}

	public static void generateFiles(World w,EntityPlayer player)
	{
		try{
		File file = new File(dir,player.getCommandSenderName() + ".dat");
		if(!file.exists() )
		{
			file.createNewFile();
			NBTTagCompound nbt = EntityUtil.getEntityNBT(player);
			nbt.setString("UUID", player.getUniqueID().toString());
			CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(file));
		}
		}catch(Exception e){e.printStackTrace();}
	}

	@SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == Phase.END) {
            renderTime++;
        }
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        if(event.phase == Phase.START)
            renderFrame = event.renderTickTime;
    }
}
