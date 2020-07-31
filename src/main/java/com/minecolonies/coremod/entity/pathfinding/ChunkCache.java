package com.minecolonies.coremod.entity.pathfinding;

import com.minecolonies.api.util.WorldUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ChunkCache implements IWorldReader
{
    protected int       chunkX;
    protected int       chunkZ;
    protected Chunk[][] chunkArray;
    /**
     * set by !chunk.getAreLevelsEmpty
     */
    protected boolean   empty;
    /**
     * Reference to the World object.
     */
    protected World     world;

    public ChunkCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn)
    {
        this.world = worldIn;
        this.chunkX = posFromIn.getX() - subIn >> 4;
        this.chunkZ = posFromIn.getZ() - subIn >> 4;
        int i = posToIn.getX() + subIn >> 4;
        int j = posToIn.getZ() + subIn >> 4;
        this.chunkArray = new Chunk[i - this.chunkX + 1][j - this.chunkZ + 1];
        this.empty = true;

        for (int k = this.chunkX; k <= i; ++k)
        {
            for (int l = this.chunkZ; l <= j; ++l)
            {
                if (WorldUtil.isChunkLoaded(world, new ChunkPos(k, l)))
                {
                    this.chunkArray[k - this.chunkX][l - this.chunkZ] = worldIn.getChunk(k, l);
                }
            }
        }
    }

    /**
     * set by !chunk.getAreLevelsEmpty
     *
     * @return if so.
     */
    @OnlyIn(Dist.CLIENT)
    public boolean isEmpty()
    {
        return this.empty;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(@NotNull BlockPos pos)
    {
        return this.getTileEntity(pos, Chunk.CreateEntityType.CHECK); // Forge: don't modify world from other threads
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos, Chunk.CreateEntityType createType)
    {
        int i = (pos.getX() >> 4) - this.chunkX;
        int j = (pos.getZ() >> 4) - this.chunkZ;
        if (!withinBounds(i, j))
        {
            return null;
        }
        return this.chunkArray[i][j].getTileEntity(pos, createType);
    }

    @NotNull
    @Override
    public BlockState getBlockState(BlockPos pos)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            int i = (pos.getX() >> 4) - this.chunkX;
            int j = (pos.getZ() >> 4) - this.chunkZ;

            if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length)
            {
                Chunk chunk = this.chunkArray[i][j];

                if (chunk != null)
                {
                    return chunk.getBlockState(pos);
                }
            }
        }

        return Blocks.AIR.getDefaultState();
    }

    @Override
    public FluidState getFluidState(final BlockPos pos)
    {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Biome getBiome(BlockPos pos)
    {
        return Biomes.PLAINS;
    }

    @Override
    public Biome getNoiseBiomeRaw(final int x, final int y, final int z)
    {
        return null;
    }

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks material is set to air, meaning it is possible for non-vanilla
     * blocks to still pass this check.
     */
    @Override
    public boolean isAirBlock(BlockPos pos)
    {
        BlockState state = this.getBlockState(pos);
        return state.getBlock().isAir(state, this, pos);
    }

    @Nullable
    @Override
    public IChunk getChunk(final int x, final int z, final ChunkStatus requiredStatus, final boolean nonnull)
    {
        return null;
    }

    @Override
    public boolean chunkExists(final int chunkX, final int chunkZ)
    {
        return false;
    }

    @Override
    public BlockPos getHeight(final Heightmap.Type heightmapType, final BlockPos pos)
    {
        return null;
    }

    @Override
    public int getHeight(final Heightmap.Type heightmapType, final int x, final int z)
    {
        return 0;
    }

    @Override
    public int getSkylightSubtracted()
    {
        return 0;
    }

    @Override
    public BiomeManager getBiomeManager()
    {
        return null;
    }

    @Override
    public WorldBorder getWorldBorder()
    {
        return null;
    }

    @Override
    public boolean checkNoEntityCollision(@Nullable final Entity entityIn, final VoxelShape shape)
    {
        return false;
    }

    @Override
    public Stream<VoxelShape> func_230318_c_(
      @Nullable final Entity entity, final AxisAlignedBB axisAlignedBB, final Predicate<Entity> predicate)
    {
        return null;
    }

    @Override
    public int getStrongPower(BlockPos pos, Direction direction)
    {
        return this.getBlockState(pos).getStrongPower(this, pos, direction);
    }

    @Override
    public boolean isRemote()
    {
        return false;
    }

    @Override
    public int getSeaLevel()
    {
        return 0;
    }

    @Override
    public DimensionType func_230315_m_()
    {
        return null;
    }

    private boolean withinBounds(int x, int z)
    {
        return x >= 0 && x < chunkArray.length && z >= 0 && z < chunkArray[x].length && chunkArray[x][z] != null;
    }

    @Override
    public float func_230487_a_(final Direction direction, final boolean b)
    {
        return 0;
    }

    @Override
    public WorldLightManager getLightManager()
    {
        return null;
    }
}
