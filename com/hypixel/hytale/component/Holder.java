/*     */ package com.hypixel.hytale.component;
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
/*     */ public class Holder<ECS_TYPE> {
/*  21 */   private static final Holder<?>[] EMPTY_ARRAY = (Holder<?>[])new Holder[0]; @Nullable
/*     */   private final ComponentRegistry<ECS_TYPE> registry;
/*     */   
/*     */   public static <T> Holder<T>[] emptyArray() {
/*  25 */     return (Holder[])EMPTY_ARRAY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   private final StampedLock lock = new StampedLock();
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
/*  56 */     this.registry = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Holder(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
/*  66 */     this.registry = registry;
/*  67 */     this.archetype = Archetype.empty();
/*  68 */     this.components = (Component<ECS_TYPE>[])Component.EMPTY_ARRAY;
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
/*  81 */     this.registry = registry;
/*  82 */     init(archetype, components);
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
/*  93 */     long stamp = this.lock.writeLock();
/*     */     try {
/*  95 */       if (this.components == null) {
/*     */         
/*  97 */         this.components = (Component<ECS_TYPE>[])new Component[size];
/*  98 */         return this.components;
/*     */       } 
/* 100 */       if (this.components.length < size) this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, size); 
/* 101 */       return this.components;
/*     */     } finally {
/* 103 */       this.lock.unlockWrite(stamp);
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
/* 114 */     archetype.validate();
/* 115 */     archetype.validateComponents(components, null);
/* 116 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 118 */       this.archetype = archetype;
/* 119 */       this.components = components;
/* 120 */       this.ensureValidComponents = true;
/*     */     } finally {
/* 122 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void _internal_init(@Nonnull Archetype<ECS_TYPE> archetype, @Nonnull Component<ECS_TYPE>[] components, @Nonnull ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType) {
/* 130 */     archetype.validateComponents(components, unknownComponentType);
/* 131 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 133 */       this.archetype = archetype;
/* 134 */       this.components = components;
/* 135 */       this.ensureValidComponents = false;
/*     */     } finally {
/* 137 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Archetype<ECS_TYPE> getArchetype() {
/* 146 */     return this.archetype;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Component<ECS_TYPE>> void ensureComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType) {
/* 156 */     assert this.archetype != null;
/* 157 */     assert this.registry != null;
/*     */     
/* 159 */     if (this.ensureValidComponents) componentType.validate(); 
/* 160 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 162 */       if (this.archetype.contains(componentType))
/* 163 */         return;  T component = this.registry.createComponent(componentType);
/* 164 */       addComponent0(componentType, component);
/*     */     } finally {
/* 166 */       this.lock.unlockWrite(stamp);
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
/* 180 */     ensureComponent(componentType);
/*     */     
/* 182 */     return getComponent(componentType);
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
/* 193 */     assert this.archetype != null;
/*     */     
/* 195 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 197 */       if (this.ensureValidComponents) componentType.validate(); 
/* 198 */       if (this.archetype.contains(componentType)) throw new IllegalArgumentException("Entity contains component type: " + String.valueOf(componentType));
/*     */       
/* 200 */       addComponent0(componentType, component);
/*     */     } finally {
/* 202 */       this.lock.unlockWrite(stamp);
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
/* 214 */     assert this.archetype != null;
/* 215 */     assert this.components != null;
/*     */     
/* 217 */     this.archetype = Archetype.add(this.archetype, componentType);
/*     */ 
/*     */     
/* 220 */     int newLength = this.archetype.length();
/* 221 */     if (this.components.length < newLength) {
/* 222 */       this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, newLength);
/*     */     }
/*     */     
/* 225 */     this.components[componentType.getIndex()] = (Component<ECS_TYPE>)component;
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
/* 236 */     assert this.archetype != null;
/* 237 */     assert this.components != null;
/*     */     
/* 239 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 241 */       if (this.ensureValidComponents) componentType.validate(); 
/* 242 */       this.archetype.validateComponentType(componentType);
/*     */       
/* 244 */       this.components[componentType.getIndex()] = (Component<ECS_TYPE>)component;
/*     */     } finally {
/* 246 */       this.lock.unlockWrite(stamp);
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
/* 261 */     if (getComponent(componentType) != null) {
/* 262 */       replaceComponent(componentType, component);
/*     */     } else {
/* 264 */       addComponent(componentType, component);
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
/* 278 */     assert this.archetype != null;
/* 279 */     assert this.components != null;
/*     */     
/* 281 */     long stamp = this.lock.readLock();
/*     */     try {
/* 283 */       if (this.ensureValidComponents) componentType.validate(); 
/* 284 */       if (!this.archetype.contains(componentType)) return null; 
/* 285 */       return (T)this.components[componentType.getIndex()];
/*     */     } finally {
/* 287 */       this.lock.unlockRead(stamp);
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
/* 298 */     assert this.archetype != null;
/* 299 */     assert this.components != null;
/*     */     
/* 301 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 303 */       if (this.ensureValidComponents) componentType.validate(); 
/* 304 */       this.archetype.validateComponentType(componentType);
/*     */       
/* 306 */       this.archetype = Archetype.remove(this.archetype, componentType);
/* 307 */       this.components[componentType.getIndex()] = null;
/*     */     } finally {
/* 309 */       this.lock.unlockWrite(stamp);
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
/* 322 */     if (getComponent(componentType) == null) return false; 
/* 323 */     removeComponent(componentType);
/* 324 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasSerializableComponents(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
/* 334 */     assert this.archetype != null;
/* 335 */     return this.archetype.hasSerializableComponents(data);
/*     */   }
/*     */   
/*     */   public void updateData(@Nonnull ComponentRegistry.Data<ECS_TYPE> oldData, @Nonnull ComponentRegistry.Data<ECS_TYPE> newData) {
/* 339 */     assert this.archetype != null;
/* 340 */     assert this.components != null;
/* 341 */     assert this.registry != null;
/*     */     
/* 343 */     long stamp = this.lock.writeLock();
/*     */     
/*     */     try {
/* 346 */       if (this.archetype.isEmpty())
/*     */         return; 
/* 348 */       ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType = this.registry.getUnknownComponentType();
/* 349 */       for (int i = 0; i < newData.getDataChangeCount(); i++) {
/*     */         
/* 351 */         DataChange dataChange = newData.getDataChange(i);
/* 352 */         if (dataChange instanceof ComponentChange) {
/*     */           String componentId;
/*     */           Codec<Component<ECS_TYPE>> componentCodec;
/* 355 */           ComponentChange<ECS_TYPE, ? extends Component<ECS_TYPE>> componentChange = (ComponentChange<ECS_TYPE, ? extends Component<ECS_TYPE>>)dataChange;
/* 356 */           ComponentType<ECS_TYPE, ? extends Component<ECS_TYPE>> componentType = componentChange.getComponentType();
/*     */           
/* 358 */           switch (componentChange.getType()) {
/*     */             case REGISTERED:
/* 360 */               assert this.archetype != null;
/*     */ 
/*     */               
/* 363 */               if (this.archetype.contains(componentType)) {
/*     */                 break;
/*     */               }
/* 366 */               if (!this.archetype.contains(unknownComponentType))
/*     */                 break; 
/* 368 */               componentId = newData.getComponentId(componentType);
/*     */               
/* 370 */               componentCodec = newData.getComponentCodec((ComponentType)componentType);
/*     */ 
/*     */               
/* 373 */               if (componentCodec != null) {
/* 374 */                 UnknownComponents<ECS_TYPE> unknownComponents = (UnknownComponents<ECS_TYPE>)this.components[unknownComponentType.getIndex()];
/* 375 */                 assert unknownComponents != null;
/* 376 */                 Component<ECS_TYPE> component = unknownComponents.removeComponent(componentId, componentCodec);
/* 377 */                 if (component != null)
/*     */                 {
/* 379 */                   addComponent0(componentType, component);
/*     */                 }
/*     */               } 
/*     */               break;
/*     */             case UNREGISTERED:
/* 384 */               assert this.archetype != null;
/*     */ 
/*     */               
/* 387 */               if (!this.archetype.contains(componentType))
/*     */                 break; 
/* 389 */               componentId = oldData.getComponentId(componentType);
/*     */               
/* 391 */               componentCodec = oldData.getComponentCodec((ComponentType)componentType);
/*     */ 
/*     */               
/* 394 */               if (componentCodec != null) {
/*     */                 UnknownComponents<ECS_TYPE> unknownComponents;
/*     */                 
/* 397 */                 if (this.archetype.contains(unknownComponentType)) {
/* 398 */                   unknownComponents = (UnknownComponents<ECS_TYPE>)this.components[unknownComponentType.getIndex()];
/* 399 */                   assert unknownComponents != null;
/*     */                 } else {
/* 401 */                   unknownComponents = new UnknownComponents();
/* 402 */                   addComponent0(unknownComponentType, unknownComponents);
/*     */                 } 
/*     */ 
/*     */                 
/* 406 */                 Component<ECS_TYPE> component = this.components[componentType.getIndex()];
/* 407 */                 unknownComponents.addComponent(componentId, component, componentCodec);
/*     */               } 
/*     */               
/* 410 */               this.archetype = Archetype.remove(this.archetype, componentType);
/* 411 */               this.components[componentType.getIndex()] = null;
/*     */               break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 417 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Holder<ECS_TYPE> clone() {
/* 424 */     assert this.archetype != null;
/* 425 */     assert this.components != null;
/* 426 */     assert this.registry != null;
/*     */     
/* 428 */     long stamp = this.lock.readLock();
/*     */     
/*     */     try {
/* 431 */       Component[] arrayOfComponent = new Component[this.components.length];
/* 432 */       for (int i = 0; i < this.components.length; i++) {
/* 433 */         Component<ECS_TYPE> component = this.components[i];
/* 434 */         if (component != null) arrayOfComponent[i] = component.clone(); 
/*     */       } 
/* 436 */       return this.registry.newHolder(this.archetype, (Component<ECS_TYPE>[])arrayOfComponent);
/*     */     } finally {
/* 438 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Holder<ECS_TYPE> cloneSerializable(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
/* 443 */     assert this.archetype != null;
/* 444 */     assert this.components != null;
/* 445 */     assert this.registry != null;
/*     */     
/* 447 */     long stamp = this.lock.readLock();
/*     */     try {
/* 449 */       Archetype<ECS_TYPE> serializableArchetype = this.archetype.getSerializableArchetype(data);
/*     */ 
/*     */       
/* 452 */       Component[] arrayOfComponent = new Component[serializableArchetype.length()];
/* 453 */       for (int i = serializableArchetype.getMinIndex(); i < serializableArchetype.length(); i++) {
/* 454 */         ComponentType<ECS_TYPE, ? extends Component<ECS_TYPE>> componentType = (ComponentType)serializableArchetype.get(i);
/* 455 */         if (componentType != null)
/* 456 */           arrayOfComponent[i] = this.components[i].cloneSerializable(); 
/*     */       } 
/* 458 */       return this.registry.newHolder(serializableArchetype, (Component<ECS_TYPE>[])arrayOfComponent);
/*     */     } finally {
/* 460 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */   
/*     */   void loadComponentsMap(@Nonnull ComponentRegistry.Data<ECS_TYPE> data, @Nonnull Map<String, Component<ECS_TYPE>> map) {
/* 465 */     assert this.components != null;
/*     */     
/* 467 */     long stamp = this.lock.writeLock();
/*     */     
/*     */     try {
/* 470 */       ComponentType[] arrayOfComponentType = new ComponentType[map.size()];
/* 471 */       int i = 0;
/*     */       
/* 473 */       ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType = data.getRegistry().getUnknownComponentType();
/* 474 */       UnknownComponents<ECS_TYPE> unknownComponents = (UnknownComponents<ECS_TYPE>)map.remove("Unknown");
/* 475 */       if (unknownComponents != null) {
/*     */         
/* 477 */         for (Map.Entry<String, BsonDocument> e : (Iterable<Map.Entry<String, BsonDocument>>)unknownComponents.getUnknownComponents().entrySet()) {
/* 478 */           ComponentType<ECS_TYPE, ? extends Component<ECS_TYPE>> type = (ComponentType)data.getComponentType(e.getKey());
/* 479 */           if (type == null) {
/*     */             continue;
/*     */           }
/* 482 */           if (map.containsKey(e.getKey())) {
/*     */             continue;
/*     */           }
/* 485 */           Codec<Component<ECS_TYPE>> codec = data.getComponentCodec((ComponentType)type);
/*     */           
/* 487 */           ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
/* 488 */           Component<ECS_TYPE> decodedComponent = (Component<ECS_TYPE>)codec.decode((BsonValue)e.getValue(), extraInfo);
/* 489 */           extraInfo.getValidationResults().logOrThrowValidatorExceptions(UnknownComponents.LOGGER);
/*     */           
/* 491 */           if (arrayOfComponentType.length <= i) {
/* 492 */             arrayOfComponentType = Arrays.<ComponentType>copyOf(arrayOfComponentType, i + 1);
/*     */           }
/*     */           
/* 495 */           arrayOfComponentType[i++] = type;
/* 496 */           int j = type.getIndex();
/* 497 */           if (this.components.length <= j) {
/* 498 */             this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, j + 1);
/*     */           }
/* 500 */           this.components[j] = decodedComponent;
/*     */         } 
/*     */         
/* 503 */         if (arrayOfComponentType.length <= i) {
/* 504 */           arrayOfComponentType = Arrays.<ComponentType>copyOf(arrayOfComponentType, i + 1);
/*     */         }
/*     */         
/* 507 */         arrayOfComponentType[i++] = unknownComponentType;
/* 508 */         int index = unknownComponentType.getIndex();
/* 509 */         if (this.components.length <= index) {
/* 510 */           this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, index + 1);
/*     */         }
/* 512 */         this.components[index] = (Component<ECS_TYPE>)unknownComponents;
/*     */       } 
/*     */       
/* 515 */       for (Map.Entry<String, Component<ECS_TYPE>> entry : map.entrySet()) {
/* 516 */         Component<ECS_TYPE> component = entry.getValue();
/*     */ 
/*     */         
/* 519 */         if (component instanceof TempUnknownComponent) { TempUnknownComponent tempUnknownComponent = (TempUnknownComponent)component;
/* 520 */           if (unknownComponents == null) {
/* 521 */             unknownComponents = new UnknownComponents();
/* 522 */             if (arrayOfComponentType.length <= i) {
/* 523 */               arrayOfComponentType = Arrays.<ComponentType>copyOf(arrayOfComponentType, i + 1);
/*     */             }
/* 525 */             arrayOfComponentType[i++] = unknownComponentType;
/* 526 */             int j = unknownComponentType.getIndex();
/* 527 */             if (this.components.length <= j) {
/* 528 */               this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, j + 1);
/*     */             }
/* 530 */             this.components[j] = (Component<ECS_TYPE>)unknownComponents;
/*     */           } 
/*     */           
/* 533 */           unknownComponents.addComponent(entry.getKey(), tempUnknownComponent);
/*     */           
/*     */           continue; }
/*     */         
/* 537 */         ComponentType<ECS_TYPE, ? extends Component<ECS_TYPE>> componentType = (ComponentType)data.getComponentType(entry.getKey());
/* 538 */         if (arrayOfComponentType.length <= i) {
/* 539 */           arrayOfComponentType = Arrays.<ComponentType>copyOf(arrayOfComponentType, i + 1);
/*     */         }
/* 541 */         arrayOfComponentType[i++] = componentType;
/* 542 */         int index = componentType.getIndex();
/* 543 */         if (this.components.length <= index) {
/* 544 */           this.components = Arrays.<Component<ECS_TYPE>>copyOf(this.components, index + 1);
/*     */         }
/* 546 */         this.components[index] = component;
/*     */       } 
/* 548 */       this.archetype = Archetype.of((arrayOfComponentType.length == i) ? (ComponentType<ECS_TYPE, ?>[])arrayOfComponentType : Arrays.<ComponentType<ECS_TYPE, ?>>copyOf((ComponentType<ECS_TYPE, ?>[])arrayOfComponentType, i));
/*     */     } finally {
/* 550 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   Map<String, Component<ECS_TYPE>> createComponentsMap(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
/* 556 */     assert this.archetype != null;
/* 557 */     assert this.components != null;
/*     */     
/* 559 */     long stamp = this.lock.readLock();
/*     */     try {
/* 561 */       if (this.archetype.isEmpty()) return (Map)Collections.emptyMap();
/*     */       
/* 563 */       ComponentRegistry<ECS_TYPE> registry = data.getRegistry();
/* 564 */       ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType = registry.getUnknownComponentType();
/*     */       
/* 566 */       Object2ObjectOpenHashMap<String, TempUnknownComponent> object2ObjectOpenHashMap = new Object2ObjectOpenHashMap(this.archetype.length());
/* 567 */       for (int i = this.archetype.getMinIndex(); i < this.archetype.length(); i++) {
/* 568 */         ComponentType<ECS_TYPE, ? extends Component<ECS_TYPE>> componentType = (ComponentType)this.archetype.get(i);
/* 569 */         if (componentType != null && 
/* 570 */           data.getComponentCodec(componentType) != null)
/*     */         {
/*     */           
/* 573 */           if (componentType == unknownComponentType) {
/* 574 */             UnknownComponents<ECS_TYPE> unknownComponents = (UnknownComponents<ECS_TYPE>)this.components[componentType.getIndex()];
/* 575 */             for (Map.Entry<String, BsonDocument> entry : (Iterable<Map.Entry<String, BsonDocument>>)unknownComponents.getUnknownComponents().entrySet()) {
/* 576 */               object2ObjectOpenHashMap.putIfAbsent(entry.getKey(), new TempUnknownComponent(entry.getValue()));
/*     */             }
/*     */           }
/*     */           else {
/*     */             
/* 581 */             object2ObjectOpenHashMap.put(data.getComponentId(componentType), this.components[componentType.getIndex()]);
/*     */           }  } 
/* 583 */       }  return (Map)object2ObjectOpenHashMap;
/*     */     } finally {
/* 585 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object o) {
/* 591 */     if (this == o) return true; 
/* 592 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/* 594 */     Holder<?> that = (Holder)o;
/*     */     
/* 596 */     long stamp = this.lock.readLock();
/* 597 */     long thatStamp = that.lock.readLock();
/*     */     try {
/* 599 */       if (!Objects.equals(this.archetype, that.archetype)) return false;
/*     */       
/* 601 */       return Arrays.equals((Object[])this.components, (Object[])that.components);
/*     */     } finally {
/* 603 */       that.lock.unlockRead(thatStamp);
/* 604 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 610 */     long stamp = this.lock.readLock();
/*     */     try {
/* 612 */       int result = (this.archetype != null) ? this.archetype.hashCode() : 0;
/* 613 */       result = 31 * result + Arrays.hashCode((Object[])this.components);
/* 614 */       return result;
/*     */     } finally {
/* 616 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 623 */     long stamp = this.lock.readLock();
/*     */     try {
/* 625 */       return "EntityHolder{archetype=" + String.valueOf(this.archetype) + ", components=" + 
/*     */         
/* 627 */         Arrays.toString((Object[])this.components) + "}";
/*     */     } finally {
/*     */       
/* 630 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\Holder.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */