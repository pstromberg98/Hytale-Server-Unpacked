package com.hypixel.hytale.server.core.universe.world.worldgen;

import java.util.concurrent.CompletableFuture;

public interface IWorldGenBenchmark {
  void start();
  
  void stop();
  
  CompletableFuture<String> buildReport();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldgen\IWorldGenBenchmark.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */