/*     */ package com.hypixel.hytale.server.core.command.commands.player.effect;
/*     */ 
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
/*     */ import com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*     */ import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.permissions.HytalePermissions;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
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
/*     */ class PlayerEffectApplyOtherCommand
/*     */   extends CommandBase
/*     */ {
/*  73 */   private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");
/*     */   
/*     */   @Nonnull
/*  76 */   private final RequiredArg<PlayerRef> playerArg = withRequiredArg("player", "server.commands.argtype.player.desc", (ArgumentType)ArgTypes.PLAYER_REF);
/*     */   
/*     */   @Nonnull
/*  79 */   private final RequiredArg<EntityEffect> effectArg = withRequiredArg("effect", "server.commands.player.effect.apply.effect.desc", (ArgumentType)ArgTypes.EFFECT_ASSET);
/*     */   @Nonnull
/*  81 */   private final DefaultArg<Float> durationArg = (DefaultArg<Float>)
/*  82 */     withDefaultArg("duration", "server.commands.player.effect.apply.duration.desc", (ArgumentType)ArgTypes.FLOAT, Float.valueOf(100.0F), "server.commands.entity.effect.duration")
/*  83 */     .addValidator(Validators.greaterThan(Float.valueOf(0.0F)));
/*     */   
/*     */   PlayerEffectApplyOtherCommand() {
/*  86 */     super("server.commands.player.effect.apply.other.desc");
/*  87 */     requirePermission(HytalePermissions.fromCommand("player.effect.apply.other"));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void executeSync(@Nonnull CommandContext context) {
/*  92 */     PlayerRef targetPlayerRef = (PlayerRef)this.playerArg.get(context);
/*  93 */     Ref<EntityStore> ref = targetPlayerRef.getReference();
/*     */     
/*  95 */     if (ref == null || !ref.isValid()) {
/*  96 */       context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
/*     */       
/*     */       return;
/*     */     } 
/* 100 */     Store<EntityStore> store = ref.getStore();
/* 101 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 103 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (playerComponent == null) {
/*     */             context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
/*     */             return;
/*     */           } 
/*     */           PlayerRef playerRefComponent = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/*     */           assert playerRefComponent != null;
/*     */           EffectControllerComponent effectControllerComponent = (EffectControllerComponent)store.getComponent(ref, EffectControllerComponent.getComponentType());
/*     */           assert effectControllerComponent != null;
/*     */           EntityEffect effect = (EntityEffect)this.effectArg.get(context);
/*     */           Float duration = (Float)this.durationArg.get(context);
/*     */           effectControllerComponent.addEffect(ref, effect, duration.floatValue(), OverlapBehavior.OVERWRITE, (ComponentAccessor)store);
/*     */           context.sendMessage(Message.translation("server.commands.player.effect.apply.success.other").param("username", playerRefComponent.getUsername()).param("effect", effect.getId()).param("duration", duration.floatValue()));
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\player\effect\PlayerEffectApplyCommand$PlayerEffectApplyOtherCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */