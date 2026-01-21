/*    */ package com.hypixel.hytale.server.npc.corecomponents.builders;
/*    */ 
/*    */ import com.hypixel.hytale.server.npc.instructions.BodyMotion;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class BuilderBodyMotionBase
/*    */   extends BuilderMotionBase<BodyMotion>
/*    */ {
/*    */   @Nonnull
/*    */   public final Class<BodyMotion> category() {
/* 14 */     return BodyMotion.class;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\corecomponents\builders\BuilderBodyMotionBase.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */