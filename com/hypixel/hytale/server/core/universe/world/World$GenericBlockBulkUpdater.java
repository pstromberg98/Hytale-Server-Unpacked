package com.hypixel.hytale.server.core.universe.world;

import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;

@FunctionalInterface
public interface GenericBlockBulkUpdater<T> {
  void apply(World paramWorld, T paramT, long paramLong, WorldChunk paramWorldChunk, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\World$GenericBlockBulkUpdater.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */