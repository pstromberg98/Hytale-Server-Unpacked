/*    */ package com.hypixel.hytale.server.core.modules.entity;
/*    */ 
/*    */ import com.hypixel.hytale.component.Archetype;
/*    */ import com.hypixel.hytale.component.ComponentRegistry;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.component.query.Query;
/*    */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class AllLegacyEntityTypesQuery
/*    */   implements Query<EntityStore>
/*    */ {
/*    */   @Nonnull
/* 22 */   public static final AllLegacyEntityTypesQuery INSTANCE = new AllLegacyEntityTypesQuery();
/*    */ 
/*    */   
/*    */   public boolean test(@Nonnull Archetype<EntityStore> archetype) {
/* 26 */     return EntityUtils.hasEntity(archetype);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean requiresComponentType(ComponentType<EntityStore, ?> componentType) {
/* 31 */     return false;
/*    */   }
/*    */   
/*    */   public void validateRegistry(@Nonnull ComponentRegistry<EntityStore> registry) {}
/*    */   
/*    */   public void validate() {}
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\AllLegacyEntityTypesQuery.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */