/*     */ package com.hypixel.hytale.builtin.adventure.objectives.commands;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.adventure.objectives.Objective;
/*     */ import com.hypixel.hytale.builtin.adventure.objectives.ObjectivePlugin;
/*     */ import com.hypixel.hytale.builtin.adventure.objectives.config.ObjectiveAsset;
/*     */ import com.hypixel.hytale.builtin.adventure.objectives.config.ObjectiveLineAsset;
/*     */ import com.hypixel.hytale.common.util.StringUtil;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandUtil;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.HashSet;
/*     */ import java.util.UUID;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ObjectiveStartCommand
/*     */   extends AbstractCommandCollection
/*     */ {
/*     */   public ObjectiveStartCommand() {
/*  35 */     super("start", "server.commands.objective.start");
/*  36 */     addSubCommand((AbstractCommand)new StartObjectiveCommand());
/*  37 */     addSubCommand((AbstractCommand)new StartObjectiveLineCommand());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class StartObjectiveCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/*  48 */     private final RequiredArg<String> objectiveArg = withRequiredArg("objectiveId", "server.commands.objective.start.objective.arg.objectiveId.desc", (ArgumentType)ArgTypes.STRING);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public StartObjectiveCommand() {
/*  54 */       super("objective", "server.commands.objective.start.objective");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*  65 */       String objectiveId = (String)this.objectiveArg.get(context);
/*     */       
/*  67 */       ObjectiveAsset asset = (ObjectiveAsset)ObjectiveAsset.getAssetMap().getAsset(objectiveId);
/*  68 */       if (asset == null) {
/*  69 */         context.sendMessage(Message.translation("server.commands.objective.objectiveNotFound")
/*  70 */             .param("id", objectiveId));
/*  71 */         context.sendMessage(Message.translation("server.general.failed.didYouMean")
/*  72 */             .param("choices", StringUtil.sortByFuzzyDistance(objectiveId, ObjectiveAsset.getAssetMap().getAssetMap().keySet(), CommandUtil.RECOMMEND_COUNT).toString()));
/*     */         
/*     */         return;
/*     */       } 
/*  76 */       HashSet<UUID> playerSet = new HashSet<>();
/*  77 */       playerSet.add(playerRef.getUuid());
/*     */       
/*  79 */       Objective objective = ObjectivePlugin.get().startObjective(objectiveId, playerSet, world.getWorldConfig().getUuid(), null, store);
/*  80 */       if (objective == null)
/*     */         return; 
/*  82 */       objective.checkTaskSetCompletion(store);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class StartObjectiveLineCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/*  94 */     private final RequiredArg<String> objectiveLineArg = withRequiredArg("objectiveLineId", "server.commands.objective.start.objectiveLine.arg.objectiveLineId.desc", (ArgumentType)ArgTypes.STRING);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public StartObjectiveLineCommand() {
/* 100 */       super("line", "server.commands.objective.start.objectiveLine");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 111 */       String objectiveLineId = (String)this.objectiveLineArg.get(context);
/*     */ 
/*     */       
/* 114 */       if (ObjectiveLineAsset.getAssetMap().getAsset(objectiveLineId) == null) {
/* 115 */         context.sendMessage(Message.translation("server.commands.objective.objectiveLineNotFound")
/* 116 */             .param("id", objectiveLineId));
/* 117 */         context.sendMessage(Message.translation("server.general.failed.didYouMean")
/* 118 */             .param("choices", StringUtil.sortByFuzzyDistance(objectiveLineId, ObjectiveLineAsset.getAssetMap().getAssetMap().keySet(), CommandUtil.RECOMMEND_COUNT).toString()));
/*     */         
/*     */         return;
/*     */       } 
/* 122 */       HashSet<UUID> playerSet = new HashSet<>();
/* 123 */       playerSet.add(playerRef.getUuid());
/*     */       
/* 125 */       ObjectivePlugin.get().startObjectiveLine(store, objectiveLineId, playerSet, world.getWorldConfig().getUuid(), null);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\objectives\commands\ObjectiveStartCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */