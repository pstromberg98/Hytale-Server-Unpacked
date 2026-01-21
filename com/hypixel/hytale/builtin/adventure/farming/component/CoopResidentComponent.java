/*    */ package com.hypixel.hytale.builtin.adventure.farming.component;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.adventure.farming.FarmingPlugin;
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.math.vector.Vector3i;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.function.Supplier;
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
/*    */ 
/*    */ public class CoopResidentComponent
/*    */   implements Component<EntityStore>
/*    */ {
/*    */   public static final BuilderCodec<CoopResidentComponent> CODEC;
/*    */   
/*    */   static {
/* 30 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CoopResidentComponent.class, CoopResidentComponent::new).append(new KeyedCodec("CoopLocation", (Codec)Vector3i.CODEC), (comp, ref) -> comp.coopLocation = ref, comp -> comp.coopLocation).add()).append(new KeyedCodec("MarkedForDespawn", (Codec)BuilderCodec.BOOLEAN), (comp, markedForDespawn) -> comp.markedForDespawn = markedForDespawn.booleanValue(), comp -> Boolean.valueOf(comp.markedForDespawn)).add()).build();
/*    */   }
/*    */   public static ComponentType<EntityStore, CoopResidentComponent> getComponentType() {
/* 33 */     return FarmingPlugin.get().getCoopResidentComponentType();
/*    */   }
/*    */ 
/*    */   
/* 37 */   private Vector3i coopLocation = new Vector3i();
/*    */ 
/*    */   
/*    */   private boolean markedForDespawn;
/*    */ 
/*    */   
/*    */   public void setCoopLocation(Vector3i coopLocation) {
/* 44 */     this.coopLocation = coopLocation;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Vector3i getCoopLocation() {
/* 51 */     return this.coopLocation;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setMarkedForDespawn(boolean markedForDespawn) {
/* 58 */     this.markedForDespawn = markedForDespawn;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean getMarkedForDespawn() {
/* 65 */     return this.markedForDespawn;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Component<EntityStore> clone() {
/* 71 */     CoopResidentComponent component = new CoopResidentComponent();
/* 72 */     component.coopLocation.assign(this.coopLocation);
/* 73 */     component.markedForDespawn = this.markedForDespawn;
/* 74 */     return component;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\farming\component\CoopResidentComponent.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */