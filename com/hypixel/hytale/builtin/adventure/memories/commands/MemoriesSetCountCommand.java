/*    */ package com.hypixel.hytale.builtin.adventure.memories.commands;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MemoriesSetCountCommand
/*    */   extends AbstractWorldCommand
/*    */ {
/*    */   @Nonnull
/* 21 */   private static final Message MESSAGE_COMMANDS_MEMORIES_SETCOUNT_SUCCESS = Message.translation("server.commands.memories.setCount.success");
/*    */   @Nonnull
/* 23 */   private static final Message MESSAGE_COMMANDS_MEMORIES_SETCOUNT_INVALID = Message.translation("server.commands.memories.setCount.invalid");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 29 */   private final RequiredArg<Integer> countArg = withRequiredArg("count", "server.commands.memories.setCount.count.desc", (ArgumentType)ArgTypes.INTEGER);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MemoriesSetCountCommand() {
/* 35 */     super("setCount", "server.commands.memories.setCount.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 40 */     int count = ((Integer)this.countArg.get(context)).intValue();
/*    */     
/* 42 */     if (count < 0) {
/* 43 */       context.sendMessage(MESSAGE_COMMANDS_MEMORIES_SETCOUNT_INVALID);
/*    */       
/*    */       return;
/*    */     } 
/* 47 */     int actualCount = MemoriesPlugin.get().setRecordedMemoriesCount(count);
/* 48 */     context.sendMessage(MESSAGE_COMMANDS_MEMORIES_SETCOUNT_SUCCESS
/* 49 */         .param("requested", count)
/* 50 */         .param("actual", actualCount));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\memories\commands\MemoriesSetCountCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */