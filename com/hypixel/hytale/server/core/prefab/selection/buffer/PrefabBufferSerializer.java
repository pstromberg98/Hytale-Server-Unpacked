package com.hypixel.hytale.server.core.prefab.selection.buffer;

import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;

public interface PrefabBufferSerializer<T> {
  T serialize(PrefabBuffer paramPrefabBuffer);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\prefab\selection\buffer\PrefabBufferSerializer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */