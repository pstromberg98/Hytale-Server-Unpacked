/*    */ package com.hypixel.hytale.builtin.buildertools.commands;
/*    */ import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
/*    */ import com.hypixel.hytale.builtin.buildertools.PrototypePlayerBuilderToolSettings;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.RemoveReason;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.math.vector.Vector3i;
/*    */ import com.hypixel.hytale.protocol.GameMode;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.ArrayList;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class ClearEntitiesCommand extends AbstractPlayerCommand {
/*    */   @Nonnull
/* 23 */   private static final Message MESSAGE_NO_SELECTION = Message.translation("server.commands.clearEntities.noSelection");
/*    */   
/*    */   public ClearEntitiesCommand() {
/* 26 */     super("clearEntities", "server.commands.clearEntities.desc");
/* 27 */     setPermissionGroup(GameMode.Creative);
/* 28 */     requirePermission("hytale.editor.selection.clipboard");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 37 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 38 */     assert playerComponent != null;
/*    */     
/* 40 */     if (!PrototypePlayerBuilderToolSettings.isOkayToDoCommandsOnSelection(ref, playerComponent, (ComponentAccessor)store))
/*    */       return; 
/* 42 */     BuilderToolsPlugin.BuilderState builderState = BuilderToolsPlugin.getState(playerComponent, playerRef);
/* 43 */     BlockSelection selection = builderState.getSelection();
/*    */     
/* 45 */     if (selection == null) {
/* 46 */       context.sendMessage(MESSAGE_NO_SELECTION);
/*    */       
/*    */       return;
/*    */     } 
/* 50 */     Vector3i min = selection.getSelectionMin();
/* 51 */     Vector3i max = selection.getSelectionMax();
/* 52 */     int width = max.getX() - min.getX();
/* 53 */     int height = max.getY() - min.getY();
/* 54 */     int depth = max.getZ() - min.getZ();
/*    */     
/* 56 */     ArrayList<Ref<EntityStore>> entitiesToRemove = new ArrayList<>();
/* 57 */     Objects.requireNonNull(entitiesToRemove); BuilderToolsPlugin.forEachCopyableInSelection(world, min.getX(), min.getY(), min.getZ(), width, height, depth, entitiesToRemove::add);
/*    */     
/* 59 */     Store<EntityStore> entityStore = world.getEntityStore().getStore();
/* 60 */     for (Ref<EntityStore> entityRef : entitiesToRemove) {
/* 61 */       entityStore.removeEntity(entityRef, RemoveReason.REMOVE);
/*    */     }
/*    */     
/* 64 */     context.sendMessage(Message.translation("server.commands.clearEntities.cleared")
/* 65 */         .param("count", entitiesToRemove.size()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\commands\ClearEntitiesCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */