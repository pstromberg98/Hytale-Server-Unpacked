/*    */ package com.hypixel.hytale.server.core.command.commands.server.auth;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.server.core.HytaleServer;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.auth.AuthCredentialStoreProvider;
/*    */ import com.hypixel.hytale.server.core.auth.ServerAuthManager;
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import java.awt.Color;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AuthPersistenceCommand
/*    */   extends CommandBase
/*    */ {
/*    */   @Nonnull
/* 27 */   private static final Message MESSAGE_SINGLEPLAYER = Message.translation("server.commands.auth.persistence.singleplayer").color(Color.RED);
/*    */   
/*    */   public AuthPersistenceCommand() {
/* 30 */     super("persistence", "server.commands.auth.persistence.desc");
/* 31 */     addUsageVariant((AbstractCommand)new SetPersistenceVariant());
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 36 */     if (ServerAuthManager.getInstance().isSingleplayer()) {
/* 37 */       context.sendMessage(MESSAGE_SINGLEPLAYER);
/*    */       
/*    */       return;
/*    */     } 
/* 41 */     AuthCredentialStoreProvider provider = HytaleServer.get().getConfig().getAuthCredentialStoreProvider();
/* 42 */     String typeName = (String)AuthCredentialStoreProvider.CODEC.getIdFor(provider.getClass());
/* 43 */     context.sendMessage(Message.translation("server.commands.auth.persistence.current")
/* 44 */         .color(Color.YELLOW)
/* 45 */         .param("type", typeName));
/*    */ 
/*    */     
/* 48 */     String availableTypes = String.join(", ", AuthCredentialStoreProvider.CODEC.getRegisteredIds());
/* 49 */     context.sendMessage(Message.translation("server.commands.auth.persistence.available")
/* 50 */         .color(Color.GRAY)
/* 51 */         .param("types", availableTypes));
/*    */   }
/*    */ 
/*    */   
/*    */   private static class SetPersistenceVariant
/*    */     extends CommandBase
/*    */   {
/*    */     @Nonnull
/* 59 */     private final RequiredArg<String> typeArg = withRequiredArg("type", "server.commands.auth.persistence.type.desc", (ArgumentType)ArgTypes.STRING);
/*    */     
/*    */     SetPersistenceVariant() {
/* 62 */       super("server.commands.auth.persistence.variant.desc");
/*    */     }
/*    */ 
/*    */     
/*    */     protected void executeSync(@Nonnull CommandContext context) {
/* 67 */       ServerAuthManager authManager = ServerAuthManager.getInstance();
/*    */       
/* 69 */       if (authManager.isSingleplayer()) {
/* 70 */         context.sendMessage(AuthPersistenceCommand.MESSAGE_SINGLEPLAYER);
/*    */         
/*    */         return;
/*    */       } 
/* 74 */       String typeName = (String)this.typeArg.get(context);
/*    */ 
/*    */       
/* 77 */       BuilderCodec<? extends AuthCredentialStoreProvider> codec = (BuilderCodec<? extends AuthCredentialStoreProvider>)AuthCredentialStoreProvider.CODEC.getCodecFor(typeName);
/* 78 */       if (codec == null) {
/* 79 */         context.sendMessage(Message.translation("server.commands.auth.persistence.unknownType")
/* 80 */             .color(Color.RED)
/* 81 */             .param("type", typeName));
/*    */         
/*    */         return;
/*    */       } 
/* 85 */       AuthCredentialStoreProvider newProvider = (AuthCredentialStoreProvider)codec.getDefaultValue();
/*    */ 
/*    */       
/* 88 */       HytaleServer.get().getConfig().setAuthCredentialStoreProvider(newProvider);
/* 89 */       authManager.swapCredentialStoreProvider(newProvider);
/*    */       
/* 91 */       context.sendMessage(Message.translation("server.commands.auth.persistence.changed")
/* 92 */           .color(Color.GREEN)
/* 93 */           .param("type", typeName));
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\server\auth\AuthPersistenceCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */