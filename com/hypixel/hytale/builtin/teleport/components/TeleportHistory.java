/*     */ package com.hypixel.hytale.builtin.teleport.components;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TeleportHistory
/*     */   implements Component<EntityStore>
/*     */ {
/*     */   private static final int MAX_TELEPORT_HISTORY = 100;
/*  34 */   private static final Message MESSAGE_COMMANDS_TELEPORT_NOT_FURTHER = Message.translation("server.commands.teleport.notFurther");
/*  35 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WORLD_NOT_LOADED = Message.translation("server.commands.teleport.worldNotLoaded");
/*     */   
/*     */   @Nonnull
/*     */   private final Deque<Waypoint> back;
/*     */   
/*     */   @Nonnull
/*     */   public static ComponentType<EntityStore, TeleportHistory> getComponentType() {
/*  42 */     return TeleportPlugin.get().getTeleportHistoryComponentType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final Deque<Waypoint> forward;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TeleportHistory() {
/*  61 */     this.back = new ArrayDeque<>();
/*  62 */     this.forward = new ArrayDeque<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void forward(@Nonnull Ref<EntityStore> ref, int count) {
/*  72 */     Store<EntityStore> store = ref.getStore();
/*  73 */     go(store, ref, this.forward, this.back, count, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void back(@Nonnull Ref<EntityStore> ref, int count) {
/*  83 */     Store<EntityStore> store = ref.getStore();
/*  84 */     go(store, ref, this.back, this.forward, count, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getForwardSize() {
/*  91 */     return this.forward.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBackSize() {
/*  98 */     return this.back.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void go(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull Deque<Waypoint> from, @Nonnull Deque<Waypoint> to, int count, boolean isForward) {
/* 117 */     if (count <= 0) throw new IllegalArgumentException(String.valueOf(count));
/*     */     
/* 119 */     PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/* 120 */     assert playerRef != null;
/*     */     
/* 122 */     Waypoint point = null;
/* 123 */     int i = 0;
/* 124 */     for (; i < count; i++) {
/* 125 */       if (from.isEmpty()) {
/* 126 */         if (point == null) {
/* 127 */           playerRef.sendMessage(MESSAGE_COMMANDS_TELEPORT_NOT_FURTHER);
/*     */           
/*     */           return;
/*     */         } 
/*     */         break;
/*     */       } 
/* 133 */       point = from.pop();
/* 134 */       to.push(point);
/*     */     } 
/*     */ 
/*     */     
/* 138 */     if (point == null) throw new NullPointerException(to.toString());
/*     */     
/* 140 */     World targetWorld = Universe.get().getWorld(point.world);
/* 141 */     if (targetWorld == null) {
/* 142 */       playerRef.sendMessage(MESSAGE_COMMANDS_TELEPORT_WORLD_NOT_LOADED);
/*     */     } else {
/* 144 */       to.push(point);
/* 145 */       Teleport teleportComponent = Teleport.createForPlayer(targetWorld, point.position, point.rotation);
/* 146 */       store.addComponent(ref, Teleport.getComponentType(), (Component)teleportComponent);
/*     */ 
/*     */       
/* 149 */       Vector3d pos = point.position;
/* 150 */       int remainingInDirection = from.size();
/* 151 */       int totalInOtherDirection = to.size() - 1;
/*     */       
/* 153 */       if (point.message != null && !point.message.isEmpty()) {
/* 154 */         playerRef.sendMessage((isForward ? 
/* 155 */             Message.translation("server.commands.teleport.teleportedForwardToWaypoint") : 
/* 156 */             Message.translation("server.commands.teleport.teleportedBackToWaypoint"))
/* 157 */             .param("name", point.message)
/* 158 */             .param("x", pos.getX())
/* 159 */             .param("y", pos.getY())
/* 160 */             .param("z", pos.getZ())
/* 161 */             .param("remaining", remainingInDirection)
/* 162 */             .param("otherDirection", totalInOtherDirection));
/*     */       } else {
/* 164 */         playerRef.sendMessage((isForward ? 
/* 165 */             Message.translation("server.commands.teleport.teleportedForwardToCoordinates") : 
/* 166 */             Message.translation("server.commands.teleport.teleportedBackToCoordinates"))
/* 167 */             .param("x", pos.getX())
/* 168 */             .param("y", pos.getY())
/* 169 */             .param("z", pos.getZ())
/* 170 */             .param("remaining", remainingInDirection)
/* 171 */             .param("otherDirection", totalInOtherDirection));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull Vector3f rotation, @Nonnull String key) {
/* 185 */     this.back.push(new Waypoint(world.getName(), pos, rotation, key));
/* 186 */     this.forward.clear();
/* 187 */     while (this.back.size() > 100) {
/* 188 */       this.back.removeLast();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 195 */     return "TeleportHistory{back=" + String.valueOf(this.back) + ", forward=" + String.valueOf(this.forward) + "}";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Component<EntityStore> clone() {
/* 204 */     TeleportHistory cloned = new TeleportHistory();
/* 205 */     cloned.back.addAll(this.back);
/* 206 */     cloned.forward.addAll(this.forward);
/* 207 */     return cloned;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Waypoint
/*     */   {
/*     */     private final String world;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final Vector3d position;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final Vector3f rotation;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final String message;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Waypoint(@Nonnull String world, @Nonnull Vector3d position, @Nonnull Vector3f rotation, @Nonnull String message) {
/* 244 */       this.world = world;
/* 245 */       this.position = position;
/* 246 */       this.rotation = rotation;
/* 247 */       this.message = message;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String toString() {
/* 253 */       return "Waypoint{world='" + this.world + "', position=" + String.valueOf(this.position) + ", rotation=" + String.valueOf(this.rotation) + ", message='" + this.message + "'}";
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\components\TeleportHistory.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */