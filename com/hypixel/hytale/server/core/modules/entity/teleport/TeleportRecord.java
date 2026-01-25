/*    */ package com.hypixel.hytale.server.core.modules.entity.teleport;
/*    */ 
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.math.vector.Location;
/*    */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.time.Duration;
/*    */ import javax.annotation.Nullable;
/*    */ import org.checkerframework.checker.nullness.compatqual.NullableDecl;
/*    */ 
/*    */ 
/*    */ public class TeleportRecord
/*    */   implements Component<EntityStore>
/*    */ {
/*    */   private Entry lastTeleport;
/*    */   
/*    */   public static ComponentType<EntityStore, TeleportRecord> getComponentType() {
/* 19 */     return EntityModule.get().getTeleportRecordComponentType();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Entry getLastTeleport() {
/* 26 */     return this.lastTeleport;
/*    */   }
/*    */   
/*    */   public void setLastTeleport(Entry lastTeleport) {
/* 30 */     this.lastTeleport = lastTeleport;
/*    */   }
/*    */   
/*    */   public boolean hasElapsedSinceLastTeleport(Duration duration) {
/* 34 */     return hasElapsedSinceLastTeleport(System.nanoTime(), duration);
/*    */   }
/*    */   
/*    */   public boolean hasElapsedSinceLastTeleport(long nowNanos, Duration duration) {
/* 38 */     if (this.lastTeleport == null) return true; 
/* 39 */     long elapsedNanos = nowNanos - this.lastTeleport.timestampNanos();
/* 40 */     return (elapsedNanos >= duration.toNanos());
/*    */   }
/*    */ 
/*    */   
/*    */   @NullableDecl
/*    */   public Component<EntityStore> clone() {
/* 46 */     TeleportRecord clone = new TeleportRecord();
/* 47 */     clone.lastTeleport = this.lastTeleport;
/* 48 */     return clone;
/*    */   }
/*    */   public static final class Entry extends Record { private final Location origin; private final Location destination; private final long timestampNanos;
/* 51 */     public Entry(Location origin, Location destination, long timestampNanos) { this.origin = origin; this.destination = destination; this.timestampNanos = timestampNanos; } public final String toString() { // Byte code:
/*    */       //   0: aload_0
/*    */       //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/server/core/modules/entity/teleport/TeleportRecord$Entry;)Ljava/lang/String;
/*    */       //   6: areturn
/*    */       // Line number table:
/*    */       //   Java source line number -> byte code offset
/*    */       //   #51	-> 0
/*    */       // Local variable table:
/*    */       //   start	length	slot	name	descriptor
/* 51 */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/modules/entity/teleport/TeleportRecord$Entry; } public Location origin() { return this.origin; } public final int hashCode() { // Byte code:
/*    */       //   0: aload_0
/*    */       //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/server/core/modules/entity/teleport/TeleportRecord$Entry;)I
/*    */       //   6: ireturn
/*    */       // Line number table:
/*    */       //   Java source line number -> byte code offset
/*    */       //   #51	-> 0
/*    */       // Local variable table:
/*    */       //   start	length	slot	name	descriptor
/*    */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/modules/entity/teleport/TeleportRecord$Entry; } public final boolean equals(Object o) { // Byte code:
/*    */       //   0: aload_0
/*    */       //   1: aload_1
/*    */       //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/server/core/modules/entity/teleport/TeleportRecord$Entry;Ljava/lang/Object;)Z
/*    */       //   7: ireturn
/*    */       // Line number table:
/*    */       //   Java source line number -> byte code offset
/*    */       //   #51	-> 0
/*    */       // Local variable table:
/*    */       //   start	length	slot	name	descriptor
/*    */       //   0	8	0	this	Lcom/hypixel/hytale/server/core/modules/entity/teleport/TeleportRecord$Entry;
/* 51 */       //   0	8	1	o	Ljava/lang/Object; } public Location destination() { return this.destination; } public long timestampNanos() { return this.timestampNanos; }
/*    */      }
/*    */ 
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\teleport\TeleportRecord.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */