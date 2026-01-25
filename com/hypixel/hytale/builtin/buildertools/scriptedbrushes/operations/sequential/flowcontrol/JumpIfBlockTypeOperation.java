/*    */ package com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfig;
/*    */ import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfigCommandExecutor;
/*    */ import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.system.SequenceBrushOperation;
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.math.vector.Vector3i;
/*    */ import com.hypixel.hytale.server.core.prefab.selection.mask.BlockMask;
/*    */ import com.hypixel.hytale.server.core.universe.world.accessor.ChunkAccessor;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.function.Supplier;
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
/*    */ public class JumpIfBlockTypeOperation
/*    */   extends SequenceBrushOperation
/*    */ {
/*    */   public static final BuilderCodec<JumpIfBlockTypeOperation> CODEC;
/*    */   
/*    */   static {
/* 33 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(JumpIfBlockTypeOperation.class, JumpIfBlockTypeOperation::new).append(new KeyedCodec("Mask", BlockMask.CODEC), (op, val) -> op.blockMaskArg = val, op -> op.blockMaskArg).documentation("The block mask for the comparison.").add()).append(new KeyedCodec("StoredIndexName", (Codec)Codec.STRING), (op, val) -> op.indexVariableNameArg = val, op -> op.indexVariableNameArg).documentation("The labeled index to jump to, previous or future").add()).documentation("Jump the execution of the stack based on a block type comparison")).build();
/*    */   } @Nonnull
/* 35 */   public BlockMask blockMaskArg = BlockMask.EMPTY;
/*    */   @Nonnull
/* 37 */   public String indexVariableNameArg = "Undefined";
/*    */ 
/*    */   
/*    */   public JumpIfBlockTypeOperation() {
/* 41 */     super("Jump If Block Type Comparison", "Jump the execution of the stack based on a block type comparison", false);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void modifyBrushConfig(@Nonnull Ref<EntityStore> ref, @Nonnull BrushConfig brushConfig, @Nonnull BrushConfigCommandExecutor brushConfigCommandExecutor, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 47 */     Vector3i currentBrushOrigin = brushConfig.getOrigin();
/*    */     
/* 49 */     if (currentBrushOrigin == null) {
/* 50 */       brushConfig.setErrorFlag("Could not find the origin for the operation.");
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 55 */     int targetBlockId = brushConfigCommandExecutor.getEdit().getBlock(currentBrushOrigin.x, currentBrushOrigin.y, currentBrushOrigin.z);
/* 56 */     int targetFluidId = brushConfigCommandExecutor.getEdit().getFluid(currentBrushOrigin.x, currentBrushOrigin.y, currentBrushOrigin.z);
/*    */     
/* 58 */     if (!this.blockMaskArg.isExcluded((ChunkAccessor)brushConfigCommandExecutor.getEdit().getAccessor(), currentBrushOrigin.x, currentBrushOrigin.y, currentBrushOrigin.z, null, null, targetBlockId, targetFluidId))
/*    */     {
/* 60 */       brushConfigCommandExecutor.loadOperatingIndex(this.indexVariableNameArg);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\scriptedbrushes\operations\sequential\flowcontrol\JumpIfBlockTypeOperation.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */