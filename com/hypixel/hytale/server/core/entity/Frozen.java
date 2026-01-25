/*    */ package com.hypixel.hytale.server.core.entity;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Frozen
/*    */   implements Component<EntityStore>
/*    */ {
/*    */   @Nonnull
/* 24 */   public static final BuilderCodec<Frozen> CODEC = BuilderCodec.builder(Frozen.class, Frozen::get)
/* 25 */     .build();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ComponentType<EntityStore, Frozen> getComponentType() {
/* 31 */     return EntityModule.get().getFrozenComponentType();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 37 */   private static final Frozen INSTANCE = new Frozen();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Frozen get() {
/* 43 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Component<EntityStore> clone() {
/* 55 */     return get();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\entity\Frozen.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */