/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.codec.codecs.map.MapCodec;
/*     */ import com.hypixel.hytale.codec.validation.LateValidator;
/*     */ import com.hypixel.hytale.codec.validation.Validator;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionSyncData;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.objects.Object2IntMap;
/*     */ import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
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
/*     */ public class ChainingInteraction
/*     */   extends Interaction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<ChainingInteraction> CODEC;
/*     */   protected String chainId;
/*     */   protected float chainingAllowance;
/*     */   protected String[] next;
/*     */   @Nullable
/*     */   protected Map<String, String> flags;
/*     */   @Nullable
/*     */   protected Object2IntMap<String> flagIndex;
/*     */   private String[] sortedFlagKeys;
/*     */   
/*     */   static {
/*  91 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ChainingInteraction.class, ChainingInteraction::new, Interaction.ABSTRACT_CODEC).documentation("Runs one of the entries in `Next` based on how many times this interaction was run before the `ChainingAllowance` timer was reset.")).appendInherited(new KeyedCodec("ChainingAllowance", (Codec)Codec.DOUBLE), (chainingInteraction, d) -> chainingInteraction.chainingAllowance = d.floatValue(), chainingInteraction -> Double.valueOf(chainingInteraction.chainingAllowance), (chainingInteraction, parent) -> chainingInteraction.chainingAllowance = parent.chainingAllowance).documentation("Time in seconds that the user has to run this interaction again in order to hit the next chain entry.\nResets the timer each time the interaction is reached.").add()).appendInherited(new KeyedCodec("Next", (Codec)new ArrayCodec(Interaction.CHILD_ASSET_CODEC, x$0 -> new String[x$0])), (interaction, s) -> interaction.next = s, interaction -> interaction.next, (interaction, parent) -> interaction.next = parent.next).addValidator(Validators.nonNull()).addValidator((Validator)Validators.nonNullArrayElements()).addValidatorLate(() -> Interaction.VALIDATOR_CACHE.getArrayValidator().late()).add()).appendInherited(new KeyedCodec("ChainId", (Codec)Codec.STRING), (o, i) -> o.chainId = i, o -> o.chainId, (o, p) -> o.chainId = p.chainId).add()).appendInherited(new KeyedCodec("Flags", (Codec)new MapCodec(CHILD_ASSET_CODEC, java.util.HashMap::new)), (o, i) -> o.flags = i, o -> o.flags, (o, p) -> o.flags = p.flags).addValidatorLate(() -> Interaction.VALIDATOR_CACHE.getMapValueValidator().late()).add()).afterDecode(o -> { if (o.flags != null) { String[] sortedFlagKeys = o.sortedFlagKeys = (String[])o.flags.keySet().toArray(()); Arrays.sort((Object[])sortedFlagKeys); o.flagIndex = (Object2IntMap<String>)new Object2IntOpenHashMap(); for (int i = 0; i < sortedFlagKeys.length; i++) o.flagIndex.put(sortedFlagKeys[i], i);  }  })).build();
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
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/* 132 */     return WaitForDataFrom.Client;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 137 */     InteractionSyncData clientState = context.getClientState();
/* 138 */     assert clientState != null;
/*     */     
/* 140 */     InteractionSyncData state = context.getState();
/*     */     
/* 142 */     if (clientState.flagIndex != -1) {
/* 143 */       state.state = InteractionState.Finished;
/*     */       
/* 145 */       context.jump(context.getLabel(this.next.length + clientState.flagIndex));
/*     */       
/*     */       return;
/*     */     } 
/* 149 */     if (clientState.chainingIndex == -1) {
/* 150 */       state.state = InteractionState.NotFinished;
/*     */       
/*     */       return;
/*     */     } 
/* 154 */     state.state = InteractionState.Finished;
/*     */     
/* 156 */     context.jump(context.getLabel(clientState.chainingIndex));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 162 */     if (!firstRun) {
/*     */       return;
/*     */     }
/*     */     
/* 166 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 167 */     assert commandBuffer != null;
/*     */     
/* 169 */     Ref<EntityStore> ref = context.getEntity();
/* 170 */     Data dataComponent = (Data)commandBuffer.getComponent(ref, Data.getComponentType());
/* 171 */     if (dataComponent == null)
/*     */       return; 
/* 173 */     InteractionSyncData state = context.getState();
/*     */     
/* 175 */     String id = (this.chainId == null) ? this.id : this.chainId;
/* 176 */     Object2IntMap<String> map = (this.chainId == null) ? dataComponent.map : dataComponent.namedMap;
/*     */     
/* 178 */     int lastSequenceIndex = map.getInt(id);
/*     */     
/* 180 */     lastSequenceIndex++;
/* 181 */     if (lastSequenceIndex >= this.next.length) lastSequenceIndex = 0;
/*     */ 
/*     */     
/* 184 */     if (this.chainingAllowance > 0.0F && dataComponent.getTimeSinceLastAttackInSeconds() > this.chainingAllowance) {
/* 185 */       lastSequenceIndex = 0;
/*     */     }
/*     */     
/* 188 */     map.put(id, lastSequenceIndex);
/* 189 */     state.chainingIndex = lastSequenceIndex;
/* 190 */     state.state = InteractionState.Finished;
/* 191 */     context.jump(context.getLabel(lastSequenceIndex));
/* 192 */     dataComponent.lastAttack = System.nanoTime();
/*     */   }
/*     */ 
/*     */   
/*     */   public void compile(@Nonnull OperationsBuilder builder) {
/* 197 */     int len = this.next.length + ((this.sortedFlagKeys != null) ? this.sortedFlagKeys.length : 0);
/* 198 */     Label[] labels = new Label[len];
/*     */     
/* 200 */     for (int i = 0; i < labels.length; i++) {
/* 201 */       labels[i] = builder.createUnresolvedLabel();
/*     */     }
/*     */     
/* 204 */     builder.addOperation((Operation)this, labels);
/*     */     
/* 206 */     Label end = builder.createUnresolvedLabel();
/*     */     int j;
/* 208 */     for (j = 0; j < this.next.length; j++) {
/* 209 */       builder.resolveLabel(labels[j]);
/* 210 */       Interaction interaction = Interaction.getInteractionOrUnknown(this.next[j]);
/* 211 */       interaction.compile(builder);
/* 212 */       builder.jump(end);
/*     */     } 
/*     */     
/* 215 */     if (this.flags != null) {
/* 216 */       for (j = 0; j < this.sortedFlagKeys.length; j++) {
/* 217 */         String flag = this.sortedFlagKeys[j];
/* 218 */         builder.resolveLabel(labels[this.next.length + j]);
/*     */         
/* 220 */         Interaction interaction = Interaction.getInteractionOrUnknown(this.flags.get(flag));
/* 221 */         interaction.compile(builder);
/* 222 */         builder.jump(end);
/*     */       } 
/*     */     }
/*     */     
/* 226 */     builder.resolveLabel(end);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
/* 231 */     for (int i = 0; i < this.next.length; i++) {
/* 232 */       if (InteractionManager.walkInteraction(collector, context, ChainingTag.of(i), this.next[i])) return true; 
/*     */     } 
/* 234 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 240 */     return (Interaction)new com.hypixel.hytale.protocol.ChainingInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 245 */     super.configurePacket(packet);
/* 246 */     com.hypixel.hytale.protocol.ChainingInteraction p = (com.hypixel.hytale.protocol.ChainingInteraction)packet;
/* 247 */     p.chainingAllowance = this.chainingAllowance;
/*     */     
/* 249 */     int[] chainingNext = p.chainingNext = new int[this.next.length];
/* 250 */     for (int i = 0; i < this.next.length; i++) {
/* 251 */       chainingNext[i] = Interaction.getInteractionIdOrUnknown(this.next[i]);
/*     */     }
/*     */     
/* 254 */     if (this.flags != null) {
/* 255 */       p.flags = (Map)new Object2IntOpenHashMap();
/* 256 */       for (Map.Entry<String, String> e : this.flags.entrySet()) {
/* 257 */         p.flags.put(e.getKey(), Integer.valueOf(Interaction.getInteractionIdOrUnknown(e.getValue())));
/*     */       }
/*     */     } 
/*     */     
/* 261 */     p.chainId = this.chainId;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 266 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 272 */     return "ChainingInteraction{chainingAllowance=" + this.chainingAllowance + ", next=" + 
/*     */       
/* 274 */       Arrays.toString((Object[])this.next) + "} " + super
/* 275 */       .toString();
/*     */   }
/*     */   
/*     */   public static class Data
/*     */     implements Component<EntityStore>
/*     */   {
/*     */     public static ComponentType<EntityStore, Data> getComponentType() {
/* 282 */       return InteractionModule.get().getChainingDataComponent();
/*     */     }
/*     */     
/* 285 */     private final Object2IntMap<String> map = (Object2IntMap<String>)new Object2IntOpenHashMap();
/* 286 */     private final Object2IntMap<String> namedMap = (Object2IntMap<String>)new Object2IntOpenHashMap();
/*     */     private long lastAttack;
/*     */     
/*     */     public float getTimeSinceLastAttackInSeconds() {
/* 290 */       if (this.lastAttack == 0L) return 0.0F; 
/* 291 */       long diff = System.nanoTime() - this.lastAttack;
/* 292 */       return (float)diff / 1.0E9F;
/*     */     }
/*     */     
/*     */     @Nonnull
/*     */     public Object2IntMap<String> getNamedMap() {
/* 297 */       return this.namedMap;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Component<EntityStore> clone() {
/* 303 */       Data c = new Data();
/* 304 */       c.map.putAll((Map)this.map);
/* 305 */       c.lastAttack = this.lastAttack;
/* 306 */       return c;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ChainingTag implements CollectorTag {
/*     */     private final int index;
/*     */     
/*     */     private ChainingTag(int index) {
/* 314 */       this.index = index;
/*     */     }
/*     */     
/*     */     public int getIndex() {
/* 318 */       return this.index;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object o) {
/* 323 */       if (this == o) return true; 
/* 324 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 326 */       ChainingTag that = (ChainingTag)o;
/* 327 */       return (this.index == that.index);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 332 */       return this.index;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String toString() {
/* 338 */       return "ChainingTag{index=" + this.index + "}";
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public static ChainingTag of(int index) {
/* 345 */       return new ChainingTag(index);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\client\ChainingInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */