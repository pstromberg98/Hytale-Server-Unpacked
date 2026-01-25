/*     */ package com.hypixel.hytale.server.core.modules.entity.player;
/*     */ 
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.spatial.SpatialResource;
/*     */ import com.hypixel.hytale.component.system.RefSystem;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolsSetSoundSet;
/*     */ import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
/*     */ import com.hypixel.hytale.protocol.packets.player.SetBlockPlacementOverride;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.SpawnConfig;
/*     */ import com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.pages.RespawnPage;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.io.PacketHandler;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PlayerAddedSystem
/*     */   extends RefSystem<EntityStore>
/*     */ {
/*     */   @Nonnull
/* 254 */   private static final Message MESSAGE_SERVER_GENERAL_KILLED_BY_UNKNOWN = Message.translation("server.general.killedByUnknown");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/* 260 */   private final Set<Dependency<EntityStore>> dependencies = (Set)Set.of(new SystemDependency(Order.AFTER, PlayerSystems.PlayerSpawnedSystem.class));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PlayerAddedSystem(@Nonnull ComponentType<EntityStore, MovementManager> movementManagerComponentType) {
/* 274 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)Player.getComponentType(), (Query)PlayerRef.getComponentType(), (Query)movementManagerComponentType });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 280 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/* 286 */     return this.dependencies;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 291 */     World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/*     */     
/* 293 */     Player playerComponent = (Player)commandBuffer.getComponent(ref, Player.getComponentType());
/* 294 */     assert playerComponent != null;
/*     */     
/* 296 */     PlayerRef playerRefComponent = (PlayerRef)commandBuffer.getComponent(ref, PlayerRef.getComponentType());
/* 297 */     assert playerRefComponent != null;
/*     */     
/* 299 */     MovementManager movementManagerComponent = (MovementManager)commandBuffer.getComponent(ref, MovementManager.getComponentType());
/* 300 */     assert movementManagerComponent != null;
/*     */     
/* 302 */     if (commandBuffer.getComponent(ref, DisplayNameComponent.getComponentType()) == null) {
/* 303 */       Message displayName = Message.raw(playerRefComponent.getUsername());
/* 304 */       commandBuffer.putComponent(ref, DisplayNameComponent.getComponentType(), (Component)new DisplayNameComponent(displayName));
/*     */     } 
/*     */     
/* 307 */     playerComponent.setLastSpawnTimeNanos(System.nanoTime());
/*     */     
/* 309 */     PacketHandler playerConnection = playerRefComponent.getPacketHandler();
/*     */     
/* 311 */     Objects.requireNonNull(world, "world");
/* 312 */     Objects.requireNonNull(playerComponent.getPlayerConfigData(), "data");
/*     */     
/* 314 */     PlayerWorldData perWorldData = playerComponent.getPlayerConfigData().getPerWorldData(world.getName());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 319 */     Player.initGameMode(ref, (ComponentAccessor)commandBuffer);
/*     */     
/* 321 */     playerConnection.writeNoCache((Packet)new BuilderToolsSetSoundSet(world.getGameplayConfig().getCreativePlaySoundSetIndex()));
/*     */     
/* 323 */     playerComponent.sendInventory();
/* 324 */     Inventory playerInventory = playerComponent.getInventory();
/* 325 */     playerConnection.writeNoCache((Packet)new SetActiveSlot(-1, playerInventory.getActiveHotbarSlot()));
/* 326 */     playerConnection.writeNoCache((Packet)new SetActiveSlot(-5, playerInventory.getActiveUtilitySlot()));
/* 327 */     playerConnection.writeNoCache((Packet)new SetActiveSlot(-8, playerInventory.getActiveToolsSlot()));
/*     */     
/* 329 */     if (playerInventory.containsBrokenItem()) {
/* 330 */       playerComponent.sendMessage(Message.translation("server.general.repair.itemBrokenOnRespawn")
/* 331 */           .color("#ff5555"));
/*     */     }
/*     */     
/* 334 */     playerConnection.writeNoCache((Packet)new SetBlockPlacementOverride(playerComponent.isOverrideBlockPlacementRestrictions()));
/*     */ 
/*     */     
/* 337 */     DeathComponent deathComponent = (DeathComponent)commandBuffer.getComponent(ref, DeathComponent.getComponentType());
/* 338 */     if (deathComponent != null) {
/* 339 */       Message pendingDeathMessage = deathComponent.getDeathMessage();
/* 340 */       if (pendingDeathMessage == null) {
/* 341 */         ((HytaleLogger.Api)Entity.LOGGER.at(Level.SEVERE).withCause(new Throwable()))
/* 342 */           .log("Player wasn't alive but didn't have a pending death message?");
/* 343 */         pendingDeathMessage = MESSAGE_SERVER_GENERAL_KILLED_BY_UNKNOWN;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 350 */       RespawnPage respawnPage = new RespawnPage(playerRefComponent, pendingDeathMessage, deathComponent.displayDataOnDeathScreen(), deathComponent.getDeathItemLoss());
/*     */       
/* 352 */       playerComponent.getPageManager().openCustomPage(ref, store, (CustomUIPage)respawnPage);
/*     */     } 
/*     */     
/* 355 */     TransformComponent transform = (TransformComponent)commandBuffer.getComponent(ref, TransformComponent.getComponentType());
/* 356 */     GameplayConfig gameplayConfig = world.getGameplayConfig();
/* 357 */     SpawnConfig spawnConfig = gameplayConfig.getSpawnConfig();
/*     */     
/* 359 */     if (transform != null) {
/* 360 */       Vector3d position = transform.getPosition();
/* 361 */       SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = (SpatialResource<Ref<EntityStore>, EntityStore>)commandBuffer.getResource(EntityModule.get().getPlayerSpatialResourceType());
/* 362 */       ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
/* 363 */       playerSpatialResource.getSpatialStructure().collect(position, 75.0D, (List)results);
/*     */ 
/*     */       
/* 366 */       results.add(ref);
/*     */       
/* 368 */       if (playerComponent.isFirstSpawn()) {
/* 369 */         WorldParticle[] firstSpawnParticles = spawnConfig.getFirstSpawnParticles();
/* 370 */         if (firstSpawnParticles == null) firstSpawnParticles = spawnConfig.getSpawnParticles(); 
/* 371 */         if (firstSpawnParticles != null) {
/* 372 */           ParticleUtil.spawnParticleEffects(firstSpawnParticles, position, null, (List)results, (ComponentAccessor)commandBuffer);
/*     */         }
/*     */       } else {
/* 375 */         WorldParticle[] spawnParticles = spawnConfig.getSpawnParticles();
/* 376 */         if (spawnParticles != null) {
/* 377 */           ParticleUtil.spawnParticleEffects(spawnParticles, position, null, (List)results, (ComponentAccessor)commandBuffer);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 382 */     playerConnection.tryFlush();
/*     */     
/* 384 */     perWorldData.setFirstSpawn(false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 390 */     Player playerComponent = (Player)commandBuffer.getComponent(ref, Player.getComponentType());
/* 391 */     assert playerComponent != null;
/*     */ 
/*     */     
/* 394 */     playerComponent.getWindowManager().closeAllWindows(ref, (ComponentAccessor)commandBuffer);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\player\PlayerSystems$PlayerAddedSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */