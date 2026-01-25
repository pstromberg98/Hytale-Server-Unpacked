/*    */ package com.hypixel.hytale.server.core.command.commands.debug.server;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import java.lang.management.GarbageCollectorMXBean;
/*    */ import java.lang.management.ManagementFactory;
/*    */ import java.util.Arrays;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServerStatsGcCommand
/*    */   extends CommandBase
/*    */ {
/*    */   public ServerStatsGcCommand() {
/* 19 */     super("gc", "server.commands.server.stats.gc.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 24 */     for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans())
/* 25 */       context.sendMessage(Message.translation("server.commands.server.stats.gc.usageInfo")
/* 26 */           .param("name", garbageCollectorMXBean.getName())
/* 27 */           .param("poolNames", Arrays.toString((Object[])garbageCollectorMXBean.getMemoryPoolNames()))
/* 28 */           .param("collectionCount", garbageCollectorMXBean.getCollectionCount())
/* 29 */           .param("collectionTime", garbageCollectorMXBean.getCollectionTime())); 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\debug\server\ServerStatsGcCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */