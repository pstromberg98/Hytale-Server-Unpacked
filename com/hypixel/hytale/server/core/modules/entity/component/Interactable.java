/*    */ package com.hypixel.hytale.server.core.modules.entity.component;
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
/*    */ public class Interactable
/*    */   implements Component<EntityStore>
/*    */ {
/*    */   @Nonnull
/* 20 */   public static final Interactable INSTANCE = new Interactable();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 26 */   public static final BuilderCodec<Interactable> CODEC = BuilderCodec.builder(Interactable.class, () -> INSTANCE)
/* 27 */     .build();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ComponentType<EntityStore, Interactable> getComponentType() {
/* 33 */     return EntityModule.get().getInteractableComponentType();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Component<EntityStore> clone() {
/* 44 */     return INSTANCE;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\component\Interactable.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */