/*     */ package com.hypixel.hytale.builtin.buildertools.commands;
/*     */ 
/*     */ import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
/*     */ import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
/*     */ import com.hypixel.hytale.builtin.buildertools.PrototypePlayerBuilderToolSettings;
/*     */ import com.hypixel.hytale.builtin.buildertools.utils.Material;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.protocol.GameMode;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.regex.PatternSyntaxException;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReplaceCommand
/*     */   extends AbstractPlayerCommand
/*     */ {
/*     */   @Nonnull
/*  38 */   private final RequiredArg<BlockPattern> toArg = withRequiredArg("to", "server.commands.replace.toBlock.desc", ArgTypes.BLOCK_PATTERN);
/*     */   
/*     */   @Nonnull
/*  41 */   private final FlagArg substringSwapFlag = withFlagArg("substringSwap", "server.commands.replace.substringSwap.desc");
/*     */   
/*     */   @Nonnull
/*  44 */   private final FlagArg regexFlag = withFlagArg("regex", "server.commands.replace.regex.desc");
/*     */   
/*     */   public ReplaceCommand() {
/*  47 */     super("replace", "server.commands.replace.desc");
/*  48 */     setPermissionGroup(GameMode.Creative);
/*  49 */     addUsageVariant((AbstractCommand)new ReplaceFromToCommand());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*  58 */     executeReplace(context, store, ref, playerRef, (String)null, (BlockPattern)this.toArg.get(context), ((Boolean)this.substringSwapFlag
/*  59 */         .get(context)).booleanValue(), ((Boolean)this.regexFlag.get(context)).booleanValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void executeReplace(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nullable String fromValue, @Nonnull BlockPattern toPattern, boolean substringSwap, boolean regex) {
/*  70 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*  71 */     assert playerComponent != null;
/*     */     
/*  73 */     if (!PrototypePlayerBuilderToolSettings.isOkayToDoCommandsOnSelection(ref, playerComponent, (ComponentAccessor)store))
/*     */       return; 
/*  75 */     if (toPattern == null || toPattern.isEmpty()) {
/*  76 */       context.sendMessage(Message.translation("server.builderTools.invalidBlockType")
/*  77 */           .param("name", "")
/*  78 */           .param("key", ""));
/*     */       
/*     */       return;
/*     */     } 
/*  82 */     String toValue = toPattern.toString();
/*  83 */     Integer[] toBlockIds = toPattern.getResolvedKeys();
/*     */ 
/*     */     
/*  86 */     Material fromMaterial = (fromValue != null) ? Material.fromKey(fromValue) : null;
/*     */ 
/*     */     
/*  89 */     if (fromMaterial != null && fromMaterial.isFluid()) {
/*  90 */       Material toMaterial = Material.fromKey(toValue);
/*  91 */       if (toMaterial == null) {
/*  92 */         context.sendMessage(Message.translation("server.builderTools.invalidBlockType")
/*  93 */             .param("name", toValue)
/*  94 */             .param("key", toValue));
/*     */         return;
/*     */       } 
/*  97 */       BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace(r, fromMaterial, toMaterial, componentAccessor));
/*     */       
/*  99 */       context.sendMessage(Message.translation("server.builderTools.replace.replacementBlockDone")
/* 100 */           .param("from", fromValue)
/* 101 */           .param("to", toValue));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 106 */     BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
/*     */ 
/*     */     
/* 109 */     if (fromValue == null && !substringSwap && !regex) {
/* 110 */       int[] arrayOfInt = toIntArray(toBlockIds);
/* 111 */       BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace(r, null, toIds, componentAccessor));
/*     */       
/* 113 */       context.sendMessage(Message.translation("server.builderTools.replace.replacementAllDone").param("to", toValue));
/*     */       
/*     */       return;
/*     */     } 
/* 117 */     if (fromValue == null) {
/* 118 */       context.sendMessage(Message.translation("server.commands.replace.fromRequired"));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 123 */     if (regex) {
/*     */       Pattern pattern;
/*     */       try {
/* 126 */         pattern = Pattern.compile(fromValue);
/* 127 */       } catch (PatternSyntaxException e) {
/* 128 */         context.sendMessage(Message.translation("server.commands.replace.invalidRegex")
/* 129 */             .param("error", e.getMessage()));
/*     */         
/*     */         return;
/*     */       } 
/* 133 */       int[] arrayOfInt = toIntArray(toBlockIds);
/* 134 */       BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> {
/*     */             s.replace(r, (), toIds, componentAccessor);
/*     */ 
/*     */ 
/*     */             
/*     */             context.sendMessage(Message.translation("server.commands.replace.success").param("regex", fromValue).param("replacement", toValue));
/*     */           });
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 147 */     if (fromMaterial == null) {
/* 148 */       context.sendMessage(Message.translation("server.builderTools.invalidBlockType")
/* 149 */           .param("name", fromValue)
/* 150 */           .param("key", fromValue));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 155 */     if (substringSwap) {
/* 156 */       String[] blockKeys = fromValue.split(",");
/* 157 */       Int2IntArrayMap swapMap = new Int2IntArrayMap();
/*     */       
/* 159 */       for (int blockId = 0; blockId < assetMap.getAssetCount(); blockId++) {
/* 160 */         BlockType blockType = (BlockType)assetMap.getAsset(blockId);
/* 161 */         String blockKeyStr = blockType.getId();
/*     */         
/* 163 */         for (String from : blockKeys) {
/* 164 */           if (blockKeyStr.contains(from.trim())) {
/*     */             String replacedKey;
/*     */             try {
/* 167 */               replacedKey = blockKeyStr.replace(from.trim(), toValue);
/* 168 */             } catch (Exception e) {}
/*     */ 
/*     */ 
/*     */             
/* 172 */             int index = assetMap.getIndex(replacedKey);
/* 173 */             if (index != Integer.MIN_VALUE) {
/* 174 */               swapMap.put(blockId, index);
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 181 */       if (!swapMap.isEmpty()) {
/* 182 */         BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace(r, (), componentAccessor));
/*     */         
/* 184 */         context.sendMessage(Message.translation("server.builderTools.replace.replacementDone")
/* 185 */             .param("nb", swapMap.size())
/* 186 */             .param("to", toValue));
/*     */       } else {
/* 188 */         context.sendMessage(Message.translation("server.commands.replace.noMatchingBlocks")
/* 189 */             .param("blockType", fromValue));
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 195 */     int[] toIds = toIntArray(toBlockIds);
/* 196 */     int fromBlockId = fromMaterial.getBlockId();
/* 197 */     BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace(r, (), toIds, componentAccessor));
/*     */ 
/*     */     
/* 200 */     context.sendMessage(Message.translation("server.builderTools.replace.replacementBlockDone")
/* 201 */         .param("from", fromValue)
/* 202 */         .param("to", toValue));
/*     */   }
/*     */   
/*     */   private static int[] toIntArray(Integer[] arr) {
/* 206 */     int[] result = new int[arr.length];
/* 207 */     for (int i = 0; i < arr.length; i++) {
/* 208 */       result[i] = arr[i].intValue();
/*     */     }
/* 210 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ReplaceFromToCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/* 219 */     private final RequiredArg<String> fromArg = withRequiredArg("from", "server.commands.replace.from.desc", (ArgumentType)ArgTypes.STRING);
/*     */     
/*     */     @Nonnull
/* 222 */     private final RequiredArg<BlockPattern> toArg = withRequiredArg("to", "server.commands.replace.toBlock.desc", ArgTypes.BLOCK_PATTERN);
/*     */     
/*     */     @Nonnull
/* 225 */     private final FlagArg substringSwapFlag = withFlagArg("substringSwap", "server.commands.replace.substringSwap.desc");
/*     */     
/*     */     @Nonnull
/* 228 */     private final FlagArg regexFlag = withFlagArg("regex", "server.commands.replace.regex.desc");
/*     */     
/*     */     public ReplaceFromToCommand() {
/* 231 */       super("server.commands.replace.desc");
/* 232 */       setPermissionGroup(GameMode.Creative);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 241 */       ReplaceCommand.executeReplace(context, store, ref, playerRef, (String)this.fromArg.get(context), (BlockPattern)this.toArg.get(context), ((Boolean)this.substringSwapFlag
/* 242 */           .get(context)).booleanValue(), ((Boolean)this.regexFlag.get(context)).booleanValue());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\commands\ReplaceCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */