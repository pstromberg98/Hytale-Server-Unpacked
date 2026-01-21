/*     */ package com.hypixel.hytale.builtin.adventure.memories.interactions;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.map.Int2ObjectMapCodec;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.StringTag;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
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
/*     */ public class MemoriesConditionInteraction
/*     */   extends Interaction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<MemoriesConditionInteraction> CODEC;
/*     */   
/*     */   static {
/*  65 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(MemoriesConditionInteraction.class, MemoriesConditionInteraction::new, ABSTRACT_CODEC).appendInherited(new KeyedCodec("Next", (Codec)new Int2ObjectMapCodec(Interaction.CHILD_ASSET_CODEC, it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap::new)), (o, v) -> o.next = v, o -> o.next, (o, p) -> o.next = p.next).documentation("The interaction to run if the player's memories level matches the key.").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("Failed", Interaction.CHILD_ASSET_CODEC), (o, v) -> o.failed = v, o -> o.failed, (o, p) -> o.failed = p.failed).documentation("The interaction to run if the player's memories level does not match any key.").add()).afterDecode(o -> { o.levelToLabel.defaultReturnValue(-1); o.sortedKeys = o.next.keySet().toIntArray(); Arrays.sort(o.sortedKeys); o.levelToLabel.clear(); for (int i = 0; i < o.sortedKeys.length; i++) o.levelToLabel.put(o.sortedKeys[i], i);  })).build();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  71 */   private static final StringTag TAG_FAILED = StringTag.of("Failed");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  77 */   private Int2ObjectMap<String> next = Int2ObjectMaps.emptyMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient int[] sortedKeys;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  87 */   private final Int2IntOpenHashMap levelToLabel = new Int2IntOpenHashMap();
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String failed;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*  98 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/*  99 */     assert commandBuffer != null;
/*     */     
/* 101 */     World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/* 102 */     int memoriesLevel = MemoriesPlugin.get().getMemoriesLevel(world.getGameplayConfig());
/*     */ 
/*     */     
/* 105 */     (context.getState()).chainingIndex = memoriesLevel;
/*     */     
/* 107 */     int labelIndex = this.levelToLabel.get(memoriesLevel);
/* 108 */     if (labelIndex == -1) {
/* 109 */       labelIndex = this.sortedKeys.length;
/* 110 */       (context.getState()).state = InteractionState.Failed;
/*     */     } else {
/* 112 */       (context.getState()).state = InteractionState.Finished;
/*     */     } 
/*     */     
/* 115 */     context.jump(context.getLabel(labelIndex));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 120 */     int memoriesLevel = (context.getServerState()).chainingIndex;
/*     */     
/* 122 */     int labelIndex = this.levelToLabel.get(memoriesLevel);
/* 123 */     if (labelIndex == -1) {
/* 124 */       labelIndex = this.sortedKeys.length;
/* 125 */       (context.getState()).state = InteractionState.Failed;
/*     */     } else {
/* 127 */       (context.getState()).state = InteractionState.Finished;
/*     */     } 
/*     */     
/* 130 */     context.jump(context.getLabel(labelIndex));
/*     */   }
/*     */ 
/*     */   
/*     */   public void compile(@Nonnull OperationsBuilder builder) {
/* 135 */     Label end = builder.createUnresolvedLabel();
/* 136 */     Label[] labels = new Label[this.next.size() + 1];
/*     */     int i;
/* 138 */     for (i = 0; i < labels.length; i++) {
/* 139 */       labels[i] = builder.createUnresolvedLabel();
/*     */     }
/*     */     
/* 142 */     builder.addOperation((Operation)this, labels);
/* 143 */     builder.jump(end);
/*     */     
/* 145 */     for (i = 0; i < this.sortedKeys.length; i++) {
/* 146 */       int key = this.sortedKeys[i];
/* 147 */       builder.resolveLabel(labels[i]);
/* 148 */       Interaction interaction = Interaction.getInteractionOrUnknown((String)this.next.get(key));
/* 149 */       interaction.compile(builder);
/* 150 */       builder.jump(end);
/*     */     } 
/*     */ 
/*     */     
/* 154 */     int failedIndex = this.sortedKeys.length;
/* 155 */     builder.resolveLabel(labels[failedIndex]);
/*     */     
/* 157 */     if (this.failed != null) {
/* 158 */       Interaction interaction = Interaction.getInteractionOrUnknown(this.failed);
/* 159 */       interaction.compile(builder);
/*     */     } 
/*     */     
/* 162 */     builder.resolveLabel(end);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 168 */     return (Interaction)new com.hypixel.hytale.protocol.MemoriesConditionInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 173 */     super.configurePacket(packet);
/* 174 */     com.hypixel.hytale.protocol.MemoriesConditionInteraction p = (com.hypixel.hytale.protocol.MemoriesConditionInteraction)packet;
/* 175 */     p.memoriesNext = (Map)new Int2IntOpenHashMap(this.next.size());
/*     */     
/* 177 */     for (ObjectIterator<Int2ObjectMap.Entry<String>> objectIterator = this.next.int2ObjectEntrySet().iterator(); objectIterator.hasNext(); ) { Int2ObjectMap.Entry<String> e = objectIterator.next();
/* 178 */       p.memoriesNext.put(Integer.valueOf(e.getIntKey()), Integer.valueOf(Interaction.getInteractionIdOrUnknown((String)e.getValue()))); }
/*     */ 
/*     */     
/* 181 */     p.failed = Interaction.getInteractionIdOrUnknown(this.failed);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
/* 186 */     if (this.next != null) {
/* 187 */       for (ObjectIterator<Int2ObjectMap.Entry<String>> objectIterator = this.next.int2ObjectEntrySet().iterator(); objectIterator.hasNext(); ) { Int2ObjectMap.Entry<String> entry = objectIterator.next();
/* 188 */         if (InteractionManager.walkInteraction(collector, context, new MemoriesTag(entry.getIntKey()), (String)entry.getValue())) {
/* 189 */           return true;
/*     */         } }
/*     */     
/*     */     }
/*     */     
/* 194 */     if (this.failed != null) {
/* 195 */       return InteractionManager.walkInteraction(collector, context, (CollectorTag)TAG_FAILED, this.failed);
/*     */     }
/* 197 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 202 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/* 207 */     return WaitForDataFrom.Server;
/*     */   }
/*     */   private static final class MemoriesTag extends Record implements CollectorTag { private final int memoryLevel;
/* 210 */     private MemoriesTag(int memoryLevel) { this.memoryLevel = memoryLevel; } public int memoryLevel() { return this.memoryLevel; }
/*     */ 
/*     */     
/*     */     public final String toString() {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/builtin/adventure/memories/interactions/MemoriesConditionInteraction$MemoriesTag;)Ljava/lang/String;
/*     */       //   6: areturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #210	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/hypixel/hytale/builtin/adventure/memories/interactions/MemoriesConditionInteraction$MemoriesTag;
/*     */     }
/*     */     
/*     */     public final int hashCode() {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/builtin/adventure/memories/interactions/MemoriesConditionInteraction$MemoriesTag;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #210	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/hypixel/hytale/builtin/adventure/memories/interactions/MemoriesConditionInteraction$MemoriesTag;
/*     */     }
/*     */     
/*     */     public final boolean equals(Object o) {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/builtin/adventure/memories/interactions/MemoriesConditionInteraction$MemoriesTag;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #210	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lcom/hypixel/hytale/builtin/adventure/memories/interactions/MemoriesConditionInteraction$MemoriesTag;
/*     */       //   0	8	1	o	Ljava/lang/Object;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\memories\interactions\MemoriesConditionInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */