/*    */ package com.hypixel.hytale.server.core.command.commands.world.chunk;
/*    */ 
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
/*    */ import com.hypixel.hytale.server.core.modules.entity.player.ChunkTracker;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChunkMaxSendRateCommand
/*    */   extends AbstractTargetPlayerCommand
/*    */ {
/*    */   @Nonnull
/* 27 */   private final OptionalArg<Integer> secArg = withOptionalArg("sec", "server.commands.chunk.maxsendrate.sec.desc", (ArgumentType)ArgTypes.INTEGER);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 33 */   private final OptionalArg<Integer> tickArg = withOptionalArg("tick", "server.commands.chunk.maxsendrate.tick.desc", (ArgumentType)ArgTypes.INTEGER);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChunkMaxSendRateCommand() {
/* 39 */     super("maxsendrate", "server.commands.chunk.maxsendrate.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nullable Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 44 */     ChunkTracker chunkTracker = (ChunkTracker)store.getComponent(ref, ChunkTracker.getComponentType());
/* 45 */     assert chunkTracker != null;
/*    */     
/* 47 */     if (this.secArg.provided(context)) {
/* 48 */       int sec = ((Integer)this.secArg.get(context)).intValue();
/* 49 */       chunkTracker.setMaxChunksPerSecond(sec);
/* 50 */       context.sendMessage(Message.translation("server.commands.chunk.maxsendrate.sec.set")
/* 51 */           .param("value", sec));
/*    */     } 
/*    */     
/* 54 */     if (this.tickArg.provided(context)) {
/* 55 */       int tick = ((Integer)this.tickArg.get(context)).intValue();
/* 56 */       chunkTracker.setMaxChunksPerTick(tick);
/* 57 */       context.sendMessage(Message.translation("server.commands.chunk.maxsendrate.tick.set")
/* 58 */           .param("value", tick));
/*    */     } 
/*    */     
/* 61 */     context.sendMessage(Message.translation("server.commands.chunk.maxsendrate.summary")
/* 62 */         .param("perSecond", chunkTracker.getMaxChunksPerSecond())
/* 63 */         .param("perTick", chunkTracker.getMaxChunksPerTick()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\world\chunk\ChunkMaxSendRateCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */