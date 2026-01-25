/*    */ package com.hypixel.hytale.server.core.command.commands.debug.server;
/*    */ 
/*    */ import com.hypixel.hytale.common.util.FormatUtil;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import com.sun.management.OperatingSystemMXBean;
/*    */ import java.lang.management.ManagementFactory;
/*    */ import java.lang.management.MemoryMXBean;
/*    */ import java.lang.management.MemoryUsage;
/*    */ import java.lang.management.OperatingSystemMXBean;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ public class ServerStatsMemoryCommand
/*    */   extends CommandBase
/*    */ {
/*    */   @Nonnull
/* 19 */   private static final Message MESSAGE_COMMANDS_SERVER_STATS_FULL_INFO_UNAVAILABLE = Message.translation("server.commands.server.stats.fullInfoUnavailable");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ServerStatsMemoryCommand() {
/* 25 */     super("memory", "server.commands.server.stats.memory.desc");
/* 26 */     addAliases(new String[] { "mem" });
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 31 */     OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
/*    */     
/* 33 */     if (operatingSystemMXBean instanceof OperatingSystemMXBean) { OperatingSystemMXBean sunOSBean = (OperatingSystemMXBean)operatingSystemMXBean;
/* 34 */       context.sendMessage(Message.translation("server.commands.server.stats.memory.fullUsageInfo")
/* 35 */           .param("totalPhysicalMemory", FormatUtil.bytesToString(sunOSBean.getTotalPhysicalMemorySize()))
/* 36 */           .param("freePhysicalMemory", FormatUtil.bytesToString(sunOSBean.getFreePhysicalMemorySize()))
/* 37 */           .param("totalSwapMemory", FormatUtil.bytesToString(sunOSBean.getTotalSwapSpaceSize()))
/* 38 */           .param("freeSwapMemory", FormatUtil.bytesToString(sunOSBean.getFreeSwapSpaceSize()))); }
/*    */     else
/* 40 */     { context.sendMessage(MESSAGE_COMMANDS_SERVER_STATS_FULL_INFO_UNAVAILABLE); }
/*    */ 
/*    */     
/* 43 */     MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
/* 44 */     context.sendMessage(Message.translation("server.commands.server.stats.memory.usageInfo")
/* 45 */         .param("heapMemoryUsage", formatMemoryUsage(memoryMXBean.getHeapMemoryUsage()))
/* 46 */         .param("nonHeapMemoryUsage", formatMemoryUsage(memoryMXBean.getNonHeapMemoryUsage()))
/* 47 */         .param("objectsPendingFinalizationCount", memoryMXBean.getObjectPendingFinalizationCount()));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   private static Message formatMemoryUsage(@Nonnull MemoryUsage memoryUsage) {
/* 58 */     return Message.translation("server.commands.server.stats.memory.usage")
/* 59 */       .param("init", FormatUtil.bytesToString(memoryUsage.getInit()))
/* 60 */       .param("used", FormatUtil.bytesToString(memoryUsage.getUsed()))
/* 61 */       .param("committed", FormatUtil.bytesToString(memoryUsage.getCommitted()))
/* 62 */       .param("max", FormatUtil.bytesToString(memoryUsage.getMax()))
/* 63 */       .param("free", FormatUtil.bytesToString(memoryUsage.getMax() - memoryUsage.getCommitted()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\debug\server\ServerStatsMemoryCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */