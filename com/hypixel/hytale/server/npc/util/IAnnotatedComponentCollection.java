package com.hypixel.hytale.server.npc.util;

import javax.annotation.Nullable;

public interface IAnnotatedComponentCollection extends IAnnotatedComponent {
  int componentCount();
  
  @Nullable
  IAnnotatedComponent getComponent(int paramInt);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\np\\util\IAnnotatedComponentCollection.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */