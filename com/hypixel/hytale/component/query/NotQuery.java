/*    */ package com.hypixel.hytale.component.query;
/*    */ 
/*    */ import com.hypixel.hytale.component.Archetype;
/*    */ import com.hypixel.hytale.component.ComponentRegistry;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import java.util.Objects;
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
/*    */ public class NotQuery<ECS_TYPE>
/*    */   implements Query<ECS_TYPE>
/*    */ {
/*    */   @Nonnull
/*    */   private final Query<ECS_TYPE> query;
/*    */   
/*    */   public NotQuery(@Nonnull Query<ECS_TYPE> query) {
/* 29 */     this.query = query;
/*    */ 
/*    */     
/* 32 */     Objects.requireNonNull(query, "Sub-query for NotQuery cannot be null");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean test(Archetype<ECS_TYPE> archetype) {
/* 38 */     return !this.query.test(archetype);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean requiresComponentType(ComponentType<ECS_TYPE, ?> componentType) {
/* 44 */     return this.query.requiresComponentType(componentType);
/*    */   }
/*    */ 
/*    */   
/*    */   public void validateRegistry(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
/* 49 */     this.query.validateRegistry(registry);
/*    */   }
/*    */ 
/*    */   
/*    */   public void validate() {
/* 54 */     this.query.validate();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\query\NotQuery.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */