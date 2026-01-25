/*     */ package com.hypixel.hytale.server.core.command.commands.world.chunk;
/*     */ 
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector2i;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeChunkPosition;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.ChunkFlag;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.EntityChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class ChunkInfoCommand
/*     */   extends AbstractWorldCommand {
/*     */   @Nonnull
/*  28 */   private final RequiredArg<RelativeChunkPosition> chunkPosArg = withRequiredArg("x z", "server.commands.chunk.info.position.desc", ArgTypes.RELATIVE_CHUNK_POSITION);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkInfoCommand() {
/*  34 */     super("info", "server.commands.chunk.info.desc");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/*  39 */     RelativeChunkPosition chunkPosition = (RelativeChunkPosition)this.chunkPosArg.get(context);
/*  40 */     Vector2i position = chunkPosition.getChunkPosition(context, (ComponentAccessor)store);
/*     */     
/*  42 */     ChunkStore chunkStore = world.getChunkStore();
/*  43 */     Store<ChunkStore> chunkStoreStore = chunkStore.getStore();
/*  44 */     long chunkIndex = ChunkUtil.indexChunk(position.x, position.y);
/*  45 */     Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
/*     */ 
/*     */     
/*  48 */     if (chunkRef == null || !chunkRef.isValid()) {
/*  49 */       context.sendMessage(Message.translation("server.general.chunkNotLoaded")
/*  50 */           .param("chunkX", position.x)
/*  51 */           .param("chunkZ", position.y));
/*     */ 
/*     */       
/*  54 */       context.sendMessage(Message.translation("server.commands.chunkinfo.load.usage")
/*  55 */           .param("chunkX", position.x)
/*  56 */           .param("chunkZ", position.y));
/*     */       
/*     */       return;
/*     */     } 
/*  60 */     WorldChunk worldChunkComponent = (WorldChunk)chunkStoreStore.getComponent(chunkRef, WorldChunk.getComponentType());
/*  61 */     assert worldChunkComponent != null;
/*     */     
/*  63 */     BlockChunk blockChunkComponent = (BlockChunk)chunkStoreStore.getComponent(chunkRef, BlockChunk.getComponentType());
/*  64 */     assert blockChunkComponent != null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  77 */     Message msg = Message.translation("server.commands.chunkinfo.chunk").param("chunkX", position.x).param("chunkZ", position.y).param("startInit", worldChunkComponent.is(ChunkFlag.START_INIT)).param("init", worldChunkComponent.is(ChunkFlag.INIT)).param("newlyGenerated", worldChunkComponent.is(ChunkFlag.NEWLY_GENERATED)).param("onDisk", worldChunkComponent.is(ChunkFlag.ON_DISK)).param("ticking", worldChunkComponent.is(ChunkFlag.TICKING)).param("keepLoaded", worldChunkComponent.shouldKeepLoaded()).param("saving", worldChunkComponent.getNeedsSaving()).param("savingChunk", blockChunkComponent.getNeedsSaving());
/*     */     
/*  79 */     for (int i = 0; i < 10; i++) {
/*  80 */       BlockSection section = blockChunkComponent.getSectionAtIndex(i);
/*  81 */       msg.insert(Message.translation("server.commands.chunkinfo.section").param("index", i));
/*  82 */       if (section instanceof BlockSection) { BlockSection blockSection = section;
/*  83 */         msg.insert(Message.translation("server.commands.chunkinfo.dataType")
/*  84 */             .param("data", blockSection.getChunkSection().getClass().getSimpleName())); }
/*     */       
/*  86 */       msg.insert(Message.translation("server.commands.chunkinfo.sectionInfo")
/*  87 */           .param("ticking", section.hasTicking())
/*  88 */           .param("solidAir", section.isSolidAir())
/*  89 */           .param("count", section.count())
/*  90 */           .param("counts", section.valueCounts().toString()));
/*     */     } 
/*     */     
/*  93 */     BlockComponentChunk blockStateChunk = (BlockComponentChunk)chunkStoreStore.getComponent(chunkRef, BlockComponentChunk.getComponentType());
/*  94 */     EntityChunk entityChunk = (EntityChunk)chunkStoreStore.getComponent(chunkRef, EntityChunk.getComponentType());
/*  95 */     if (blockStateChunk != null && entityChunk != null) {
/*  96 */       msg.insert(Message.translation("server.commands.chunkinfo.blockStateChunk")
/*  97 */           .param("saving", blockStateChunk.getNeedsSaving())
/*  98 */           .param("countStates", blockStateChunk.getEntityHolders().size() + blockStateChunk.getEntityReferences().size())
/*  99 */           .param("savingEntity", entityChunk.getNeedsSaving())
/* 100 */           .param("countEntities", entityChunk.getEntityHolders().size() + entityChunk.getEntityReferences().size()));
/*     */     }
/*     */     
/* 103 */     context.sendMessage(msg);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\world\chunk\ChunkInfoCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */