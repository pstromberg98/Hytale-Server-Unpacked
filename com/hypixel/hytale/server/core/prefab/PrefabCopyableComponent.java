/*    */ package com.hypixel.hytale.server.core.prefab;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ 
/*    */ public class PrefabCopyableComponent implements Component<EntityStore> {
/* 10 */   public static final PrefabCopyableComponent INSTANCE = new PrefabCopyableComponent();
/*    */   
/* 12 */   public static final BuilderCodec<PrefabCopyableComponent> CODEC = BuilderCodec.builder(PrefabCopyableComponent.class, () -> INSTANCE)
/* 13 */     .build();
/*    */   
/*    */   public static ComponentType<EntityStore, PrefabCopyableComponent> getComponentType() {
/* 16 */     return EntityModule.get().getPrefabCopyableComponentType();
/*    */   }
/*    */   
/*    */   public static PrefabCopyableComponent get() {
/* 20 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public Component<EntityStore> clone() {
/* 25 */     return INSTANCE;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\prefab\PrefabCopyableComponent.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */