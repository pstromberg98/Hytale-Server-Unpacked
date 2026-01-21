package com.hypixel.hytale.server.npc.sensorinfo;

import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.ParameterProvider;
import javax.annotation.Nullable;

public interface InfoProvider {
  @Nullable
  IPositionProvider getPositionProvider();
  
  @Nullable
  ParameterProvider getParameterProvider(int paramInt);
  
  @Nullable
  <E extends ExtraInfoProvider> E getExtraInfo(Class<E> paramClass);
  
  <E extends ExtraInfoProvider> void passExtraInfo(E paramE);
  
  @Nullable
  <E extends ExtraInfoProvider> E getPassedExtraInfo(Class<E> paramClass);
  
  boolean hasPosition();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\sensorinfo\InfoProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */