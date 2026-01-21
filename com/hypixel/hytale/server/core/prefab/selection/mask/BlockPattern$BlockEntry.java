/*     */ package com.hypixel.hytale.server.core.prefab.selection.mask;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.function.FunctionCodec;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
/*     */ import com.hypixel.hytale.server.core.util.FillerBlockUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BlockEntry
/*     */   extends Record
/*     */ {
/*     */   private final String blockTypeKey;
/*     */   private final int rotation;
/*     */   private final int filler;
/*     */   
/*     */   public final String toString() {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/server/core/prefab/selection/mask/BlockPattern$BlockEntry;)Ljava/lang/String;
/*     */     //   6: areturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #192	-> 0
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	7	0	this	Lcom/hypixel/hytale/server/core/prefab/selection/mask/BlockPattern$BlockEntry;
/*     */   }
/*     */   
/*     */   public final int hashCode() {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/server/core/prefab/selection/mask/BlockPattern$BlockEntry;)I
/*     */     //   6: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #192	-> 0
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	7	0	this	Lcom/hypixel/hytale/server/core/prefab/selection/mask/BlockPattern$BlockEntry;
/*     */   }
/*     */   
/*     */   public final boolean equals(Object o) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: aload_1
/*     */     //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/server/core/prefab/selection/mask/BlockPattern$BlockEntry;Ljava/lang/Object;)Z
/*     */     //   7: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #192	-> 0
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	8	0	this	Lcom/hypixel/hytale/server/core/prefab/selection/mask/BlockPattern$BlockEntry;
/*     */     //   0	8	1	o	Ljava/lang/Object;
/*     */   }
/*     */   
/*     */   public BlockEntry(String blockTypeKey, int rotation, int filler) {
/* 192 */     this.blockTypeKey = blockTypeKey; this.rotation = rotation; this.filler = filler; } public String blockTypeKey() { return this.blockTypeKey; } public int rotation() { return this.rotation; } public int filler() { return this.filler; }
/*     */   
/*     */   @Deprecated(forRemoval = true)
/* 195 */   public static Codec<BlockEntry> CODEC = (Codec<BlockEntry>)new FunctionCodec((Codec)Codec.STRING, BlockEntry::decode, BlockEntry::encode);
/*     */   
/*     */   @Deprecated(forRemoval = true)
/*     */   private String encode() {
/* 199 */     if (this.filler == 0 && this.rotation == 0) return this.blockTypeKey; 
/* 200 */     StringBuilder out = new StringBuilder(this.blockTypeKey);
/* 201 */     RotationTuple rot = RotationTuple.get(this.rotation);
/* 202 */     if (rot.yaw() != Rotation.None) {
/* 203 */       out.append("|Yaw=").append(rot.yaw().getDegrees());
/*     */     }
/* 205 */     if (rot.pitch() != Rotation.None) {
/* 206 */       out.append("|Pitch=").append(rot.pitch().getDegrees());
/*     */     }
/* 208 */     if (rot.roll() != Rotation.None) {
/* 209 */       out.append("|Roll=").append(rot.roll().getDegrees());
/*     */     }
/* 211 */     if (this.filler != 0) {
/* 212 */       int fillerX = FillerBlockUtil.unpackX(this.filler);
/* 213 */       int fillerY = FillerBlockUtil.unpackY(this.filler);
/* 214 */       int fillerZ = FillerBlockUtil.unpackZ(this.filler);
/* 215 */       out.append("|Filler=").append(fillerX).append(",").append(fillerY).append(",").append(fillerZ);
/*     */     } 
/* 217 */     return out.toString();
/*     */   }
/*     */   
/*     */   @Deprecated(forRemoval = true)
/*     */   public static BlockEntry decode(String key) {
/* 222 */     int filler = 0;
/* 223 */     if (key.contains("|Filler=")) {
/* 224 */       int start = key.indexOf("|Filler=") + "|Filler=".length();
/* 225 */       int firstComma = key.indexOf(',', start);
/* 226 */       if (firstComma == -1) throw new IllegalArgumentException("Invalid filler metadata! Missing comma"); 
/* 227 */       int secondComma = key.indexOf(',', firstComma + 1);
/* 228 */       if (secondComma == -1) throw new IllegalArgumentException("Invalid filler metadata! Missing second comma");
/*     */       
/* 230 */       int i = key.indexOf('|', start);
/* 231 */       if (i == -1) i = key.length();
/*     */       
/* 233 */       int fillerX = Integer.parseInt(key, start, firstComma, 10);
/* 234 */       int fillerY = Integer.parseInt(key, firstComma + 1, secondComma, 10);
/* 235 */       int fillerZ = Integer.parseInt(key, secondComma + 1, i, 10);
/*     */       
/* 237 */       filler = FillerBlockUtil.pack(fillerX, fillerY, fillerZ);
/*     */     } 
/*     */     
/* 240 */     Rotation rotationYaw = Rotation.None;
/* 241 */     Rotation rotationPitch = Rotation.None;
/* 242 */     Rotation rotationRoll = Rotation.None;
/*     */     
/* 244 */     if (key.contains("|Yaw=")) {
/* 245 */       int start = key.indexOf("|Yaw=") + "|Yaw=".length();
/* 246 */       int i = key.indexOf('|', start);
/* 247 */       if (i == -1) i = key.length(); 
/* 248 */       rotationYaw = Rotation.ofDegrees(Integer.parseInt(key, start, i, 10));
/*     */     } 
/* 250 */     if (key.contains("|Pitch=")) {
/* 251 */       int start = key.indexOf("|Pitch=") + "|Pitch=".length();
/* 252 */       int i = key.indexOf('|', start);
/* 253 */       if (i == -1) i = key.length(); 
/* 254 */       rotationPitch = Rotation.ofDegrees(Integer.parseInt(key, start, i, 10));
/*     */     } 
/* 256 */     if (key.contains("|Roll=")) {
/* 257 */       int start = key.indexOf("|Roll=") + "|Roll=".length();
/* 258 */       int i = key.indexOf('|', start);
/* 259 */       if (i == -1) i = key.length(); 
/* 260 */       rotationRoll = Rotation.ofDegrees(Integer.parseInt(key, start, i, 10));
/*     */     } 
/*     */ 
/*     */     
/* 264 */     int end = key.indexOf('|');
/* 265 */     if (end == -1) end = key.length(); 
/* 266 */     String name = key.substring(0, end);
/* 267 */     return new BlockEntry(name, RotationTuple.of(rotationYaw, rotationPitch, rotationRoll).index(), filler);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\prefab\selection\mask\BlockPattern$BlockEntry.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */