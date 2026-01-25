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
/*    */ public class ExactArchetypeQuery<ECS_TYPE>
/*    */   implements Query<ECS_TYPE>
/*    */ {
/*    */   @Nonnull
/*    */   private final Archetype<ECS_TYPE> archetype;
/*    */   
/*    */   public ExactArchetypeQuery(@Nonnull Archetype<ECS_TYPE> archetype) {
/* 29 */     this.archetype = archetype;
/*    */ 
/*    */     
/* 32 */     Objects.requireNonNull(archetype, "Archetype for ExactArchetypeQuery cannot be null");
/*    */   }
/*    */   
/*    */   public Archetype<ECS_TYPE> getArchetype() {
/* 36 */     return this.archetype;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean test(@Nonnull Archetype<ECS_TYPE> archetype) {
/* 41 */     return archetype.equals(this.archetype);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean requiresComponentType(@Nonnull ComponentType<ECS_TYPE, ?> componentType) {
/* 46 */     return this.archetype.requiresComponentType(componentType);
/*    */   }
/*    */ 
/*    */   
/*    */   public void validateRegistry(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
/* 51 */     this.archetype.validateRegistry(registry);
/*    */   }
/*    */ 
/*    */   
/*    */   public void validate() {
/* 56 */     this.archetype.validate();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\query\ExactArchetypeQuery.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */