/*    */ package com.hypixel.hytale.builtin.weather.commands;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.weather.resources.WeatherResource;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WeatherGetCommand
/*    */   extends AbstractWorldCommand
/*    */ {
/*    */   public WeatherGetCommand() {
/* 22 */     super("get", "server.commands.weather.get.desc");
/*    */   }
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/*    */     String weatherId;
/* 27 */     WeatherResource weatherResource = (WeatherResource)store.getResource(WeatherResource.getResourceType());
/* 28 */     int forcedWeatherIndex = weatherResource.getForcedWeatherIndex();
/*    */ 
/*    */     
/* 31 */     if (forcedWeatherIndex != 0) {
/* 32 */       Weather weatherAsset = (Weather)Weather.getAssetMap().getAsset(forcedWeatherIndex);
/* 33 */       weatherId = weatherAsset.getId();
/*    */     } else {
/* 35 */       weatherId = "not locked";
/*    */     } 
/*    */     
/* 38 */     context.sendMessage(Message.translation("server.commands.weather.get.getForcedWeather")
/* 39 */         .param("worldName", world.getName())
/* 40 */         .param("weather", weatherId));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\weather\commands\WeatherGetCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */