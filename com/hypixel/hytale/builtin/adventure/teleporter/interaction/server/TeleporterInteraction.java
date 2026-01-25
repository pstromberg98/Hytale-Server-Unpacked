/*     */ package com.hypixel.hytale.builtin.adventure.teleporter.interaction.server;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.adventure.teleporter.component.Teleporter;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.component.Archetype;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.spatial.SpatialResource;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
/*     */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*     */ import com.hypixel.hytale.server.core.modules.entity.teleport.TeleportRecord;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
/*     */ import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*     */ import java.time.Duration;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TeleporterInteraction
/*     */   extends SimpleBlockInteraction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<TeleporterInteraction> CODEC;
/*     */   
/*     */   static {
/*  53 */     CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(TeleporterInteraction.class, TeleporterInteraction::new, SimpleBlockInteraction.CODEC).appendInherited(new KeyedCodec("Particle", (Codec)Codec.STRING), (interaction, s) -> interaction.particle = s, interaction -> interaction.particle, (interaction, parent) -> interaction.particle = parent.particle).documentation("The particle to play on the entity when teleporting.").add()).build();
/*     */   }
/*  55 */   private static final Duration TELEPORT_GLOBAL_COOLDOWN = Duration.ofMillis(250L);
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String particle;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/*  66 */     return WaitForDataFrom.Server;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
/*  71 */     ChunkStore chunkStore = world.getChunkStore();
/*     */     
/*  73 */     long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.getX(), targetBlock.getZ());
/*  74 */     BlockComponentChunk blockComponentChunk = (BlockComponentChunk)chunkStore.getChunkComponent(chunkIndex, BlockComponentChunk.getComponentType());
/*  75 */     if (blockComponentChunk == null)
/*     */       return; 
/*  77 */     int blockIndex = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
/*  78 */     Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);
/*  79 */     if (blockRef == null || !blockRef.isValid())
/*     */       return; 
/*  81 */     BlockModule.BlockStateInfo blockStateInfoComponent = (BlockModule.BlockStateInfo)blockRef.getStore().getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
/*  82 */     if (blockStateInfoComponent == null)
/*     */       return; 
/*  84 */     Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
/*  85 */     if (chunkRef == null && !chunkRef.isValid())
/*     */       return; 
/*  87 */     Teleporter teleporter = (Teleporter)chunkStore.getStore().getComponent(blockRef, Teleporter.getComponentType());
/*  88 */     if (teleporter == null)
/*     */       return; 
/*  90 */     Ref<EntityStore> ref = context.getEntity();
/*     */     
/*  92 */     Player playerComponent = (Player)commandBuffer.getComponent(ref, Player.getComponentType());
/*  93 */     if (playerComponent != null && playerComponent.isWaitingForClientReady()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  99 */     Archetype<EntityStore> archetype = commandBuffer.getArchetype(ref);
/* 100 */     if (archetype.contains(Teleport.getComponentType()) || archetype.contains(PendingTeleport.getComponentType()))
/*     */       return; 
/* 102 */     WorldChunk worldChunkComponent = (WorldChunk)chunkRef.getStore().getComponent(chunkRef, WorldChunk.getComponentType());
/* 103 */     assert worldChunkComponent != null;
/*     */     
/* 105 */     BlockType blockType = worldChunkComponent.getBlockType(targetBlock.x, targetBlock.y, targetBlock.z);
/*     */ 
/*     */     
/* 108 */     if (!teleporter.isValid()) {
/* 109 */       String currentState = blockType.getStateForBlock(blockType);
/* 110 */       if (!"default".equals(currentState)) {
/* 111 */         BlockType variantBlockType = blockType.getBlockForState("default");
/* 112 */         if (variantBlockType != null) {
/* 113 */           worldChunkComponent.setBlockInteractionState(targetBlock.x, targetBlock.y, targetBlock.z, variantBlockType, "default", true);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 118 */     TransformComponent transformComponent = (TransformComponent)commandBuffer.getComponent(ref, TransformComponent.getComponentType());
/* 119 */     assert transformComponent != null;
/*     */     
/* 121 */     Teleport teleportComponent = teleporter.toTeleport(transformComponent.getPosition(), transformComponent.getRotation(), targetBlock);
/* 122 */     if (teleportComponent == null)
/*     */       return; 
/* 124 */     TeleportRecord recorder = (TeleportRecord)commandBuffer.getComponent(ref, TeleportRecord.getComponentType());
/* 125 */     if (recorder != null && !recorder.hasElapsedSinceLastTeleport(TELEPORT_GLOBAL_COOLDOWN)) {
/*     */       return;
/*     */     }
/*     */     
/* 129 */     commandBuffer.addComponent(ref, Teleport.getComponentType(), (Component)teleportComponent);
/*     */     
/* 131 */     if (this.particle != null) {
/* 132 */       Vector3d particlePosition = transformComponent.getPosition();
/* 133 */       SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = (SpatialResource<Ref<EntityStore>, EntityStore>)commandBuffer.getResource(EntityModule.get().getPlayerSpatialResourceType());
/* 134 */       ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
/* 135 */       playerSpatialResource.getSpatialStructure().collect(particlePosition, 75.0D, (List)results);
/*     */       
/* 137 */       ParticleUtil.spawnParticleEffect(this.particle, particlePosition, (List)results, (ComponentAccessor)commandBuffer);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\teleporter\interaction\server\TeleporterInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */