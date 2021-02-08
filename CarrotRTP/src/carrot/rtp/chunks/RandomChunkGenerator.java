package carrot.rtp.chunks;

import carrot.rtp.CarrotRTP;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Random;

public final class RandomChunkGenerator {
    public static Chunk FindRandomChunk(World world, int minX, int minZ, int maxX, int maxZ) {
        Random randomX = new Random();
        Random randomZ = new Random(randomX.nextInt() ^ (System.nanoTime() + 2169420));

        int aX = maxX + (-minX);
        int aZ = maxZ + (-minZ);

        int bX = randomX.nextInt(aX);
        int bZ = randomZ.nextInt(aZ);

        int cX = bX + minX;
        int cZ = bZ + minZ;

        int randomXChunk = (cX) >> 4;
        int randomZChunk = (cZ) >> 4;

        return world.getChunkAt(randomXChunk, randomZChunk);
    }

    public static Chunk FindRandomChunkInBiome(World world, Biome biome, int minX, int minZ, int maxX, int maxZ) {
        Chunk previouslyFoundChunk = world.getChunkAt(0, 0);
        Random randomX = new Random();
        Random randomZ = new Random(randomX.nextInt() ^ (System.nanoTime() + 2169420));
        for (int i = 0; i < CarrotRTP.MaxNumberOfBiomeSearches; i++) {
            int aX = maxX + (-minX);
            int aZ = maxZ + (-minZ);

            int bX = randomX.nextInt(aX);
            int bZ = randomZ.nextInt(aZ);

            int cX = bX + minX;
            int cZ = bZ + minZ;

            int randomXChunk = (cX) >> 4;
            int randomZChunk = (cZ) >> 4;

            previouslyFoundChunk = world.getChunkAt(randomXChunk, randomZChunk);

            Biome randomBiome = world.getBiome(previouslyFoundChunk.getX() << 4, previouslyFoundChunk.getZ() << 4);
            if (randomBiome == biome) {
                return previouslyFoundChunk;
            }
        }
        return previouslyFoundChunk;
    }
}
