/*    */ package com.hypixel.hytale.server.core.modules.entity.repulsion;
/*    */ 
/*    */ import com.hypixel.hytale.component.AddReason;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.component.Holder;
/*    */ import com.hypixel.hytale.component.RemoveReason;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.component.query.Query;
/*    */ import com.hypixel.hytale.component.system.HolderSystem;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PlayerSetup
/*    */   extends HolderSystem<EntityStore>
/*    */ {
/*    */   private final ComponentType<EntityStore, Repulsion> componentType;
/*    */   @Nonnull
/*    */   private final Query<EntityStore> query;
/*    */   
/*    */   public PlayerSetup(ComponentType<EntityStore, Repulsion> componentType, ComponentType<EntityStore, Player> playerComponentType) {
/* 41 */     this.componentType = componentType;
/* 42 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)playerComponentType, (Query)Query.not((Query)componentType) });
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public Query<EntityStore> getQuery() {
/* 48 */     return this.query;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/* 53 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*    */     
/* 55 */     int repulsionConfigIndex = world.getGameplayConfig().getPlayerConfig().getRepulsionConfigIndex();
/* 56 */     if (repulsionConfigIndex == -1) {
/* 57 */       if (holder.getComponent(this.componentType) != null) {
/* 58 */         holder.removeComponent(this.componentType);
/*    */       }
/*    */     } else {
/* 61 */       RepulsionConfig repulsion = (RepulsionConfig)RepulsionConfig.getAssetMap().getAsset(repulsionConfigIndex);
/* 62 */       if (holder.getComponent(this.componentType) != null) {
/* 63 */         holder.removeComponent(this.componentType);
/*    */       }
/* 65 */       holder.addComponent(this.componentType, new Repulsion(repulsion));
/*    */     } 
/*    */   }
/*    */   
/*    */   public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {}
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\repulsion\RepulsionSystems$PlayerSetup.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */