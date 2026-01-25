/*    */ package com.hypixel.hytale.component.query;
/*    */ 
/*    */ import com.hypixel.hytale.component.Archetype;
/*    */ import com.hypixel.hytale.component.ComponentRegistry;
/*    */ import com.hypixel.hytale.component.ComponentType;
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
/*    */ public class OrQuery<ECS_TYPE>
/*    */   implements Query<ECS_TYPE>
/*    */ {
/*    */   @Nonnull
/*    */   private final Query<ECS_TYPE>[] queries;
/*    */   
/*    */   public OrQuery(@Nonnull Query<ECS_TYPE>... queries) {
/* 29 */     this.queries = queries;
/*    */ 
/*    */     
/* 32 */     if (queries.length == 0) {
/* 33 */       throw new IllegalArgumentException("At least one query must be provided for OrQuery");
/*    */     }
/*    */ 
/*    */     
/* 37 */     for (int i = 0; i < queries.length; i++) {
/* 38 */       if (queries[i] == null) {
/* 39 */         throw new NullPointerException("Query at index " + i + " for OrQuery cannot be null");
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean test(Archetype<ECS_TYPE> archetype) {
/* 47 */     for (Query<ECS_TYPE> query : this.queries) {
/* 48 */       if (query.test(archetype)) return true; 
/*    */     } 
/* 50 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean requiresComponentType(ComponentType<ECS_TYPE, ?> componentType) {
/* 55 */     for (Query<ECS_TYPE> query : this.queries) {
/* 56 */       if (query.requiresComponentType(componentType)) return true; 
/*    */     } 
/* 58 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void validateRegistry(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
/* 63 */     for (Query<ECS_TYPE> query : this.queries) query.validateRegistry(registry);
/*    */   
/*    */   }
/*    */   
/*    */   public void validate() {
/* 68 */     for (Query<ECS_TYPE> query : this.queries) query.validate(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\query\OrQuery.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */