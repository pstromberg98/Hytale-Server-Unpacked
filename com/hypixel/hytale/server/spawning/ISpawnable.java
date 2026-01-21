package com.hypixel.hytale.server.spawning;

import javax.annotation.Nonnull;

public interface ISpawnable {
  @Nonnull
  String getIdentifier();
  
  @Nonnull
  SpawnTestResult canSpawn(@Nonnull SpawningContext paramSpawningContext);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\spawning\ISpawnable.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */