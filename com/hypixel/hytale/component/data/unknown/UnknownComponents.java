/*     */ package com.hypixel.hytale.component.data.unknown;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.ExtraInfo;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.map.MapCodec;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ import org.bson.BsonDocument;
/*     */ import org.bson.BsonValue;
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
/*     */ public class UnknownComponents<ECS_TYPE>
/*     */   implements Component<ECS_TYPE>
/*     */ {
/*     */   @Nonnull
/*  32 */   public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static final String ID = "Unknown";
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static final BuilderCodec<UnknownComponents> CODEC;
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private Map<String, BsonDocument> unknownComponents;
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  51 */     CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(UnknownComponents.class, UnknownComponents::new).addField(new KeyedCodec("Components", (Codec)new MapCodec((Codec)Codec.BSON_DOCUMENT, Object2ObjectOpenHashMap::new, false)), (o, map) -> o.unknownComponents = map, o -> o.unknownComponents)).build();
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
/*     */   public UnknownComponents() {
/*  63 */     this.unknownComponents = (Map<String, BsonDocument>)new Object2ObjectOpenHashMap();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UnknownComponents(@Nonnull Map<String, BsonDocument> unknownComponents) {
/*  72 */     this.unknownComponents = unknownComponents;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addComponent(@Nonnull String componentId, @Nonnull Component<ECS_TYPE> component, @Nonnull Codec<Component<ECS_TYPE>> codec) {
/*  83 */     ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
/*  84 */     BsonValue bsonValue = codec.encode(component, extraInfo);
/*  85 */     extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
/*     */     
/*  87 */     this.unknownComponents.put(componentId, bsonValue.asDocument());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addComponent(@Nonnull String componentId, @Nonnull TempUnknownComponent<ECS_TYPE> component) {
/*  97 */     this.unknownComponents.put(componentId, component.getDocument());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(@Nonnull String componentId) {
/* 107 */     return this.unknownComponents.containsKey(componentId);
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
/*     */   @Nullable
/*     */   public <T extends Component<ECS_TYPE>> T removeComponent(@Nonnull String componentId, @Nonnull Codec<T> codec) {
/* 120 */     BsonDocument bsonDocument = this.unknownComponents.remove(componentId);
/* 121 */     if (bsonDocument == null) return null;
/*     */     
/* 123 */     ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
/* 124 */     Component component = (Component)codec.decode((BsonValue)bsonDocument, extraInfo);
/* 125 */     extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
/* 126 */     return (T)component;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Map<String, BsonDocument> getUnknownComponents() {
/* 134 */     return this.unknownComponents;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Component<ECS_TYPE> clone() {
/* 140 */     return new UnknownComponents((Map<String, BsonDocument>)new Object2ObjectOpenHashMap(this.unknownComponents));
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\dat\\unknown\UnknownComponents.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */