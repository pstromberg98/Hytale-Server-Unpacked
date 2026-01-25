/*    */ package com.hypixel.hytale.component;
/*    */ 
/*    */ import com.hypixel.hytale.component.query.ReadWriteArchetypeQuery;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ReadWriteQuery<ECS_TYPE>
/*    */   implements ReadWriteArchetypeQuery<ECS_TYPE>
/*    */ {
/*    */   @Nonnull
/*    */   private final Archetype<ECS_TYPE> read;
/*    */   @Nonnull
/*    */   private final Archetype<ECS_TYPE> write;
/*    */   
/*    */   public ReadWriteQuery(@Nonnull Archetype<ECS_TYPE> read, @Nonnull Archetype<ECS_TYPE> write) {
/* 34 */     this.read = read;
/* 35 */     this.write = write;
/*    */ 
/*    */     
/* 38 */     Objects.requireNonNull(read, "Read archetype for ReadWriteQuery cannot be null");
/* 39 */     Objects.requireNonNull(write, "Write archetype for ReadWriteQuery cannot be null");
/*    */   }
/*    */ 
/*    */   
/*    */   public Archetype<ECS_TYPE> getReadArchetype() {
/* 44 */     return this.read;
/*    */   }
/*    */ 
/*    */   
/*    */   public Archetype<ECS_TYPE> getWriteArchetype() {
/* 49 */     return this.write;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\ReadWriteQuery.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */