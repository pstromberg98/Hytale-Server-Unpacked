/*     */ package com.hypixel.hytale.builtin.adventure.camera.command;
/*     */ 
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.camera.CameraEffect;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.Argument;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.AssetArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
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
/*     */ public class CameraEffectCommand
/*     */   extends AbstractCommandCollection
/*     */ {
/*     */   @Nonnull
/*  34 */   protected static final ArgumentType<CameraEffect> CAMERA_EFFECT_ARGUMENT_TYPE = (ArgumentType<CameraEffect>)new AssetArgumentType("CameraEffect", CameraEffect.class, "");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CameraEffectCommand() {
/*  40 */     super("camshake", "server.commands.camshake.desc");
/*  41 */     addSubCommand((AbstractCommand)new DamageCommand());
/*  42 */     addSubCommand((AbstractCommand)new DebugCommand());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static class DamageCommand
/*     */     extends AbstractTargetPlayerCommand
/*     */   {
/*     */     @Nonnull
/*  54 */     protected static final ArgumentType<DamageCause> DAMAGE_CAUSE_ARGUMENT_TYPE = (ArgumentType<DamageCause>)new AssetArgumentType("DamageCause", DamageCause.class, "");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*  60 */     protected final OptionalArg<CameraEffect> effectArg = withOptionalArg("effect", "server.commands.camshake.effect.desc", CameraEffectCommand.CAMERA_EFFECT_ARGUMENT_TYPE);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*  66 */     protected final RequiredArg<DamageCause> causeArg = withRequiredArg("cause", "server.commands.camshake.damage.cause.desc", DAMAGE_CAUSE_ARGUMENT_TYPE);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*  72 */     protected final RequiredArg<Float> damageArg = withRequiredArg("amount", "server.commands.camshake.damage.amount.desc", (ArgumentType)ArgTypes.FLOAT);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public DamageCommand() {
/*  78 */       super("damage", "server.commands.camshake.damage.desc");
/*     */     }
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nullable Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/*  83 */       DamageCause damageCause = (DamageCause)context.get((Argument)this.causeArg);
/*  84 */       float damageAmount = ((Float)context.get((Argument)this.damageArg)).floatValue();
/*     */       
/*  86 */       Damage.CommandSource damageSource = new Damage.CommandSource(context.sender(), getName());
/*  87 */       Damage damageEvent = new Damage((Damage.Source)damageSource, damageCause, damageAmount);
/*     */ 
/*     */       
/*  90 */       String cameraEffectId = "Default";
/*  91 */       if (this.effectArg.provided(context)) {
/*  92 */         cameraEffectId = ((CameraEffect)context.get((Argument)this.effectArg)).getId();
/*     */         
/*  94 */         Damage.CameraEffect damageEffect = new Damage.CameraEffect(CameraEffect.getAssetMap().getIndex(cameraEffectId));
/*  95 */         damageEvent.getMetaStore().putMetaObject(Damage.CAMERA_EFFECT, damageEffect);
/*     */       } 
/*     */       
/*  98 */       DamageSystems.executeDamage(ref, (ComponentAccessor)store, damageEvent);
/*     */       
/* 100 */       context.sendMessage(Message.translation("server.commands.camshake.damage.success")
/* 101 */           .param("effect", cameraEffectId)
/* 102 */           .param("cause", damageCause.getId())
/* 103 */           .param("amount", damageAmount));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static class DebugCommand
/*     */     extends AbstractTargetPlayerCommand
/*     */   {
/*     */     private static final String MESSAGE_SUCCESS = "server.commands.camshake.debug.success";
/*     */ 
/*     */     
/*     */     @Nonnull
/* 117 */     protected final RequiredArg<CameraEffect> effectArg = withRequiredArg("effect", "server.commands.camshake.effect.desc", CameraEffectCommand.CAMERA_EFFECT_ARGUMENT_TYPE);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 123 */     protected final RequiredArg<Float> intensityArg = withRequiredArg("intensity", "server.commands.camshake.debug.intensity.desc", (ArgumentType)ArgTypes.FLOAT);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public DebugCommand() {
/* 129 */       super("debug", "server.commands.camshake.debug.desc");
/*     */     }
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nullable Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 134 */       CameraEffect cameraEffect = (CameraEffect)context.get((Argument)this.effectArg);
/* 135 */       float intensity = ((Float)context.get((Argument)this.intensityArg)).floatValue();
/*     */       
/* 137 */       PlayerRef playerRefComponent = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/* 138 */       assert playerRefComponent != null;
/*     */       
/* 140 */       playerRefComponent.getPacketHandler().writeNoCache((Packet)cameraEffect.createCameraShakePacket(intensity));
/*     */       
/* 142 */       context.sendMessage(Message.translation("server.commands.camshake.debug.success")
/* 143 */           .param("effect", cameraEffect.getId())
/* 144 */           .param("intensity", intensity));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\camera\command\CameraEffectCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */