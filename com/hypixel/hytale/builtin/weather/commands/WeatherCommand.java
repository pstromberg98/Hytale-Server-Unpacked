/*    */ package com.hypixel.hytale.builtin.weather.commands;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WeatherCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public WeatherCommand() {
/* 14 */     super("weather", "server.commands.weather.desc");
/* 15 */     addSubCommand((AbstractCommand)new WeatherSetCommand());
/* 16 */     addSubCommand((AbstractCommand)new WeatherGetCommand());
/* 17 */     addSubCommand((AbstractCommand)new WeatherResetCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\weather\commands\WeatherCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */