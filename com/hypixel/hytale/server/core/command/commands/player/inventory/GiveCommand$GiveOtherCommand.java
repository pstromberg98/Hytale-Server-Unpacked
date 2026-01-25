/*     */ package com.hypixel.hytale.server.core.command.commands.player.inventory;
/*     */ 
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
/*     */ import com.hypixel.hytale.server.core.permissions.HytalePermissions;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.bson.BsonDocument;
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
/*     */ class GiveOtherCommand
/*     */   extends CommandBase
/*     */ {
/*     */   @Nonnull
/* 121 */   private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/* 127 */   private final RequiredArg<PlayerRef> playerArg = withRequiredArg("player", "server.commands.argtype.player.desc", (ArgumentType)ArgTypes.PLAYER_REF);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/* 133 */   private final RequiredArg<Item> itemArg = withRequiredArg("item", "server.commands.give.item.desc", (ArgumentType)ArgTypes.ITEM_ASSET);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/* 139 */   private final DefaultArg<Integer> quantityArg = withDefaultArg("quantity", "server.commands.give.quantity.desc", (ArgumentType)ArgTypes.INTEGER, Integer.valueOf(1), "1");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/* 145 */   private final OptionalArg<Double> durabilityArg = withOptionalArg("durability", "server.commands.give.durability.desc", (ArgumentType)ArgTypes.DOUBLE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/* 151 */   private final OptionalArg<String> metadataArg = withOptionalArg("metadata", "server.commands.give.metadata.desc", (ArgumentType)ArgTypes.STRING);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   GiveOtherCommand() {
/* 157 */     super("server.commands.give.other.desc");
/* 158 */     requirePermission(HytalePermissions.fromCommand("give.other"));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void executeSync(@Nonnull CommandContext context) {
/* 163 */     PlayerRef targetPlayerRef = (PlayerRef)this.playerArg.get(context);
/* 164 */     Ref<EntityStore> ref = targetPlayerRef.getReference();
/*     */     
/* 166 */     if (ref == null || !ref.isValid()) {
/* 167 */       context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
/*     */       
/*     */       return;
/*     */     } 
/* 171 */     Store<EntityStore> store = ref.getStore();
/* 172 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 174 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           
/*     */           if (playerComponent == null) {
/*     */             context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
/*     */             
/*     */             return;
/*     */           } 
/*     */           
/*     */           PlayerRef playerRefComponent = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/*     */           
/*     */           assert playerRefComponent != null;
/*     */           
/*     */           Item item = (Item)this.itemArg.get(context);
/*     */           
/*     */           Integer quantity = (Integer)this.quantityArg.get(context);
/*     */           
/*     */           double durability = Double.MAX_VALUE;
/*     */           
/*     */           if (this.durabilityArg.provided(context)) {
/*     */             durability = ((Double)this.durabilityArg.get(context)).doubleValue();
/*     */           }
/*     */           
/*     */           BsonDocument metadata = null;
/*     */           if (this.metadataArg.provided(context)) {
/*     */             String metadataStr = (String)this.metadataArg.get(context);
/*     */             try {
/*     */               metadata = BsonDocument.parse(metadataStr);
/* 202 */             } catch (Exception e) {
/*     */               context.sendMessage(Message.translation("server.commands.give.invalidMetadata").param("error", e.getMessage()));
/*     */               return;
/*     */             } 
/*     */           } 
/*     */           ItemStack stack = (new ItemStack(item.getId(), quantity.intValue(), metadata)).withDurability(durability);
/*     */           ItemStackTransaction transaction = playerComponent.getInventory().getCombinedHotbarFirst().addItemStack(stack);
/*     */           ItemStack remainder = transaction.getRemainder();
/*     */           Message itemNameMessage = Message.translation(item.getTranslationKey());
/*     */           if (remainder == null || remainder.isEmpty()) {
/*     */             context.sendMessage(Message.translation("server.commands.give.gave").param("targetUsername", targetPlayerRef.getUsername()).param("quantity", quantity.intValue()).param("item", itemNameMessage));
/*     */           } else {
/*     */             context.sendMessage(Message.translation("server.commands.give.insufficientInvSpace").param("quantity", quantity.intValue()).param("item", itemNameMessage));
/*     */           } 
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\player\inventory\GiveCommand$GiveOtherCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */