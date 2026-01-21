/*    */ package com.hypixel.hytale.server.core.entity.entities.player.data;
/*    */ 
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.Collections;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
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
/*    */ public class UniqueItemUsagesComponent
/*    */   implements Component<EntityStore>
/*    */ {
/*    */   @Nonnull
/*    */   public static final BuilderCodec<UniqueItemUsagesComponent> CODEC;
/*    */   
/*    */   static {
/* 34 */     CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(UniqueItemUsagesComponent.class, UniqueItemUsagesComponent::new).append(new KeyedCodec("UniqueItemUsed", (Codec)new ArrayCodec((Codec)Codec.STRING, x$0 -> new String[x$0])), (playerMemories, usages) -> { if (usages == null) return;  Collections.addAll(playerMemories.usedUniqueItems, usages); }playerMemories -> (String[])playerMemories.usedUniqueItems.toArray(())).add()).build();
/*    */   }
/* 36 */   private final Set<String> usedUniqueItems = new HashSet<>();
/*    */   
/*    */   public static ComponentType<EntityStore, UniqueItemUsagesComponent> getComponentType() {
/* 39 */     return EntityModule.get().getUniqueItemUsagesComponentType();
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Component<EntityStore> clone() {
/* 45 */     UniqueItemUsagesComponent component = new UniqueItemUsagesComponent();
/* 46 */     component.usedUniqueItems.addAll(this.usedUniqueItems);
/* 47 */     return component;
/*    */   }
/*    */   
/*    */   public boolean hasUsedUniqueItem(String itemId) {
/* 51 */     return this.usedUniqueItems.contains(itemId);
/*    */   }
/*    */   
/*    */   public void recordUniqueItemUsage(String itemId) {
/* 55 */     this.usedUniqueItems.add(itemId);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\entity\entities\player\data\UniqueItemUsagesComponent.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */