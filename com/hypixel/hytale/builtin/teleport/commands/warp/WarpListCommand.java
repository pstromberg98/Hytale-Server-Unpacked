/*     */ package com.hypixel.hytale.builtin.teleport.commands.warp;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
/*     */ import com.hypixel.hytale.builtin.teleport.Warp;
/*     */ import com.hypixel.hytale.builtin.teleport.WarpListPage;
/*     */ import com.hypixel.hytale.common.util.ListUtil;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
/*     */ import com.hypixel.hytale.server.core.permissions.HytalePermissions;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.sneakythrow.SneakyThrow;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WarpListCommand
/*     */   extends CommandBase
/*     */ {
/*     */   private static final int WARPS_PER_LIST_PAGE = 8;
/*  34 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WARP_NOT_LOADED = Message.translation("server.commands.teleport.warp.notLoaded");
/*  35 */   private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");
/*  36 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WARP_NO_WARPS = Message.translation("server.commands.teleport.warp.noWarps");
/*  37 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WARP_PAGE_NUM_TOO_HIGH = Message.translation("server.commands.teleport.warp.pageNumTooHigh");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  43 */   private final OptionalArg<Integer> pageArg = withOptionalArg("page", "server.commands.warp.list.page.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WarpListCommand() {
/*  49 */     super("list", "server.commands.warp.list.desc");
/*  50 */     requirePermission(HytalePermissions.fromCommand("warp.list"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void executeSync(@Nonnull CommandContext context) {
/*  60 */     if (!TeleportPlugin.get().isWarpsLoaded()) {
/*  61 */       context.sendMessage(MESSAGE_COMMANDS_TELEPORT_WARP_NOT_LOADED);
/*     */       
/*     */       return;
/*     */     } 
/*  65 */     Map<String, Warp> warps = TeleportPlugin.get().getWarps();
/*     */ 
/*     */     
/*  68 */     if (context.isPlayer() && !this.pageArg.provided(context)) {
/*  69 */       Ref<EntityStore> ref = context.senderAsPlayerRef();
/*  70 */       if (ref == null || !ref.isValid()) {
/*  71 */         context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
/*     */         
/*     */         return;
/*     */       } 
/*  75 */       Store<EntityStore> store = ref.getStore();
/*  76 */       World playerWorld = ((EntityStore)store.getExternalData()).getWorld();
/*     */       
/*  78 */       playerWorld.execute(() -> {
/*     */             Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */ 
/*     */             
/*     */             assert playerComponent != null;
/*     */ 
/*     */             
/*     */             PlayerRef playerRefComponent = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/*     */ 
/*     */             
/*     */             assert playerRefComponent != null;
/*     */ 
/*     */             
/*     */             playerComponent.getPageManager().openCustomPage(ref, store, (CustomUIPage)new WarpListPage(playerRefComponent, warps, ()));
/*     */           });
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  97 */     if (warps.isEmpty()) {
/*  98 */       context.sendMessage(MESSAGE_COMMANDS_TELEPORT_WARP_NO_WARPS);
/*     */       
/*     */       return;
/*     */     } 
/* 102 */     int pageNumber = this.pageArg.provided(context) ? ((Integer)this.pageArg.get(context)).intValue() : 1;
/* 103 */     if (pageNumber < 1) {
/* 104 */       context.sendMessage(Message.translation("server.commands.teleport.warp.pageNumTooLow")
/* 105 */           .param("page", pageNumber));
/*     */       
/*     */       return;
/*     */     } 
/* 109 */     ObjectArrayList objectArrayList = new ObjectArrayList(warps.values());
/* 110 */     objectArrayList.sort((o1, o2) -> o2.getCreationDate().compareTo(o1.getCreationDate()));
/* 111 */     List<List<Warp>> paginated = ListUtil.partition((List)objectArrayList, 8);
/*     */     
/* 113 */     if (paginated.size() < pageNumber) {
/* 114 */       context.sendMessage(MESSAGE_COMMANDS_TELEPORT_WARP_PAGE_NUM_TOO_HIGH);
/*     */     } else {
/* 116 */       context.sendMessage(Message.translation("server.commands.teleport.warp.listHeader")
/* 117 */           .param("page", pageNumber)
/* 118 */           .param("pages", paginated.size()));
/* 119 */       int startIndex = (pageNumber - 1) * 8;
/* 120 */       List<Warp> page = paginated.get(pageNumber - 1);
/*     */       
/* 122 */       int i = 1;
/* 123 */       for (Warp w : page) {
/* 124 */         context.sendMessage(Message.translation("server.commands.teleport.warp.listEntry")
/* 125 */             .param("index", startIndex + i)
/* 126 */             .param("name", w.getId())
/* 127 */             .param("creator", w.getCreator()));
/* 128 */         i++;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\commands\warp\WarpListCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */