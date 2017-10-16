package com.EvilNotch.Core.Events;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.ZipFile;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Api.MCPMappings;
import com.EvilNotch.Core.Api.NBTPathApi;
import com.EvilNotch.Core.Api.ReflectionUtil;
import com.EvilNotch.Core.Interfaces.BasicSpawner;
import com.EvilNotch.Core.Interfaces.BasicSpawnerEntry;
import com.EvilNotch.Core.Items.Render.ItemMobSpawnerRender;
import com.EvilNotch.Core.TileEntity.TileVMobSpawner;
import com.EvilNotch.Core.TileEntity.Render.InterfacialSpawnerRender;
import com.EvilNotch.Core.Util.FakeWorld;
import com.EvilNotch.Core.Util.PerfectSpawnTeleporter;
import com.EvilNotch.Core.Util.SilentTeleport;
import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Util.EntityUtil;
import com.EvilNotch.Core.Util.Util.NBTUtil;
import com.EvilNotch.Core.Util.Util.TileEntityUtil;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class TickHandler {
	public static int renderTime;
	public static float renderFrame;
	public static File dir = null;
	
	public boolean errored = false;
	public NBTTagCompound cached = null;
	public static int tick_player = 0;
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void inventoryFixer(TickEvent.PlayerTickEvent e) {
		if(e.player.worldObj.isRemote && errored)
		{
			e.player.mountEntity((Entity)null);//if is mounted remove from mount
			e.player.readFromNBT(cached);
			e.player.respawnPlayer();
			errored = false;
			cached = null;
		}
	    if(e.player.worldObj.isRemote)
			return;
	    	
	    tick_player++;
	    String display = e.player.getCommandSenderName();
	    EntityPlayerMP player = (EntityPlayerMP) e.player;
		dir = new File(e.player.worldObj.getSaveHandler().getWorldDirectory(),"playerdata/names");
		if(!dir.exists())
			dir.mkdirs();
		boolean hasPlayerData = EntityUtil.getPlayerFile(player, true).exists();
		boolean hasCachedData = EntityUtil.getPlayerFile(player, false).exists();
		NBTTagCompound nbt = (NBTTagCompound) EntityUtil.getEntityNBT(e.player).copy();

		if(!hasPlayerData && !hasCachedData)
		{
			EntityUtil.updatePlayerFile(new File(dir.getParent(),player.getUniqueID().toString() + ".dat"), nbt);//For Debugging to ensure it works
			hasPlayerData = true;
		}
		generateFiles(e.player.worldObj,e.player);//Creates initial cache from player
					
		NBTTagCompound cached_nbt = (NBTTagCompound) EntityUtil.getPlayerFileNBT(display);
		if(cached_nbt == null)
			return;
		nbt.setString("UUID", e.player.getUniqueID().toString());
		
		//Update player if uuid changed with last cached one
		if(hasPlayerUUIDChanged(cached_nbt,nbt) || !hasPlayerData)//&& MainCommonMod.isEclispe //Can't fix bug in debug cause uuid changes all the time?
		{
			player.mountEntity((Entity)null);//if is mounted remove from mount
			cached_nbt.setLong("UUIDLeast", nbt.getLong("UUIDLeast"));
			cached_nbt.setLong("UUIDMost", nbt.getLong("UUIDMost"));
			
			System.out.println("Corrupted PlayerData Patching User: " + display);
			System.out.println("NBT:" + cached_nbt);
			
			if(!hasPlayerData)
				EntityUtil.updatePlayerFile(new File(dir.getParent(),player.getUniqueID().toString() + ".dat"), cached_nbt);//For Debugging to ensure it works
			
			cached_nbt.setString("UUID", e.player.getUniqueID().toString());
			EntityUtil.updatePlayerFile(new File(dir,display + ".dat"), cached_nbt);//For Debugging to ensure it works
			errored = true;
			cached = cached_nbt;
			int currentdim = e.player.dimension;
			int traveldim = cached_nbt.getInteger("Dimension");
			
			//Spawn Entity into world
			if(currentdim != traveldim)
				MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player,traveldim, new PerfectSpawnTeleporter(player.mcServer.worldServerForDimension(currentdim)));
			
			//Register Vanilla and change location
			e.player.readFromNBT(cached_nbt);
			player.playerNetServerHandler.setPlayerLocation(player.posX,player.posY,player.posZ, e.player.cameraYaw, e.player.cameraPitch);
			
			//Make compatibility for uncommon methods like AoA
			FMLCommonHandler.instance().firePlayerRespawnEvent(player);
			
			NBTTagList nbttaglist = cached_nbt.getTagList("Pos", 6);
			double x = nbttaglist.func_150309_d(0);
			double y = nbttaglist.func_150309_d(1);
			double z = nbttaglist.func_150309_d(2);
			boolean flag = player.dimension != traveldim || (int)player.posX != (int)x || (int)player.posY != (int)y || (int)player.posZ != (int)z;
			if(flag)
			{
				MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player,traveldim, new PerfectSpawnTeleporter(player.mcServer.worldServerForDimension(currentdim)));
				player.playerNetServerHandler.setPlayerLocation(x,y,z, e.player.cameraYaw, e.player.cameraPitch);
				if(Config.Debug)
					EntityUtil.printChat(player, EnumChatFormatting.RED, EnumChatFormatting.YELLOW, "Mod Incompatabilty Detected!");
			}
			
			if(currentdim == traveldim && !flag)
			{
				EntityJoinWorldEvent joinevent = new EntityJoinWorldEvent(player, player.worldObj);
				MinecraftForge.EVENT_BUS.post(joinevent);//Make player updates if the dimension change does do it
			}
			e.player.readFromNBT(cached_nbt);
			if(hasPlayerData)
				EntityUtil.printChat(player, EnumChatFormatting.DARK_RED, EnumChatFormatting.GOLD, "Player Corruption Detected Reverting To Cache's PlayerData");
			else
				EntityUtil.printChat(player, EnumChatFormatting.DARK_RED, EnumChatFormatting.GOLD, "Player Data Missing Using Cache");
		}
		//Since player event savetofile didn't do shit I do it manually here
		if(!errored && tick_player == Config.playerCacheTicks)
		{
			tick_player = 0;
			EntityUtil.updatePlayerFile(new File(dir,display + ".dat"), nbt); //actual cache every two minuets in game
			if(Config.Debug)
				EntityUtil.printChat(player, EnumChatFormatting.DARK_PURPLE, EnumChatFormatting.GOLD, "Cached Player's PlayerData Ticks:" + Config.playerCacheTicks);
		}
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
	public static int delay = 0;
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void tick(TickEvent.PlayerTickEvent event) {
		if(event.player.worldObj.isRemote)
			return;
		if(delay < 40)
		{
			delay++;
			return;
		}
		else
			delay = 0;
		try{
//		NBTTagCompound nbt = NBTUtil.JsonToNBT("{display:{id:\"EntityHorse\",EntityNBT:{Size:3,SkeletonType:1}},render:255,isVillager:true,SkeletonType:1}");
//		NBTTagCompound nbt2 = NBTUtil.JsonToNBT("{display:{id:\"EntityHorse\",EntityNBT:{Size:3,SkeletonType:1}},render:254,isVillager:true,SkeletonType:1}");
		NBTTagCompound nbt = NBTUtil.JsonToNBT("{render:2,display:{id:1,tile:{id:10} }}");
		NBTTagCompound nbt2 = NBTUtil.JsonToNBT("{render:2,display:{id:40,id2:10,tile:{render:2,name:\"a\"} }}");
		NBTTagCompound nbt3 = NBTUtil.JsonToNBT("{ench:[{id:33,lvl:1,EntityNBT:{display:\"name\"},Pos:[0,51,2]},{id:33,lvl:1,glint:100}]}");
		NBTTagCompound nbt4 = NBTUtil.JsonToNBT("{display:{id:40,tile:{} } }");
		NBTTagCompound nbt5 = NBTUtil.JsonToNBT("{ ench:[{id:\"name\",tile:{} },{id:33,lvl:1}] }");
		NBTTagCompound nbt6 = NBTUtil.JsonToNBT("{ ench:[ [ [2.0d] ] ]  }");
//		NBTTagCompound nbt7 = NBTUtil.JsonToNBT("{ ench:[ {Pos:[0,51,10],id:22},{} ],name:\"modid:block\"  }");
		NBTPathApi api = new NBTPathApi(nbt);
		NBTPathApi api2 = new NBTPathApi(nbt2);
		NBTPathApi api3 = new NBTPathApi(nbt3);
		NBTPathApi api4 = new NBTPathApi(nbt4);
		NBTPathApi api5 = new NBTPathApi(nbt5);
		NBTPathApi api6 = new NBTPathApi(nbt6);
//		System.out.println(NBTUtil.getNBTFromPath("ench \"0:\"/Pos", nbt6));
//		System.out.println(NBTUtil.getNBTFromPath("ench \"0:\"", nbt5));
		byte[] bytes = {(byte)0,(byte)120,(byte)1};
		NBTTagByteArray arr = new NBTTagByteArray(bytes);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("bytes", arr);
		
//		System.out.println(api6);
//		System.out.println("NBT:" + NBTPathApi.compilePaths(api6));
//		Entity e = EntityList.createEntityByName("Skeleton", null);
//		EntityUtil.readFromNBTSafely(e, NBTUtil.JsonToNBT("{SkeletonType:1}"));
//		System.out.println(EntityUtil.getEntityNBT(e) );
//		System.out.println(nbt6 );
//		NBTUtil.getNBTFromPath("ench \"0:\"", nbt3);
//		System.out.println("Path:" + NBTUtil.getNBTFromPath("ench \"0:\"", nbt3));
//		System.out.println("Path:" + NBTUtil.getNBTFromPath("ench \"0:\"/Pos", nbt3));
//		System.out.println(api4);
//		api4.removeTag("display",false);
//		System.out.println(api3);
//		api3.removeTag("render");
//		NBTPathApi api2 = new NBTPathApi(NBTUtil.JsonToNBT("{display:[0,2,2]}"));
		}catch(Exception e){e.printStackTrace();}
	}
	
	@SubscribeEvent
    public void rotateSpawners(TickEvent.ClientTickEvent event) {
		 if(event.phase != Phase.END)
			return;
		if(Minecraft.getMinecraft().currentScreen != null)
		{
			if(Minecraft.getMinecraft().currentScreen.getClass().equals(GuiIngameMenu.class))
				return;
		}
		Iterator it = InterfacialSpawnerRender.tiles.keySet().iterator();
		while(it.hasNext())
		{
			TileEntity tile = (TileEntity) it.next();
			BasicSpawnerEntry e = InterfacialSpawnerRender.tiles.get(tile);
			NBTTagCompound nbt = TileEntityUtil.getTileNBT(tile);
			int delay = e.spawner.getDelay(nbt);
			e.updateDelay(delay);	
		}
	}
	
	public static int tick_item = 0;
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void cacheClear(TickEvent.PlayerTickEvent e) {
		 
	  //Attempt to remove stuff from the hashmap keeps occasional throwing exceptions unsure if it is fixed
	  try{
		  World w = e.player.worldObj;
		 if(w.isRemote)
		 {
			Iterator it = InterfacialSpawnerRender.tiles.keySet().iterator();
			while(it.hasNext())
			{
				TileEntity tile = (TileEntity) it.next();
				
				if(!w.loadedTileEntityList.contains(tile))
				{
					BasicSpawnerEntry map = InterfacialSpawnerRender.tiles.get(tile);
					ArrayList<Entity> ents = map.ents;
					for(Entity ee : ents)
						ee.setDead();
					it.remove();
					if(Config.Debug)
						System.out.println("Removed TileEntity" + tile.xCoord + " " + tile.yCoord + " " + tile.zCoord);
				}
			}
		}
	  }catch(Throwable t){t.printStackTrace();}
	 
	   if(e.player.worldObj.isRemote)
			return;
		
		 //Made to clear entity cache NBTTagList,Entity tied to NBTTagList unique to that stack's EntityData
	  if(tick_item == 1200 * Config.spawnerClearMinuets)
	  {
		int stacks = ItemMobSpawnerRender.itemstacks.size();
		int tiles = InterfacialSpawnerRender.tiles.size();
		try{
			Iterator<Map.Entry<TileEntity, BasicSpawnerEntry>> it = InterfacialSpawnerRender.tiles.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry<TileEntity, BasicSpawnerEntry> pair = it.next();
				BasicSpawnerEntry map = pair.getValue();
				ArrayList<Entity> ents = map.ents;
				for(Entity ee : ents)
					ee.setDead();
			}
		}catch(Throwable t){t.printStackTrace();}
		ItemMobSpawnerRender.itemstacks.clear();
		ItemMobSpawnerRender.itemstacks = new HashMap();
		InterfacialSpawnerRender.tiles.clear();
		InterfacialSpawnerRender.tiles = new HashMap();
		tick_item = 0;
			
		if(Config.Debug)
		{
		  EntityUtil.printChat(e.player, EnumChatFormatting.DARK_AQUA, EnumChatFormatting.YELLOW, "Cleared " + stacks + " Item Arrays<Entity>");
		  EntityUtil.printChat(e.player, EnumChatFormatting.DARK_AQUA, EnumChatFormatting.YELLOW, "Cleared " + tiles + " Tile Arrays<Entity>");
		}
	  }
	  else
		  tick_item++;
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
