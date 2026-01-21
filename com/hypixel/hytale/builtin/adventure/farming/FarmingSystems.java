/*     */ package com.hypixel.hytale.builtin.adventure.farming;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.component.CoopResidentComponent;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.config.FarmingCoopAsset;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.states.CoopBlock;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlockState;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.RefSystem;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.Rangef;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingData;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*     */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.time.Instant;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class FarmingSystems {
/*     */   public static class OnSoilAdded extends RefSystem<ChunkStore> {
/*  44 */     private static final Query<ChunkStore> QUERY = (Query<ChunkStore>)Query.and(new Query[] {
/*  45 */           (Query)BlockModule.BlockStateInfo.getComponentType(), 
/*  46 */           (Query)TilledSoilBlock.getComponentType()
/*     */         });
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/*  51 */       TilledSoilBlock soil = (TilledSoilBlock)commandBuffer.getComponent(ref, TilledSoilBlock.getComponentType());
/*  52 */       assert soil != null;
/*  53 */       BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo)commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
/*  54 */       assert info != null;
/*     */       
/*  56 */       if (!soil.isPlanted()) {
/*  57 */         int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
/*  58 */         int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
/*  59 */         int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
/*     */         
/*  61 */         assert info.getChunkRef() != null;
/*  62 */         BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
/*  63 */         assert blockChunk != null;
/*  64 */         BlockSection blockSection = blockChunk.getSectionAtBlockY(y);
/*     */ 
/*     */         
/*  67 */         Instant decayTime = soil.getDecayTime();
/*  68 */         if (decayTime == null) {
/*     */ 
/*     */           
/*  71 */           BlockType blockType = (BlockType)BlockType.getAssetMap().getAsset(blockSection.get(x, y, z));
/*  72 */           FarmingSystems.updateSoilDecayTime(commandBuffer, soil, blockType);
/*     */         } 
/*  74 */         if (decayTime == null) {
/*     */           return;
/*     */         }
/*     */         
/*  78 */         blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), decayTime);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {}
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Query<ChunkStore> getQuery() {
/*  90 */       return QUERY;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class OnFarmBlockAdded
/*     */     extends RefSystem<ChunkStore> {
/*  96 */     private static final Query<ChunkStore> QUERY = (Query<ChunkStore>)Query.and(new Query[] {
/*  97 */           (Query)BlockModule.BlockStateInfo.getComponentType(), 
/*  98 */           (Query)FarmingBlock.getComponentType()
/*     */         });
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
/*     */     public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/*     */       // Byte code:
/*     */       //   0: aload #4
/*     */       //   2: aload_1
/*     */       //   3: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */       //   6: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */       //   9: checkcast com/hypixel/hytale/builtin/adventure/farming/states/FarmingBlock
/*     */       //   12: astore #5
/*     */       //   14: getstatic com/hypixel/hytale/builtin/adventure/farming/FarmingSystems$OnFarmBlockAdded.$assertionsDisabled : Z
/*     */       //   17: ifne -> 33
/*     */       //   20: aload #5
/*     */       //   22: ifnonnull -> 33
/*     */       //   25: new java/lang/AssertionError
/*     */       //   28: dup
/*     */       //   29: invokespecial <init> : ()V
/*     */       //   32: athrow
/*     */       //   33: aload #4
/*     */       //   35: aload_1
/*     */       //   36: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */       //   39: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */       //   42: checkcast com/hypixel/hytale/server/core/modules/block/BlockModule$BlockStateInfo
/*     */       //   45: astore #6
/*     */       //   47: getstatic com/hypixel/hytale/builtin/adventure/farming/FarmingSystems$OnFarmBlockAdded.$assertionsDisabled : Z
/*     */       //   50: ifne -> 66
/*     */       //   53: aload #6
/*     */       //   55: ifnonnull -> 66
/*     */       //   58: new java/lang/AssertionError
/*     */       //   61: dup
/*     */       //   62: invokespecial <init> : ()V
/*     */       //   65: athrow
/*     */       //   66: aload #4
/*     */       //   68: aload #6
/*     */       //   70: invokevirtual getChunkRef : ()Lcom/hypixel/hytale/component/Ref;
/*     */       //   73: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */       //   76: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */       //   79: checkcast com/hypixel/hytale/server/core/universe/world/chunk/BlockChunk
/*     */       //   82: astore #7
/*     */       //   84: aload #5
/*     */       //   86: invokevirtual getLastTickGameTime : ()Ljava/time/Instant;
/*     */       //   89: ifnonnull -> 483
/*     */       //   92: aload #7
/*     */       //   94: aload #6
/*     */       //   96: invokevirtual getIndex : ()I
/*     */       //   99: invokestatic xFromBlockInColumn : (I)I
/*     */       //   102: aload #6
/*     */       //   104: invokevirtual getIndex : ()I
/*     */       //   107: invokestatic yFromBlockInColumn : (I)I
/*     */       //   110: aload #6
/*     */       //   112: invokevirtual getIndex : ()I
/*     */       //   115: invokestatic zFromBlockInColumn : (I)I
/*     */       //   118: invokevirtual getBlock : (III)I
/*     */       //   121: istore #8
/*     */       //   123: invokestatic getAssetMap : ()Lcom/hypixel/hytale/assetstore/map/BlockTypeAssetMap;
/*     */       //   126: iload #8
/*     */       //   128: invokevirtual getAsset : (I)Lcom/hypixel/hytale/assetstore/map/JsonAssetWithMap;
/*     */       //   131: checkcast com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockType
/*     */       //   134: astore #9
/*     */       //   136: aload #9
/*     */       //   138: invokevirtual getFarming : ()Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingData;
/*     */       //   141: ifnonnull -> 145
/*     */       //   144: return
/*     */       //   145: aload #5
/*     */       //   147: aload #9
/*     */       //   149: invokevirtual getFarming : ()Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingData;
/*     */       //   152: invokevirtual getStartingStageSet : ()Ljava/lang/String;
/*     */       //   155: invokevirtual setCurrentStageSet : (Ljava/lang/String;)V
/*     */       //   158: aload #5
/*     */       //   160: aload_3
/*     */       //   161: invokevirtual getExternalData : ()Ljava/lang/Object;
/*     */       //   164: checkcast com/hypixel/hytale/server/core/universe/world/storage/ChunkStore
/*     */       //   167: invokevirtual getWorld : ()Lcom/hypixel/hytale/server/core/universe/world/World;
/*     */       //   170: invokevirtual getEntityStore : ()Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;
/*     */       //   173: invokevirtual getStore : ()Lcom/hypixel/hytale/component/Store;
/*     */       //   176: invokestatic getResourceType : ()Lcom/hypixel/hytale/component/ResourceType;
/*     */       //   179: invokevirtual getResource : (Lcom/hypixel/hytale/component/ResourceType;)Lcom/hypixel/hytale/component/Resource;
/*     */       //   182: checkcast com/hypixel/hytale/server/core/modules/time/WorldTimeResource
/*     */       //   185: invokevirtual getGameTime : ()Ljava/time/Instant;
/*     */       //   188: invokevirtual setLastTickGameTime : (Ljava/time/Instant;)V
/*     */       //   191: aload #7
/*     */       //   193: invokevirtual markNeedsSaving : ()V
/*     */       //   196: aload #9
/*     */       //   198: invokevirtual getFarming : ()Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingData;
/*     */       //   201: invokevirtual getStages : ()Ljava/util/Map;
/*     */       //   204: ifnull -> 483
/*     */       //   207: aload #9
/*     */       //   209: invokevirtual getFarming : ()Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingData;
/*     */       //   212: invokevirtual getStages : ()Ljava/util/Map;
/*     */       //   215: aload #9
/*     */       //   217: invokevirtual getFarming : ()Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingData;
/*     */       //   220: invokevirtual getStartingStageSet : ()Ljava/lang/String;
/*     */       //   223: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */       //   228: checkcast [Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingStageData;
/*     */       //   231: astore #10
/*     */       //   233: aload #10
/*     */       //   235: ifnull -> 483
/*     */       //   238: aload #10
/*     */       //   240: arraylength
/*     */       //   241: ifle -> 483
/*     */       //   244: iconst_0
/*     */       //   245: istore #11
/*     */       //   247: iconst_0
/*     */       //   248: istore #12
/*     */       //   250: iload #12
/*     */       //   252: aload #10
/*     */       //   254: arraylength
/*     */       //   255: if_icmpge -> 409
/*     */       //   258: aload #10
/*     */       //   260: iload #12
/*     */       //   262: aaload
/*     */       //   263: astore #13
/*     */       //   265: aload #13
/*     */       //   267: dup
/*     */       //   268: invokestatic requireNonNull : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */       //   271: pop
/*     */       //   272: astore #14
/*     */       //   274: iconst_0
/*     */       //   275: istore #15
/*     */       //   277: aload #14
/*     */       //   279: iload #15
/*     */       //   281: <illegal opcode> typeSwitch : (Ljava/lang/Object;I)I
/*     */       //   286: lookupswitch default -> 403, 0 -> 312, 1 -> 349
/*     */       //   312: aload #14
/*     */       //   314: checkcast com/hypixel/hytale/builtin/adventure/farming/config/stages/BlockTypeFarmingStageData
/*     */       //   317: astore #16
/*     */       //   319: aload #16
/*     */       //   321: invokevirtual getBlock : ()Ljava/lang/String;
/*     */       //   324: aload #9
/*     */       //   326: invokevirtual getId : ()Ljava/lang/String;
/*     */       //   329: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */       //   332: ifeq -> 403
/*     */       //   335: aload #5
/*     */       //   337: iload #12
/*     */       //   339: i2f
/*     */       //   340: invokevirtual setGrowthProgress : (F)V
/*     */       //   343: iconst_1
/*     */       //   344: istore #11
/*     */       //   346: goto -> 403
/*     */       //   349: aload #14
/*     */       //   351: checkcast com/hypixel/hytale/builtin/adventure/farming/config/stages/BlockStateFarmingStageData
/*     */       //   354: astore #17
/*     */       //   356: aload #9
/*     */       //   358: aload #17
/*     */       //   360: invokevirtual getState : ()Ljava/lang/String;
/*     */       //   363: invokevirtual getBlockForState : (Ljava/lang/String;)Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/BlockType;
/*     */       //   366: astore #18
/*     */       //   368: aload #18
/*     */       //   370: ifnull -> 400
/*     */       //   373: aload #18
/*     */       //   375: invokevirtual getId : ()Ljava/lang/String;
/*     */       //   378: aload #9
/*     */       //   380: invokevirtual getId : ()Ljava/lang/String;
/*     */       //   383: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */       //   386: ifeq -> 400
/*     */       //   389: aload #5
/*     */       //   391: iload #12
/*     */       //   393: i2f
/*     */       //   394: invokevirtual setGrowthProgress : (F)V
/*     */       //   397: iconst_1
/*     */       //   398: istore #11
/*     */       //   400: goto -> 403
/*     */       //   403: iinc #12, 1
/*     */       //   406: goto -> 250
/*     */       //   409: iload #11
/*     */       //   411: ifne -> 483
/*     */       //   414: aload #4
/*     */       //   416: aload #6
/*     */       //   418: invokevirtual getChunkRef : ()Lcom/hypixel/hytale/component/Ref;
/*     */       //   421: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */       //   424: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */       //   427: checkcast com/hypixel/hytale/server/core/universe/world/chunk/ChunkColumn
/*     */       //   430: aload #6
/*     */       //   432: invokevirtual getIndex : ()I
/*     */       //   435: invokestatic yFromBlockInColumn : (I)I
/*     */       //   438: invokestatic chunkCoordinate : (I)I
/*     */       //   441: invokevirtual getSection : (I)Lcom/hypixel/hytale/component/Ref;
/*     */       //   444: astore #12
/*     */       //   446: aload #10
/*     */       //   448: iconst_0
/*     */       //   449: aaload
/*     */       //   450: aload #4
/*     */       //   452: aload #12
/*     */       //   454: aload_1
/*     */       //   455: aload #6
/*     */       //   457: invokevirtual getIndex : ()I
/*     */       //   460: invokestatic xFromBlockInColumn : (I)I
/*     */       //   463: aload #6
/*     */       //   465: invokevirtual getIndex : ()I
/*     */       //   468: invokestatic yFromBlockInColumn : (I)I
/*     */       //   471: aload #6
/*     */       //   473: invokevirtual getIndex : ()I
/*     */       //   476: invokestatic zFromBlockInColumn : (I)I
/*     */       //   479: aconst_null
/*     */       //   480: invokevirtual apply : (Lcom/hypixel/hytale/component/ComponentAccessor;Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/Ref;IIILcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingStageData;)V
/*     */       //   483: aload #5
/*     */       //   485: invokevirtual getLastTickGameTime : ()Ljava/time/Instant;
/*     */       //   488: ifnonnull -> 529
/*     */       //   491: aload #5
/*     */       //   493: aload_3
/*     */       //   494: invokevirtual getExternalData : ()Ljava/lang/Object;
/*     */       //   497: checkcast com/hypixel/hytale/server/core/universe/world/storage/ChunkStore
/*     */       //   500: invokevirtual getWorld : ()Lcom/hypixel/hytale/server/core/universe/world/World;
/*     */       //   503: invokevirtual getEntityStore : ()Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;
/*     */       //   506: invokevirtual getStore : ()Lcom/hypixel/hytale/component/Store;
/*     */       //   509: invokestatic getResourceType : ()Lcom/hypixel/hytale/component/ResourceType;
/*     */       //   512: invokevirtual getResource : (Lcom/hypixel/hytale/component/ResourceType;)Lcom/hypixel/hytale/component/Resource;
/*     */       //   515: checkcast com/hypixel/hytale/server/core/modules/time/WorldTimeResource
/*     */       //   518: invokevirtual getGameTime : ()Ljava/time/Instant;
/*     */       //   521: invokevirtual setLastTickGameTime : (Ljava/time/Instant;)V
/*     */       //   524: aload #7
/*     */       //   526: invokevirtual markNeedsSaving : ()V
/*     */       //   529: aload #6
/*     */       //   531: invokevirtual getIndex : ()I
/*     */       //   534: invokestatic xFromBlockInColumn : (I)I
/*     */       //   537: istore #8
/*     */       //   539: aload #6
/*     */       //   541: invokevirtual getIndex : ()I
/*     */       //   544: invokestatic yFromBlockInColumn : (I)I
/*     */       //   547: istore #9
/*     */       //   549: aload #6
/*     */       //   551: invokevirtual getIndex : ()I
/*     */       //   554: invokestatic zFromBlockInColumn : (I)I
/*     */       //   557: istore #10
/*     */       //   559: aload #4
/*     */       //   561: aload #6
/*     */       //   563: invokevirtual getChunkRef : ()Lcom/hypixel/hytale/component/Ref;
/*     */       //   566: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */       //   569: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */       //   572: checkcast com/hypixel/hytale/server/core/universe/world/chunk/BlockComponentChunk
/*     */       //   575: astore #11
/*     */       //   577: getstatic com/hypixel/hytale/builtin/adventure/farming/FarmingSystems$OnFarmBlockAdded.$assertionsDisabled : Z
/*     */       //   580: ifne -> 596
/*     */       //   583: aload #11
/*     */       //   585: ifnonnull -> 596
/*     */       //   588: new java/lang/AssertionError
/*     */       //   591: dup
/*     */       //   592: invokespecial <init> : ()V
/*     */       //   595: athrow
/*     */       //   596: aload #4
/*     */       //   598: aload #6
/*     */       //   600: invokevirtual getChunkRef : ()Lcom/hypixel/hytale/component/Ref;
/*     */       //   603: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */       //   606: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */       //   609: checkcast com/hypixel/hytale/server/core/universe/world/chunk/ChunkColumn
/*     */       //   612: astore #12
/*     */       //   614: getstatic com/hypixel/hytale/builtin/adventure/farming/FarmingSystems$OnFarmBlockAdded.$assertionsDisabled : Z
/*     */       //   617: ifne -> 633
/*     */       //   620: aload #12
/*     */       //   622: ifnonnull -> 633
/*     */       //   625: new java/lang/AssertionError
/*     */       //   628: dup
/*     */       //   629: invokespecial <init> : ()V
/*     */       //   632: athrow
/*     */       //   633: aload #12
/*     */       //   635: iload #9
/*     */       //   637: invokestatic chunkCoordinate : (I)I
/*     */       //   640: invokevirtual getSection : (I)Lcom/hypixel/hytale/component/Ref;
/*     */       //   643: astore #13
/*     */       //   645: aload #4
/*     */       //   647: aload #13
/*     */       //   649: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */       //   652: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */       //   655: checkcast com/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection
/*     */       //   658: astore #14
/*     */       //   660: aload #4
/*     */       //   662: aload #7
/*     */       //   664: aload #14
/*     */       //   666: aload #13
/*     */       //   668: aload_1
/*     */       //   669: aload #5
/*     */       //   671: iload #8
/*     */       //   673: iload #9
/*     */       //   675: iload #10
/*     */       //   677: iconst_1
/*     */       //   678: invokestatic tickFarming : (Lcom/hypixel/hytale/component/CommandBuffer;Lcom/hypixel/hytale/server/core/universe/world/chunk/BlockChunk;Lcom/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection;Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/builtin/adventure/farming/states/FarmingBlock;IIIZ)V
/*     */       //   681: return
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #103	-> 0
/*     */       //   #104	-> 14
/*     */       //   #105	-> 33
/*     */       //   #106	-> 47
/*     */       //   #109	-> 66
/*     */       //   #110	-> 84
/*     */       //   #111	-> 92
/*     */       //   #112	-> 96
/*     */       //   #113	-> 104
/*     */       //   #114	-> 112
/*     */       //   #111	-> 118
/*     */       //   #116	-> 123
/*     */       //   #117	-> 136
/*     */       //   #118	-> 145
/*     */       //   #120	-> 158
/*     */       //   #121	-> 191
/*     */       //   #123	-> 196
/*     */       //   #124	-> 207
/*     */       //   #125	-> 233
/*     */       //   #127	-> 244
/*     */       //   #128	-> 247
/*     */       //   #129	-> 258
/*     */       //   #130	-> 265
/*     */       //   #131	-> 312
/*     */       //   #132	-> 319
/*     */       //   #133	-> 335
/*     */       //   #134	-> 343
/*     */       //   #137	-> 349
/*     */       //   #138	-> 356
/*     */       //   #139	-> 368
/*     */       //   #140	-> 389
/*     */       //   #141	-> 397
/*     */       //   #143	-> 400
/*     */       //   #128	-> 403
/*     */       //   #149	-> 409
/*     */       //   #150	-> 414
/*     */       //   #151	-> 446
/*     */       //   #152	-> 457
/*     */       //   #153	-> 465
/*     */       //   #154	-> 473
/*     */       //   #151	-> 480
/*     */       //   #161	-> 483
/*     */       //   #162	-> 491
/*     */       //   #163	-> 524
/*     */       //   #166	-> 529
/*     */       //   #167	-> 539
/*     */       //   #168	-> 549
/*     */       //   #170	-> 559
/*     */       //   #171	-> 577
/*     */       //   #172	-> 596
/*     */       //   #173	-> 614
/*     */       //   #174	-> 633
/*     */       //   #175	-> 645
/*     */       //   #177	-> 660
/*     */       //   #178	-> 681
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   319	30	16	data	Lcom/hypixel/hytale/builtin/adventure/farming/config/stages/BlockTypeFarmingStageData;
/*     */       //   368	32	18	stateBlockType	Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/BlockType;
/*     */       //   356	47	17	data	Lcom/hypixel/hytale/builtin/adventure/farming/config/stages/BlockStateFarmingStageData;
/*     */       //   265	138	13	stage	Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingStageData;
/*     */       //   250	159	12	i	I
/*     */       //   446	37	12	sectionRef	Lcom/hypixel/hytale/component/Ref;
/*     */       //   247	236	11	found	Z
/*     */       //   233	250	10	stages	[Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingStageData;
/*     */       //   123	360	8	blockId	I
/*     */       //   136	347	9	blockType	Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/BlockType;
/*     */       //   0	682	0	this	Lcom/hypixel/hytale/builtin/adventure/farming/FarmingSystems$OnFarmBlockAdded;
/*     */       //   0	682	1	ref	Lcom/hypixel/hytale/component/Ref;
/*     */       //   0	682	2	reason	Lcom/hypixel/hytale/component/AddReason;
/*     */       //   0	682	3	store	Lcom/hypixel/hytale/component/Store;
/*     */       //   0	682	4	commandBuffer	Lcom/hypixel/hytale/component/CommandBuffer;
/*     */       //   14	668	5	farmingBlock	Lcom/hypixel/hytale/builtin/adventure/farming/states/FarmingBlock;
/*     */       //   47	635	6	info	Lcom/hypixel/hytale/server/core/modules/block/BlockModule$BlockStateInfo;
/*     */       //   84	598	7	blockChunk	Lcom/hypixel/hytale/server/core/universe/world/chunk/BlockChunk;
/*     */       //   539	143	8	x	I
/*     */       //   549	133	9	y	I
/*     */       //   559	123	10	z	I
/*     */       //   577	105	11	blockComponentChunk	Lcom/hypixel/hytale/server/core/universe/world/chunk/BlockComponentChunk;
/*     */       //   614	68	12	column	Lcom/hypixel/hytale/server/core/universe/world/chunk/ChunkColumn;
/*     */       //   645	37	13	section	Lcom/hypixel/hytale/component/Ref;
/*     */       //   660	22	14	blockSection	Lcom/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection;
/*     */       // Local variable type table:
/*     */       //   start	length	slot	name	signature
/*     */       //   446	37	12	sectionRef	Lcom/hypixel/hytale/component/Ref<Lcom/hypixel/hytale/server/core/universe/world/storage/ChunkStore;>;
/*     */       //   0	682	1	ref	Lcom/hypixel/hytale/component/Ref<Lcom/hypixel/hytale/server/core/universe/world/storage/ChunkStore;>;
/*     */       //   0	682	3	store	Lcom/hypixel/hytale/component/Store<Lcom/hypixel/hytale/server/core/universe/world/storage/ChunkStore;>;
/*     */       //   0	682	4	commandBuffer	Lcom/hypixel/hytale/component/CommandBuffer<Lcom/hypixel/hytale/server/core/universe/world/storage/ChunkStore;>;
/*     */       //   645	37	13	section	Lcom/hypixel/hytale/component/Ref<Lcom/hypixel/hytale/server/core/universe/world/storage/ChunkStore;>;
/*     */     }
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
/*     */     public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {}
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
/*     */     @Nullable
/*     */     public Query<ChunkStore> getQuery() {
/* 187 */       return QUERY;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Ticking
/*     */     extends EntityTickingSystem<ChunkStore> {
/* 193 */     private static final Query<ChunkStore> QUERY = (Query<ChunkStore>)Query.and(new Query[] { (Query)BlockSection.getComponentType(), (Query)ChunkSection.getComponentType() });
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/* 197 */       BlockSection blocks = (BlockSection)archetypeChunk.getComponent(index, BlockSection.getComponentType());
/* 198 */       assert blocks != null;
/*     */       
/* 200 */       if (blocks.getTickingBlocksCountCopy() == 0)
/*     */         return; 
/* 202 */       ChunkSection section = (ChunkSection)archetypeChunk.getComponent(index, ChunkSection.getComponentType());
/* 203 */       assert section != null;
/* 204 */       if (section.getChunkColumnReference() == null || !section.getChunkColumnReference().isValid())
/*     */         return; 
/* 206 */       BlockComponentChunk blockComponentChunk = (BlockComponentChunk)commandBuffer.getComponent(section.getChunkColumnReference(), BlockComponentChunk.getComponentType());
/* 207 */       assert blockComponentChunk != null;
/*     */       
/* 209 */       Ref<ChunkStore> ref = archetypeChunk.getReferenceTo(index);
/* 210 */       BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(section.getChunkColumnReference(), BlockChunk.getComponentType());
/* 211 */       assert blockChunk != null;
/*     */ 
/*     */       
/* 214 */       blocks.forEachTicking(blockComponentChunk, commandBuffer, section.getY(), (blockComponentChunk1, commandBuffer1, localX, localY, localZ, blockId) -> {
/*     */             Ref<ChunkStore> blockRef = blockComponentChunk1.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
/*     */             if (blockRef == null) {
/*     */               return BlockTickStrategy.IGNORED;
/*     */             }
/*     */             FarmingBlock farming = (FarmingBlock)commandBuffer1.getComponent(blockRef, FarmingBlock.getComponentType());
/*     */             if (farming != null) {
/*     */               FarmingUtil.tickFarming(commandBuffer1, blockChunk, blocks, ref, blockRef, farming, localX, localY, localZ, false);
/*     */               return BlockTickStrategy.SLEEP;
/*     */             } 
/*     */             TilledSoilBlock soil = (TilledSoilBlock)commandBuffer1.getComponent(blockRef, TilledSoilBlock.getComponentType());
/*     */             if (soil != null) {
/*     */               tickSoil(commandBuffer1, blockComponentChunk1, blockRef, soil);
/*     */               return BlockTickStrategy.SLEEP;
/*     */             } 
/*     */             CoopBlock coop = (CoopBlock)commandBuffer1.getComponent(blockRef, CoopBlock.getComponentType());
/*     */             if (coop != null) {
/*     */               tickCoop(commandBuffer1, blockComponentChunk1, blockRef, coop);
/*     */               return BlockTickStrategy.SLEEP;
/*     */             } 
/*     */             return BlockTickStrategy.IGNORED;
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private static void tickSoil(CommandBuffer<ChunkStore> commandBuffer, BlockComponentChunk blockComponentChunk, Ref<ChunkStore> blockRef, TilledSoilBlock soilBlock) {
/* 241 */       BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo)commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
/* 242 */       assert info != null;
/*     */       
/* 244 */       int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
/* 245 */       int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
/* 246 */       int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
/*     */       
/* 248 */       if (y >= 320)
/*     */         return; 
/* 250 */       int checkIndex = ChunkUtil.indexBlockInColumn(x, y + 1, z);
/* 251 */       Ref<ChunkStore> aboveBlockRef = blockComponentChunk.getEntityReference(checkIndex);
/*     */       
/* 253 */       boolean hasCrop = false;
/* 254 */       if (aboveBlockRef != null) {
/* 255 */         FarmingBlock farmingBlock = (FarmingBlock)commandBuffer.getComponent(aboveBlockRef, FarmingBlock.getComponentType());
/* 256 */         hasCrop = (farmingBlock != null);
/*     */       } 
/*     */       
/* 259 */       assert info.getChunkRef() != null;
/* 260 */       BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
/* 261 */       assert blockChunk != null;
/* 262 */       BlockSection blockSection = blockChunk.getSectionAtBlockY(y);
/*     */       
/* 264 */       BlockType blockType = (BlockType)BlockType.getAssetMap().getAsset(blockSection.get(x, y, z));
/* 265 */       Instant currentTime = ((WorldTimeResource)((ChunkStore)commandBuffer.getExternalData()).getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType())).getGameTime();
/*     */       
/* 267 */       Instant decayTime = soilBlock.getDecayTime();
/*     */ 
/*     */       
/* 270 */       if (soilBlock.isPlanted() && !hasCrop) {
/* 271 */         if (!FarmingSystems.updateSoilDecayTime(commandBuffer, soilBlock, blockType))
/* 272 */           return;  if (decayTime != null) {
/* 273 */           blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), decayTime);
/*     */         }
/* 275 */       } else if (!soilBlock.isPlanted() && !hasCrop) {
/*     */         
/* 277 */         if (decayTime == null || !decayTime.isAfter(currentTime)) {
/* 278 */           assert info.getChunkRef() != null;
/*     */           
/* 280 */           if (blockType == null || blockType.getFarming() == null || blockType.getFarming().getSoilConfig() == null)
/* 281 */             return;  FarmingData.SoilConfig soilConfig = blockType.getFarming().getSoilConfig();
/* 282 */           String str = soilConfig.getTargetBlock();
/* 283 */           if (str == null)
/*     */             return; 
/* 285 */           int targetBlockId = BlockType.getAssetMap().getIndex(str);
/* 286 */           if (targetBlockId == Integer.MIN_VALUE)
/* 287 */             return;  BlockType targetBlockType = (BlockType)BlockType.getAssetMap().getAsset(targetBlockId);
/*     */           
/* 289 */           int rotation = blockSection.getRotationIndex(x, y, z);
/*     */           
/* 291 */           WorldChunk worldChunk = (WorldChunk)commandBuffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
/* 292 */           commandBuffer.run(_store -> worldChunk.setBlock(x, y, z, targetBlockId, targetBlockType, rotation, 0, 0));
/*     */ 
/*     */           
/*     */           return;
/*     */         } 
/* 297 */       } else if (hasCrop) {
/* 298 */         soilBlock.setDecayTime(null);
/*     */       } 
/*     */       
/* 301 */       String targetBlock = soilBlock.computeBlockType(currentTime, blockType);
/* 302 */       if (targetBlock != null && !targetBlock.equals(blockType.getId())) {
/* 303 */         WorldChunk worldChunk = (WorldChunk)commandBuffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
/* 304 */         int rotation = blockSection.getRotationIndex(x, y, z);
/* 305 */         int targetBlockId = BlockType.getAssetMap().getIndex(targetBlock);
/* 306 */         BlockType targetBlockType = (BlockType)BlockType.getAssetMap().getAsset(targetBlockId);
/* 307 */         commandBuffer.run(_store -> worldChunk.setBlock(x, y, z, targetBlockId, targetBlockType, rotation, 0, 2));
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 312 */       soilBlock.setPlanted(hasCrop);
/*     */     }
/*     */     
/*     */     private static void tickCoop(CommandBuffer<ChunkStore> commandBuffer, BlockComponentChunk blockComponentChunk, Ref<ChunkStore> blockRef, CoopBlock coopBlock) {
/* 316 */       BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo)commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
/* 317 */       assert info != null;
/*     */       
/* 319 */       Store<EntityStore> store = ((ChunkStore)commandBuffer.getExternalData()).getWorld().getEntityStore().getStore();
/* 320 */       WorldTimeResource worldTimeResource = (WorldTimeResource)store.getResource(WorldTimeResource.getResourceType());
/* 321 */       FarmingCoopAsset coopAsset = coopBlock.getCoopAsset();
/* 322 */       if (coopAsset == null)
/*     */         return; 
/* 324 */       int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
/* 325 */       int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
/* 326 */       int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
/*     */       
/* 328 */       BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
/* 329 */       assert blockChunk != null;
/*     */       
/* 331 */       ChunkColumn column = (ChunkColumn)commandBuffer.getComponent(info.getChunkRef(), ChunkColumn.getComponentType());
/* 332 */       assert column != null;
/* 333 */       Ref<ChunkStore> sectionRef = column.getSection(ChunkUtil.chunkCoordinate(y));
/* 334 */       assert sectionRef != null;
/* 335 */       BlockSection blockSection = (BlockSection)commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
/* 336 */       assert blockSection != null;
/* 337 */       ChunkSection chunkSection = (ChunkSection)commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
/* 338 */       assert chunkSection != null;
/*     */       
/* 340 */       int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), x);
/* 341 */       int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), y);
/* 342 */       int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), z);
/*     */       
/* 344 */       World world = ((ChunkStore)commandBuffer.getExternalData()).getWorld();
/* 345 */       WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(worldX, worldZ));
/* 346 */       double blockRotation = chunk.getRotation(worldX, worldY, worldZ).yaw().getRadians();
/* 347 */       Vector3d spawnOffset = (new Vector3d()).assign(coopAsset.getResidentSpawnOffset()).rotateY((float)blockRotation);
/* 348 */       Vector3i coopLocation = new Vector3i(worldX, worldY, worldZ);
/*     */ 
/*     */       
/* 351 */       boolean tryCapture = coopAsset.getCaptureWildNPCsInRange();
/* 352 */       float captureRange = coopAsset.getWildCaptureRadius();
/* 353 */       if (tryCapture && captureRange >= 0.0F) {
/* 354 */         world.execute(() -> {
/*     */               List<Ref<EntityStore>> entities = TargetUtil.getAllEntitiesInSphere(coopLocation.toVector3d(), captureRange, (ComponentAccessor)store);
/*     */ 
/*     */ 
/*     */               
/*     */               for (Ref<EntityStore> entity : entities) {
/*     */                 coopBlock.tryPutWildResidentFromWild(store, entity, worldTimeResource, coopLocation);
/*     */               }
/*     */             });
/*     */       }
/*     */ 
/*     */       
/* 366 */       if (coopBlock.shouldResidentsBeInCoop(worldTimeResource)) {
/* 367 */         world.execute(() -> coopBlock.ensureNoResidentsInWorld(store));
/*     */       } else {
/* 369 */         world.execute(() -> {
/*     */               coopBlock.ensureSpawnResidentsInWorld(world, store, coopLocation.toVector3d(), spawnOffset);
/*     */               
/*     */               coopBlock.generateProduceToInventory(worldTimeResource);
/*     */               
/*     */               Vector3i blockPos = new Vector3i(worldX, worldY, worldZ);
/*     */               
/*     */               BlockType currentBlockType = world.getBlockType(blockPos);
/*     */               
/*     */               assert currentBlockType != null;
/*     */               chunk.setBlockInteractionState(blockPos, currentBlockType, coopBlock.hasProduce() ? "Produce_Ready" : "default");
/*     */             });
/*     */       } 
/* 382 */       Instant nextTickInstant = coopBlock.getNextScheduledTick(worldTimeResource);
/* 383 */       if (nextTickInstant != null) {
/* 384 */         blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), nextTickInstant);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Query<ChunkStore> getQuery() {
/* 391 */       return QUERY;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class OnCoopAdded
/*     */     extends RefSystem<ChunkStore> {
/* 397 */     private static final Query<ChunkStore> QUERY = (Query<ChunkStore>)Query.and(new Query[] {
/* 398 */           (Query)BlockModule.BlockStateInfo.getComponentType(), 
/* 399 */           (Query)CoopBlock.getComponentType()
/*     */         });
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/* 404 */       CoopBlock coopBlock = (CoopBlock)commandBuffer.getComponent(ref, CoopBlock.getComponentType());
/* 405 */       if (coopBlock == null)
/*     */         return; 
/* 407 */       WorldTimeResource worldTimeResource = (WorldTimeResource)((ChunkStore)commandBuffer.getExternalData()).getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());
/*     */       
/* 409 */       BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo)commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
/* 410 */       assert info != null;
/*     */       
/* 412 */       int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
/* 413 */       int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
/* 414 */       int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
/*     */       
/* 416 */       BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
/* 417 */       assert blockChunk != null;
/* 418 */       BlockSection blockSection = blockChunk.getSectionAtBlockY(y);
/*     */       
/* 420 */       blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), coopBlock.getNextScheduledTick(worldTimeResource));
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/* 425 */       if (reason == RemoveReason.UNLOAD) {
/*     */         return;
/*     */       }
/*     */       
/* 429 */       CoopBlock coop = (CoopBlock)commandBuffer.getComponent(ref, CoopBlock.getComponentType());
/* 430 */       if (coop == null) {
/*     */         return;
/*     */       }
/*     */       
/* 434 */       BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo)commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
/* 435 */       assert info != null;
/*     */       
/* 437 */       Store<EntityStore> entityStore = ((ChunkStore)commandBuffer.getExternalData()).getWorld().getEntityStore().getStore();
/*     */       
/* 439 */       int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
/* 440 */       int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
/* 441 */       int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
/*     */       
/* 443 */       BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
/* 444 */       assert blockChunk != null;
/*     */       
/* 446 */       ChunkColumn column = (ChunkColumn)commandBuffer.getComponent(info.getChunkRef(), ChunkColumn.getComponentType());
/* 447 */       assert column != null;
/* 448 */       Ref<ChunkStore> sectionRef = column.getSection(ChunkUtil.chunkCoordinate(y));
/* 449 */       assert sectionRef != null;
/* 450 */       BlockSection blockSection = (BlockSection)commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
/* 451 */       assert blockSection != null;
/* 452 */       ChunkSection chunkSection = (ChunkSection)commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
/* 453 */       assert chunkSection != null;
/*     */       
/* 455 */       int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), x);
/* 456 */       int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), y);
/* 457 */       int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), z);
/*     */       
/* 459 */       World world = ((ChunkStore)commandBuffer.getExternalData()).getWorld();
/* 460 */       WorldTimeResource worldTimeResource = (WorldTimeResource)world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());
/* 461 */       coop.handleBlockBroken(world, worldTimeResource, entityStore, worldX, worldY, worldZ);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Query<ChunkStore> getQuery() {
/* 467 */       return QUERY;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class CoopResidentEntitySystem extends RefSystem<EntityStore> {
/* 472 */     private static final ComponentType<EntityStore, CoopResidentComponent> componentType = CoopResidentComponent.getComponentType();
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/* 476 */       return (Query)componentType;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 486 */       if (reason == RemoveReason.UNLOAD) {
/*     */         return;
/*     */       }
/*     */       
/* 490 */       UUIDComponent uuidComponent = (UUIDComponent)commandBuffer.getComponent(ref, UUIDComponent.getComponentType());
/* 491 */       if (uuidComponent == null) {
/*     */         return;
/*     */       }
/*     */       
/* 495 */       UUID uuid = uuidComponent.getUuid();
/*     */       
/* 497 */       CoopResidentComponent coopResidentComponent = (CoopResidentComponent)commandBuffer.getComponent(ref, componentType);
/* 498 */       if (coopResidentComponent == null) {
/*     */         return;
/*     */       }
/*     */       
/* 502 */       Vector3i coopPosition = coopResidentComponent.getCoopLocation();
/* 503 */       World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/*     */       
/* 505 */       long chunkIndex = ChunkUtil.indexChunkFromBlock(coopPosition.x, coopPosition.z);
/* 506 */       WorldChunk chunk = world.getChunkIfLoaded(chunkIndex);
/* 507 */       if (chunk == null) {
/*     */         return;
/*     */       }
/*     */       
/* 511 */       Ref<ChunkStore> chunkReference = world.getChunkStore().getChunkReference(chunkIndex);
/* 512 */       if (chunkReference == null || !chunkReference.isValid()) {
/*     */         return;
/*     */       }
/*     */       
/* 516 */       Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
/*     */       
/* 518 */       ChunkColumn chunkColumnComponent = (ChunkColumn)chunkStore.getComponent(chunkReference, ChunkColumn.getComponentType());
/* 519 */       if (chunkColumnComponent == null) {
/*     */         return;
/*     */       }
/*     */       
/* 523 */       BlockChunk blockChunkComponent = (BlockChunk)chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
/* 524 */       if (blockChunkComponent == null) {
/*     */         return;
/*     */       }
/*     */       
/* 528 */       Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(coopPosition.y));
/* 529 */       if (sectionRef == null || !sectionRef.isValid()) {
/*     */         return;
/*     */       }
/*     */       
/* 533 */       BlockComponentChunk blockComponentChunk = (BlockComponentChunk)chunkStore.getComponent(chunkReference, BlockComponentChunk.getComponentType());
/* 534 */       if (blockComponentChunk == null) {
/*     */         return;
/*     */       }
/*     */       
/* 538 */       int blockIndexColumn = ChunkUtil.indexBlockInColumn(coopPosition.x, coopPosition.y, coopPosition.z);
/* 539 */       Ref<ChunkStore> coopEntityReference = blockComponentChunk.getEntityReference(blockIndexColumn);
/* 540 */       if (coopEntityReference == null) {
/*     */         return;
/*     */       }
/*     */       
/* 544 */       CoopBlock coop = (CoopBlock)chunkStore.getComponent(coopEntityReference, CoopBlock.getComponentType());
/* 545 */       if (coop == null) {
/*     */         return;
/*     */       }
/*     */       
/* 549 */       coop.handleResidentDespawn(uuid);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class CoopResidentTicking
/*     */     extends EntityTickingSystem<EntityStore> {
/* 555 */     private static final ComponentType<EntityStore, CoopResidentComponent> componentType = CoopResidentComponent.getComponentType();
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/* 559 */       return (Query)componentType;
/*     */     }
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 564 */       CoopResidentComponent coopResidentComponent = (CoopResidentComponent)archetypeChunk.getComponent(index, CoopResidentComponent.getComponentType());
/* 565 */       if (coopResidentComponent == null) {
/*     */         return;
/*     */       }
/*     */       
/* 569 */       if (coopResidentComponent.getMarkedForDespawn()) {
/* 570 */         commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean updateSoilDecayTime(CommandBuffer<ChunkStore> commandBuffer, TilledSoilBlock soilBlock, BlockType blockType) {
/* 576 */     if (blockType == null || blockType.getFarming() == null || blockType.getFarming().getSoilConfig() == null) return false; 
/* 577 */     FarmingData.SoilConfig soilConfig = blockType.getFarming().getSoilConfig();
/* 578 */     Rangef range = soilConfig.getLifetime();
/* 579 */     if (range == null) return false;
/*     */     
/* 581 */     double baseDuration = range.min + (range.max - range.min) * ThreadLocalRandom.current().nextDouble();
/*     */     
/* 583 */     Instant currentTime = ((WorldTimeResource)((ChunkStore)commandBuffer.getExternalData()).getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType())).getGameTime();
/* 584 */     Instant endTime = currentTime.plus(Math.round(baseDuration), ChronoUnit.SECONDS);
/* 585 */     soilBlock.setDecayTime(endTime);
/* 586 */     return true;
/*     */   }
/*     */   
/*     */   @Deprecated(forRemoval = true)
/*     */   public static class MigrateFarming
/*     */     extends BlockModule.MigrationSystem
/*     */   {
/*     */     public void onEntityAdd(@Nonnull Holder<ChunkStore> holder, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store) {
/* 594 */       FarmingBlockState oldState = (FarmingBlockState)holder.getComponent(FarmingPlugin.get().getFarmingBlockStateComponentType());
/* 595 */       FarmingBlock farming = new FarmingBlock();
/* 596 */       farming.setGrowthProgress(oldState.getCurrentFarmingStageIndex());
/* 597 */       farming.setCurrentStageSet(oldState.getCurrentFarmingStageSetName());
/* 598 */       farming.setSpreadRate(oldState.getSpreadRate());
/* 599 */       holder.putComponent(FarmingBlock.getComponentType(), (Component)farming);
/* 600 */       holder.removeComponent(FarmingPlugin.get().getFarmingBlockStateComponentType());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemoved(@Nonnull Holder<ChunkStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store) {}
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Query<ChunkStore> getQuery() {
/* 611 */       return (Query)FarmingPlugin.get().getFarmingBlockStateComponentType();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\farming\FarmingSystems.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */