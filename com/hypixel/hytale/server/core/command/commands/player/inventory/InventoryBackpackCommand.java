/*    */ package com.hypixel.hytale.server.core.command.commands.player.inventory;
/*    */ 
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*    */ import com.hypixel.hytale.server.core.entity.ItemUtils;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*    */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*    */ import it.unimi.dsi.fastutil.objects.ObjectListIterator;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ public class InventoryBackpackCommand
/*    */   extends AbstractPlayerCommand
/*    */ {
/*    */   @Nonnull
/* 29 */   private final OptionalArg<Integer> sizeArg = withOptionalArg("size", "server.commands.inventorybackpack.size.desc", (ArgumentType)ArgTypes.INTEGER);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InventoryBackpackCommand() {
/* 35 */     super("backpack", "server.commands.inventorybackpack.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 40 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 41 */     assert playerComponent != null;
/*    */     
/* 43 */     Inventory inventory = playerComponent.getInventory();
/*    */     
/* 45 */     if (!this.sizeArg.provided(context)) {
/*    */       
/* 47 */       context.sendMessage(Message.translation("server.commands.inventory.backpack.size")
/* 48 */           .param("capacity", inventory.getBackpack().getCapacity()));
/*    */     } else {
/*    */       
/* 51 */       short capacity = ((Integer)this.sizeArg.get(context)).shortValue();
/*    */       
/* 53 */       ObjectArrayList<ItemStack> remainder = new ObjectArrayList();
/* 54 */       inventory.resizeBackpack(capacity, (List)remainder);
/*    */       
/* 56 */       for (ObjectListIterator<ItemStack> objectListIterator = remainder.iterator(); objectListIterator.hasNext(); ) { ItemStack item = objectListIterator.next();
/* 57 */         ItemUtils.dropItem(ref, item, (ComponentAccessor)store); }
/*    */ 
/*    */       
/* 60 */       context.sendMessage(Message.translation("server.commands.inventory.backpack.resized")
/* 61 */           .param("capacity", inventory.getBackpack().getCapacity())
/* 62 */           .param("dropped", remainder.size()));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\player\inventory\InventoryBackpackCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */