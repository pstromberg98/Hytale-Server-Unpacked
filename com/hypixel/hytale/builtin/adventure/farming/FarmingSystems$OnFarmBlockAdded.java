/*     */ package com.hypixel.hytale.builtin.adventure.farming;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.RefSystem;
/*     */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
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
/*     */ public class OnFarmBlockAdded
/*     */   extends RefSystem<ChunkStore>
/*     */ {
/*  96 */   private static final Query<ChunkStore> QUERY = (Query<ChunkStore>)Query.and(new Query[] {
/*  97 */         (Query)BlockModule.BlockStateInfo.getComponentType(), 
/*  98 */         (Query)FarmingBlock.getComponentType()
/*     */       });
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
/*     */   public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/*     */     // Byte code:
/*     */     //   0: aload #4
/*     */     //   2: aload_1
/*     */     //   3: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   6: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   9: checkcast com/hypixel/hytale/builtin/adventure/farming/states/FarmingBlock
/*     */     //   12: astore #5
/*     */     //   14: getstatic com/hypixel/hytale/builtin/adventure/farming/FarmingSystems$OnFarmBlockAdded.$assertionsDisabled : Z
/*     */     //   17: ifne -> 33
/*     */     //   20: aload #5
/*     */     //   22: ifnonnull -> 33
/*     */     //   25: new java/lang/AssertionError
/*     */     //   28: dup
/*     */     //   29: invokespecial <init> : ()V
/*     */     //   32: athrow
/*     */     //   33: aload #4
/*     */     //   35: aload_1
/*     */     //   36: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   39: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   42: checkcast com/hypixel/hytale/server/core/modules/block/BlockModule$BlockStateInfo
/*     */     //   45: astore #6
/*     */     //   47: getstatic com/hypixel/hytale/builtin/adventure/farming/FarmingSystems$OnFarmBlockAdded.$assertionsDisabled : Z
/*     */     //   50: ifne -> 66
/*     */     //   53: aload #6
/*     */     //   55: ifnonnull -> 66
/*     */     //   58: new java/lang/AssertionError
/*     */     //   61: dup
/*     */     //   62: invokespecial <init> : ()V
/*     */     //   65: athrow
/*     */     //   66: aload #4
/*     */     //   68: aload #6
/*     */     //   70: invokevirtual getChunkRef : ()Lcom/hypixel/hytale/component/Ref;
/*     */     //   73: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   76: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   79: checkcast com/hypixel/hytale/server/core/universe/world/chunk/BlockChunk
/*     */     //   82: astore #7
/*     */     //   84: aload #5
/*     */     //   86: invokevirtual getLastTickGameTime : ()Ljava/time/Instant;
/*     */     //   89: ifnonnull -> 483
/*     */     //   92: aload #7
/*     */     //   94: aload #6
/*     */     //   96: invokevirtual getIndex : ()I
/*     */     //   99: invokestatic xFromBlockInColumn : (I)I
/*     */     //   102: aload #6
/*     */     //   104: invokevirtual getIndex : ()I
/*     */     //   107: invokestatic yFromBlockInColumn : (I)I
/*     */     //   110: aload #6
/*     */     //   112: invokevirtual getIndex : ()I
/*     */     //   115: invokestatic zFromBlockInColumn : (I)I
/*     */     //   118: invokevirtual getBlock : (III)I
/*     */     //   121: istore #8
/*     */     //   123: invokestatic getAssetMap : ()Lcom/hypixel/hytale/assetstore/map/BlockTypeAssetMap;
/*     */     //   126: iload #8
/*     */     //   128: invokevirtual getAsset : (I)Lcom/hypixel/hytale/assetstore/map/JsonAssetWithMap;
/*     */     //   131: checkcast com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockType
/*     */     //   134: astore #9
/*     */     //   136: aload #9
/*     */     //   138: invokevirtual getFarming : ()Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingData;
/*     */     //   141: ifnonnull -> 145
/*     */     //   144: return
/*     */     //   145: aload #5
/*     */     //   147: aload #9
/*     */     //   149: invokevirtual getFarming : ()Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingData;
/*     */     //   152: invokevirtual getStartingStageSet : ()Ljava/lang/String;
/*     */     //   155: invokevirtual setCurrentStageSet : (Ljava/lang/String;)V
/*     */     //   158: aload #5
/*     */     //   160: aload_3
/*     */     //   161: invokevirtual getExternalData : ()Ljava/lang/Object;
/*     */     //   164: checkcast com/hypixel/hytale/server/core/universe/world/storage/ChunkStore
/*     */     //   167: invokevirtual getWorld : ()Lcom/hypixel/hytale/server/core/universe/world/World;
/*     */     //   170: invokevirtual getEntityStore : ()Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;
/*     */     //   173: invokevirtual getStore : ()Lcom/hypixel/hytale/component/Store;
/*     */     //   176: invokestatic getResourceType : ()Lcom/hypixel/hytale/component/ResourceType;
/*     */     //   179: invokevirtual getResource : (Lcom/hypixel/hytale/component/ResourceType;)Lcom/hypixel/hytale/component/Resource;
/*     */     //   182: checkcast com/hypixel/hytale/server/core/modules/time/WorldTimeResource
/*     */     //   185: invokevirtual getGameTime : ()Ljava/time/Instant;
/*     */     //   188: invokevirtual setLastTickGameTime : (Ljava/time/Instant;)V
/*     */     //   191: aload #7
/*     */     //   193: invokevirtual markNeedsSaving : ()V
/*     */     //   196: aload #9
/*     */     //   198: invokevirtual getFarming : ()Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingData;
/*     */     //   201: invokevirtual getStages : ()Ljava/util/Map;
/*     */     //   204: ifnull -> 483
/*     */     //   207: aload #9
/*     */     //   209: invokevirtual getFarming : ()Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingData;
/*     */     //   212: invokevirtual getStages : ()Ljava/util/Map;
/*     */     //   215: aload #9
/*     */     //   217: invokevirtual getFarming : ()Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingData;
/*     */     //   220: invokevirtual getStartingStageSet : ()Ljava/lang/String;
/*     */     //   223: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   228: checkcast [Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingStageData;
/*     */     //   231: astore #10
/*     */     //   233: aload #10
/*     */     //   235: ifnull -> 483
/*     */     //   238: aload #10
/*     */     //   240: arraylength
/*     */     //   241: ifle -> 483
/*     */     //   244: iconst_0
/*     */     //   245: istore #11
/*     */     //   247: iconst_0
/*     */     //   248: istore #12
/*     */     //   250: iload #12
/*     */     //   252: aload #10
/*     */     //   254: arraylength
/*     */     //   255: if_icmpge -> 409
/*     */     //   258: aload #10
/*     */     //   260: iload #12
/*     */     //   262: aaload
/*     */     //   263: astore #13
/*     */     //   265: aload #13
/*     */     //   267: dup
/*     */     //   268: invokestatic requireNonNull : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   271: pop
/*     */     //   272: astore #14
/*     */     //   274: iconst_0
/*     */     //   275: istore #15
/*     */     //   277: aload #14
/*     */     //   279: iload #15
/*     */     //   281: <illegal opcode> typeSwitch : (Ljava/lang/Object;I)I
/*     */     //   286: lookupswitch default -> 403, 0 -> 312, 1 -> 349
/*     */     //   312: aload #14
/*     */     //   314: checkcast com/hypixel/hytale/builtin/adventure/farming/config/stages/BlockTypeFarmingStageData
/*     */     //   317: astore #16
/*     */     //   319: aload #16
/*     */     //   321: invokevirtual getBlock : ()Ljava/lang/String;
/*     */     //   324: aload #9
/*     */     //   326: invokevirtual getId : ()Ljava/lang/String;
/*     */     //   329: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   332: ifeq -> 403
/*     */     //   335: aload #5
/*     */     //   337: iload #12
/*     */     //   339: i2f
/*     */     //   340: invokevirtual setGrowthProgress : (F)V
/*     */     //   343: iconst_1
/*     */     //   344: istore #11
/*     */     //   346: goto -> 403
/*     */     //   349: aload #14
/*     */     //   351: checkcast com/hypixel/hytale/builtin/adventure/farming/config/stages/BlockStateFarmingStageData
/*     */     //   354: astore #17
/*     */     //   356: aload #9
/*     */     //   358: aload #17
/*     */     //   360: invokevirtual getState : ()Ljava/lang/String;
/*     */     //   363: invokevirtual getBlockForState : (Ljava/lang/String;)Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/BlockType;
/*     */     //   366: astore #18
/*     */     //   368: aload #18
/*     */     //   370: ifnull -> 400
/*     */     //   373: aload #18
/*     */     //   375: invokevirtual getId : ()Ljava/lang/String;
/*     */     //   378: aload #9
/*     */     //   380: invokevirtual getId : ()Ljava/lang/String;
/*     */     //   383: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   386: ifeq -> 400
/*     */     //   389: aload #5
/*     */     //   391: iload #12
/*     */     //   393: i2f
/*     */     //   394: invokevirtual setGrowthProgress : (F)V
/*     */     //   397: iconst_1
/*     */     //   398: istore #11
/*     */     //   400: goto -> 403
/*     */     //   403: iinc #12, 1
/*     */     //   406: goto -> 250
/*     */     //   409: iload #11
/*     */     //   411: ifne -> 483
/*     */     //   414: aload #4
/*     */     //   416: aload #6
/*     */     //   418: invokevirtual getChunkRef : ()Lcom/hypixel/hytale/component/Ref;
/*     */     //   421: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   424: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   427: checkcast com/hypixel/hytale/server/core/universe/world/chunk/ChunkColumn
/*     */     //   430: aload #6
/*     */     //   432: invokevirtual getIndex : ()I
/*     */     //   435: invokestatic yFromBlockInColumn : (I)I
/*     */     //   438: invokestatic chunkCoordinate : (I)I
/*     */     //   441: invokevirtual getSection : (I)Lcom/hypixel/hytale/component/Ref;
/*     */     //   444: astore #12
/*     */     //   446: aload #10
/*     */     //   448: iconst_0
/*     */     //   449: aaload
/*     */     //   450: aload #4
/*     */     //   452: aload #12
/*     */     //   454: aload_1
/*     */     //   455: aload #6
/*     */     //   457: invokevirtual getIndex : ()I
/*     */     //   460: invokestatic xFromBlockInColumn : (I)I
/*     */     //   463: aload #6
/*     */     //   465: invokevirtual getIndex : ()I
/*     */     //   468: invokestatic yFromBlockInColumn : (I)I
/*     */     //   471: aload #6
/*     */     //   473: invokevirtual getIndex : ()I
/*     */     //   476: invokestatic zFromBlockInColumn : (I)I
/*     */     //   479: aconst_null
/*     */     //   480: invokevirtual apply : (Lcom/hypixel/hytale/component/ComponentAccessor;Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/Ref;IIILcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingStageData;)V
/*     */     //   483: aload #5
/*     */     //   485: invokevirtual getLastTickGameTime : ()Ljava/time/Instant;
/*     */     //   488: ifnonnull -> 529
/*     */     //   491: aload #5
/*     */     //   493: aload_3
/*     */     //   494: invokevirtual getExternalData : ()Ljava/lang/Object;
/*     */     //   497: checkcast com/hypixel/hytale/server/core/universe/world/storage/ChunkStore
/*     */     //   500: invokevirtual getWorld : ()Lcom/hypixel/hytale/server/core/universe/world/World;
/*     */     //   503: invokevirtual getEntityStore : ()Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;
/*     */     //   506: invokevirtual getStore : ()Lcom/hypixel/hytale/component/Store;
/*     */     //   509: invokestatic getResourceType : ()Lcom/hypixel/hytale/component/ResourceType;
/*     */     //   512: invokevirtual getResource : (Lcom/hypixel/hytale/component/ResourceType;)Lcom/hypixel/hytale/component/Resource;
/*     */     //   515: checkcast com/hypixel/hytale/server/core/modules/time/WorldTimeResource
/*     */     //   518: invokevirtual getGameTime : ()Ljava/time/Instant;
/*     */     //   521: invokevirtual setLastTickGameTime : (Ljava/time/Instant;)V
/*     */     //   524: aload #7
/*     */     //   526: invokevirtual markNeedsSaving : ()V
/*     */     //   529: aload #6
/*     */     //   531: invokevirtual getIndex : ()I
/*     */     //   534: invokestatic xFromBlockInColumn : (I)I
/*     */     //   537: istore #8
/*     */     //   539: aload #6
/*     */     //   541: invokevirtual getIndex : ()I
/*     */     //   544: invokestatic yFromBlockInColumn : (I)I
/*     */     //   547: istore #9
/*     */     //   549: aload #6
/*     */     //   551: invokevirtual getIndex : ()I
/*     */     //   554: invokestatic zFromBlockInColumn : (I)I
/*     */     //   557: istore #10
/*     */     //   559: aload #4
/*     */     //   561: aload #6
/*     */     //   563: invokevirtual getChunkRef : ()Lcom/hypixel/hytale/component/Ref;
/*     */     //   566: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   569: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   572: checkcast com/hypixel/hytale/server/core/universe/world/chunk/BlockComponentChunk
/*     */     //   575: astore #11
/*     */     //   577: getstatic com/hypixel/hytale/builtin/adventure/farming/FarmingSystems$OnFarmBlockAdded.$assertionsDisabled : Z
/*     */     //   580: ifne -> 596
/*     */     //   583: aload #11
/*     */     //   585: ifnonnull -> 596
/*     */     //   588: new java/lang/AssertionError
/*     */     //   591: dup
/*     */     //   592: invokespecial <init> : ()V
/*     */     //   595: athrow
/*     */     //   596: aload #4
/*     */     //   598: aload #6
/*     */     //   600: invokevirtual getChunkRef : ()Lcom/hypixel/hytale/component/Ref;
/*     */     //   603: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   606: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   609: checkcast com/hypixel/hytale/server/core/universe/world/chunk/ChunkColumn
/*     */     //   612: astore #12
/*     */     //   614: getstatic com/hypixel/hytale/builtin/adventure/farming/FarmingSystems$OnFarmBlockAdded.$assertionsDisabled : Z
/*     */     //   617: ifne -> 633
/*     */     //   620: aload #12
/*     */     //   622: ifnonnull -> 633
/*     */     //   625: new java/lang/AssertionError
/*     */     //   628: dup
/*     */     //   629: invokespecial <init> : ()V
/*     */     //   632: athrow
/*     */     //   633: aload #12
/*     */     //   635: iload #9
/*     */     //   637: invokestatic chunkCoordinate : (I)I
/*     */     //   640: invokevirtual getSection : (I)Lcom/hypixel/hytale/component/Ref;
/*     */     //   643: astore #13
/*     */     //   645: aload #4
/*     */     //   647: aload #13
/*     */     //   649: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   652: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   655: checkcast com/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection
/*     */     //   658: astore #14
/*     */     //   660: aload #4
/*     */     //   662: aload #7
/*     */     //   664: aload #14
/*     */     //   666: aload #13
/*     */     //   668: aload_1
/*     */     //   669: aload #5
/*     */     //   671: iload #8
/*     */     //   673: iload #9
/*     */     //   675: iload #10
/*     */     //   677: iconst_1
/*     */     //   678: invokestatic tickFarming : (Lcom/hypixel/hytale/component/CommandBuffer;Lcom/hypixel/hytale/server/core/universe/world/chunk/BlockChunk;Lcom/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection;Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/builtin/adventure/farming/states/FarmingBlock;IIIZ)V
/*     */     //   681: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #103	-> 0
/*     */     //   #104	-> 14
/*     */     //   #105	-> 33
/*     */     //   #106	-> 47
/*     */     //   #109	-> 66
/*     */     //   #110	-> 84
/*     */     //   #111	-> 92
/*     */     //   #112	-> 96
/*     */     //   #113	-> 104
/*     */     //   #114	-> 112
/*     */     //   #111	-> 118
/*     */     //   #116	-> 123
/*     */     //   #117	-> 136
/*     */     //   #118	-> 145
/*     */     //   #120	-> 158
/*     */     //   #121	-> 191
/*     */     //   #123	-> 196
/*     */     //   #124	-> 207
/*     */     //   #125	-> 233
/*     */     //   #127	-> 244
/*     */     //   #128	-> 247
/*     */     //   #129	-> 258
/*     */     //   #130	-> 265
/*     */     //   #131	-> 312
/*     */     //   #132	-> 319
/*     */     //   #133	-> 335
/*     */     //   #134	-> 343
/*     */     //   #137	-> 349
/*     */     //   #138	-> 356
/*     */     //   #139	-> 368
/*     */     //   #140	-> 389
/*     */     //   #141	-> 397
/*     */     //   #143	-> 400
/*     */     //   #128	-> 403
/*     */     //   #149	-> 409
/*     */     //   #150	-> 414
/*     */     //   #151	-> 446
/*     */     //   #152	-> 457
/*     */     //   #153	-> 465
/*     */     //   #154	-> 473
/*     */     //   #151	-> 480
/*     */     //   #161	-> 483
/*     */     //   #162	-> 491
/*     */     //   #163	-> 524
/*     */     //   #166	-> 529
/*     */     //   #167	-> 539
/*     */     //   #168	-> 549
/*     */     //   #170	-> 559
/*     */     //   #171	-> 577
/*     */     //   #172	-> 596
/*     */     //   #173	-> 614
/*     */     //   #174	-> 633
/*     */     //   #175	-> 645
/*     */     //   #177	-> 660
/*     */     //   #178	-> 681
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   319	30	16	data	Lcom/hypixel/hytale/builtin/adventure/farming/config/stages/BlockTypeFarmingStageData;
/*     */     //   368	32	18	stateBlockType	Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/BlockType;
/*     */     //   356	47	17	data	Lcom/hypixel/hytale/builtin/adventure/farming/config/stages/BlockStateFarmingStageData;
/*     */     //   265	138	13	stage	Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingStageData;
/*     */     //   250	159	12	i	I
/*     */     //   446	37	12	sectionRef	Lcom/hypixel/hytale/component/Ref;
/*     */     //   247	236	11	found	Z
/*     */     //   233	250	10	stages	[Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/farming/FarmingStageData;
/*     */     //   123	360	8	blockId	I
/*     */     //   136	347	9	blockType	Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/BlockType;
/*     */     //   0	682	0	this	Lcom/hypixel/hytale/builtin/adventure/farming/FarmingSystems$OnFarmBlockAdded;
/*     */     //   0	682	1	ref	Lcom/hypixel/hytale/component/Ref;
/*     */     //   0	682	2	reason	Lcom/hypixel/hytale/component/AddReason;
/*     */     //   0	682	3	store	Lcom/hypixel/hytale/component/Store;
/*     */     //   0	682	4	commandBuffer	Lcom/hypixel/hytale/component/CommandBuffer;
/*     */     //   14	668	5	farmingBlock	Lcom/hypixel/hytale/builtin/adventure/farming/states/FarmingBlock;
/*     */     //   47	635	6	info	Lcom/hypixel/hytale/server/core/modules/block/BlockModule$BlockStateInfo;
/*     */     //   84	598	7	blockChunk	Lcom/hypixel/hytale/server/core/universe/world/chunk/BlockChunk;
/*     */     //   539	143	8	x	I
/*     */     //   549	133	9	y	I
/*     */     //   559	123	10	z	I
/*     */     //   577	105	11	blockComponentChunk	Lcom/hypixel/hytale/server/core/universe/world/chunk/BlockComponentChunk;
/*     */     //   614	68	12	column	Lcom/hypixel/hytale/server/core/universe/world/chunk/ChunkColumn;
/*     */     //   645	37	13	section	Lcom/hypixel/hytale/component/Ref;
/*     */     //   660	22	14	blockSection	Lcom/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   446	37	12	sectionRef	Lcom/hypixel/hytale/component/Ref<Lcom/hypixel/hytale/server/core/universe/world/storage/ChunkStore;>;
/*     */     //   0	682	1	ref	Lcom/hypixel/hytale/component/Ref<Lcom/hypixel/hytale/server/core/universe/world/storage/ChunkStore;>;
/*     */     //   0	682	3	store	Lcom/hypixel/hytale/component/Store<Lcom/hypixel/hytale/server/core/universe/world/storage/ChunkStore;>;
/*     */     //   0	682	4	commandBuffer	Lcom/hypixel/hytale/component/CommandBuffer<Lcom/hypixel/hytale/server/core/universe/world/storage/ChunkStore;>;
/*     */     //   645	37	13	section	Lcom/hypixel/hytale/component/Ref<Lcom/hypixel/hytale/server/core/universe/world/storage/ChunkStore;>;
/*     */   }
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
/*     */   public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {}
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
/*     */   @Nullable
/*     */   public Query<ChunkStore> getQuery() {
/* 187 */     return QUERY;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\farming\FarmingSystems$OnFarmBlockAdded.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */