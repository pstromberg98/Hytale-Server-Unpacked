/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.validation.LateValidator;
/*     */ import com.hypixel.hytale.codec.validation.Validator;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionChain;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*     */ import com.hypixel.hytale.server.core.meta.DynamicMetaStore;
/*     */ import com.hypixel.hytale.server.core.meta.MetaKey;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.StringTag;
/*     */ import java.util.function.Supplier;
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
/*     */ public class RepeatInteraction
/*     */   extends SimpleInteraction
/*     */ {
/*     */   public static final BuilderCodec<RepeatInteraction> CODEC;
/*     */   
/*     */   static {
/*  53 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RepeatInteraction.class, RepeatInteraction::new, SimpleInteraction.CODEC).documentation("Forks from the current interaction into one or more chains that run the specified interactions.\n\nWhen run this will create a new chain that will run the interactions specified in `ForkInteractions`. This will then wait until that chain completes. If the chain completes successfully it will then check the `Repeat` field to see if it needs to run again, if not then the interactions `Next` are run otherwise this repeats with the next fork. If the chain fails then any repeating is ignored and the interactions `Failed` are run instead.")).appendInherited(new KeyedCodec("ForkInteractions", (Codec)RootInteraction.CHILD_ASSET_CODEC), (i, s) -> i.forkInteractions = s, i -> i.forkInteractions, (i, parent) -> i.forkInteractions = parent.forkInteractions).documentation("The interactions to run in the forks created by this interaction.").addValidator(Validators.nonNull()).addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("Repeat", (Codec)Codec.INTEGER), (i, s) -> i.repeat = s.intValue(), i -> Integer.valueOf(i.repeat), (i, parent) -> i.repeat = parent.repeat).documentation("The number of times to repeat. -1 is considered as infinite, be careful when using this value.").addValidator(Validators.or(new Validator[] { Validators.greaterThanOrEqual(Integer.valueOf(1)), Validators.equal(Integer.valueOf(-1)) })).add()).build();
/*     */   }
/*  55 */   private static final MetaKey<InteractionChain> FORKED_CHAIN = Interaction.META_REGISTRY.registerMetaObject(i -> null);
/*  56 */   private static final MetaKey<Integer> REMAINING_REPEATS = Interaction.META_REGISTRY.registerMetaObject(i -> null);
/*     */   
/*  58 */   private static final StringTag TAG_FORK = StringTag.of("Fork");
/*  59 */   private static final StringTag TAG_NEXT = StringTag.of("Next");
/*  60 */   private static final StringTag TAG_FAILED = StringTag.of("Failed");
/*     */   
/*     */   protected String forkInteractions;
/*  63 */   protected int repeat = 1;
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/*  68 */     return WaitForDataFrom.None;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*  73 */     DynamicMetaStore<Interaction> instanceStore = context.getInstanceStore();
/*  74 */     if (firstRun && this.repeat != -1) {
/*  75 */       instanceStore.putMetaObject(REMAINING_REPEATS, Integer.valueOf(this.repeat));
/*     */     }
/*     */     
/*  78 */     InteractionChain chain = (InteractionChain)instanceStore.getMetaObject(FORKED_CHAIN);
/*  79 */     if (chain != null) {
/*  80 */       switch (chain.getServerState()) {
/*     */         case NotFinished:
/*  82 */           (context.getState()).state = InteractionState.NotFinished;
/*     */           return;
/*     */         
/*     */         case Finished:
/*  86 */           if (this.repeat != -1 && ((Integer)instanceStore.getMetaObject(REMAINING_REPEATS)).intValue() <= 0) {
/*  87 */             (context.getState()).state = InteractionState.Finished;
/*  88 */             super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */             return;
/*     */           } 
/*  91 */           (context.getState()).state = InteractionState.NotFinished;
/*     */           break;
/*     */         
/*     */         case Failed:
/*  95 */           (context.getState()).state = InteractionState.Failed;
/*  96 */           super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */           return;
/*     */       } 
/*     */ 
/*     */     
/*     */     }
/* 102 */     chain = context.fork(context.duplicate(), RootInteraction.getRootInteractionOrUnknown(this.forkInteractions), true);
/* 103 */     instanceStore.putMetaObject(FORKED_CHAIN, chain);
/* 104 */     (context.getState()).state = InteractionState.NotFinished;
/* 105 */     if (this.repeat != -1) instanceStore.putMetaObject(REMAINING_REPEATS, Integer.valueOf(((Integer)instanceStore.getMetaObject(REMAINING_REPEATS)).intValue() - 1));
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 112 */     InteractionChain chain = (InteractionChain)context.getInstanceStore().getMetaObject(FORKED_CHAIN);
/* 113 */     DynamicMetaStore<Interaction> instanceStore = context.getInstanceStore();
/* 114 */     if (chain != null) {
/* 115 */       switch (chain.getServerState()) {
/*     */         case NotFinished:
/* 117 */           (context.getState()).state = InteractionState.NotFinished;
/*     */           break;
/*     */         case Finished:
/* 120 */           if (this.repeat != -1 && ((Integer)instanceStore.getMetaObject(REMAINING_REPEATS)).intValue() <= 0) {
/* 121 */             (context.getState()).state = InteractionState.Finished;
/* 122 */             super.simulateTick0(firstRun, time, type, context, cooldownHandler); break;
/*     */           } 
/* 124 */           (context.getState()).state = InteractionState.NotFinished;
/*     */           break;
/*     */         
/*     */         case Failed:
/* 128 */           (context.getState()).state = InteractionState.Failed;
/* 129 */           super.simulateTick0(firstRun, time, type, context, cooldownHandler);
/*     */           break;
/*     */       } 
/*     */     } else {
/* 133 */       (context.getState()).state = InteractionState.NotFinished;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
/* 139 */     if (this.forkInteractions != null && 
/* 140 */       InteractionManager.walkInteractions(collector, context, (CollectorTag)TAG_FORK, RootInteraction.getRootInteractionOrUnknown(this.forkInteractions).getInteractionIds())) {
/* 141 */       return true;
/*     */     }
/*     */     
/* 144 */     if (this.next != null && 
/* 145 */       InteractionManager.walkInteraction(collector, context, (CollectorTag)TAG_NEXT, this.next)) return true;
/*     */     
/* 147 */     if (this.failed != null && 
/* 148 */       InteractionManager.walkInteraction(collector, context, (CollectorTag)TAG_FAILED, this.failed)) return true;
/*     */     
/* 150 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 156 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 162 */     return (Interaction)new com.hypixel.hytale.protocol.RepeatInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 167 */     super.configurePacket(packet);
/* 168 */     com.hypixel.hytale.protocol.RepeatInteraction p = (com.hypixel.hytale.protocol.RepeatInteraction)packet;
/* 169 */     p.forkInteractions = RootInteraction.getRootInteractionIdOrUnknown(this.forkInteractions);
/* 170 */     p.repeat = this.repeat;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 177 */     return "RepeatInteraction{forkInteractions='" + this.forkInteractions + "', repeat=" + this.repeat + "} " + super
/*     */ 
/*     */       
/* 180 */       .toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\none\RepeatInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */