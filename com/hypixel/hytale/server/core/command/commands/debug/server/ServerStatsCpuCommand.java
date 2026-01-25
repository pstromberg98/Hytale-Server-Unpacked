/*    */ package com.hypixel.hytale.server.core.command.commands.debug.server;
/*    */ 
/*    */ import com.hypixel.hytale.common.util.FormatUtil;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import com.sun.management.OperatingSystemMXBean;
/*    */ import java.lang.management.ManagementFactory;
/*    */ import java.lang.management.OperatingSystemMXBean;
/*    */ import java.lang.management.RuntimeMXBean;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class ServerStatsCpuCommand
/*    */   extends CommandBase
/*    */ {
/*    */   @Nonnull
/* 18 */   private static final Message MESSAGE_COMMANDS_SERVER_STATS_FULL_INFO_UNAVAILABLE = Message.translation("server.commands.server.stats.fullInfoUnavailable");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ServerStatsCpuCommand() {
/* 24 */     super("cpu", "server.commands.server.stats.cpu.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 29 */     RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
/* 30 */     OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
/*    */     
/* 32 */     if (operatingSystemMXBean instanceof OperatingSystemMXBean) { OperatingSystemMXBean sunOSBean = (OperatingSystemMXBean)operatingSystemMXBean;
/* 33 */       context.sendMessage(Message.translation("server.commands.server.stats.cpu.fullUsageInfo")
/* 34 */           .param("systemLoad", sunOSBean.getSystemCpuLoad())
/* 35 */           .param("processLoad", sunOSBean.getProcessCpuLoad())); }
/*    */     else
/* 37 */     { context.sendMessage(MESSAGE_COMMANDS_SERVER_STATS_FULL_INFO_UNAVAILABLE); }
/*    */ 
/*    */     
/* 40 */     context.sendMessage(Message.translation("server.commands.server.stats.cpu.usageInfo")
/* 41 */         .param("loadAverage", operatingSystemMXBean.getSystemLoadAverage())
/* 42 */         .param("processUptime", FormatUtil.timeUnitToString(runtimeMXBean.getUptime(), TimeUnit.MILLISECONDS)));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\debug\server\ServerStatsCpuCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */