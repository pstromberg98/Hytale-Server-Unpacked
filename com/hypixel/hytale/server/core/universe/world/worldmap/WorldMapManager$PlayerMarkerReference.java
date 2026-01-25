/*     */ package com.hypixel.hytale.server.core.universe.world.worldmap;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerConfigData;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletionStage;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PlayerMarkerReference
/*     */   implements WorldMapManager.MarkerReference
/*     */ {
/*     */   public static final BuilderCodec<PlayerMarkerReference> CODEC;
/*     */   private UUID player;
/*     */   private String world;
/*     */   private String markerId;
/*     */   
/*     */   static {
/* 339 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PlayerMarkerReference.class, PlayerMarkerReference::new).addField(new KeyedCodec("Player", (Codec)Codec.UUID_BINARY), (playerMarkerReference, uuid) -> playerMarkerReference.player = uuid, playerMarkerReference -> playerMarkerReference.player)).addField(new KeyedCodec("World", (Codec)Codec.STRING), (playerMarkerReference, s) -> playerMarkerReference.world = s, playerMarkerReference -> playerMarkerReference.world)).addField(new KeyedCodec("MarkerId", (Codec)Codec.STRING), (playerMarkerReference, s) -> playerMarkerReference.markerId = s, playerMarkerReference -> playerMarkerReference.markerId)).build();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private PlayerMarkerReference() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public PlayerMarkerReference(@Nonnull UUID player, @Nonnull String world, @Nonnull String markerId) {
/* 349 */     this.player = player;
/* 350 */     this.world = world;
/* 351 */     this.markerId = markerId;
/*     */   }
/*     */   
/*     */   public UUID getPlayer() {
/* 355 */     return this.player;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMarkerId() {
/* 360 */     return this.markerId;
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove() {
/* 365 */     PlayerRef playerRef = Universe.get().getPlayer(this.player);
/* 366 */     if (playerRef != null) {
/*     */       
/* 368 */       Player playerComponent = (Player)playerRef.getComponent(Player.getComponentType());
/* 369 */       removeMarkerFromOnlinePlayer(playerComponent);
/*     */     } else {
/* 371 */       removeMarkerFromOfflinePlayer();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void removeMarkerFromOnlinePlayer(@Nonnull Player player) {
/* 376 */     PlayerConfigData data = player.getPlayerConfigData();
/*     */ 
/*     */ 
/*     */     
/* 380 */     String world = this.world;
/* 381 */     if (world == null) world = player.getWorld().getName();
/*     */     
/* 383 */     removeMarkerFromData(data, world, this.markerId);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void removeMarkerFromOfflinePlayer() {
/* 390 */     Universe.get().getPlayerStorage().load(this.player)
/* 391 */       .thenApply(holder -> {
/*     */           Player player = (Player)holder.getComponent(Player.getComponentType());
/*     */           
/*     */           PlayerConfigData data = player.getPlayerConfigData();
/*     */           
/*     */           String world = this.world;
/*     */           
/*     */           if (world == null) {
/*     */             world = data.getWorld();
/*     */           }
/*     */           removeMarkerFromData(data, world, this.markerId);
/*     */           return holder;
/* 403 */         }).thenCompose(holder -> Universe.get().getPlayerStorage().save(this.player, holder));
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static MapMarker removeMarkerFromData(@Nonnull PlayerConfigData data, @Nonnull String worldName, @Nonnull String markerId) {
/* 408 */     PlayerWorldData perWorldData = data.getPerWorldData(worldName);
/* 409 */     MapMarker[] worldMapMarkers = perWorldData.getWorldMapMarkers();
/* 410 */     if (worldMapMarkers == null) return null;
/*     */     
/* 412 */     int index = -1;
/* 413 */     for (int i = 0; i < worldMapMarkers.length; i++) {
/* 414 */       if ((worldMapMarkers[i]).id.equals(markerId)) {
/* 415 */         index = i;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 420 */     if (index == -1) return null;
/*     */     
/* 422 */     MapMarker[] newWorldMapMarkers = new MapMarker[worldMapMarkers.length - 1];
/* 423 */     System.arraycopy(worldMapMarkers, 0, newWorldMapMarkers, 0, index);
/* 424 */     System.arraycopy(worldMapMarkers, index + 1, newWorldMapMarkers, index, newWorldMapMarkers.length - index);
/* 425 */     perWorldData.setWorldMapMarkers(newWorldMapMarkers);
/*     */     
/* 427 */     return worldMapMarkers[index];
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldmap\WorldMapManager$PlayerMarkerReference.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */