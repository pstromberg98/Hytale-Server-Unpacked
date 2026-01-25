/*    */ package com.hypixel.hytale.builtin.buildertools.tooloperations;
/*    */ 
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolOnUseInteraction;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TintOperation
/*    */   extends ToolOperation
/*    */ {
/*    */   private final int tintColor;
/*    */   
/*    */   public TintOperation(@Nonnull Ref<EntityStore> ref, @Nonnull Player player, @Nonnull BuilderToolOnUseInteraction packet, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 20 */     super(ref, packet, componentAccessor);
/*    */     
/* 22 */     String colorText = (String)this.args.tool().get("TintColor");
/*    */     
/*    */     try {
/* 25 */       this.tintColor = ColorParseUtil.hexStringToRGBInt(colorText);
/* 26 */     } catch (NumberFormatException e) {
/* 27 */       player.sendMessage(Message.translation("server.builderTools.tintOperation.colorParseError").param("value", colorText));
/* 28 */       throw e;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(ComponentAccessor<EntityStore> componentAccessor) {
/* 34 */     this.builderState.tint(this.x, this.y, this.z, this.tintColor, this.shape, this.shapeRange, this.shapeHeight, componentAccessor);
/*    */   }
/*    */ 
/*    */   
/*    */   public void executeAt(int posX, int posY, int posZ, ComponentAccessor<EntityStore> componentAccessor) {
/* 39 */     this.builderState.tint(posX, posY, posZ, this.tintColor, this.shape, this.shapeRange, this.shapeHeight, componentAccessor);
/*    */   }
/*    */ 
/*    */   
/*    */   boolean execute0(int x, int y, int z) {
/* 44 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\tooloperations\TintOperation.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */