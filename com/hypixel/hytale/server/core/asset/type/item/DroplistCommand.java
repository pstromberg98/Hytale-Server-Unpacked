/*    */ package com.hypixel.hytale.server.core.asset.type.item;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*    */ import com.hypixel.hytale.server.core.modules.item.ItemModule;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class DroplistCommand extends CommandBase {
/*    */   @Nonnull
/* 19 */   private final RequiredArg<String> itemDroplistArg = withRequiredArg("droplist", "server.commands.droplist.set.droplist.desc", (ArgumentType)ArgTypes.STRING);
/*    */   
/* 21 */   private final OptionalArg<Integer> countArg = (OptionalArg<Integer>)withOptionalArg("count", "server.commands.droplist.set.count.desc", (ArgumentType)ArgTypes.INTEGER)
/* 22 */     .addValidator(Validators.greaterThan(Integer.valueOf(0)));
/*    */   
/*    */   public DroplistCommand() {
/* 25 */     super("droplist", "server.commands.droplist.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 30 */     String droplistId = (String)this.itemDroplistArg.get(context);
/* 31 */     ItemDropList itemDropList = (ItemDropList)ItemDropList.getAssetMap().getAsset(droplistId);
/* 32 */     if (itemDropList == null) {
/* 33 */       context.sendMessage(Message.translation("server.commands.droplist.notFound").param("droplistId", droplistId));
/*    */       
/*    */       return;
/*    */     } 
/* 37 */     int count = this.countArg.provided(context) ? ((Integer)this.countArg.get(context)).intValue() : 1;
/* 38 */     LinkedHashMap<String, Integer> accumulatedDrops = new LinkedHashMap<>();
/*    */     
/* 40 */     for (int i = 0; i < count; i++) {
/* 41 */       List<ItemStack> randomItemsToDrop = ItemModule.get().getRandomItemDrops(droplistId);
/* 42 */       for (ItemStack itemStack : randomItemsToDrop) {
/* 43 */         accumulatedDrops.merge(itemStack.getItemId(), Integer.valueOf(itemStack.getQuantity()), Integer::sum);
/*    */       }
/*    */     } 
/*    */     
/* 47 */     if (accumulatedDrops.isEmpty()) {
/* 48 */       context.sendMessage(Message.translation("server.commands.droplist.empty").param("droplistId", droplistId));
/*    */       
/*    */       return;
/*    */     } 
/* 52 */     context.sendMessage(Message.translation("server.commands.droplist.result").param("droplistId", droplistId));
/* 53 */     for (Map.Entry<String, Integer> entry : accumulatedDrops.entrySet()) {
/*    */ 
/*    */       
/* 56 */       Message message = Message.translation("server.commands.droplist.result.item").param("itemName", entry.getKey()).param("itemQuantity", ((Integer)entry.getValue()).intValue());
/* 57 */       context.sendMessage(message);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\asset\type\item\DroplistCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */