/*    */ package com.hypixel.hytale.builtin.crafting.system;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
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
/*    */ public class CraftingHolderSystem
/*    */   extends HolderSystem<EntityStore>
/*    */ {
/*    */   @Nonnull
/* 28 */   private final ComponentType<EntityStore, Player> playerComponentType = Player.getComponentType();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   private final ComponentType<EntityStore, CraftingManager> craftingManagerComponentType;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CraftingHolderSystem(@Nonnull ComponentType<EntityStore, CraftingManager> craftingManagerComponentType) {
/* 42 */     this.craftingManagerComponentType = craftingManagerComponentType;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/* 47 */     holder.ensureComponent(this.craftingManagerComponentType);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
/* 52 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/* 53 */     if (world.getWorldConfig().isSavingPlayers()) {
/* 54 */       Player playerComponent = (Player)holder.getComponent(this.playerComponentType);
/* 55 */       assert playerComponent != null;
/*    */       
/* 57 */       playerComponent.saveConfig(world, holder);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public Query<EntityStore> getQuery() {
/* 64 */     return (Query)this.playerComponentType;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\system\PlayerCraftingSystems$CraftingHolderSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */