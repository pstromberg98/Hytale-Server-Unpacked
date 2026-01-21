/*     */ package com.hypixel.hytale.server.npc.asset.builder;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.ExtraInfo;
/*     */ import com.hypixel.hytale.codec.schema.NamedSchema;
/*     */ import com.hypixel.hytale.codec.schema.SchemaContext;
/*     */ import com.hypixel.hytale.codec.schema.SchemaConvertable;
/*     */ import com.hypixel.hytale.codec.schema.config.ArraySchema;
/*     */ import com.hypixel.hytale.codec.schema.config.ObjectSchema;
/*     */ import com.hypixel.hytale.codec.schema.config.Schema;
/*     */ import com.hypixel.hytale.codec.schema.config.StringSchema;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.logger.sentry.SkipSentryException;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
/*     */ import com.hypixel.hytale.server.core.util.BsonUtil;
/*     */ import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpression;
/*     */ import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
/*     */ import com.hypixel.hytale.server.npc.asset.builder.validators.StateStringValidator;
/*     */ import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
/*     */ import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
/*     */ import com.hypixel.hytale.server.npc.config.balancing.BalanceAsset;
/*     */ import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
/*     */ import com.hypixel.hytale.server.npc.util.expression.Scope;
/*     */ import com.hypixel.hytale.server.npc.util.expression.StdScope;
/*     */ import com.hypixel.hytale.server.npc.util.expression.ValueType;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Consumer;
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
/*     */ public class BuilderModifier
/*     */ {
/*     */   public static final String KEY_MODIFY = "Modify";
/*     */   public static final String KEY_EXPORT_STATES = "_ExportStates";
/*     */   public static final String KEY_INTERFACE_PARAMETERS = "_InterfaceParameters";
/*     */   public static final String KEY_COMBAT_CONFIG = "_CombatConfig";
/*     */   public static final String KEY_INTERACTION_VARS = "_InteractionVars";
/*     */   private final Object2ObjectMap<String, ExpressionHolder> builderExpressionMap;
/*     */   private final StatePair[] exportedStateIndexes;
/*     */   private final StateMappingHelper stateHelper;
/*     */   private final String combatConfig;
/*     */   private final Map<String, String> interactionVars;
/*     */   
/*     */   protected BuilderModifier(Object2ObjectMap<String, ExpressionHolder> builderExpressionMap, StatePair[] exportedStateIndexes, StateMappingHelper stateHelper, String combatConfig, Map<String, String> interactionVars) {
/*  90 */     this.builderExpressionMap = builderExpressionMap;
/*  91 */     this.exportedStateIndexes = exportedStateIndexes;
/*  92 */     this.stateHelper = stateHelper;
/*  93 */     this.combatConfig = combatConfig;
/*  94 */     this.interactionVars = interactionVars;
/*     */   }
/*     */   
/*     */   public String getCombatConfig() {
/*  98 */     return this.combatConfig;
/*     */   }
/*     */   
/*     */   public Map<String, String> getInteractionVars() {
/* 102 */     return this.interactionVars;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 106 */     return this.builderExpressionMap.isEmpty();
/*     */   }
/*     */   
/*     */   public int exportedStateCount() {
/* 110 */     return this.exportedStateIndexes.length;
/*     */   }
/*     */   
/*     */   public void applyComponentStateMap(@Nonnull BuilderSupport support) {
/* 114 */     support.setModifiedStateMap(this.stateHelper, this.exportedStateIndexes);
/*     */   }
/*     */   
/*     */   public void popComponentStateMap(@Nonnull BuilderSupport support) {
/* 118 */     support.popModifiedStateMap();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Scope createScope(@Nonnull BuilderSupport builderSupport, @Nonnull BuilderParameters builderParameters, Scope globalScope) {
/* 123 */     ExecutionContext executionContext = builderSupport.getExecutionContext();
/* 124 */     return createScope(executionContext, builderParameters, globalScope);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Scope createScope(ExecutionContext executionContext, @Nonnull BuilderParameters builderParameters, @Nullable Scope globalScope) {
/* 129 */     StdScope scope = builderParameters.createScope();
/* 130 */     if (globalScope != null) {
/* 131 */       StdScope mergedScope = new StdScope(globalScope);
/* 132 */       mergedScope.merge(scope);
/* 133 */       scope = mergedScope;
/*     */     } 
/*     */     
/* 136 */     StdScope finalScope = scope;
/* 137 */     ObjectIterator<Object2ObjectMap.Entry<String, ExpressionHolder>> iterator = Object2ObjectMaps.fastIterator(this.builderExpressionMap);
/* 138 */     while (iterator.hasNext()) {
/* 139 */       Object2ObjectMap.Entry<String, ExpressionHolder> pair = (Object2ObjectMap.Entry<String, ExpressionHolder>)iterator.next();
/* 140 */       String name = (String)pair.getKey();
/* 141 */       ExpressionHolder holder = (ExpressionHolder)pair.getValue();
/* 142 */       ValueType valueType = builderParameters.getParameterType(name);
/*     */ 
/*     */       
/* 145 */       BuilderExpression expression = holder.getExpression(builderParameters.getInterfaceCode());
/* 146 */       if (expression == null) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/* 151 */       if (valueType == ValueType.VOID) {
/* 152 */         throw new SkipSentryException(new IllegalStateException("Parameter " + name + " does not exist or is private"));
/*     */       }
/*     */       
/* 155 */       if (!ValueType.isAssignableType(expression.getType(), valueType)) {
/* 156 */         throw new SkipSentryException(new IllegalStateException("Parameter " + name + " has type " + String.valueOf(expression.getType()) + " but should be " + String.valueOf(valueType)));
/*     */       }
/*     */       
/* 159 */       expression.updateScope(finalScope, name, executionContext);
/*     */     } 
/* 161 */     return (Scope)scope;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static BuilderModifier fromJSON(@Nonnull JsonObject jsonObject, @Nonnull BuilderParameters builderParameters, @Nonnull StateMappingHelper helper, @Nonnull ExtraInfo extraInfo) {
/* 166 */     JsonObject modify = null;
/* 167 */     JsonElement modifyObject = jsonObject.get("Modify");
/* 168 */     if (modifyObject != null) {
/* 169 */       modify = BuilderBase.expectObject(modifyObject, "Modify");
/*     */     }
/*     */     
/* 172 */     if (modify == null || modify.entrySet().isEmpty()) {
/* 173 */       return EmptyBuilderModifier.INSTANCE;
/*     */     }
/*     */     
/* 176 */     Object2ObjectOpenHashMap object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
/* 177 */     ObjectArrayList objectArrayList = new ObjectArrayList();
/* 178 */     for (Map.Entry<String, JsonElement> stringElementPair : (Iterable<Map.Entry<String, JsonElement>>)modify.entrySet()) {
/* 179 */       String key = stringElementPair.getKey();
/* 180 */       if (object2ObjectOpenHashMap.containsKey(key)) {
/* 181 */         throw new SkipSentryException(new IllegalStateException("Duplicate entry '" + key + "' in 'Modify' block"));
/*     */       }
/* 183 */       if (key.equals("_InterfaceParameters") || key.equals("_CombatConfig") || key.equals("_InteractionVars")) {
/*     */         continue;
/*     */       }
/* 186 */       if (key.equals("_ExportStates")) {
/* 187 */         if (!((JsonElement)stringElementPair.getValue()).isJsonArray()) {
/* 188 */           throw new SkipSentryException(new IllegalStateException(String.format("%s in modifier block must be a Json Array", new Object[] { "_ExportStates" })));
/*     */         }
/* 190 */         StateStringValidator validator = StateStringValidator.requireMainState();
/* 191 */         JsonArray array = ((JsonElement)stringElementPair.getValue()).getAsJsonArray();
/* 192 */         for (int i = 0; i < array.size(); i++) {
/* 193 */           String state = array.get(i).getAsString();
/* 194 */           if (!validator.test(state)) {
/* 195 */             throw new SkipSentryException(new IllegalStateException(validator.errorMessage(state)));
/*     */           }
/* 197 */           String substate = validator.hasSubState() ? validator.getSubState() : helper.getDefaultSubState();
/* 198 */           helper.getAndPutSetterIndex(validator.getMainState(), substate, (m, s) -> exportedStateIndexes.add(new StatePair(validator.getMainState(), m.intValue(), s.intValue())));
/*     */         } 
/*     */         continue;
/*     */       } 
/* 202 */       BuilderExpression expression = BuilderExpression.fromJSON(stringElementPair.getValue(), builderParameters, false);
/* 203 */       object2ObjectOpenHashMap.put(key, new ExpressionHolder(expression));
/*     */     } 
/*     */     
/* 206 */     JsonElement interfaceValue = modify.get("_InterfaceParameters");
/* 207 */     if (interfaceValue != null) {
/* 208 */       JsonObject interfaceParameters = BuilderBase.expectObject(interfaceValue, "_InterfaceParameters");
/* 209 */       for (Map.Entry<String, JsonElement> interfaceEntry : (Iterable<Map.Entry<String, JsonElement>>)interfaceParameters.entrySet()) {
/* 210 */         String interfaceKey = interfaceEntry.getKey();
/* 211 */         JsonObject parameters = BuilderBase.expectObject(interfaceEntry.getValue());
/* 212 */         for (Map.Entry<String, JsonElement> parameterEntry : (Iterable<Map.Entry<String, JsonElement>>)parameters.entrySet()) {
/* 213 */           ExpressionHolder holder = (ExpressionHolder)object2ObjectOpenHashMap.computeIfAbsent(parameterEntry.getKey(), key -> new ExpressionHolder());
/* 214 */           if (holder.hasInterfaceMappedExpression(interfaceKey)) {
/* 215 */             throw new SkipSentryException(new IllegalStateException("Duplicate entry '" + (String)parameterEntry.getKey() + "' in 'Modify' block for interface '" + interfaceKey));
/*     */           }
/* 217 */           holder.addInterfaceMappedExpression(interfaceKey, BuilderExpression.fromJSON(parameterEntry.getValue(), builderParameters, false));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 222 */     String combatConfig = null;
/* 223 */     JsonElement combatConfigValue = modify.get("_CombatConfig");
/* 224 */     if (combatConfigValue != null) {
/* 225 */       combatConfig = (String)BalanceAsset.CHILD_ASSET_CODEC.decode(BsonUtil.translateJsonToBson(combatConfigValue), extraInfo);
/*     */       
/* 227 */       extraInfo.getValidationResults()._processValidationResults();
/* 228 */       extraInfo.getValidationResults().logOrThrowValidatorExceptions(HytaleLogger.getLogger());
/*     */     } 
/*     */     
/* 231 */     Map<String, String> interactionVars = null;
/* 232 */     JsonElement interactionVarsValue = modify.get("_InteractionVars");
/* 233 */     if (interactionVarsValue != null) {
/* 234 */       interactionVars = RootInteraction.CHILD_ASSET_CODEC_MAP.decode(BsonUtil.translateJsonToBson(interactionVarsValue), extraInfo);
/*     */       
/* 236 */       extraInfo.getValidationResults()._processValidationResults();
/* 237 */       extraInfo.getValidationResults().logOrThrowValidatorExceptions(HytaleLogger.getLogger());
/*     */     } 
/*     */     
/* 240 */     return new BuilderModifier((Object2ObjectMap<String, ExpressionHolder>)object2ObjectOpenHashMap, (StatePair[])objectArrayList.toArray(x$0 -> new StatePair[x$0]), helper, combatConfig, interactionVars);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void readModifierObject(@Nonnull JsonObject jsonObject, @Nonnull BuilderParameters builderParameters, @Nonnull StringHolder holder, @Nonnull Consumer<StringHolder> referenceConsumer, @Nonnull Consumer<BuilderModifier> builderModifierConsumer, @Nonnull StateMappingHelper helper, @Nonnull ExtraInfo extraInfo) {
/* 246 */     holder.readJSON(BuilderBase.expectKey(jsonObject, "Reference"), (StringValidator)StringNotEmptyValidator.get(), "Reference", builderParameters);
/* 247 */     BuilderModifier modifier = fromJSON(jsonObject, builderParameters, helper, extraInfo);
/* 248 */     referenceConsumer.accept(holder);
/* 249 */     builderModifierConsumer.accept(modifier);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static Schema toSchema(@Nonnull SchemaContext context) {
/* 254 */     return context.refDefinition(SchemaGenerator.INSTANCE);
/*     */   }
/*     */   
/*     */   private static class SchemaGenerator implements SchemaConvertable<Void>, NamedSchema {
/*     */     @Nonnull
/* 259 */     public static SchemaGenerator INSTANCE = new SchemaGenerator();
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String getSchemaName() {
/* 264 */       return "NPC:Type:BuilderModifier";
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Schema toSchema(@Nonnull SchemaContext context) {
/* 270 */       ObjectSchema s = new ObjectSchema();
/* 271 */       s.setTitle("BuilderModifier");
/* 272 */       LinkedHashMap<String, Schema> props = new LinkedHashMap<>();
/* 273 */       s.setProperties(props);
/*     */       
/* 275 */       props.put("_ExportStates", new ArraySchema((Schema)new StringSchema()));
/* 276 */       props.put("_InterfaceParameters", new ObjectSchema());
/*     */       
/* 278 */       Schema combatConfigKeySchema = context.refDefinition((SchemaConvertable)Codec.STRING);
/* 279 */       combatConfigKeySchema.setTitle("Reference to " + BalanceAsset.class.getSimpleName());
/* 280 */       Schema combatConfigNestedSchema = context.refDefinition((SchemaConvertable)BalanceAsset.CHILD_ASSET_CODEC);
/* 281 */       Schema combatConfigSchema = Schema.anyOf(new Schema[] { combatConfigKeySchema, combatConfigNestedSchema });
/* 282 */       props.put("_CombatConfig", combatConfigSchema);
/*     */       
/* 284 */       ObjectSchema interactionVars = new ObjectSchema();
/* 285 */       interactionVars.setTitle("Map");
/* 286 */       Schema childSchema = context.refDefinition((SchemaConvertable)RootInteraction.CHILD_ASSET_CODEC);
/* 287 */       interactionVars.setAdditionalProperties(childSchema);
/* 288 */       props.put("_InteractionVars", interactionVars);
/*     */ 
/*     */       
/* 291 */       s.setAdditionalProperties(BuilderExpression.toSchema(context));
/* 292 */       return (Schema)s;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ExpressionHolder {
/*     */     private final BuilderExpression expression;
/*     */     private Object2ObjectMap<String, BuilderExpression> interfaceMappedExpressions;
/*     */     
/*     */     public ExpressionHolder() {
/* 301 */       this(null);
/*     */     }
/*     */     
/*     */     public ExpressionHolder(BuilderExpression expression) {
/* 305 */       this.expression = expression;
/*     */     }
/*     */     
/*     */     public boolean hasInterfaceMappedExpression(String interfaceKey) {
/* 309 */       return (this.interfaceMappedExpressions != null && this.interfaceMappedExpressions.containsKey(interfaceKey));
/*     */     }
/*     */     
/*     */     public void addInterfaceMappedExpression(String interfaceKey, BuilderExpression expression) {
/* 313 */       if (this.interfaceMappedExpressions == null) this.interfaceMappedExpressions = (Object2ObjectMap<String, BuilderExpression>)new Object2ObjectOpenHashMap(); 
/* 314 */       this.interfaceMappedExpressions.put(interfaceKey, expression);
/*     */     }
/*     */     
/*     */     public BuilderExpression getExpression(@Nullable String interfaceKey) {
/* 318 */       if (interfaceKey == null || this.interfaceMappedExpressions == null || !this.interfaceMappedExpressions.containsKey(interfaceKey)) return this.expression; 
/* 319 */       return (BuilderExpression)this.interfaceMappedExpressions.get(interfaceKey);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\asset\builder\BuilderModifier.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */