package com.hypixel.hytale.server.worldgen.cache;

@FunctionalInterface
public interface ExtendedCoordinateObjectFunction<K, T> {
  T compute(K paramK, int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\cache\ExtendedCoordinateCache$ExtendedCoordinateObjectFunction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */