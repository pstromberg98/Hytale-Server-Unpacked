/*     */ package com.hypixel.hytale.server.npc.systems;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.npc.decisionmaker.core.EvaluationContext;
/*     */ import com.hypixel.hytale.server.npc.decisionmaker.core.Evaluator;
/*     */ import com.hypixel.hytale.server.npc.decisionmaker.stateevaluator.StateEvaluator;
/*     */ import com.hypixel.hytale.server.npc.decisionmaker.stateevaluator.StateOption;
/*     */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*     */ import com.hypixel.hytale.server.npc.role.Role;
/*     */ import com.hypixel.hytale.server.npc.role.support.StateSupport;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StateEvaluatorSystem
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*     */   @Nonnull
/*  34 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, StateEvaluator> stateEvaluatorComponent;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, NPCEntity> npcComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  52 */   private final ComponentType<EntityStore, UUIDComponent> uuidComponentType = UUIDComponent.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final Set<Dependency<EntityStore>> dependencies;
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
/*     */   
/*     */   public StateEvaluatorSystem(@Nonnull ComponentType<EntityStore, StateEvaluator> stateEvaluatorComponent, @Nonnull ComponentType<EntityStore, NPCEntity> npcComponentType) {
/*  74 */     this.stateEvaluatorComponent = stateEvaluatorComponent;
/*  75 */     this.npcComponentType = npcComponentType;
/*  76 */     this.dependencies = Set.of(new SystemDependency(Order.BEFORE, RoleSystems.BehaviourTickSystem.class), new SystemDependency(Order.AFTER, RoleSystems.PreBehaviourSupportTickSystem.class));
/*     */     
/*  78 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)npcComponentType, (Query)stateEvaluatorComponent, (Query)this.uuidComponentType });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/*  84 */     return this.dependencies;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/*  89 */     return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/*  95 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 100 */     NPCEntity npcComponent = (NPCEntity)archetypeChunk.getComponent(index, this.npcComponentType);
/* 101 */     assert npcComponent != null;
/*     */     
/* 103 */     UUIDComponent uuidComponent = (UUIDComponent)archetypeChunk.getComponent(index, this.uuidComponentType);
/* 104 */     assert uuidComponent != null;
/*     */     
/* 106 */     Role role = npcComponent.getRole();
/* 107 */     if (role == null)
/*     */       return; 
/* 109 */     StateSupport stateSupport = role.getStateSupport();
/* 110 */     if (stateSupport.isRunningTransitionActions())
/*     */       return; 
/* 112 */     StateEvaluator stateEvaluator = (StateEvaluator)archetypeChunk.getComponent(index, this.stateEvaluatorComponent);
/* 113 */     assert stateEvaluator != null;
/* 114 */     if (!stateEvaluator.isActive() || !stateEvaluator.shouldExecute(dt))
/*     */       return; 
/* 116 */     HytaleLogger.Api logContext = LOGGER.at(Level.FINE);
/* 117 */     if (logContext.isEnabled()) {
/* 118 */       logContext.log("%s with uuid %s: Beginning state evaluation", npcComponent.getRoleName(), uuidComponent.getUuid());
/*     */     }
/*     */     
/* 121 */     EvaluationContext evaluationContext = stateEvaluator.getEvaluationContext();
/* 122 */     stateEvaluator.prepareEvaluationContext(evaluationContext);
/*     */     
/* 124 */     Evaluator<StateOption>.OptionHolder chosenOption = stateEvaluator.evaluate(index, archetypeChunk, commandBuffer, evaluationContext);
/* 125 */     evaluationContext.reset();
/* 126 */     logContext = LOGGER.at(Level.FINE);
/* 127 */     if (logContext.isEnabled()) {
/* 128 */       logContext.log("%s with uuid %s: Chose state option %s", npcComponent.getRoleName(), uuidComponent.getUuid(), chosenOption);
/*     */     }
/*     */     
/* 131 */     if (chosenOption == null)
/*     */       return; 
/* 133 */     StateOption action = (StateOption)chosenOption.getOption();
/* 134 */     int targetState = action.getStateIndex();
/* 135 */     int targetSubState = action.getSubStateIndex();
/* 136 */     if (!stateSupport.inState(targetState) || !stateSupport.inSubState(targetSubState)) {
/* 137 */       stateSupport.setState(action.getStateIndex(), action.getSubStateIndex(), true, false);
/* 138 */       logContext = LOGGER.at(Level.FINE);
/* 139 */       if (logContext.isEnabled()) {
/* 140 */         logContext.log("%s with uuid %s: Setting state", npcComponent.getRoleName(), uuidComponent.getUuid());
/*     */       }
/* 142 */       stateEvaluator.onStateSwitched();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\systems\StateEvaluatorSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */