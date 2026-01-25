/*     */ package com.hypixel.hytale.server.npc.systems;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.server.core.entity.knockback.KnockbackSystems;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.system.TransformSystems;
/*     */ import com.hypixel.hytale.server.core.modules.physics.util.PhysicsMath;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.npc.NPCPlugin;
/*     */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*     */ import com.hypixel.hytale.server.npc.role.Role;
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
/*     */ public class SteeringSystem
/*     */   extends SteppableTickingSystem
/*     */ {
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, NPCEntity> npcEntityComponent;
/*     */   @Nonnull
/*     */   private final Set<Dependency<EntityStore>> dependencies;
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/*     */   
/*     */   public SteeringSystem(@Nonnull ComponentType<EntityStore, NPCEntity> npcEntityComponent) {
/*  52 */     this.npcEntityComponent = npcEntityComponent;
/*  53 */     this.dependencies = Set.of(new SystemDependency(Order.AFTER, AvoidanceSystem.class), new SystemDependency(Order.AFTER, KnockbackSystems.ApplyKnockback.class), new SystemDependency(Order.BEFORE, TransformSystems.EntityTrackerUpdate.class));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  59 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)npcEntityComponent, (Query)TransformComponent.getComponentType() });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/*  65 */     return this.dependencies;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/*  70 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/*  76 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public void steppedTick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  81 */     NPCEntity npcComponent = (NPCEntity)archetypeChunk.getComponent(index, this.npcEntityComponent);
/*  82 */     assert npcComponent != null;
/*     */     
/*  84 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, TransformComponent.getComponentType());
/*  85 */     assert transformComponent != null;
/*     */     
/*  87 */     Role role = npcComponent.getRole();
/*  88 */     if (role == null)
/*     */       return; 
/*  90 */     Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  95 */       if (role.getDebugSupport().isDebugMotionSteering()) {
/*  96 */         Vector3d position = transformComponent.getPosition();
/*  97 */         double x = position.getX();
/*  98 */         double z = position.getZ();
/*  99 */         float yaw = transformComponent.getRotation().getYaw();
/*     */         
/* 101 */         role.getActiveMotionController().steer(ref, role, role.getBodySteering(), role.getHeadSteering(), dt, (ComponentAccessor)commandBuffer);
/*     */         
/* 103 */         x = position.getX() - x;
/* 104 */         z = position.getZ() - z;
/*     */         
/* 106 */         double l = Math.sqrt(x * x + z * z);
/* 107 */         double v = l / dt;
/* 108 */         double vx = x / dt;
/* 109 */         double vz = z / dt;
/* 110 */         double vh = (l > 0.0D) ? PhysicsMath.normalizeTurnAngle(PhysicsMath.headingFromDirection(x, z)) : 0.0D;
/*     */         
/* 112 */         NPCPlugin.get().getLogger().at(Level.FINER).log("=   Role    = t =%.4f v =%.4f vx=%.4f vz=%.4f h =%.4f nh=%.4f vh=%.4f", Float.valueOf(dt), 
/* 113 */             Double.valueOf(v), Double.valueOf(vx), Double.valueOf(vz), Float.valueOf(57.295776F * yaw), Float.valueOf(57.295776F * yaw), 
/* 114 */             Double.valueOf(57.2957763671875D * vh));
/*     */       } else {
/* 116 */         role.getActiveMotionController().steer(ref, role, role.getBodySteering(), role.getHeadSteering(), dt, (ComponentAccessor)commandBuffer);
/*     */       } 
/* 118 */     } catch (IllegalArgumentException|IllegalStateException e) {
/* 119 */       ((HytaleLogger.Api)NPCPlugin.get().getLogger().at(Level.SEVERE).withCause(e)).log();
/* 120 */       commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\systems\SteeringSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */