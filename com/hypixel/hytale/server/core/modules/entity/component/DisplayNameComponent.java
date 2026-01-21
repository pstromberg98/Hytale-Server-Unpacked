/*    */ package com.hypixel.hytale.server.core.modules.entity.component;
/*    */ 
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ public class DisplayNameComponent
/*    */   implements Component<EntityStore>
/*    */ {
/*    */   @Nonnull
/*    */   public static final BuilderCodec<DisplayNameComponent> CODEC;
/*    */   @Nullable
/*    */   private Message displayName;
/*    */   
/*    */   @Nonnull
/*    */   public static ComponentType<EntityStore, DisplayNameComponent> getComponentType() {
/* 26 */     return EntityModule.get().getDisplayNameComponentType();
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
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static {
/* 42 */     CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(DisplayNameComponent.class, DisplayNameComponent::new).appendInherited(new KeyedCodec("DisplayName", (Codec)Message.CODEC), (e, s) -> e.displayName = s, e -> e.displayName, (e, p) -> e.displayName = p.displayName).documentation("The value of the display name.").add()).build();
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
/*    */   
/*    */   public DisplayNameComponent() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DisplayNameComponent(@Nullable Message displayName) {
/* 65 */     this.displayName = displayName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Message getDisplayName() {
/* 73 */     return this.displayName;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Component<EntityStore> clone() {
/* 79 */     return new DisplayNameComponent(this.displayName);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\component\DisplayNameComponent.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */