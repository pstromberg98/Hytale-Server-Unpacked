package com.hypixel.hytale.server.core.io;

@FunctionalInterface
public interface NetworkSerializer<Type, Packet> {
  Packet toPacket(Type paramType);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\io\NetworkSerializer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */