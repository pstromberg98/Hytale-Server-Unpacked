/*    */ package com.hypixel.hytale.builtin.path;
/*    */ 
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.server.core.universe.world.path.WorldPath;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WorldPathBuilder
/*    */   implements Component<EntityStore>
/*    */ {
/*    */   private WorldPath path;
/*    */   
/*    */   public static ComponentType<EntityStore, WorldPathBuilder> getComponentType() {
/* 19 */     return PathPlugin.get().getWorldPathBuilderComponentType();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public WorldPath getPath() {
/* 25 */     return this.path;
/*    */   }
/*    */   
/*    */   public void setPath(WorldPath path) {
/* 29 */     this.path = path;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public Component<EntityStore> clone() {
/* 36 */     WorldPathBuilder builder = new WorldPathBuilder();
/* 37 */     builder.path = new WorldPath(this.path.getName(), List.copyOf(this.path.getWaypoints()));
/* 38 */     return builder;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\path\WorldPathBuilder.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */