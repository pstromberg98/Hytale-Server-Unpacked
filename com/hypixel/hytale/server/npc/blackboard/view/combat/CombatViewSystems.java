/*     */ package com.hypixel.hytale.server.npc.blackboard.view.combat;
/*     */ import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Resource;
/*     */ import com.hypixel.hytale.component.ResourceType;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionChain;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.npc.NPCPlugin;
/*     */ import com.hypixel.hytale.server.npc.role.support.CombatSupport;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class CombatViewSystems {
/*     */   public static class Ensure extends HolderSystem<EntityStore> {
/*     */     private final ComponentType<EntityStore, CombatViewSystems.CombatData> combatDataComponentType;
/*     */     
/*     */     public Ensure(ComponentType<EntityStore, CombatViewSystems.CombatData> combatDataComponentType) {
/*  30 */       this.combatDataComponentType = combatDataComponentType;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/*  36 */       return (Query<EntityStore>)AllLegacyEntityTypesQuery.INSTANCE;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/*  41 */       holder.ensureComponent(this.combatDataComponentType);
/*     */     }
/*     */     
/*     */     public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {}
/*     */   }
/*     */   
/*     */   public static class EntityRemoved
/*     */     extends HolderSystem<EntityStore>
/*     */   {
/*     */     private final ComponentType<EntityStore, CombatViewSystems.CombatData> combatDataComponentType;
/*     */     private final ResourceType<EntityStore, CombatViewSystems.CombatDataPool> dataPoolResourceType;
/*     */     
/*     */     public EntityRemoved(ComponentType<EntityStore, CombatViewSystems.CombatData> combatDataComponentType, ResourceType<EntityStore, CombatViewSystems.CombatDataPool> dataPoolResourceType) {
/*  54 */       this.combatDataComponentType = combatDataComponentType;
/*  55 */       this.dataPoolResourceType = dataPoolResourceType;
/*     */     }
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/*  60 */       return (Query)this.combatDataComponentType;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
/*  70 */       CombatViewSystems.CombatData combatData = (CombatViewSystems.CombatData)holder.getComponent(this.combatDataComponentType);
/*  71 */       CombatViewSystems.CombatDataPool dataPool = (CombatViewSystems.CombatDataPool)store.getResource(this.dataPoolResourceType);
/*  72 */       CombatViewSystems.clearCombatData(combatData, dataPool);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Ticking extends EntityTickingSystem<EntityStore> {
/*     */     private final ComponentType<EntityStore, CombatViewSystems.CombatData> combatDataComponentType;
/*     */     private final ResourceType<EntityStore, CombatViewSystems.CombatDataPool> dataPoolResourceType;
/*     */     
/*     */     public Ticking(ComponentType<EntityStore, CombatViewSystems.CombatData> combatDataComponentType, ResourceType<EntityStore, CombatViewSystems.CombatDataPool> dataPoolResourceType) {
/*  81 */       this.combatDataComponentType = combatDataComponentType;
/*  82 */       this.dataPoolResourceType = dataPoolResourceType;
/*     */     }
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/*  87 */       return (Query)this.combatDataComponentType;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isParallel(int archetypeChunkSize, int taskCount) {
/*  93 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  99 */       CombatViewSystems.CombatData combatData = (CombatViewSystems.CombatData)archetypeChunk.getComponent(index, this.combatDataComponentType);
/* 100 */       CombatViewSystems.CombatDataPool dataPool = (CombatViewSystems.CombatDataPool)store.getResource(this.dataPoolResourceType);
/* 101 */       CombatViewSystems.clearCombatData(combatData, dataPool);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void clearCombatData(@Nonnull CombatData combatData, @Nonnull CombatDataPool dataPool) {
/* 106 */     if (!combatData.interpreted)
/*     */       return; 
/* 108 */     List<InterpretedCombatData> dataList = combatData.combatData;
/* 109 */     for (int i = 0; i < dataList.size(); i++) {
/* 110 */       dataPool.releaseCombatData(dataList.get(i));
/*     */     }
/* 112 */     dataList.clear();
/* 113 */     combatData.interpreted = false;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static List<InterpretedCombatData> getCombatData(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 118 */     CombatData combatDataComponent = (CombatData)store.getComponent(ref, CombatData.getComponentType());
/* 119 */     if (combatDataComponent.interpreted) {
/* 120 */       return combatDataComponent.unmodifiableCombatData;
/*     */     }
/* 122 */     InteractionManager interactionManager = (InteractionManager)store.getComponent(ref, InteractionModule.get().getInteractionManagerComponent());
/*     */     
/* 124 */     CombatDataPool combatDataPool = (CombatDataPool)store.getResource(CombatDataPool.getResourceType());
/* 125 */     List<InterpretedCombatData> dataList = combatDataComponent.combatData;
/*     */     
/* 127 */     IndexedLookupTableAssetMap<String, RootInteraction> interactionAssetMap = RootInteraction.getAssetMap();
/* 128 */     Set<String> attackInteractions = interactionAssetMap.getKeysForTag(CombatSupport.ATTACK_TAG_INDEX);
/* 129 */     Set<String> meleeInteractions = interactionAssetMap.getKeysForTag(CombatSupport.MELEE_TAG_INDEX);
/* 130 */     Set<String> rangedInteractions = interactionAssetMap.getKeysForTag(CombatSupport.RANGED_TAG_INDEX);
/* 131 */     Set<String> blockInteractions = interactionAssetMap.getKeysForTag(CombatSupport.BLOCK_TAG_INDEX);
/*     */ 
/*     */     
/* 134 */     interactionManager.forEachInteraction((chain, interaction, list) -> { String rootId = chain.getRootInteraction().getId(); if (!attackInteractions.contains(rootId)) return list;  InterpretedCombatData entry = combatDataPool.getEmptyCombatData(); entry.setAttack(rootId); entry.setCurrentElapsedTime(chain.getTimeInSeconds()); entry.setCharging(interaction instanceof com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.ChargingInteraction); entry.setPerformingMeleeAttack(meleeInteractions.contains(rootId)); entry.setPerformingRangedAttack(rangedInteractions.contains(rootId)); entry.setPerformingBlock(blockInteractions.contains(rootId)); list.add(entry); return list; }dataList);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 149 */     combatDataComponent.interpreted = true;
/*     */     
/* 151 */     return combatDataComponent.unmodifiableCombatData;
/*     */   }
/*     */   
/*     */   public static class CombatData implements Component<EntityStore> {
/*     */     public static ComponentType<EntityStore, CombatData> getComponentType() {
/* 156 */       return NPCPlugin.get().getCombatDataComponentType();
/*     */     }
/*     */     
/* 159 */     private final List<InterpretedCombatData> combatData = (List<InterpretedCombatData>)new ObjectArrayList();
/* 160 */     private final List<InterpretedCombatData> unmodifiableCombatData = Collections.unmodifiableList(this.combatData);
/*     */     
/*     */     private boolean interpreted;
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Component<EntityStore> clone() {
/* 167 */       CombatData data = new CombatData();
/* 168 */       data.interpreted = this.interpreted;
/* 169 */       for (int i = 0; i < this.combatData.size(); i++) {
/* 170 */         data.combatData.add(((InterpretedCombatData)this.combatData.get(i)).clone());
/*     */       }
/* 172 */       return data;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class CombatDataPool
/*     */     implements Resource<EntityStore> {
/*     */     private final ArrayDeque<InterpretedCombatData> combatDataPool;
/*     */     
/*     */     public CombatDataPool() {
/* 181 */       this.combatDataPool = new ArrayDeque<>();
/*     */     } public static ResourceType<EntityStore, CombatDataPool> getResourceType() {
/*     */       return NPCPlugin.get().getCombatDataPoolResourceType();
/*     */     }
/*     */     @Nonnull
/*     */     public Resource<EntityStore> clone() {
/* 187 */       return new CombatDataPool();
/*     */     }
/*     */     
/*     */     public InterpretedCombatData getEmptyCombatData() {
/* 191 */       if (this.combatDataPool.isEmpty()) {
/* 192 */         return new InterpretedCombatData();
/*     */       }
/* 194 */       return this.combatDataPool.poll();
/*     */     }
/*     */     
/*     */     public void releaseCombatData(@Nonnull InterpretedCombatData combatData) {
/* 198 */       this.combatDataPool.push(combatData);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\blackboard\view\combat\CombatViewSystems.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */