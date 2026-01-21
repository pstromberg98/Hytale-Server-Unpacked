/*    */ package com.hypixel.hytale.server.core.universe.world.commands.world.perf;
/*    */ 
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
/*    */ import com.hypixel.hytale.server.core.universe.Universe;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WorldPerfResetCommand
/*    */   extends AbstractWorldCommand
/*    */ {
/*    */   @Nonnull
/* 19 */   private static final Message MESSAGE_COMMANDS_WORLD_PERF_RESET_ALL = Message.translation("server.commands.world.perf.reset.all");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 24 */   private final FlagArg allFlag = withFlagArg("all", "server.commands.world.perf.reset.all.desc");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WorldPerfResetCommand() {
/* 30 */     super("reset", "server.commands.world.perf.reset.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 35 */     if (this.allFlag.provided(context)) {
/* 36 */       Universe.get().getWorlds().forEach((name, w) -> w.clearMetrics());
/* 37 */       context.sendMessage(MESSAGE_COMMANDS_WORLD_PERF_RESET_ALL);
/*    */       
/*    */       return;
/*    */     } 
/* 41 */     world.clearMetrics();
/* 42 */     context.sendMessage(Message.translation("server.commands.world.perf.reset")
/* 43 */         .param("worldName", world.getName()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\commands\world\perf\WorldPerfResetCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */