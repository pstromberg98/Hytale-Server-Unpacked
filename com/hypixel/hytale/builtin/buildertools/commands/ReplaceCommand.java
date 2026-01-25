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
/*     */ import java.util.concurrent.ThreadLocalRandom;
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
/*  39 */   private final RequiredArg<BlockPattern> toArg = withRequiredArg("to", "server.commands.replace.toBlock.desc", ArgTypes.BLOCK_PATTERN);
/*     */   
/*     */   @Nonnull
/*  42 */   private final FlagArg substringSwapFlag = withFlagArg("substringSwap", "server.commands.replace.substringSwap.desc");
/*     */   
/*     */   @Nonnull
/*  45 */   private final FlagArg regexFlag = withFlagArg("regex", "server.commands.replace.regex.desc");
/*     */   
/*     */   public ReplaceCommand() {
/*  48 */     super("replace", "server.commands.replace.desc");
/*  49 */     setPermissionGroup(GameMode.Creative);
/*  50 */     addUsageVariant((AbstractCommand)new ReplaceFromToCommand());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*  59 */     executeReplace(context, store, ref, playerRef, (String)null, (BlockPattern)this.toArg.get(context), ((Boolean)this.substringSwapFlag
/*  60 */         .get(context)).booleanValue(), ((Boolean)this.regexFlag.get(context)).booleanValue());
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
/*  71 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*  72 */     assert playerComponent != null;
/*     */     
/*  74 */     if (!PrototypePlayerBuilderToolSettings.isOkayToDoCommandsOnSelection(ref, playerComponent, (ComponentAccessor)store))
/*     */       return; 
/*  76 */     if (toPattern == null || toPattern.isEmpty()) {
/*  77 */       context.sendMessage(Message.translation("server.builderTools.invalidBlockType")
/*  78 */           .param("name", "")
/*  79 */           .param("key", ""));
/*     */       
/*     */       return;
/*     */     } 
/*  83 */     String toValue = toPattern.toString();
/*     */ 
/*     */     
/*  86 */     Material fromMaterial = (fromValue != null) ? Material.fromKey(fromValue) : null;
/*     */ 
/*     */     
/*  89 */     Material toMaterial = Material.fromPattern(toPattern, ThreadLocalRandom.current());
/*  90 */     if (toMaterial.isFluid() && !substringSwap && !regex) {
/*  91 */       if (fromMaterial == null) {
/*  92 */         context.sendMessage(Message.translation("server.commands.replace.fromRequired"));
/*     */         return;
/*     */       } 
/*  95 */       BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace(r, fromMaterial, toMaterial, componentAccessor));
/*     */       
/*  97 */       context.sendMessage(Message.translation("server.builderTools.replace.replacementBlockDone")
/*  98 */           .param("from", fromValue)
/*  99 */           .param("to", toValue));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 104 */     if (fromMaterial != null && fromMaterial.isFluid()) {
/* 105 */       BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace(r, fromMaterial, toMaterial, componentAccessor));
/*     */       
/* 107 */       context.sendMessage(Message.translation("server.builderTools.replace.replacementBlockDone")
/* 108 */           .param("from", fromValue)
/* 109 */           .param("to", toValue));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 114 */     BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
/*     */ 
/*     */     
/* 117 */     if (fromValue == null && !substringSwap && !regex) {
/* 118 */       BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace(r, null, toPattern, componentAccessor));
/*     */       
/* 120 */       context.sendMessage(Message.translation("server.builderTools.replace.replacementAllDone").param("to", toValue));
/*     */       
/*     */       return;
/*     */     } 
/* 124 */     if (fromValue == null) {
/* 125 */       context.sendMessage(Message.translation("server.commands.replace.fromRequired"));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 130 */     if (regex) {
/*     */       Pattern pattern;
/*     */       try {
/* 133 */         pattern = Pattern.compile(fromValue);
/* 134 */       } catch (PatternSyntaxException e) {
/* 135 */         context.sendMessage(Message.translation("server.commands.replace.invalidRegex")
/* 136 */             .param("error", e.getMessage()));
/*     */         
/*     */         return;
/*     */       } 
/* 140 */       BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> {
/*     */             s.replace(r, (), toPattern, componentAccessor);
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
/* 153 */     if (fromMaterial == null) {
/* 154 */       context.sendMessage(Message.translation("server.builderTools.invalidBlockType")
/* 155 */           .param("name", fromValue)
/* 156 */           .param("key", fromValue));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 161 */     if (substringSwap) {
/* 162 */       String[] blockKeys = fromValue.split(",");
/* 163 */       Int2IntArrayMap swapMap = new Int2IntArrayMap();
/*     */       
/* 165 */       for (int blockId = 0; blockId < assetMap.getAssetCount(); blockId++) {
/* 166 */         BlockType blockType = (BlockType)assetMap.getAsset(blockId);
/* 167 */         String blockKeyStr = blockType.getId();
/*     */         
/* 169 */         for (String from : blockKeys) {
/* 170 */           if (blockKeyStr.contains(from.trim())) {
/*     */             String replacedKey;
/*     */             try {
/* 173 */               replacedKey = blockKeyStr.replace(from.trim(), toValue);
/* 174 */             } catch (Exception e) {}
/*     */ 
/*     */ 
/*     */             
/* 178 */             int index = assetMap.getIndex(replacedKey);
/* 179 */             if (index != Integer.MIN_VALUE) {
/* 180 */               swapMap.put(blockId, index);
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 187 */       if (!swapMap.isEmpty()) {
/* 188 */         BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace(r, (), componentAccessor));
/*     */         
/* 190 */         context.sendMessage(Message.translation("server.builderTools.replace.replacementDone")
/* 191 */             .param("nb", swapMap.size())
/* 192 */             .param("to", toValue));
/*     */       } else {
/* 194 */         context.sendMessage(Message.translation("server.commands.replace.noMatchingBlocks")
/* 195 */             .param("blockType", fromValue));
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 201 */     int fromBlockId = fromMaterial.getBlockId();
/* 202 */     BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace(r, (), toPattern, componentAccessor));
/*     */ 
/*     */     
/* 205 */     context.sendMessage(Message.translation("server.builderTools.replace.replacementBlockDone")
/* 206 */         .param("from", fromValue)
/* 207 */         .param("to", toValue));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ReplaceFromToCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/* 216 */     private final RequiredArg<String> fromArg = withRequiredArg("from", "server.commands.replace.from.desc", (ArgumentType)ArgTypes.STRING);
/*     */     
/*     */     @Nonnull
/* 219 */     private final RequiredArg<BlockPattern> toArg = withRequiredArg("to", "server.commands.replace.toBlock.desc", ArgTypes.BLOCK_PATTERN);
/*     */     
/*     */     @Nonnull
/* 222 */     private final FlagArg substringSwapFlag = withFlagArg("substringSwap", "server.commands.replace.substringSwap.desc");
/*     */     
/*     */     @Nonnull
/* 225 */     private final FlagArg regexFlag = withFlagArg("regex", "server.commands.replace.regex.desc");
/*     */     
/*     */     public ReplaceFromToCommand() {
/* 228 */       super("server.commands.replace.desc");
/* 229 */       setPermissionGroup(GameMode.Creative);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 238 */       ReplaceCommand.executeReplace(context, store, ref, playerRef, (String)this.fromArg.get(context), (BlockPattern)this.toArg.get(context), ((Boolean)this.substringSwapFlag
/* 239 */           .get(context)).booleanValue(), ((Boolean)this.regexFlag.get(context)).booleanValue());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\commands\ReplaceCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */