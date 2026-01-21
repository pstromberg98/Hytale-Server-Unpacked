/*    */ package com.hypixel.hytale.server.core.prefab.selection.buffer;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.prefab.PrefabRotation;
/*    */ import java.util.Random;
/*    */ 
/*    */ 
/*    */ public class PrefabBufferCall
/*    */ {
/*    */   public Random random;
/*    */   public PrefabRotation rotation;
/*    */   
/*    */   public PrefabBufferCall() {}
/*    */   
/*    */   public PrefabBufferCall(Random random, PrefabRotation rotation) {
/* 15 */     this.random = random;
/* 16 */     this.rotation = rotation;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\prefab\selection\buffer\PrefabBufferCall.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */