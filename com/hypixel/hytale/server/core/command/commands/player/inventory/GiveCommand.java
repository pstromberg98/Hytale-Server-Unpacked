/*     */ package com.hypixel.hytale.server.core.command.commands.player.inventory;
/*     */ 
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
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
/*     */ public class GiveCommand
/*     */   extends AbstractPlayerCommand
/*     */ {
/*     */   @Nonnull
/*  30 */   private static final Message MESSAGE_COMMANDS_GIVE_RECEIVED = Message.translation("server.commands.give.received");
/*     */   @Nonnull
/*  32 */   private static final Message MESSAGE_COMMANDS_GIVE_INSUFFICIENT_INV_SPACE = Message.translation("server.commands.give.insufficientInvSpace");
/*     */   @Nonnull
/*  34 */   private static final Message MESSAGE_COMMANDS_GIVE_INVALID_METADATA = Message.translation("server.commands.give.invalidMetadata");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  40 */   private final RequiredArg<Item> itemArg = withRequiredArg("item", "server.commands.give.item.desc", (ArgumentType)ArgTypes.ITEM_ASSET);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  46 */   private final DefaultArg<Integer> quantityArg = withDefaultArg("quantity", "server.commands.give.quantity.desc", (ArgumentType)ArgTypes.INTEGER, Integer.valueOf(1), "1");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  52 */   private final OptionalArg<Double> durabilityArg = withOptionalArg("durability", "server.commands.give.durability.desc", (ArgumentType)ArgTypes.DOUBLE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  58 */   private final OptionalArg<String> metadataArg = withOptionalArg("metadata", "server.commands.give.metadata.desc", (ArgumentType)ArgTypes.STRING);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GiveCommand() {
/*  64 */     super("give", "server.commands.give.desc");
/*  65 */     requirePermission(HytalePermissions.fromCommand("give.self"));
/*  66 */     addUsageVariant((AbstractCommand)new GiveOtherCommand());
/*  67 */     addSubCommand((AbstractCommand)new GiveArmorCommand());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*  76 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*  77 */     assert playerComponent != null;
/*     */ 
/*     */     
/*  80 */     Item item = (Item)this.itemArg.get(context);
/*     */ 
/*     */     
/*  83 */     Integer quantity = (Integer)this.quantityArg.get(context);
/*     */ 
/*     */     
/*  86 */     double durability = Double.MAX_VALUE;
/*  87 */     if (this.durabilityArg.provided(context)) {
/*  88 */       durability = ((Double)this.durabilityArg.get(context)).doubleValue();
/*     */     }
/*     */ 
/*     */     
/*  92 */     BsonDocument metadata = null;
/*  93 */     if (this.metadataArg.provided(context)) {
/*  94 */       String metadataStr = (String)this.metadataArg.get(context);
/*     */       try {
/*  96 */         metadata = BsonDocument.parse(metadataStr);
/*  97 */       } catch (Exception e) {
/*  98 */         context.sendMessage(MESSAGE_COMMANDS_GIVE_INVALID_METADATA
/*  99 */             .param("error", e.getMessage()));
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 105 */     ItemStack stack = (new ItemStack(item.getId(), quantity.intValue(), metadata)).withDurability(durability);
/* 106 */     ItemStackTransaction transaction = playerComponent.getInventory().getCombinedHotbarFirst().addItemStack(stack);
/*     */     
/* 108 */     ItemStack remainder = transaction.getRemainder();
/* 109 */     Message itemNameMessage = Message.translation(item.getTranslationKey());
/*     */     
/* 111 */     if (remainder == null || remainder.isEmpty()) {
/* 112 */       context.sendMessage(MESSAGE_COMMANDS_GIVE_RECEIVED
/* 113 */           .param("quantity", quantity.intValue())
/* 114 */           .param("item", itemNameMessage));
/*     */     } else {
/* 116 */       context.sendMessage(MESSAGE_COMMANDS_GIVE_INSUFFICIENT_INV_SPACE
/* 117 */           .param("quantity", quantity.intValue())
/* 118 */           .param("item", itemNameMessage));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static class GiveOtherCommand
/*     */     extends CommandBase
/*     */   {
/*     */     @Nonnull
/* 127 */     private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");
/*     */     @Nonnull
/* 129 */     private static final Message MESSAGE_COMMANDS_GIVE_GAVE = Message.translation("server.commands.give.gave");
/*     */     @Nonnull
/* 131 */     private static final Message MESSAGE_COMMANDS_GIVE_INSUFFICIENT_INV_SPACE = Message.translation("server.commands.give.insufficientInvSpace");
/*     */     @Nonnull
/* 133 */     private static final Message MESSAGE_COMMANDS_GIVE_INVALID_METADATA = Message.translation("server.commands.give.invalidMetadata");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 139 */     private final RequiredArg<PlayerRef> playerArg = withRequiredArg("player", "server.commands.argtype.player.desc", (ArgumentType)ArgTypes.PLAYER_REF);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 145 */     private final RequiredArg<Item> itemArg = withRequiredArg("item", "server.commands.give.item.desc", (ArgumentType)ArgTypes.ITEM_ASSET);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 151 */     private final DefaultArg<Integer> quantityArg = withDefaultArg("quantity", "server.commands.give.quantity.desc", (ArgumentType)ArgTypes.INTEGER, Integer.valueOf(1), "1");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 157 */     private final OptionalArg<Double> durabilityArg = withOptionalArg("durability", "server.commands.give.durability.desc", (ArgumentType)ArgTypes.DOUBLE);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 163 */     private final OptionalArg<String> metadataArg = withOptionalArg("metadata", "server.commands.give.metadata.desc", (ArgumentType)ArgTypes.STRING);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     GiveOtherCommand() {
/* 169 */       super("server.commands.give.other.desc");
/* 170 */       requirePermission(HytalePermissions.fromCommand("give.other"));
/*     */     }
/*     */ 
/*     */     
/*     */     protected void executeSync(@Nonnull CommandContext context) {
/* 175 */       PlayerRef targetPlayerRef = (PlayerRef)this.playerArg.get(context);
/* 176 */       Ref<EntityStore> ref = targetPlayerRef.getReference();
/*     */       
/* 178 */       if (ref == null || !ref.isValid()) {
/* 179 */         context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
/*     */         
/*     */         return;
/*     */       } 
/* 183 */       Store<EntityStore> store = ref.getStore();
/* 184 */       World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */       
/* 186 */       world.execute(() -> {
/*     */             Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */             
/*     */             if (playerComponent == null) {
/*     */               context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
/*     */               
/*     */               return;
/*     */             } 
/*     */             
/*     */             PlayerRef playerRefComponent = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/*     */             
/*     */             assert playerRefComponent != null;
/*     */             
/*     */             Item item = (Item)this.itemArg.get(context);
/*     */             
/*     */             Integer quantity = (Integer)this.quantityArg.get(context);
/*     */             
/*     */             double durability = Double.MAX_VALUE;
/*     */             
/*     */             if (this.durabilityArg.provided(context)) {
/*     */               durability = ((Double)this.durabilityArg.get(context)).doubleValue();
/*     */             }
/*     */             
/*     */             BsonDocument metadata = null;
/*     */             if (this.metadataArg.provided(context)) {
/*     */               String metadataStr = (String)this.metadataArg.get(context);
/*     */               try {
/*     */                 metadata = BsonDocument.parse(metadataStr);
/* 214 */               } catch (Exception e) {
/*     */                 context.sendMessage(MESSAGE_COMMANDS_GIVE_INVALID_METADATA.param("error", e.getMessage()));
/*     */                 return;
/*     */               } 
/*     */             } 
/*     */             ItemStack stack = (new ItemStack(item.getId(), quantity.intValue(), metadata)).withDurability(durability);
/*     */             ItemStackTransaction transaction = playerComponent.getInventory().getCombinedHotbarFirst().addItemStack(stack);
/*     */             ItemStack remainder = transaction.getRemainder();
/*     */             Message itemNameMessage = Message.translation(item.getTranslationKey());
/*     */             if (remainder == null || remainder.isEmpty()) {
/*     */               context.sendMessage(MESSAGE_COMMANDS_GIVE_GAVE.param("targetUsername", targetPlayerRef.getUsername()).param("quantity", quantity.intValue()).param("item", itemNameMessage));
/*     */             } else {
/*     */               context.sendMessage(MESSAGE_COMMANDS_GIVE_INSUFFICIENT_INV_SPACE.param("quantity", quantity.intValue()).param("item", itemNameMessage));
/*     */             } 
/*     */           });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\player\inventory\GiveCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */