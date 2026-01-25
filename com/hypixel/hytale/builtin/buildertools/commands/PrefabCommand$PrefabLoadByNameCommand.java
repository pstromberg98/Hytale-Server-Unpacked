/*     */ package com.hypixel.hytale.builtin.buildertools.commands;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
/*     */ import com.hypixel.hytale.builtin.buildertools.utils.RecursivePrefabLoader;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabStore;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Objects;
/*     */ import java.util.Random;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.Function;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class PrefabLoadByNameCommand
/*     */   extends AbstractPlayerCommand
/*     */ {
/*     */   @Nonnull
/* 189 */   private final RequiredArg<String> nameArg = withRequiredArg("name", "server.commands.prefab.load.name.desc", (ArgumentType)ArgTypes.STRING);
/*     */   
/*     */   @Nonnull
/* 192 */   private final DefaultArg<String> storeTypeArg = withDefaultArg("storeType", "server.commands.prefab.load.storeType.desc", (ArgumentType)ArgTypes.STRING, "asset", "server.commands.prefab.load.storeType.desc");
/*     */   
/*     */   @Nonnull
/* 195 */   private final DefaultArg<String> storeNameArg = withDefaultArg("storeName", "server.commands.prefab.load.storeName.desc", (ArgumentType)ArgTypes.STRING, null, "");
/*     */   
/*     */   @Nonnull
/* 198 */   private final FlagArg childrenFlag = withFlagArg("children", "server.commands.prefab.load.children.desc");
/*     */   
/*     */   public PrefabLoadByNameCommand() {
/* 201 */     super("server.commands.prefab.load.desc");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*     */     BiFunction<String, Random, BlockSelection> loader;
/*     */     Path foundPath, storePath;
/* 210 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 211 */     assert playerComponent != null;
/*     */     
/* 213 */     String storeType = (String)this.storeTypeArg.get(context);
/* 214 */     String storeName = (String)this.storeNameArg.get(context);
/* 215 */     String name = (String)this.nameArg.get(context);
/*     */     
/* 217 */     if (!name.endsWith(".prefab.json")) {
/* 218 */       name = name + ".prefab.json";
/*     */     }
/*     */     
/* 221 */     Path prefabStorePath = null;
/* 222 */     Path resolvedPrefabPath = null;
/* 223 */     String finalName = name;
/*     */     
/* 225 */     switch (storeType) {
/*     */       case "server":
/* 227 */         prefabStorePath = PrefabStore.get().getServerPrefabsPath();
/* 228 */         Objects.requireNonNull(PrefabStore.get());
/*     */ 
/*     */       
/*     */       case "asset":
/* 232 */         foundPath = PrefabStore.get().findAssetPrefabPath(finalName);
/* 233 */         if (foundPath != null) {
/* 234 */           resolvedPrefabPath = foundPath;
/* 235 */           prefabStorePath = foundPath.getParent();
/*     */         } 
/*     */ 
/*     */         
/* 239 */         prefabStorePath = PrefabStore.get().getAssetPrefabsPath();
/* 240 */         Objects.requireNonNull(PrefabStore.get());
/*     */       
/*     */       case "worldgen":
/* 243 */         storePath = PrefabStore.get().getWorldGenPrefabsPath(storeName);
/* 244 */         prefabStorePath = PrefabStore.get().getWorldGenPrefabsPath(storeName);
/*     */ 
/*     */       
/*     */       default:
/* 248 */         context.sendMessage(Message.translation("server.commands.prefab.invalidStoreType")
/* 249 */             .param("storeType", storeType)); break;
/* 250 */     }  Function<String, BlockSelection> prefabGetter = null;
/*     */ 
/*     */ 
/*     */     
/* 254 */     if (prefabGetter == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 259 */     Path finalPrefabStorePath = prefabStorePath;
/* 260 */     if (((Boolean)this.childrenFlag.get(context)).booleanValue()) {
/* 261 */       RecursivePrefabLoader.BlockSelectionLoader blockSelectionLoader = new RecursivePrefabLoader.BlockSelectionLoader(finalPrefabStorePath, prefabGetter);
/*     */     } else {
/* 263 */       loader = ((prefabFile, rand) -> (BlockSelection)prefabGetter.apply(prefabFile));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 268 */     boolean prefabExists = ((resolvedPrefabPath != null && Files.isRegularFile(resolvedPrefabPath, new java.nio.file.LinkOption[0])) || Files.isRegularFile(prefabStorePath.resolve(name), new java.nio.file.LinkOption[0]));
/*     */     
/* 270 */     if (prefabExists) {
/* 271 */       BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.load(finalName, loader.apply(finalName, s.getRandom()), componentAccessor));
/*     */     } else {
/*     */       
/* 274 */       context.sendMessage(Message.translation("server.builderTools.prefab.prefabNotFound")
/* 275 */           .param("name", name));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\commands\PrefabCommand$PrefabLoadByNameCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */