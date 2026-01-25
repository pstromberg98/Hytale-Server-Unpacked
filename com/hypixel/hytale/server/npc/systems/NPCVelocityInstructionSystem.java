/*     */ package com.hypixel.hytale.server.npc.systems;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.dependency.SystemTypeDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.ChangeVelocityType;
/*     */ import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
/*     */ import com.hypixel.hytale.server.core.modules.physics.systems.GenericVelocityInstructionSystem;
/*     */ import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*     */ import com.hypixel.hytale.server.npc.role.Role;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ public class NPCVelocityInstructionSystem
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*     */   @Nonnull
/*  32 */   private final Set<Dependency<EntityStore>> dependencies = (Set)Set.of(new SystemDependency(Order.BEFORE, GenericVelocityInstructionSystem.class), new SystemTypeDependency(Order.AFTER, 
/*     */ 
/*     */ 
/*     */         
/*  36 */         EntityModule.get().getVelocityModifyingSystemType()));
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
/*     */   public NPCVelocityInstructionSystem() {
/*  49 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)NPCEntity.getComponentType(), (Query)Velocity.getComponentType() });
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  54 */     NPCEntity npcComponent = (NPCEntity)archetypeChunk.getComponent(index, NPCEntity.getComponentType());
/*  55 */     assert npcComponent != null;
/*     */     
/*  57 */     Role role = npcComponent.getRole();
/*  58 */     if (role == null)
/*     */       return; 
/*  60 */     Velocity velocityComponent = (Velocity)archetypeChunk.getComponent(index, Velocity.getComponentType());
/*  61 */     assert velocityComponent != null;
/*     */ 
/*     */     
/*  64 */     for (Velocity.Instruction instruction : velocityComponent.getInstructions()) {
/*  65 */       Vector3d velocity; VelocityConfig velocityConfig; switch (instruction.getType()) {
/*     */         case Set:
/*  67 */           velocity = instruction.getVelocity();
/*  68 */           velocityConfig = instruction.getConfig();
/*     */           
/*  70 */           role.processSetVelocityInstruction(velocity, velocityConfig);
/*     */           
/*  72 */           if (DebugUtils.DISPLAY_FORCES) {
/*  73 */             TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, TransformComponent.getComponentType());
/*  74 */             if (transformComponent != null) {
/*  75 */               World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/*  76 */               DebugUtils.addForce(world, transformComponent.getPosition(), velocity, velocityConfig);
/*     */             } 
/*     */           } 
/*     */         
/*     */         case Add:
/*  81 */           velocity = instruction.getVelocity();
/*  82 */           velocityConfig = instruction.getConfig();
/*     */           
/*  84 */           role.processAddVelocityInstruction(velocity, velocityConfig);
/*     */           
/*  86 */           if (DebugUtils.DISPLAY_FORCES) {
/*  87 */             TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, TransformComponent.getComponentType());
/*  88 */             if (transformComponent != null) {
/*  89 */               World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/*  90 */               DebugUtils.addForce(world, transformComponent.getPosition(), velocity, velocityConfig);
/*     */             } 
/*     */           } 
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/*  98 */     velocityComponent.getInstructions().clear();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/* 104 */     return this.dependencies;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 110 */     return this.query;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\systems\NPCVelocityInstructionSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */