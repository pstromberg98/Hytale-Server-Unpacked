/*     */ package com.hypixel.hytale.server.npc.systems;
/*     */ 
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.RefSystem;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.MovementAudioComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.NewSpawnComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.PositionDataComponent;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.npc.NPCPlugin;
/*     */ import com.hypixel.hytale.server.npc.blackboard.view.blocktype.BlockTypeView;
/*     */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*     */ import com.hypixel.hytale.server.npc.role.Role;
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
/*     */ public class AddedSystem
/*     */   extends RefSystem<EntityStore>
/*     */ {
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, NPCEntity> npcComponentType;
/*     */   
/*     */   public AddedSystem(@Nonnull ComponentType<EntityStore, NPCEntity> npcComponentType) {
/*  60 */     this.npcComponentType = npcComponentType;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  65 */     NPCEntity npcComponent = (NPCEntity)store.getComponent(ref, this.npcComponentType);
/*  66 */     assert npcComponent != null;
/*     */     
/*  68 */     Role role = npcComponent.getRole();
/*     */ 
/*     */     
/*  71 */     if (role == null) {
/*  72 */       ((HytaleLogger.Api)((HytaleLogger.Api)NPCPlugin.get().getLogger().atSevere()).withCause(new IllegalStateException("NPC has no role or role index in onLoad!"))).log();
/*  73 */       commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
/*     */       
/*     */       return;
/*     */     } 
/*  77 */     npcComponent.initBlockChangeBlackboardView(ref, (ComponentAccessor)commandBuffer);
/*  78 */     role.loaded();
/*     */     
/*  80 */     commandBuffer.ensureComponent(ref, PrefabCopyableComponent.getComponentType());
/*     */     
/*  82 */     commandBuffer.ensureComponent(ref, PositionDataComponent.getComponentType());
/*  83 */     commandBuffer.ensureComponent(ref, MovementAudioComponent.getComponentType());
/*     */     
/*  85 */     if (reason == AddReason.SPAWN) {
/*  86 */       NewSpawnComponent newSpawnComponent = new NewSpawnComponent(role.getSpawnLockTime());
/*  87 */       commandBuffer.addComponent(ref, NewSpawnComponent.getComponentType(), (Component)newSpawnComponent);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  93 */     NPCEntity npcComponent = (NPCEntity)store.getComponent(ref, this.npcComponentType);
/*  94 */     assert npcComponent != null;
/*     */     
/*  96 */     BlockTypeView blockTypeView = npcComponent.removeBlockTypeBlackboardView();
/*  97 */     if (blockTypeView != null) {
/*  98 */       blockTypeView.removeSearchedBlockSets(ref, npcComponent, npcComponent.getBlackboardBlockTypeSets());
/*     */     }
/*     */     
/* 101 */     switch (NPCSystems.null.$SwitchMap$com$hypixel$hytale$component$RemoveReason[reason.ordinal()]) { case 1:
/* 102 */         npcComponent.getRole().removed(); break;
/* 103 */       case 2: npcComponent.getRole().unloaded();
/*     */         break; }
/*     */   
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 110 */     return (Query)this.npcComponentType;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\systems\NPCSystems$AddedSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */