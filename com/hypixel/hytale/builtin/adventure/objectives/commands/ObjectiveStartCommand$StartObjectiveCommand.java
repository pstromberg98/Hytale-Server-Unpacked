/*    */ package com.hypixel.hytale.builtin.adventure.objectives.commands;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.Objective;
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.ObjectivePlugin;
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.config.ObjectiveAsset;
/*    */ import com.hypixel.hytale.common.util.StringUtil;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandUtil;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.HashSet;
/*    */ import java.util.UUID;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StartObjectiveCommand
/*    */   extends AbstractPlayerCommand
/*    */ {
/*    */   @Nonnull
/* 48 */   private final RequiredArg<String> objectiveArg = withRequiredArg("objectiveId", "server.commands.objective.start.objective.arg.objectiveId.desc", (ArgumentType)ArgTypes.STRING);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StartObjectiveCommand() {
/* 54 */     super("objective", "server.commands.objective.start.objective");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 65 */     String objectiveId = (String)this.objectiveArg.get(context);
/*    */     
/* 67 */     ObjectiveAsset asset = (ObjectiveAsset)ObjectiveAsset.getAssetMap().getAsset(objectiveId);
/* 68 */     if (asset == null) {
/* 69 */       context.sendMessage(Message.translation("server.commands.objective.objectiveNotFound")
/* 70 */           .param("id", objectiveId));
/* 71 */       context.sendMessage(Message.translation("server.general.failed.didYouMean")
/* 72 */           .param("choices", StringUtil.sortByFuzzyDistance(objectiveId, ObjectiveAsset.getAssetMap().getAssetMap().keySet(), CommandUtil.RECOMMEND_COUNT).toString()));
/*    */       
/*    */       return;
/*    */     } 
/* 76 */     HashSet<UUID> playerSet = new HashSet<>();
/* 77 */     playerSet.add(playerRef.getUuid());
/*    */     
/* 79 */     Objective objective = ObjectivePlugin.get().startObjective(objectiveId, playerSet, world.getWorldConfig().getUuid(), null, store);
/* 80 */     if (objective == null)
/*    */       return; 
/* 82 */     objective.checkTaskSetCompletion(store);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\objectives\commands\ObjectiveStartCommand$StartObjectiveCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */