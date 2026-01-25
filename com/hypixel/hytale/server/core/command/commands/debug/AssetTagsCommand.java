/*    */ package com.hypixel.hytale.server.core.command.commands.debug;
/*    */ 
/*    */ import com.hypixel.hytale.assetstore.AssetMap;
/*    */ import com.hypixel.hytale.assetstore.AssetRegistry;
/*    */ import com.hypixel.hytale.assetstore.AssetStore;
/*    */ import com.hypixel.hytale.assetstore.map.AssetMapWithIndexes;
/*    */ import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import com.hypixel.hytale.server.core.util.message.MessageFormat;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.stream.Collectors;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AssetTagsCommand
/*    */   extends CommandBase
/*    */ {
/*    */   @Nonnull
/* 26 */   private final RequiredArg<String> classArg = withRequiredArg("class", "server.commands.assets.tags.class.desc", (ArgumentType)ArgTypes.STRING);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 32 */   private final RequiredArg<String> tagArg = withRequiredArg("tag", "server.commands.assets.tags.tag.desc", (ArgumentType)ArgTypes.STRING);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AssetTagsCommand() {
/* 38 */     super("tags", "server.commands.assets.tags.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 43 */     String assetClass = (String)this.classArg.get(context);
/* 44 */     String tag = (String)this.tagArg.get(context);
/* 45 */     int tagIndex = AssetRegistry.getTagIndex(tag);
/*    */     
/* 47 */     if (tagIndex == Integer.MIN_VALUE) {
/* 48 */       context.sendMessage(Message.translation("server.commands.assets.tags.tagNotFound")
/* 49 */           .param("tag", tag));
/*    */       
/*    */       return;
/*    */     } 
/* 53 */     for (Map.Entry<Class<? extends JsonAssetWithMap>, AssetStore<?, ?, ?>> entry : (Iterable<Map.Entry<Class<? extends JsonAssetWithMap>, AssetStore<?, ?, ?>>>)AssetRegistry.getStoreMap().entrySet()) {
/* 54 */       String simpleName = ((Class)entry.getKey()).getSimpleName();
/* 55 */       if (!simpleName.equalsIgnoreCase(assetClass))
/*    */         continue; 
/* 57 */       context.sendMessage(Message.translation("server.commands.assets.tags.assetsOfTypeWithTag")
/* 58 */           .param("type", simpleName)
/* 59 */           .param("tag", tag));
/*    */       
/* 61 */       AssetMap<?, ?> assetMap = ((AssetStore)entry.getValue()).getAssetMap();
/*    */ 
/*    */ 
/*    */       
/* 65 */       Set<Message> keysForTag = (Set<Message>)assetMap.getKeysForTag(tagIndex).stream().map(Object::toString).map(Message::raw).collect(Collectors.toSet());
/* 66 */       context.sendMessage(MessageFormat.list(Message.translation("server.commands.assets.tags.assetKeys"), keysForTag));
/*    */       
/* 68 */       if (assetMap instanceof AssetMapWithIndexes) { AssetMapWithIndexes<?, ?> assetMapWithIndexes = (AssetMapWithIndexes<?, ?>)assetMap;
/*    */ 
/*    */ 
/*    */         
/* 72 */         Set<Message> indexesForTag = (Set<Message>)assetMapWithIndexes.getIndexesForTag(tagIndex).intStream().mapToObj(Integer::toString).map(Message::raw).collect(Collectors.toSet());
/* 73 */         context.sendMessage(MessageFormat.list(Message.translation("server.commands.assets.tags.assetIndexes"), indexesForTag)); }
/*    */     
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\debug\AssetTagsCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */