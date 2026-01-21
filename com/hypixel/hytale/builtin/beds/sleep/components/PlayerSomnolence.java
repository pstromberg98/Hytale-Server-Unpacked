/*    */ package com.hypixel.hytale.builtin.beds.sleep.components;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.beds.BedsPlugin;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class PlayerSomnolence
/*    */   implements Component<EntityStore> {
/*    */   public static ComponentType<EntityStore, PlayerSomnolence> getComponentType() {
/* 12 */     return BedsPlugin.getInstance().getPlayerSomnolenceComponentType();
/*    */   }
/*    */   
/* 15 */   public static PlayerSomnolence AWAKE = new PlayerSomnolence(PlayerSleep.FullyAwake.INSTANCE);
/*    */   
/* 17 */   private PlayerSleep state = PlayerSleep.FullyAwake.INSTANCE;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PlayerSomnolence(PlayerSleep state) {
/* 23 */     this.state = state;
/*    */   }
/*    */   
/*    */   public PlayerSleep getSleepState() {
/* 27 */     return this.state;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Component<EntityStore> clone() {
/* 33 */     PlayerSomnolence clone = new PlayerSomnolence();
/* 34 */     clone.state = this.state;
/* 35 */     return clone;
/*    */   }
/*    */   
/*    */   public PlayerSomnolence() {}
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\beds\sleep\components\PlayerSomnolence.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */