/*    */ package com.hypixel.hytale.server.core.command.commands.utility.lighting;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
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
/*    */ class LightingSendLocalCommand
/*    */   extends LightingSendToggleCommand
/*    */ {
/*    */   public LightingSendLocalCommand() {
/* 25 */     super("local", "server.commands.sendlighting.local.desc", "server.commands.sendlighting.local.enabled.desc", "server.commands.sendlighting.localLightingStatus", () -> BlockChunk.SEND_LOCAL_LIGHTING_DATA, value -> BlockChunk.SEND_LOCAL_LIGHTING_DATA = value.booleanValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\command\\utility\lighting\LightingSendCommand$LightingSendLocalCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */