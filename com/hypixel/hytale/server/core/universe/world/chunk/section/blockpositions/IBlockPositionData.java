package com.hypixel.hytale.server.core.universe.world.chunk.section.blockpositions;

import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;

public interface IBlockPositionData {
  BlockSection getChunkSection();
  
  int getBlockType();
  
  int getX();
  
  int getY();
  
  int getZ();
  
  double getXCentre();
  
  double getYCentre();
  
  double getZCentre();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\chunk\section\blockpositions\IBlockPositionData.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */