/*     */ package com.hypixel.hytale.builtin.adventure.camera.command;
/*     */ 
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.camera.CameraEffect;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.Argument;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.AssetArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
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
/*     */ public class DamageCommand
/*     */   extends AbstractTargetPlayerCommand
/*     */ {
/*     */   @Nonnull
/*  54 */   protected static final ArgumentType<DamageCause> DAMAGE_CAUSE_ARGUMENT_TYPE = (ArgumentType<DamageCause>)new AssetArgumentType("DamageCause", DamageCause.class, "");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  60 */   protected final OptionalArg<CameraEffect> effectArg = withOptionalArg("effect", "server.commands.camshake.effect.desc", CameraEffectCommand.CAMERA_EFFECT_ARGUMENT_TYPE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  66 */   protected final RequiredArg<DamageCause> causeArg = withRequiredArg("cause", "server.commands.camshake.damage.cause.desc", DAMAGE_CAUSE_ARGUMENT_TYPE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  72 */   protected final RequiredArg<Float> damageArg = withRequiredArg("amount", "server.commands.camshake.damage.amount.desc", (ArgumentType)ArgTypes.FLOAT);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DamageCommand() {
/*  78 */     super("damage", "server.commands.camshake.damage.desc");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nullable Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/*  83 */     DamageCause damageCause = (DamageCause)context.get((Argument)this.causeArg);
/*  84 */     float damageAmount = ((Float)context.get((Argument)this.damageArg)).floatValue();
/*     */     
/*  86 */     Damage.CommandSource damageSource = new Damage.CommandSource(context.sender(), getName());
/*  87 */     Damage damageEvent = new Damage((Damage.Source)damageSource, damageCause, damageAmount);
/*     */ 
/*     */     
/*  90 */     String cameraEffectId = "Default";
/*  91 */     if (this.effectArg.provided(context)) {
/*  92 */       cameraEffectId = ((CameraEffect)context.get((Argument)this.effectArg)).getId();
/*     */       
/*  94 */       Damage.CameraEffect damageEffect = new Damage.CameraEffect(CameraEffect.getAssetMap().getIndex(cameraEffectId));
/*  95 */       damageEvent.getMetaStore().putMetaObject(Damage.CAMERA_EFFECT, damageEffect);
/*     */     } 
/*     */     
/*  98 */     DamageSystems.executeDamage(ref, (ComponentAccessor)store, damageEvent);
/*     */     
/* 100 */     context.sendMessage(Message.translation("server.commands.camshake.damage.success")
/* 101 */         .param("effect", cameraEffectId)
/* 102 */         .param("cause", damageCause.getId())
/* 103 */         .param("amount", damageAmount));
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\camera\command\CameraEffectCommand$DamageCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */