/*    */ package com.hypixel.hytale.server.core.modules.entity;
/*    */ 
/*    */ import com.hypixel.hytale.codec.ExtraInfo;
/*    */ import java.util.function.Function;
/*    */ 
/*    */ public class BlockMigrationExtraInfo
/*    */   extends ExtraInfo
/*    */ {
/*    */   private final Function<String, String> blockMigration;
/*    */   
/*    */   public BlockMigrationExtraInfo(int version, Function<String, String> blockMigration) {
/* 12 */     super(version);
/* 13 */     this.blockMigration = blockMigration;
/*    */   }
/*    */   
/*    */   public Function<String, String> getBlockMigration() {
/* 17 */     return this.blockMigration;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\BlockMigrationExtraInfo.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */