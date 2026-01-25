/*    */ package com.hypixel.hytale.builtin.adventure.objectives.commands;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.ObjectivePlugin;
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.markers.reachlocation.ReachLocationMarker;
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.markers.reachlocation.ReachLocationMarkerAsset;
/*    */ import com.hypixel.hytale.common.util.StringUtil;
/*    */ import com.hypixel.hytale.component.AddReason;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.Holder;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.asset.type.model.config.Model;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandUtil;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*    */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*    */ import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.HiddenFromAdventurePlayers;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
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
/*    */ public class AddReachLocationMarkerCommand
/*    */   extends AbstractPlayerCommand
/*    */ {
/*    */   @Nonnull
/* 48 */   private final RequiredArg<String> reachLocationMarkerArg = withRequiredArg("reachLocationMarkerId", "server.commands.objective.reachLocationMarker.add.arg.reachLocationMarkerId.desc", (ArgumentType)ArgTypes.STRING);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AddReachLocationMarkerCommand() {
/* 54 */     super("add", "server.commands.objective.reachLocationMarker.add");
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
/* 65 */     String reachLocationMarkerId = (String)this.reachLocationMarkerArg.get(context);
/* 66 */     if (ReachLocationMarkerAsset.getAssetMap().getAsset(reachLocationMarkerId) == null) {
/* 67 */       context.sendMessage(Message.translation("server.commands.objective.reachLocationMarker.notFound")
/* 68 */           .param("id", reachLocationMarkerId));
/* 69 */       context.sendMessage(Message.translation("server.general.failed.didYouMean")
/* 70 */           .param("choices", StringUtil.sortByFuzzyDistance(reachLocationMarkerId, ReachLocationMarkerAsset.getAssetMap().getAssetMap().keySet(), CommandUtil.RECOMMEND_COUNT).toString()));
/*    */       
/*    */       return;
/*    */     } 
/* 74 */     Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
/* 75 */     holder.addComponent(ReachLocationMarker.getComponentType(), (Component)new ReachLocationMarker(reachLocationMarkerId));
/*    */     
/* 77 */     Model model = ObjectivePlugin.get().getObjectiveLocationMarkerModel();
/* 78 */     holder.addComponent(ModelComponent.getComponentType(), (Component)new ModelComponent(model));
/* 79 */     holder.addComponent(PersistentModel.getComponentType(), (Component)new PersistentModel(model.toReference()));
/* 80 */     holder.addComponent(Nameplate.getComponentType(), (Component)new Nameplate(reachLocationMarkerId));
/*    */     
/* 82 */     TransformComponent playerTransformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/* 83 */     assert playerTransformComponent != null;
/*    */     
/* 85 */     TransformComponent transform = new TransformComponent(playerTransformComponent.getPosition(), playerTransformComponent.getRotation());
/* 86 */     holder.addComponent(TransformComponent.getComponentType(), (Component)transform);
/* 87 */     holder.ensureComponent(UUIDComponent.getComponentType());
/* 88 */     holder.ensureComponent(Intangible.getComponentType());
/* 89 */     holder.ensureComponent(HiddenFromAdventurePlayers.getComponentType());
/* 90 */     store.addEntity(holder, AddReason.SPAWN);
/* 91 */     context.sendMessage(Message.translation("server.commands.objective.reachLocationMarker.added")
/* 92 */         .param("id", reachLocationMarkerId));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\objectives\commands\ObjectiveReachLocationMarkerCommand$AddReachLocationMarkerCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */