package com.hypixel.hytale.server.npc.blackboard.view.event;

import com.hypixel.hytale.server.npc.entities.NPCEntity;

@FunctionalInterface
public interface IEventCallback<EventType, NotificationType extends EventNotification> {
  void notify(NPCEntity paramNPCEntity, EventType paramEventType, NotificationType paramNotificationType);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\blackboard\view\event\IEventCallback.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */