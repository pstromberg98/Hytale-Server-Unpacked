/*    */ package com.hypixel.hytale.server.core.universe.world.commands.world.tps;
/*    */ 
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WorldTpsCommand
/*    */   extends AbstractWorldCommand
/*    */ {
/*    */   @Nonnull
/* 23 */   private final RequiredArg<Integer> tickRateArg = withRequiredArg("rate", "server.commands.world.tps.rate.desc", (ArgumentType)ArgTypes.TICK_RATE);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WorldTpsCommand() {
/* 29 */     super("tps", "server.commands.world.tps.desc");
/* 30 */     addAliases(new String[] { "tickrate" });
/* 31 */     addSubCommand((AbstractCommand)new WorldTpsResetCommand());
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 36 */     int newTickRate = ((Integer)this.tickRateArg.get(context)).intValue();
/*    */     
/* 38 */     if (newTickRate <= 0 || newTickRate > 2048) {
/* 39 */       context.sendMessage(Message.translation("server.commands.world.tps.set.invalid")
/* 40 */           .param("value", newTickRate));
/*    */       
/*    */       return;
/*    */     } 
/* 44 */     world.setTps(newTickRate);
/* 45 */     double newMs = 1000.0D / newTickRate;
/* 46 */     context.sendMessage(Message.translation("server.commands.world.tps.set.success")
/* 47 */         .param("worldName", world.getName())
/* 48 */         .param("tps", newTickRate)
/* 49 */         .param("ms", String.format("%.2f", new Object[] { Double.valueOf(newMs) })));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\commands\world\tps\WorldTpsCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */