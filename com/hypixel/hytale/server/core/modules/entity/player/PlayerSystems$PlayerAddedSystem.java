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
/*     */ public class PlayerAddedSystem
/*     */   extends RefSystem<EntityStore>
/*     */ {
/*     */   @Nonnull
/*  91 */   private static final Message MESSAGE_SERVER_GENERAL_KILLED_BY_UNKNOWN = Message.translation("server.general.killedByUnknown");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  97 */   private final Set<Dependency<EntityStore>> dependencies = (Set)Set.of(new SystemDependency(Order.AFTER, PlayerSystems.PlayerSpawnedSystem.class));
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
/* 111 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)Player.getComponentType(), (Query)PlayerRef.getComponentType(), (Query)movementManagerComponentType });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 117 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/* 123 */     return this.dependencies;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 128 */     World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/*     */     
/* 130 */     Player playerComponent = (Player)commandBuffer.getComponent(ref, Player.getComponentType());
/* 131 */     assert playerComponent != null;
/*     */     
/* 133 */     PlayerRef playerRefComponent = (PlayerRef)commandBuffer.getComponent(ref, PlayerRef.getComponentType());
/* 134 */     assert playerRefComponent != null;
/*     */     
/* 136 */     MovementManager movementManagerComponent = (MovementManager)commandBuffer.getComponent(ref, MovementManager.getComponentType());
/* 137 */     assert movementManagerComponent != null;
/*     */     
/* 139 */     if (commandBuffer.getComponent(ref, DisplayNameComponent.getComponentType()) == null) {
/* 140 */       Message displayName = Message.raw(playerRefComponent.getUsername());
/* 141 */       commandBuffer.putComponent(ref, DisplayNameComponent.getComponentType(), (Component)new DisplayNameComponent(displayName));
/*     */     } 
/*     */     
/* 144 */     playerComponent.setLastSpawnTimeNanos(System.nanoTime());
/*     */     
/* 146 */     PacketHandler playerConnection = playerRefComponent.getPacketHandler();
/*     */     
/* 148 */     Objects.requireNonNull(world, "world");
/* 149 */     Objects.requireNonNull(playerComponent.getPlayerConfigData(), "data");
/*     */     
/* 151 */     PlayerWorldData perWorldData = playerComponent.getPlayerConfigData().getPerWorldData(world.getName());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 156 */     Player.initGameMode(ref, (ComponentAccessor)commandBuffer);
/*     */     
/* 158 */     playerConnection.writeNoCache((Packet)new BuilderToolsSetSoundSet(world.getGameplayConfig().getCreativePlaySoundSetIndex()));
/*     */     
/* 160 */     playerComponent.sendInventory();
/* 161 */     Inventory playerInventory = playerComponent.getInventory();
/* 162 */     playerConnection.writeNoCache((Packet)new SetActiveSlot(-1, playerInventory.getActiveHotbarSlot()));
/* 163 */     playerConnection.writeNoCache((Packet)new SetActiveSlot(-5, playerInventory.getActiveUtilitySlot()));
/* 164 */     playerConnection.writeNoCache((Packet)new SetActiveSlot(-8, playerInventory.getActiveToolsSlot()));
/*     */     
/* 166 */     if (playerInventory.containsBrokenItem()) {
/* 167 */       playerComponent.sendMessage(Message.translation("server.general.repair.itemBrokenOnRespawn").color("#ff5555"));
/*     */     }
/*     */     
/* 170 */     playerConnection.writeNoCache((Packet)new SetBlockPlacementOverride(playerComponent.isOverrideBlockPlacementRestrictions()));
/*     */ 
/*     */     
/* 173 */     DeathComponent deathComponent = (DeathComponent)commandBuffer.getComponent(ref, DeathComponent.getComponentType());
/* 174 */     if (deathComponent != null) {
/* 175 */       Message pendingDeathMessage = deathComponent.getDeathMessage();
/* 176 */       if (pendingDeathMessage == null) {
/* 177 */         ((HytaleLogger.Api)Entity.LOGGER.at(Level.SEVERE).withCause(new Throwable()))
/* 178 */           .log("Player wasn't alive but didn't have a pending death message?");
/* 179 */         pendingDeathMessage = MESSAGE_SERVER_GENERAL_KILLED_BY_UNKNOWN;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 186 */       RespawnPage respawnPage = new RespawnPage(playerRefComponent, pendingDeathMessage, deathComponent.displayDataOnDeathScreen(), deathComponent.getDeathItemLoss());
/*     */       
/* 188 */       playerComponent.getPageManager().openCustomPage(ref, store, (CustomUIPage)respawnPage);
/*     */     } 
/*     */     
/* 191 */     TransformComponent transform = (TransformComponent)commandBuffer.getComponent(ref, TransformComponent.getComponentType());
/* 192 */     GameplayConfig gameplayConfig = world.getGameplayConfig();
/* 193 */     SpawnConfig spawnConfig = gameplayConfig.getSpawnConfig();
/*     */     
/* 195 */     if (transform != null) {
/* 196 */       Vector3d position = transform.getPosition();
/* 197 */       SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = (SpatialResource<Ref<EntityStore>, EntityStore>)commandBuffer.getResource(EntityModule.get().getPlayerSpatialResourceType());
/* 198 */       ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
/* 199 */       playerSpatialResource.getSpatialStructure().collect(position, 75.0D, (List)results);
/*     */ 
/*     */       
/* 202 */       results.add(ref);
/*     */       
/* 204 */       if (playerComponent.isFirstSpawn()) {
/* 205 */         WorldParticle[] firstSpawnParticles = spawnConfig.getFirstSpawnParticles();
/* 206 */         if (firstSpawnParticles == null) firstSpawnParticles = spawnConfig.getSpawnParticles(); 
/* 207 */         if (firstSpawnParticles != null) {
/* 208 */           ParticleUtil.spawnParticleEffects(firstSpawnParticles, position, null, (List)results, (ComponentAccessor)commandBuffer);
/*     */         }
/*     */       } else {
/* 211 */         WorldParticle[] spawnParticles = spawnConfig.getSpawnParticles();
/* 212 */         if (spawnParticles != null) {
/* 213 */           ParticleUtil.spawnParticleEffects(spawnParticles, position, null, (List)results, (ComponentAccessor)commandBuffer);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 218 */     playerConnection.tryFlush();
/*     */     
/* 220 */     perWorldData.setFirstSpawn(false);
/*     */   }
/*     */   
/*     */   public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\player\PlayerSystems$PlayerAddedSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */