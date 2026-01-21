/*    */ package com.hypixel.hytale.builtin.beds.sleep.resources;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.beds.BedsPlugin;
/*    */ import com.hypixel.hytale.component.Resource;
/*    */ import com.hypixel.hytale.component.ResourceType;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class WorldSomnolence
/*    */   implements Resource<EntityStore> {
/*    */   public static ResourceType<EntityStore, WorldSomnolence> getResourceType() {
/* 13 */     return BedsPlugin.getInstance().getWorldSomnolenceResourceType();
/*    */   }
/*    */   
/* 16 */   private WorldSleep state = WorldSleep.Awake.INSTANCE;
/*    */   
/*    */   public WorldSleep getState() {
/* 19 */     return this.state;
/*    */   }
/*    */   
/*    */   public void setState(@Nonnull WorldSleep state) {
/* 23 */     this.state = state;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Resource<EntityStore> clone() {
/* 29 */     WorldSomnolence clone = new WorldSomnolence();
/* 30 */     clone.state = this.state;
/* 31 */     return clone;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\beds\sleep\resources\WorldSomnolence.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */