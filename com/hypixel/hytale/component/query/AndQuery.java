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
/*    */ public class AndQuery<ECS_TYPE>
/*    */   implements Query<ECS_TYPE>
/*    */ {
/*    */   @Nonnull
/*    */   private final Query<ECS_TYPE>[] queries;
/*    */   
/*    */   @SafeVarargs
/*    */   public AndQuery(@Nonnull Query<ECS_TYPE>... queries) {
/* 29 */     this.queries = queries;
/*    */ 
/*    */     
/* 32 */     for (int i = 0; i < queries.length; i++) {
/* 33 */       Query<ECS_TYPE> query = queries[i];
/* 34 */       if (query == null) {
/* 35 */         throw new IllegalArgumentException("Query in AndQuery cannot be null (Index: " + i + ")");
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean test(Archetype<ECS_TYPE> archetype) {
/* 43 */     for (Query<ECS_TYPE> query : this.queries) {
/* 44 */       if (!query.test(archetype)) return false; 
/*    */     } 
/* 46 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean requiresComponentType(ComponentType<ECS_TYPE, ?> componentType) {
/* 51 */     for (Query<ECS_TYPE> query : this.queries) {
/* 52 */       if (query.requiresComponentType(componentType)) return true; 
/*    */     } 
/* 54 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void validateRegistry(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
/* 59 */     for (Query<ECS_TYPE> query : this.queries) query.validateRegistry(registry);
/*    */   
/*    */   }
/*    */   
/*    */   public void validate() {
/* 64 */     for (Query<ECS_TYPE> query : this.queries) query.validate(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\query\AndQuery.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */