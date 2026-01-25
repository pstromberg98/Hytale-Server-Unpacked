/*     */ package com.hypixel.hytale.server.core.modules.interaction;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Interactions
/*     */   implements Component<EntityStore>
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<Interactions> CODEC;
/*     */   
/*     */   @Nonnull
/*     */   public static ComponentType<EntityStore, Interactions> getComponentType() {
/*  28 */     return InteractionModule.get().getInteractionsComponentType();
/*     */   }
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
/*     */   static {
/*  50 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Interactions.class, Interactions::new).appendInherited(new KeyedCodec("Interactions", (Codec)new EnumMapCodec(InteractionType.class, (Codec)Codec.STRING, false)), (o, v) -> o.interactions = v, o -> o.interactions, (o, p) -> o.interactions = p.interactions).add()).appendInherited(new KeyedCodec("InteractionHint", (Codec)Codec.STRING), (o, v) -> o.interactionHint = v, o -> o.interactionHint, (o, p) -> o.interactionHint = p.interactionHint).add()).build();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*  55 */   private Map<InteractionType, String> interactions = new EnumMap<>(InteractionType.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String interactionHint;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isNetworkOutdated = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Interactions(@Nonnull Map<InteractionType, String> interactions) {
/*  83 */     this.interactions = new EnumMap<>(interactions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getInteractionId(@Nonnull InteractionType type) {
/*  94 */     return this.interactions.get(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInteractionId(@Nonnull InteractionType type, @Nonnull String interactionId) {
/* 104 */     this.interactions.put(type, interactionId);
/* 105 */     this.isNetworkOutdated = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Map<InteractionType, String> getInteractions() {
/* 113 */     return Collections.unmodifiableMap(this.interactions);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String getInteractionHint() {
/* 118 */     return this.interactionHint;
/*     */   }
/*     */   
/*     */   public void setInteractionHint(@Nullable String interactionHint) {
/* 122 */     this.interactionHint = interactionHint;
/* 123 */     this.isNetworkOutdated = true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Component<EntityStore> clone() {
/* 130 */     Interactions clone = new Interactions(this.interactions);
/* 131 */     clone.interactionHint = this.interactionHint;
/* 132 */     return clone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean consumeNetworkOutdated() {
/* 141 */     boolean tmp = this.isNetworkOutdated;
/* 142 */     this.isNetworkOutdated = false;
/* 143 */     return tmp;
/*     */   }
/*     */   
/*     */   public Interactions() {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\Interactions.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */