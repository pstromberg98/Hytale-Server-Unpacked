/*   */ package com.hypixel.hytale.server.core.asset.type.blocktype.config.bench;
/*   */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*   */ import java.util.function.Supplier;
/*   */ 
/*   */ public class DiagramCraftingBench extends CraftingBench {
/* 6 */   public static final BuilderCodec<DiagramCraftingBench> CODEC = BuilderCodec.builder(DiagramCraftingBench.class, DiagramCraftingBench::new, CraftingBench.CODEC)
/* 7 */     .build();
/*   */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\asset\type\blocktype\config\bench\DiagramCraftingBench.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */