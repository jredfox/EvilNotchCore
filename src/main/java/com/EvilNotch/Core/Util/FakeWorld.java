package com.EvilNotch.Core.Util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.*;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FakeWorld extends World {
    public static final WorldSettings worldSettings = new WorldSettings(0, GameType.SURVIVAL, true, false, WorldType.DEFAULT);
    public static final WorldInfo worldInfo = new WorldInfo(worldSettings, "just_enough_resources_fake");
    public static final FakeSaveHandler saveHandler = new FakeSaveHandler();
    public static final WorldProvider worldProvider = new WorldProvider() {
        @Override
        public boolean isSurfaceWorld()
        {
            return true;
        }

        @Override
        public long getWorldTime() {
            return worldInfo.getWorldTime();
        }

		@Override
		public String getDimensionName() {
			return "Overworld";
		}
    };

    public FakeWorld() {
        super(saveHandler, "evil_notch_core_fake",worldSettings, worldProvider, new Profiler());
        this.difficultySetting = EnumDifficulty.NORMAL;
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return new IChunkProvider() {
            @Nullable
            @Override
            public Chunk loadChunk(int x, int z) {
                return null;
            }

            @Override
            public Chunk provideChunk(int x, int z) {
                return null;
            }

            @Override
            public String makeString() {
                return null;
            }

            @Override
            public boolean chunkExists(int p_191062_1_, int p_191062_2_) {
                return false;
            }

			@Override
			public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean unloadQueuedChunks() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean canSave() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_,
					int p_73155_4_) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_,
					int p_147416_5_) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getLoadedChunkCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void recreateStructures(int p_82695_1_, int p_82695_2_) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void saveExtraData() {
				// TODO Auto-generated method stub
				
			}
        };
    }

    /**
     * Returns whether a chunk exists at chunk coordinates x, y
     */
    @Override
    protected boolean chunkExists(int p_72916_1_, int p_72916_2_)
    {
    	return false;
    }

    private static class FakeSaveHandler implements ISaveHandler {

        @Override
        public WorldInfo loadWorldInfo() {
            return worldInfo;
        }

        @Override
        public void checkSessionLock() throws MinecraftException {

        }

        @Override
        public IChunkLoader getChunkLoader(WorldProvider provider) {
            return new IChunkLoader() {
                @Nullable
                @Override
                public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
                    return null;
                }

                @Override
                public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException {

                }

                @Override
                public void saveExtraChunkData(World worldIn, Chunk chunkIn) {

                }

                @Override
                public void chunkTick() {

                }

                @Override
                public void saveExtraData() {

                }

     
            };
        }

        @Override
        public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {

        }

        @Override
        public void saveWorldInfo(WorldInfo worldInformation) {

        }

        @Override
        public IPlayerFileData getSaveHandler() {
            return new IPlayerFileData() {
                @Override
                public void writePlayerData(EntityPlayer player) {

                }

                @Override
                public NBTTagCompound readPlayerData(EntityPlayer player) {
                    return new NBTTagCompound();
                }

                @Override
                public String[] getAvailablePlayerDat() {
                    return new String[0];
                }
            };
        }

        @Override
        public void flush() {

        }

        @Override
        public File getWorldDirectory() {
            return null;
        }

        @Override
        public File getMapFileFromName(String mapName) {
            return null;
        }

        @Override
        public String getWorldDirectoryName() {
            return null;
        }
    }

	@Override
	protected int func_152379_p() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Entity getEntityByID(int p_73045_1_) {
		// TODO Auto-generated method stub
		return null;
	}
}