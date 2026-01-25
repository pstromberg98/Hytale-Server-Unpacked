/*     */ package com.hypixel.hytale.builtin.crafting.system;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.HolderSystem;
/*     */ import com.hypixel.hytale.component.system.RefSystem;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class PlayerCraftingSystems
/*     */ {
/*     */   public static class CraftingHolderSystem
/*     */     extends HolderSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/*  28 */     private final ComponentType<EntityStore, Player> playerComponentType = Player.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, CraftingManager> craftingManagerComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CraftingHolderSystem(@Nonnull ComponentType<EntityStore, CraftingManager> craftingManagerComponentType) {
/*  42 */       this.craftingManagerComponentType = craftingManagerComponentType;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/*  47 */       holder.ensureComponent(this.craftingManagerComponentType);
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
/*  52 */       World world = ((EntityStore)store.getExternalData()).getWorld();
/*  53 */       if (world.getWorldConfig().isSavingPlayers()) {
/*  54 */         Player playerComponent = (Player)holder.getComponent(this.playerComponentType);
/*  55 */         assert playerComponent != null;
/*     */         
/*  57 */         playerComponent.saveConfig(world, holder);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/*  64 */       return (Query)this.playerComponentType;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CraftingRefSystem
/*     */     extends RefSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/*  77 */     private final ComponentType<EntityStore, Player> playerComponentType = Player.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, CraftingManager> craftingManagerComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CraftingRefSystem(@Nonnull ComponentType<EntityStore, CraftingManager> craftingManagerComponentType) {
/*  91 */       this.craftingManagerComponentType = craftingManagerComponentType;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/*  97 */       return (Query<EntityStore>)Query.and(new Query[] { (Query)this.playerComponentType, (Query)this.craftingManagerComponentType });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 107 */       CraftingManager craftingManagerComponent = (CraftingManager)commandBuffer.getComponent(ref, CraftingManager.getComponentType());
/* 108 */       assert craftingManagerComponent != null;
/*     */       
/* 110 */       craftingManagerComponent.cancelAllCrafting(ref, (ComponentAccessor)store);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CraftingTickingSystem
/*     */     extends EntityTickingSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, CraftingManager> craftingManagerComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CraftingTickingSystem(@Nonnull ComponentType<EntityStore, CraftingManager> craftingManagerComponentType) {
/* 131 */       this.craftingManagerComponentType = craftingManagerComponentType;
/*     */     }
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/* 136 */       return (Query)this.craftingManagerComponentType;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 141 */       return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */     }
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 146 */       CraftingManager craftingManagerComponent = (CraftingManager)archetypeChunk.getComponent(index, this.craftingManagerComponentType);
/* 147 */       assert craftingManagerComponent != null;
/*     */       
/* 149 */       Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/* 150 */       craftingManagerComponent.tick(ref, (ComponentAccessor)commandBuffer, dt);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\system\PlayerCraftingSystems.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */