/*     */ package com.hypixel.hytale.server.core.command.commands.world.worldgen;
/*     */ 
/*     */ import com.hypixel.hytale.common.util.FormatUtil;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector2i;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldgen.IBenchmarkableWorldGen;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
/*     */ import it.unimi.dsi.fastutil.longs.LongArrayList;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ public class WorldGenBenchmarkCommand
/*     */   extends CommandBase
/*     */ {
/*  33 */   private static final AtomicBoolean IS_RUNNING = new AtomicBoolean(false);
/*     */   
/*  35 */   public static final Message MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_SAVING = Message.translation("server.commands.worldgenbenchmark.saving");
/*  36 */   public static final Message MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_DONE = Message.translation("server.commands.worldgenbenchmark.done");
/*  37 */   public static final Message MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_SAVE_FAILED = Message.translation("server.commands.worldgenbenchmark.saveFailed");
/*  38 */   public static final Message MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_SAVE_DONE = Message.translation("server.commands.worldgenbenchmark.saveDone");
/*  39 */   public static final Message MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_PROGRESS = Message.translation("server.commands.worldgenbenchmark.progress");
/*  40 */   public static final Message MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_STARTED = Message.translation("server.commands.worldgenbenchmark.started");
/*  41 */   public static final Message MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_ABORT = Message.translation("server.commands.worldgenbenchmark.abort");
/*  42 */   public static final Message MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_BENCHMARK_NOT_SUPPORTED = Message.translation("server.commands.worldgenbenchmark.benchmarkNotSupported");
/*  43 */   public static final Message MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_ALREADY_IN_PROGRESS = Message.translation("server.commands.worldgenbenchmark.alreadyInProgress");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  49 */   private final OptionalArg<World> worldArg = withOptionalArg("world", "server.commands.worldthread.arg.desc", (ArgumentType)ArgTypes.WORLD);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  55 */   private final OptionalArg<Integer> seedArg = withOptionalArg("seed", "server.commands.worldgenbenchmark.seed.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  61 */   private final RequiredArg<Vector2i> pos1Arg = withRequiredArg("pos1", "server.commands.worldgenbenchmark.pos1.desc", ArgTypes.VECTOR2I);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  67 */   private final RequiredArg<Vector2i> pos2Arg = withRequiredArg("pos2", "server.commands.worldgenbenchmark.pos2.desc", ArgTypes.VECTOR2I);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WorldGenBenchmarkCommand() {
/*  73 */     super("benchmark", "server.commands.worldgenbenchmark.desc");
/*     */   }
/*     */   protected void executeSync(@Nonnull CommandContext context) {
/*     */     IBenchmarkableWorldGen benchmarkableWorldGen;
/*     */     int minX, maxX, minZ, maxZ;
/*  78 */     if (IS_RUNNING.get()) {
/*  79 */       context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_ALREADY_IN_PROGRESS);
/*     */       
/*     */       return;
/*     */     } 
/*  83 */     World world = (World)this.worldArg.getProcessed(context);
/*  84 */     String worldName = world.getName();
/*  85 */     int seed = this.seedArg.provided(context) ? ((Integer)this.seedArg.get(context)).intValue() : (int)world.getWorldConfig().getSeed();
/*     */     
/*  87 */     IWorldGen worldGen = world.getChunkStore().getGenerator();
/*  88 */     if (worldGen instanceof IBenchmarkableWorldGen) { benchmarkableWorldGen = (IBenchmarkableWorldGen)worldGen; }
/*  89 */     else { context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_BENCHMARK_NOT_SUPPORTED);
/*     */       
/*     */       return; }
/*     */     
/*  93 */     Vector2i corner1 = (Vector2i)this.pos1Arg.get(context);
/*  94 */     Vector2i corner2 = (Vector2i)this.pos2Arg.get(context);
/*     */ 
/*     */     
/*  97 */     if (corner1.x < corner2.x) {
/*  98 */       minX = ChunkUtil.chunkCoordinate(corner1.x);
/*  99 */       maxX = ChunkUtil.chunkCoordinate(corner2.x);
/*     */     } else {
/* 101 */       minX = ChunkUtil.chunkCoordinate(corner2.x);
/* 102 */       maxX = ChunkUtil.chunkCoordinate(corner1.x);
/*     */     } 
/* 104 */     if (corner1.y < corner2.y) {
/* 105 */       minZ = ChunkUtil.chunkCoordinate(corner1.y);
/* 106 */       maxZ = ChunkUtil.chunkCoordinate(corner2.y);
/*     */     } else {
/* 108 */       minZ = ChunkUtil.chunkCoordinate(corner2.y);
/* 109 */       maxZ = ChunkUtil.chunkCoordinate(corner1.y);
/*     */     } 
/*     */     
/* 112 */     LongArrayList generatingChunks = new LongArrayList();
/* 113 */     for (int x = minX; x <= maxX; x++) {
/* 114 */       for (int z = minZ; z <= maxZ; z++) {
/* 115 */         generatingChunks.add(ChunkUtil.indexChunk(x, z));
/*     */       }
/*     */     } 
/*     */     
/* 119 */     if (IS_RUNNING.getAndSet(true)) {
/* 120 */       context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_ABORT);
/*     */       
/*     */       return;
/*     */     } 
/* 124 */     context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_STARTED
/* 125 */         .param("seed", seed)
/* 126 */         .param("worldName", worldName)
/* 127 */         .param("size", generatingChunks.size()));
/* 128 */     benchmarkableWorldGen.getBenchmark().start();
/*     */     
/* 130 */     int chunkCount = generatingChunks.size();
/* 131 */     long startTime = System.nanoTime();
/* 132 */     (new Thread(() -> {
/*     */           try {
/*     */             Set<CompletableFuture<GeneratedChunk>> currentChunks = new HashSet<>();
/*     */             
/*     */             long nextBroadcast = System.nanoTime();
/*     */             
/*     */             do {
/*     */               long thisTime = System.nanoTime();
/*     */               
/*     */               if (thisTime >= nextBroadcast) {
/*     */                 world.execute(());
/*     */                 
/*     */                 nextBroadcast = thisTime + 5000000000L;
/*     */               } 
/*     */               currentChunks.removeIf(CompletableFuture::isDone);
/*     */               int i = currentChunks.size();
/*     */               while (i < 20 && !generatingChunks.isEmpty()) {
/*     */                 long index = generatingChunks.removeLong(generatingChunks.size() - 1);
/*     */                 CompletableFuture<GeneratedChunk> future = worldGen.generate(seed, index, ChunkUtil.xOfChunkIndex(index), ChunkUtil.zOfChunkIndex(index), ());
/*     */                 currentChunks.add(future);
/*     */                 i++;
/*     */               } 
/*     */             } while (!currentChunks.isEmpty());
/*     */             String duration = FormatUtil.nanosToString(System.nanoTime() - startTime);
/*     */             world.execute(());
/*     */             world.execute(());
/*     */             String fileName = "quant." + System.currentTimeMillis() + "." + maxX - minX + "x" + maxZ - minZ + "." + worldName + ".txt";
/*     */             File folder = new File("quantification");
/*     */             File file = new File("quantification" + File.separator + fileName);
/*     */             folder.mkdirs();
/*     */             try {
/*     */               FileWriter fw = new FileWriter(file);
/*     */               
/*     */               try { fw.write(benchmarkableWorldGen.getBenchmark().buildReport().join());
/*     */                 world.execute(());
/*     */                 fw.close(); }
/* 168 */               catch (Throwable t$) { try { fw.close(); } catch (Throwable x2)
/*     */                 { t$.addSuppressed(x2); }
/*     */                  throw t$; }
/*     */             
/* 172 */             } catch (Exception e) {
/*     */               ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to save worldgen benchmark report!");
/*     */               world.execute(());
/*     */             } 
/*     */             benchmarkableWorldGen.getBenchmark().stop();
/* 177 */           } catch (RejectedExecutionException e) {
/*     */             HytaleLogger.getLogger().at(Level.SEVERE).log("Cancelled worldgen benchmark due to generator shutdown");
/*     */           } finally {
/*     */             IS_RUNNING.set(false);
/*     */           } 
/* 182 */         }"WorldGenBenchmarkCommand")).start();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\world\worldgen\WorldGenBenchmarkCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */