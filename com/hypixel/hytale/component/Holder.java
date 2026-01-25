/*     */ package com.hypixel.hytale.component;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.ExtraInfo;
/*     */ import com.hypixel.hytale.component.data.change.ChangeType;
/*     */ import com.hypixel.hytale.component.data.change.ComponentChange;
/*     */ import com.hypixel.hytale.component.data.change.DataChange;
/*     */ import com.hypixel.hytale.component.data.unknown.TempUnknownComponent;
/*     */ import com.hypixel.hytale.component.data.unknown.UnknownComponents;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.locks.StampedLock;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ import org.bson.BsonDocument;
/*     */ import org.bson.BsonValue;
/*     */ 
/*     */ 
/*     */ public class Holder<ECS_TYPE>
/*     */ {
/*  24 */   private static final Holder<?>[] EMPTY_ARRAY = (Holder<?>[])new Holder[0];
/*     */ 
/*     */   
/*     */   public static <T> Holder<T>[] emptyArray() {
/*  28 */     return (Holder[])EMPTY_ARRAY;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final ComponentRegistry<ECS_TYPE> registry;
/*     */ 
/*     */   
/*  37 */   private final StampedLock lock = new StampedLock();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Archetype<ECS_TYPE> archetype;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Component<ECS_TYPE>[] components;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean ensureValidComponents = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Holder() {
/*  59 */     this.registry = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Holder(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
/*  69 */     this.registry = registry;
/*  70 */     this.archetype = Archetype.empty();
/*  71 */     this.components = (Component<ECS_TYPE>[])Component.EMPTY_ARRAY;
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
/*     */   Holder(@Nonnull ComponentRegistry<ECS_TYPE> registry, @Nonnull Archetype<ECS_TYPE> archetype, @Nonnull Component<ECS_TYPE>[] components) {
/*  84 */     this.registry = registry;
/*  85 */     init(archetype, components);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Component<ECS_TYPE>[] ensureComponentsSize(int size) {
/*  96 */     long stamp = this.lock.writeLock();
/*     */     try {
/*  98 */       if (this.components == null) {
/*     */         
/* 100 */         this.components = (Component<ECS_TYPE>[])new Component[size];
/* 101 */         return this.components;
/*     */       } 
/* 103 */       if (this.components.length < size) this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, size); 
/* 104 */       return this.components;
/*     */     } finally {
/* 106 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(@Nonnull Archetype<ECS_TYPE> archetype, @Nonnull Component<ECS_TYPE>[] components) {
/* 117 */     archetype.validate();
/* 118 */     archetype.validateComponents(components, null);
/* 119 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 121 */       this.archetype = archetype;
/* 122 */       this.components = components;
/* 123 */       this.ensureValidComponents = true;
/*     */     } finally {
/* 125 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void _internal_init(@Nonnull Archetype<ECS_TYPE> archetype, @Nonnull Component<ECS_TYPE>[] components, @Nonnull ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType) {
/* 133 */     archetype.validateComponents(components, unknownComponentType);
/* 134 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 136 */       this.archetype = archetype;
/* 137 */       this.components = components;
/* 138 */       this.ensureValidComponents = false;
/*     */     } finally {
/* 140 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Archetype<ECS_TYPE> getArchetype() {
/* 149 */     return this.archetype;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Component<ECS_TYPE>> void ensureComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType) {
/* 159 */     assert this.archetype != null;
/* 160 */     assert this.registry != null;
/*     */     
/* 162 */     if (this.ensureValidComponents) componentType.validate(); 
/* 163 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 165 */       if (this.archetype.contains(componentType))
/* 166 */         return;  T component = this.registry.createComponent(componentType);
/* 167 */       addComponent0(componentType, component);
/*     */     } finally {
/* 169 */       this.lock.unlockWrite(stamp);
/*     */     } 
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
/*     */   @Nonnull
/*     */   public <T extends Component<ECS_TYPE>> T ensureAndGetComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType) {
/* 183 */     ensureComponent(componentType);
/*     */     
/* 185 */     return getComponent(componentType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Component<ECS_TYPE>> void addComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType, @Nonnull T component) {
/* 196 */     assert this.archetype != null;
/*     */     
/* 198 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 200 */       if (this.ensureValidComponents) componentType.validate(); 
/* 201 */       if (this.archetype.contains(componentType)) throw new IllegalArgumentException("Entity contains component type: " + String.valueOf(componentType));
/*     */       
/* 203 */       addComponent0(componentType, component);
/*     */     } finally {
/* 205 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private <T extends Component<ECS_TYPE>> void addComponent0(@Nonnull ComponentType<ECS_TYPE, T> componentType, @Nonnull T component) {
/* 217 */     assert this.archetype != null;
/* 218 */     assert this.components != null;
/*     */     
/* 220 */     this.archetype = Archetype.add(this.archetype, componentType);
/*     */ 
/*     */     
/* 223 */     int newLength = this.archetype.length();
/* 224 */     if (this.components.length < newLength) {
/* 225 */       this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, newLength);
/*     */     }
/*     */     
/* 228 */     this.components[componentType.getIndex()] = (Component<ECS_TYPE>)component;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Component<ECS_TYPE>> void replaceComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType, @Nonnull T component) {
/* 239 */     assert this.archetype != null;
/* 240 */     assert this.components != null;
/*     */     
/* 242 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 244 */       if (this.ensureValidComponents) componentType.validate(); 
/* 245 */       this.archetype.validateComponentType(componentType);
/*     */       
/* 247 */       this.components[componentType.getIndex()] = (Component<ECS_TYPE>)component;
/*     */     } finally {
/* 249 */       this.lock.unlockWrite(stamp);
/*     */     } 
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
/*     */   public <T extends Component<ECS_TYPE>> void putComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType, @Nonnull T component) {
/* 264 */     if (getComponent(componentType) != null) {
/* 265 */       replaceComponent(componentType, component);
/*     */     } else {
/* 267 */       addComponent(componentType, component);
/*     */     } 
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
/*     */   public <T extends Component<ECS_TYPE>> T getComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType) {
/* 281 */     assert this.archetype != null;
/* 282 */     assert this.components != null;
/*     */     
/* 284 */     long stamp = this.lock.readLock();
/*     */     try {
/* 286 */       if (this.ensureValidComponents) componentType.validate(); 
/* 287 */       if (!this.archetype.contains(componentType)) return null; 
/* 288 */       return (T)this.components[componentType.getIndex()];
/*     */     } finally {
/* 290 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Component<ECS_TYPE>> void removeComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType) {
/* 301 */     assert this.archetype != null;
/* 302 */     assert this.components != null;
/*     */     
/* 304 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 306 */       if (this.ensureValidComponents) componentType.validate(); 
/* 307 */       this.archetype.validateComponentType(componentType);
/*     */       
/* 309 */       this.archetype = Archetype.remove(this.archetype, componentType);
/* 310 */       this.components[componentType.getIndex()] = null;
/*     */     } finally {
/* 312 */       this.lock.unlockWrite(stamp);
/*     */     } 
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
/*     */   public <T extends Component<ECS_TYPE>> boolean tryRemoveComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType) {
/* 325 */     if (getComponent(componentType) == null) return false; 
/* 326 */     removeComponent(componentType);
/* 327 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasSerializableComponents(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
/* 337 */     assert this.archetype != null;
/* 338 */     return this.archetype.hasSerializableComponents(data);
/*     */   }
/*     */   
/*     */   public void updateData(@Nonnull ComponentRegistry.Data<ECS_TYPE> oldData, @Nonnull ComponentRegistry.Data<ECS_TYPE> newData) {
/* 342 */     assert this.archetype != null;
/* 343 */     assert this.components != null;
/* 344 */     assert this.registry != null;
/*     */     
/* 346 */     long stamp = this.lock.writeLock();
/*     */     
/*     */     try {
/* 349 */       if (this.archetype.isEmpty())
/*     */         return; 
/* 351 */       ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType = this.registry.getUnknownComponentType();
/* 352 */       for (int i = 0; i < newData.getDataChangeCount(); i++) {
/*     */         
/* 354 */         DataChange dataChange = newData.getDataChange(i);
/* 355 */         if (dataChange instanceof ComponentChange) {
/*     */           String componentId;
/*     */           Codec<Component<ECS_TYPE>> componentCodec;
/* 358 */           ComponentChange<ECS_TYPE, ? extends Component<ECS_TYPE>> componentChange = (ComponentChange<ECS_TYPE, ? extends Component<ECS_TYPE>>)dataChange;
/* 359 */           ComponentType<ECS_TYPE, ? extends Component<ECS_TYPE>> componentType = componentChange.getComponentType();
/*     */           
/* 361 */           switch (componentChange.getType()) {
/*     */             case REGISTERED:
/* 363 */               assert this.archetype != null;
/*     */ 
/*     */               
/* 366 */               if (this.archetype.contains(componentType)) {
/*     */                 break;
/*     */               }
/* 369 */               if (!this.archetype.contains(unknownComponentType))
/*     */                 break; 
/* 371 */               componentId = newData.getComponentId(componentType);
/*     */               
/* 373 */               componentCodec = newData.getComponentCodec((ComponentType)componentType);
/*     */ 
/*     */               
/* 376 */               if (componentCodec != null) {
/* 377 */                 UnknownComponents<ECS_TYPE> unknownComponents = (UnknownComponents<ECS_TYPE>)this.components[unknownComponentType.getIndex()];
/* 378 */                 assert unknownComponents != null;
/* 379 */                 Component<ECS_TYPE> component = unknownComponents.removeComponent(componentId, componentCodec);
/* 380 */                 if (component != null)
/*     */                 {
/* 382 */                   addComponent0(componentType, component);
/*     */                 }
/*     */               } 
/*     */               break;
/*     */             case UNREGISTERED:
/* 387 */               assert this.archetype != null;
/*     */ 
/*     */               
/* 390 */               if (!this.archetype.contains(componentType))
/*     */                 break; 
/* 392 */               componentId = oldData.getComponentId(componentType);
/*     */               
/* 394 */               componentCodec = oldData.getComponentCodec((ComponentType)componentType);
/*     */ 
/*     */               
/* 397 */               if (componentCodec != null) {
/*     */                 UnknownComponents<ECS_TYPE> unknownComponents;
/*     */                 
/* 400 */                 if (this.archetype.contains(unknownComponentType)) {
/* 401 */                   unknownComponents = (UnknownComponents<ECS_TYPE>)this.components[unknownComponentType.getIndex()];
/* 402 */                   assert unknownComponents != null;
/*     */                 } else {
/* 404 */                   unknownComponents = new UnknownComponents();
/* 405 */                   addComponent0(unknownComponentType, unknownComponents);
/*     */                 } 
/*     */ 
/*     */                 
/* 409 */                 Component<ECS_TYPE> component = this.components[componentType.getIndex()];
/* 410 */                 unknownComponents.addComponent(componentId, component, componentCodec);
/*     */               } 
/*     */               
/* 413 */               this.archetype = Archetype.remove(this.archetype, componentType);
/* 414 */               this.components[componentType.getIndex()] = null;
/*     */               break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 420 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Holder<ECS_TYPE> clone() {
/* 427 */     assert this.archetype != null;
/* 428 */     assert this.components != null;
/* 429 */     assert this.registry != null;
/*     */     
/* 431 */     long stamp = this.lock.readLock();
/*     */     
/*     */     try {
/* 434 */       Component[] arrayOfComponent = new Component[this.components.length];
/* 435 */       for (int i = 0; i < this.components.length; i++) {
/* 436 */         Component<ECS_TYPE> component = this.components[i];
/* 437 */         if (component != null) arrayOfComponent[i] = component.clone(); 
/*     */       } 
/* 439 */       return this.registry.newHolder(this.archetype, (Component<ECS_TYPE>[])arrayOfComponent);
/*     */     } finally {
/* 441 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Holder<ECS_TYPE> cloneSerializable(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
/* 446 */     assert this.archetype != null;
/* 447 */     assert this.components != null;
/* 448 */     assert this.registry != null;
/*     */     
/* 450 */     long stamp = this.lock.readLock();
/*     */     try {
/* 452 */       Archetype<ECS_TYPE> serializableArchetype = this.archetype.getSerializableArchetype(data);
/*     */ 
/*     */       
/* 455 */       Component[] arrayOfComponent = new Component[serializableArchetype.length()];
/* 456 */       for (int i = serializableArchetype.getMinIndex(); i < serializableArchetype.length(); i++) {
/* 457 */         ComponentType<ECS_TYPE, ? extends Component<ECS_TYPE>> componentType = (ComponentType)serializableArchetype.get(i);
/* 458 */         if (componentType != null)
/* 459 */           arrayOfComponent[i] = this.components[i].cloneSerializable(); 
/*     */       } 
/* 461 */       return this.registry.newHolder(serializableArchetype, (Component<ECS_TYPE>[])arrayOfComponent);
/*     */     } finally {
/* 463 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */   
/*     */   void loadComponentsMap(@Nonnull ComponentRegistry.Data<ECS_TYPE> data, @Nonnull Map<String, Component<ECS_TYPE>> map) {
/* 468 */     assert this.components != null;
/*     */     
/* 470 */     long stamp = this.lock.writeLock();
/*     */     
/*     */     try {
/* 473 */       ComponentType[] arrayOfComponentType = new ComponentType[map.size()];
/* 474 */       int i = 0;
/*     */       
/* 476 */       ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType = data.getRegistry().getUnknownComponentType();
/* 477 */       UnknownComponents<ECS_TYPE> unknownComponents = (UnknownComponents<ECS_TYPE>)map.remove("Unknown");
/* 478 */       if (unknownComponents != null) {
/*     */         
/* 480 */         for (Map.Entry<String, BsonDocument> e : (Iterable<Map.Entry<String, BsonDocument>>)unknownComponents.getUnknownComponents().entrySet()) {
/* 481 */           ComponentType<ECS_TYPE, ? extends Component<ECS_TYPE>> type = (ComponentType)data.getComponentType(e.getKey());
/* 482 */           if (type == null) {
/*     */             continue;
/*     */           }
/* 485 */           if (map.containsKey(e.getKey())) {
/*     */             continue;
/*     */           }
/* 488 */           Codec<Component<ECS_TYPE>> codec = data.getComponentCodec((ComponentType)type);
/*     */           
/* 490 */           ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
/* 491 */           Component<ECS_TYPE> decodedComponent = (Component<ECS_TYPE>)codec.decode((BsonValue)e.getValue(), extraInfo);
/* 492 */           extraInfo.getValidationResults().logOrThrowValidatorExceptions(UnknownComponents.LOGGER);
/*     */           
/* 494 */           if (arrayOfComponentType.length <= i) {
/* 495 */             arrayOfComponentType = Arrays.<ComponentType>copyOf(arrayOfComponentType, i + 1);
/*     */           }
/*     */           
/* 498 */           arrayOfComponentType[i++] = type;
/* 499 */           int j = type.getIndex();
/* 500 */           if (this.components.length <= j) {
/* 501 */             this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, j + 1);
/*     */           }
/* 503 */           this.components[j] = decodedComponent;
/*     */         } 
/*     */         
/* 506 */         if (arrayOfComponentType.length <= i) {
/* 507 */           arrayOfComponentType = Arrays.<ComponentType>copyOf(arrayOfComponentType, i + 1);
/*     */         }
/*     */         
/* 510 */         arrayOfComponentType[i++] = unknownComponentType;
/* 511 */         int index = unknownComponentType.getIndex();
/* 512 */         if (this.components.length <= index) {
/* 513 */           this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, index + 1);
/*     */         }
/* 515 */         this.components[index] = (Component<ECS_TYPE>)unknownComponents;
/*     */       } 
/*     */       
/* 518 */       for (Map.Entry<String, Component<ECS_TYPE>> entry : map.entrySet()) {
/* 519 */         Component<ECS_TYPE> component = entry.getValue();
/*     */ 
/*     */         
/* 522 */         if (component instanceof TempUnknownComponent) { TempUnknownComponent tempUnknownComponent = (TempUnknownComponent)component;
/* 523 */           if (unknownComponents == null) {
/* 524 */             unknownComponents = new UnknownComponents();
/* 525 */             if (arrayOfComponentType.length <= i) {
/* 526 */               arrayOfComponentType = Arrays.<ComponentType>copyOf(arrayOfComponentType, i + 1);
/*     */             }
/* 528 */             arrayOfComponentType[i++] = unknownComponentType;
/* 529 */             int j = unknownComponentType.getIndex();
/* 530 */             if (this.components.length <= j) {
/* 531 */               this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, j + 1);
/*     */             }
/* 533 */             this.components[j] = (Component<ECS_TYPE>)unknownComponents;
/*     */           } 
/*     */           
/* 536 */           unknownComponents.addComponent(entry.getKey(), tempUnknownComponent);
/*     */           
/*     */           continue; }
/*     */         
/* 540 */         ComponentType<ECS_TYPE, ? extends Component<ECS_TYPE>> componentType = (ComponentType)data.getComponentType(entry.getKey());
/* 541 */         if (arrayOfComponentType.length <= i) {
/* 542 */           arrayOfComponentType = Arrays.<ComponentType>copyOf(arrayOfComponentType, i + 1);
/*     */         }
/* 544 */         arrayOfComponentType[i++] = componentType;
/* 545 */         int index = componentType.getIndex();
/* 546 */         if (this.components.length <= index) {
/* 547 */           this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, index + 1);
/*     */         }
/* 549 */         this.components[index] = component;
/*     */       } 
/* 551 */       this.archetype = Archetype.of((arrayOfComponentType.length == i) ? (ComponentType<ECS_TYPE, ?>[])arrayOfComponentType : Arrays.<ComponentType<ECS_TYPE, ?>>copyOf((ComponentType<ECS_TYPE, ?>[])arrayOfComponentType, i));
/*     */     } finally {
/* 553 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   Map<String, Component<ECS_TYPE>> createComponentsMap(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
/* 559 */     assert this.archetype != null;
/* 560 */     assert this.components != null;
/*     */     
/* 562 */     long stamp = this.lock.readLock();
/*     */     try {
/* 564 */       if (this.archetype.isEmpty()) return (Map)Collections.emptyMap();
/*     */       
/* 566 */       ComponentRegistry<ECS_TYPE> registry = data.getRegistry();
/* 567 */       ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType = registry.getUnknownComponentType();
/*     */       
/* 569 */       Object2ObjectOpenHashMap<String, TempUnknownComponent> object2ObjectOpenHashMap = new Object2ObjectOpenHashMap(this.archetype.length());
/* 570 */       for (int i = this.archetype.getMinIndex(); i < this.archetype.length(); i++) {
/* 571 */         ComponentType<ECS_TYPE, ? extends Component<ECS_TYPE>> componentType = (ComponentType)this.archetype.get(i);
/* 572 */         if (componentType != null && 
/* 573 */           data.getComponentCodec(componentType) != null)
/*     */         {
/*     */           
/* 576 */           if (componentType == unknownComponentType) {
/* 577 */             UnknownComponents<ECS_TYPE> unknownComponents = (UnknownComponents<ECS_TYPE>)this.components[componentType.getIndex()];
/* 578 */             for (Map.Entry<String, BsonDocument> entry : (Iterable<Map.Entry<String, BsonDocument>>)unknownComponents.getUnknownComponents().entrySet()) {
/* 579 */               object2ObjectOpenHashMap.putIfAbsent(entry.getKey(), new TempUnknownComponent(entry.getValue()));
/*     */             }
/*     */           }
/*     */           else {
/*     */             
/* 584 */             object2ObjectOpenHashMap.put(data.getComponentId(componentType), this.components[componentType.getIndex()]);
/*     */           }  } 
/* 586 */       }  return (Map)object2ObjectOpenHashMap;
/*     */     } finally {
/* 588 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object o) {
/* 594 */     if (this == o) return true; 
/* 595 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/* 597 */     Holder<?> that = (Holder)o;
/*     */     
/* 599 */     long stamp = this.lock.readLock();
/* 600 */     long thatStamp = that.lock.readLock();
/*     */     try {
/* 602 */       if (!Objects.equals(this.archetype, that.archetype)) return false;
/*     */       
/* 604 */       return Arrays.equals((Object[])this.components, (Object[])that.components);
/*     */     } finally {
/* 606 */       that.lock.unlockRead(thatStamp);
/* 607 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 613 */     long stamp = this.lock.readLock();
/*     */     try {
/* 615 */       int result = (this.archetype != null) ? this.archetype.hashCode() : 0;
/* 616 */       result = 31 * result + Arrays.hashCode((Object[])this.components);
/* 617 */       return result;
/*     */     } finally {
/* 619 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 626 */     long stamp = this.lock.readLock();
/*     */     try {
/* 628 */       return "EntityHolder{archetype=" + String.valueOf(this.archetype) + ", components=" + 
/*     */         
/* 630 */         Arrays.toString((Object[])this.components) + "}";
/*     */     } finally {
/*     */       
/* 633 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\Holder.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */