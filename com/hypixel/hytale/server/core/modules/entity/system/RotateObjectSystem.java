/*    */ package com.hypixel.hytale.server.core.modules.entity.system;
/*    */ 
/*    */ import com.hypixel.hytale.component.ArchetypeChunk;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.component.query.Query;
/*    */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*    */ import com.hypixel.hytale.math.vector.Vector3f;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.RotateObjectComponent;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RotateObjectSystem
/*    */   extends EntityTickingSystem<EntityStore>
/*    */ {
/*    */   @Nonnull
/*    */   private final ComponentType<EntityStore, TransformComponent> transformComponentType;
/*    */   @Nonnull
/*    */   private final ComponentType<EntityStore, RotateObjectComponent> rotateObjectComponentType;
/*    */   
/*    */   public RotateObjectSystem(@Nonnull ComponentType<EntityStore, TransformComponent> transformComponentType, @Nonnull ComponentType<EntityStore, RotateObjectComponent> rotateObjectComponentType) {
/* 40 */     this.transformComponentType = transformComponentType;
/* 41 */     this.rotateObjectComponentType = rotateObjectComponentType;
/*    */   }
/*    */ 
/*    */   
/*    */   public Query<EntityStore> getQuery() {
/* 46 */     return (Query<EntityStore>)Query.and(new Query[] { (Query)this.rotateObjectComponentType, (Query)this.transformComponentType });
/*    */   }
/*    */ 
/*    */   
/*    */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 51 */     RotateObjectComponent rotateObjectComponent = (RotateObjectComponent)archetypeChunk.getComponent(index, this.rotateObjectComponentType);
/* 52 */     assert rotateObjectComponent != null;
/*    */     
/* 54 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, this.transformComponentType);
/* 55 */     assert transformComponent != null;
/*    */     
/* 57 */     Vector3f rotation = transformComponent.getRotation();
/* 58 */     rotation.y += rotateObjectComponent.getRotationSpeed() * dt;
/* 59 */     if (rotation.y >= 360.0F) rotation.y %= 360.0F; 
/* 60 */     transformComponent.setRotation(rotation);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\system\RotateObjectSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */