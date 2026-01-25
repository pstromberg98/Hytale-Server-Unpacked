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
/*    */ public interface Query<ECS_TYPE>
/*    */ {
/*    */   @Nonnull
/*    */   static <ECS_TYPE> AnyQuery<ECS_TYPE> any() {
/* 25 */     return (AnyQuery)AnyQuery.INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   static <ECS_TYPE> NotQuery<ECS_TYPE> not(@Nonnull Query<ECS_TYPE> query) {
/* 37 */     return new NotQuery<>(query);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   @SafeVarargs
/*    */   static <ECS_TYPE> AndQuery<ECS_TYPE> and(@Nonnull Query<ECS_TYPE>... queries) {
/* 50 */     return new AndQuery<>(queries);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   @SafeVarargs
/*    */   static <ECS_TYPE> OrQuery<ECS_TYPE> or(@Nonnull Query<ECS_TYPE>... queries) {
/* 63 */     return new OrQuery<>(queries);
/*    */   }
/*    */   
/*    */   boolean test(Archetype<ECS_TYPE> paramArchetype);
/*    */   
/*    */   boolean requiresComponentType(ComponentType<ECS_TYPE, ?> paramComponentType);
/*    */   
/*    */   void validateRegistry(@Nonnull ComponentRegistry<ECS_TYPE> paramComponentRegistry);
/*    */   
/*    */   void validate();
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\query\Query.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */