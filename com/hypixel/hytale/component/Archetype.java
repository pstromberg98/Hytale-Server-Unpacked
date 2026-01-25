/*     */ package com.hypixel.hytale.component;
/*     */ 
/*     */ import com.hypixel.hytale.component.data.unknown.UnknownComponents;
/*     */ import com.hypixel.hytale.component.query.ExactArchetypeQuery;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import java.util.Arrays;
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
/*     */ public class Archetype<ECS_TYPE>
/*     */   implements Query<ECS_TYPE>
/*     */ {
/*     */   @Nonnull
/*  25 */   private static final Archetype EMPTY = new Archetype(0, 0, (ComponentType<ECS_TYPE, ?>[])ComponentType.EMPTY_ARRAY);
/*     */   
/*     */   private final int minIndex;
/*     */   private final int count;
/*     */   @Nonnull
/*     */   private final ComponentType<ECS_TYPE, ?>[] componentTypes;
/*     */   
/*     */   public static <ECS_TYPE> Archetype<ECS_TYPE> empty() {
/*  33 */     return EMPTY;
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
/*     */   @Nonnull
/*  55 */   private final ExactArchetypeQuery<ECS_TYPE> exactQuery = new ExactArchetypeQuery(this);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Archetype(int minIndex, int count, @Nonnull ComponentType<ECS_TYPE, ?>[] componentTypes) {
/*  66 */     this.minIndex = minIndex;
/*  67 */     this.count = count;
/*  68 */     this.componentTypes = componentTypes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMinIndex() {
/*  76 */     return this.minIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int count() {
/*  83 */     return this.count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int length() {
/*  91 */     return this.componentTypes.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ComponentType<ECS_TYPE, ?> get(int index) {
/* 101 */     return this.componentTypes[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 108 */     return (this.componentTypes.length == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(@Nonnull ComponentType<ECS_TYPE, ?> componentType) {
/* 116 */     int index = componentType.getIndex();
/* 117 */     return (index < this.componentTypes.length && this.componentTypes[index] == componentType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(@Nonnull Archetype<ECS_TYPE> archetype) {
/* 125 */     if (this == archetype || archetype.isEmpty()) {
/* 126 */       return true;
/*     */     }
/*     */     
/* 129 */     for (int i = archetype.minIndex; i < archetype.componentTypes.length; i++) {
/* 130 */       ComponentType<ECS_TYPE, ?> componentType = archetype.componentTypes[i];
/* 131 */       if (componentType != null && 
/* 132 */         !contains(componentType)) return false; 
/*     */     } 
/* 134 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void validateComponentType(@Nonnull ComponentType<ECS_TYPE, ?> componentType) {
/* 145 */     if (!contains(componentType)) {
/* 146 */       throw new IllegalArgumentException("ComponentType is not in archetype: " + String.valueOf(componentType) + ", " + String.valueOf(this));
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
/*     */   public void validateComponents(@Nonnull Component<ECS_TYPE>[] components, @Nullable ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> ignore) {
/* 160 */     int len = Math.max(this.componentTypes.length, components.length);
/* 161 */     for (int index = 0; index < len; index++) {
/* 162 */       ComponentType<ECS_TYPE, ?> componentType = (index >= this.componentTypes.length) ? null : this.componentTypes[index];
/* 163 */       Component<ECS_TYPE> component = (index >= components.length) ? null : components[index];
/*     */       
/* 165 */       if (componentType == null) {
/* 166 */         if (component != null && (ignore == null || index != ignore.getIndex()))
/*     */         {
/*     */           
/* 169 */           throw new IllegalStateException("Invalid component at index " + index + " expected null but found " + String.valueOf(component.getClass()));
/*     */         }
/*     */       } else {
/* 172 */         Class<?> typeClass = componentType.getTypeClass();
/* 173 */         if (component == null) throw new IllegalStateException("Invalid component at index " + index + " expected " + String.valueOf(typeClass) + " but found null");
/*     */         
/* 175 */         Class<? extends Component> aClass = (Class)component.getClass();
/* 176 */         if (!aClass.equals(typeClass)) {
/* 177 */           throw new IllegalStateException("Invalid component at index " + index + " expected " + String.valueOf(typeClass) + " but found " + String.valueOf(aClass));
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasSerializableComponents(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
/* 189 */     if (isEmpty()) {
/* 190 */       return false;
/*     */     }
/*     */     
/* 193 */     if (contains(data.getRegistry().getNonSerializedComponentType())) {
/* 194 */       return false;
/*     */     }
/*     */     
/* 197 */     for (int index = this.minIndex; index < this.componentTypes.length; index++) {
/* 198 */       ComponentType<ECS_TYPE, ?> componentType = this.componentTypes[index];
/* 199 */       if (componentType != null && data.getComponentCodec(componentType) != null) return true;
/*     */     
/*     */     } 
/* 202 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Archetype<ECS_TYPE> getSerializableArchetype(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
/* 213 */     if (isEmpty()) {
/* 214 */       return EMPTY;
/*     */     }
/*     */     
/* 217 */     if (contains(data.getRegistry().getNonSerializedComponentType())) {
/* 218 */       return EMPTY;
/*     */     }
/*     */     
/* 221 */     int lastSerializableIndex = this.componentTypes.length - 1;
/* 222 */     for (int index = this.componentTypes.length - 1; index >= this.minIndex; index--) {
/* 223 */       ComponentType<ECS_TYPE, ?> componentType = this.componentTypes[index];
/* 224 */       if (componentType != null && data.getComponentCodec(componentType) != null) {
/* 225 */         lastSerializableIndex = index;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 230 */     if (lastSerializableIndex < this.minIndex) {
/* 231 */       return EMPTY;
/*     */     }
/*     */ 
/*     */     
/* 235 */     ComponentType[] arrayOfComponentType = new ComponentType[lastSerializableIndex + 1];
/*     */     
/* 237 */     int serializableMinIndex = this.minIndex;
/* 238 */     for (int i = serializableMinIndex; i < arrayOfComponentType.length; i++) {
/*     */       
/* 240 */       ComponentType<ECS_TYPE, ?> componentType = this.componentTypes[i];
/* 241 */       if (componentType != null && data.getComponentCodec(componentType) != null) {
/* 242 */         serializableMinIndex = Math.min(serializableMinIndex, i);
/* 243 */         arrayOfComponentType[i] = componentType;
/*     */       } 
/*     */     } 
/* 246 */     return new Archetype(this.minIndex, arrayOfComponentType.length, (ComponentType<ECS_TYPE, ?>[])arrayOfComponentType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ExactArchetypeQuery<ECS_TYPE> asExactQuery() {
/* 254 */     return this.exactQuery;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static <ECS_TYPE> Archetype<ECS_TYPE> of(@Nonnull ComponentType<ECS_TYPE, ?> componentTypes) {
/* 266 */     int index = componentTypes.getIndex();
/*     */ 
/*     */     
/* 269 */     ComponentType[] arrayOfComponentType = new ComponentType[index + 1];
/* 270 */     arrayOfComponentType[index] = componentTypes;
/* 271 */     return new Archetype<>(index, 1, (ComponentType<ECS_TYPE, ?>[])arrayOfComponentType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public static <ECS_TYPE> Archetype<ECS_TYPE> of(@Nonnull ComponentType<ECS_TYPE, ?>... componentTypes) {
/* 283 */     if (componentTypes.length == 0)
/*     */     {
/* 285 */       return EMPTY;
/*     */     }
/*     */     
/* 288 */     ComponentRegistry<ECS_TYPE> registry = componentTypes[0].getRegistry();
/*     */     
/* 290 */     int minIndex = Integer.MAX_VALUE;
/* 291 */     int maxIndex = Integer.MIN_VALUE;
/* 292 */     for (int i = 0; i < componentTypes.length; i++) {
/*     */       
/* 294 */       componentTypes[i].validateRegistry(registry);
/*     */ 
/*     */       
/* 297 */       int index = componentTypes[i].getIndex();
/* 298 */       if (index < minIndex) minIndex = index; 
/* 299 */       if (index > maxIndex) maxIndex = index;
/*     */ 
/*     */       
/* 302 */       for (int n = i + 1; n < componentTypes.length; n++) {
/* 303 */         if (componentTypes[i] == componentTypes[n]) {
/* 304 */           throw new IllegalArgumentException("ComponentType provided multiple times! " + Arrays.toString(componentTypes));
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 310 */     ComponentType[] arrayOfComponentType = new ComponentType[maxIndex + 1];
/* 311 */     for (ComponentType<ECS_TYPE, ?> componentType : componentTypes) {
/* 312 */       arrayOfComponentType[componentType.getIndex()] = componentType;
/*     */     }
/* 314 */     return new Archetype<>(minIndex, componentTypes.length, (ComponentType<ECS_TYPE, ?>[])arrayOfComponentType);
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
/*     */   @Nonnull
/*     */   public static <ECS_TYPE, T extends Component<ECS_TYPE>> Archetype<ECS_TYPE> add(@Nonnull Archetype<ECS_TYPE> archetype, @Nonnull ComponentType<ECS_TYPE, T> componentType) {
/* 329 */     if (archetype.isEmpty()) return of(componentType); 
/* 330 */     if (archetype.contains(componentType)) throw new IllegalArgumentException("ComponentType is already in Archetype! " + String.valueOf(archetype) + ", " + String.valueOf(componentType));
/*     */     
/* 332 */     archetype.validateRegistry(componentType.getRegistry());
/*     */     
/* 334 */     int index = componentType.getIndex();
/* 335 */     int minIndex = Math.min(index, archetype.minIndex);
/* 336 */     int newLength = Math.max(index + 1, archetype.componentTypes.length);
/* 337 */     ComponentType[] arrayOfComponentType = Arrays.<ComponentType>copyOf((ComponentType[])archetype.componentTypes, newLength);
/* 338 */     arrayOfComponentType[index] = componentType;
/* 339 */     return new Archetype<>(minIndex, archetype.count + 1, (ComponentType<ECS_TYPE, ?>[])arrayOfComponentType);
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
/*     */   public static <ECS_TYPE, T extends Component<ECS_TYPE>> Archetype<ECS_TYPE> remove(@Nonnull Archetype<ECS_TYPE> archetype, @Nonnull ComponentType<ECS_TYPE, T> componentType) {
/* 353 */     if (archetype.isEmpty()) throw new IllegalArgumentException("Archetype is already empty!"); 
/* 354 */     if (!archetype.contains(componentType)) {
/* 355 */       throw new IllegalArgumentException("Archetype doesn't contain ComponentType! " + String.valueOf(archetype) + ", " + String.valueOf(componentType));
/*     */     }
/*     */     
/* 358 */     int oldLength = archetype.componentTypes.length;
/* 359 */     int oldMinIndex = archetype.minIndex;
/* 360 */     int oldMaxIndex = oldLength - 1;
/* 361 */     if (oldMinIndex == oldMaxIndex)
/*     */     {
/* 363 */       return EMPTY;
/*     */     }
/*     */     
/* 366 */     int newCount = archetype.count - 1;
/* 367 */     int index = componentType.getIndex();
/*     */ 
/*     */     
/* 370 */     if (index == oldMaxIndex) {
/*     */ 
/*     */       
/* 373 */       int maxIndex = index - 1;
/* 374 */       for (; maxIndex > oldMinIndex && 
/* 375 */         archetype.componentTypes[maxIndex] == null; maxIndex--);
/*     */ 
/*     */       
/* 378 */       return new Archetype<>(oldMinIndex, newCount, Arrays.<ComponentType<ECS_TYPE, ?>>copyOf(archetype.componentTypes, maxIndex + 1));
/*     */     } 
/*     */     
/* 381 */     ComponentType[] arrayOfComponentType = Arrays.<ComponentType>copyOf((ComponentType[])archetype.componentTypes, oldLength);
/* 382 */     arrayOfComponentType[index] = null;
/*     */ 
/*     */     
/* 385 */     if (index == oldMinIndex) {
/*     */       
/* 387 */       int minIndex = index + 1;
/* 388 */       for (; minIndex < oldLength && 
/* 389 */         arrayOfComponentType[minIndex] == null; minIndex++);
/*     */ 
/*     */       
/* 392 */       return new Archetype<>(minIndex, newCount, (ComponentType<ECS_TYPE, ?>[])arrayOfComponentType);
/*     */     } 
/*     */ 
/*     */     
/* 396 */     return new Archetype<>(oldMinIndex, newCount, (ComponentType<ECS_TYPE, ?>[])arrayOfComponentType);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean test(@Nonnull Archetype<ECS_TYPE> archetype) {
/* 401 */     return archetype.contains(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean requiresComponentType(@Nonnull ComponentType<ECS_TYPE, ?> componentType) {
/* 406 */     return contains(componentType);
/*     */   }
/*     */ 
/*     */   
/*     */   public void validateRegistry(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
/* 411 */     if (isEmpty())
/* 412 */       return;  this.componentTypes[this.minIndex].validateRegistry(registry);
/*     */   }
/*     */ 
/*     */   
/*     */   public void validate() {
/* 417 */     for (int i = this.minIndex; i < this.componentTypes.length; i++) {
/* 418 */       ComponentType<ECS_TYPE, ?> componentType = this.componentTypes[i];
/* 419 */       if (componentType != null) componentType.validate();
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean equals(@Nullable Object o) {
/* 425 */     if (this == o) return true; 
/* 426 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/* 428 */     Archetype<?> archetype = (Archetype)o;
/*     */     
/* 430 */     return Arrays.equals((Object[])this.componentTypes, (Object[])archetype.componentTypes);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 435 */     return Arrays.hashCode((Object[])this.componentTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 442 */     return "Archetype{componentTypes=" + Arrays.toString((Object[])this.componentTypes) + "}";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\Archetype.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */