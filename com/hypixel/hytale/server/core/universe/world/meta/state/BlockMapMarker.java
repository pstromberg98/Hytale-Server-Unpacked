/*     */ package com.hypixel.hytale.server.core.universe.world.meta.state;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.ResourceType;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.RefSystem;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.Direction;
/*     */ import com.hypixel.hytale.protocol.Position;
/*     */ import com.hypixel.hytale.protocol.Transform;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*     */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*     */ import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class BlockMapMarker
/*     */   implements Component<ChunkStore>
/*     */ {
/*     */   public static final BuilderCodec<BlockMapMarker> CODEC;
/*     */   private String name;
/*     */   private String icon;
/*     */   
/*     */   static {
/*  44 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockMapMarker.class, BlockMapMarker::new).append(new KeyedCodec("Name", (Codec)Codec.STRING), (o, v) -> o.name = v, o -> o.name).addValidator(Validators.nonNull()).add()).append(new KeyedCodec("Icon", (Codec)Codec.STRING), (o, v) -> o.icon = v, o -> o.icon).addValidator(Validators.nonNull()).add()).build();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockMapMarker() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockMapMarker(String name, String icon) {
/*  54 */     this.name = name;
/*  55 */     this.icon = icon;
/*     */   }
/*     */   
/*     */   public static ComponentType<ChunkStore, BlockMapMarker> getComponentType() {
/*  59 */     return BlockModule.get().getBlockMapMarkerComponentType();
/*     */   }
/*     */   
/*     */   public String getName() {
/*  63 */     return this.name;
/*     */   }
/*     */   
/*     */   public String getIcon() {
/*  67 */     return this.icon;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Component<ChunkStore> clone() {
/*  73 */     return new BlockMapMarker(this.name, this.icon);
/*     */   }
/*     */   
/*     */   public static class OnAddRemove extends RefSystem<ChunkStore> {
/*  77 */     private static final ComponentType<ChunkStore, BlockMapMarker> COMPONENT_TYPE = BlockMapMarker.getComponentType();
/*  78 */     private static final ResourceType<ChunkStore, BlockMapMarkersResource> BLOCK_MAP_MARKERS_RESOURCE_TYPE = BlockMapMarkersResource.getResourceType();
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/*  82 */       BlockModule.BlockStateInfo blockInfo = (BlockModule.BlockStateInfo)commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
/*  83 */       assert blockInfo != null;
/*  84 */       Ref<ChunkStore> chunkRef = blockInfo.getChunkRef();
/*  85 */       if (!chunkRef.isValid()) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/*  90 */       BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(chunkRef, BlockChunk.getComponentType());
/*  91 */       assert blockChunk != null;
/*     */       
/*  93 */       BlockMapMarker blockMapMarker = (BlockMapMarker)commandBuffer.getComponent(ref, COMPONENT_TYPE);
/*  94 */       assert blockMapMarker != null;
/*     */       
/*  96 */       WorldChunk wc = (WorldChunk)commandBuffer.getComponent(chunkRef, WorldChunk.getComponentType());
/*     */ 
/*     */ 
/*     */       
/* 100 */       Vector3i blockPosition = new Vector3i(ChunkUtil.worldCoordFromLocalCoord(wc.getX(), ChunkUtil.xFromBlockInColumn(blockInfo.getIndex())), ChunkUtil.yFromBlockInColumn(blockInfo.getIndex()), ChunkUtil.worldCoordFromLocalCoord(wc.getZ(), ChunkUtil.zFromBlockInColumn(blockInfo.getIndex())));
/*     */ 
/*     */       
/* 103 */       BlockMapMarkersResource resource = (BlockMapMarkersResource)commandBuffer.getResource(BLOCK_MAP_MARKERS_RESOURCE_TYPE);
/* 104 */       resource.addMarker(blockPosition, blockMapMarker.getName(), blockMapMarker.getIcon());
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/* 109 */       if (reason != RemoveReason.REMOVE)
/*     */         return; 
/* 111 */       BlockModule.BlockStateInfo blockInfo = (BlockModule.BlockStateInfo)commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
/* 112 */       assert blockInfo != null;
/* 113 */       Ref<ChunkStore> chunkRef = blockInfo.getChunkRef();
/* 114 */       if (!chunkRef.isValid()) {
/*     */         return;
/*     */       }
/*     */       
/* 118 */       WorldChunk wc = (WorldChunk)commandBuffer.getComponent(chunkRef, WorldChunk.getComponentType());
/*     */ 
/*     */ 
/*     */       
/* 122 */       Vector3i blockPosition = new Vector3i(ChunkUtil.worldCoordFromLocalCoord(wc.getX(), ChunkUtil.xFromBlockInColumn(blockInfo.getIndex())), ChunkUtil.yFromBlockInColumn(blockInfo.getIndex()), ChunkUtil.worldCoordFromLocalCoord(wc.getZ(), ChunkUtil.zFromBlockInColumn(blockInfo.getIndex())));
/*     */ 
/*     */       
/* 125 */       BlockMapMarkersResource resource = (BlockMapMarkersResource)commandBuffer.getResource(BLOCK_MAP_MARKERS_RESOURCE_TYPE);
/* 126 */       resource.removeMarker(blockPosition);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Query<ChunkStore> getQuery() {
/* 132 */       return (Query)COMPONENT_TYPE;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class MarkerProvider implements WorldMapManager.MarkerProvider {
/* 137 */     public static final MarkerProvider INSTANCE = new MarkerProvider();
/*     */ 
/*     */     
/*     */     public void update(World world, MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 141 */       BlockMapMarkersResource resource = (BlockMapMarkersResource)world.getChunkStore().getStore().getResource(BlockMapMarkersResource.getResourceType());
/* 142 */       Long2ObjectMap<BlockMapMarkersResource.BlockMapMarkerData> markers = resource.getMarkers();
/*     */       
/* 144 */       for (ObjectIterator<BlockMapMarkersResource.BlockMapMarkerData> objectIterator = markers.values().iterator(); objectIterator.hasNext(); ) { BlockMapMarkersResource.BlockMapMarkerData markerData = objectIterator.next();
/* 145 */         Vector3i position = markerData.getPosition();
/* 146 */         Transform transform = new Transform();
/* 147 */         transform.position = new Position((position.getX() + 0.5F), position.getY(), (position.getZ() + 0.5F));
/* 148 */         transform.orientation = new Direction(0.0F, 0.0F, 0.0F);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 153 */         MapMarker marker = new MapMarker(markerData.getMarkerId(), markerData.getName(), markerData.getIcon(), transform, null);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 158 */         tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker); }
/*     */     
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\meta\state\BlockMapMarker.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */