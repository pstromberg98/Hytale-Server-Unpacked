/*     */ package com.hypixel.hytale.server.core.io;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.codecs.EnumCodec;
/*     */ import com.hypixel.hytale.metrics.MetricsRegistry;
/*     */ import com.hypixel.hytale.metrics.metric.HistoricMetric;
/*     */ import com.hypixel.hytale.metrics.metric.Metric;
/*     */ import com.hypixel.hytale.protocol.packets.connection.Pong;
/*     */ import com.hypixel.hytale.protocol.packets.connection.PongType;
/*     */ import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
/*     */ import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
/*     */ import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
/*     */ import it.unimi.dsi.fastutil.longs.LongPriorityQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PingInfo
/*     */ {
/*     */   public static final MetricsRegistry<PingInfo> METRICS_REGISTRY;
/*     */   
/*     */   static {
/* 687 */     METRICS_REGISTRY = (new MetricsRegistry()).register("PingType", pingInfo -> pingInfo.pingType, (Codec)new EnumCodec(PongType.class)).register("PingMetrics", PingInfo::getPingMetricSet, HistoricMetric.METRICS_CODEC).register("PacketQueueMin", pingInfo -> Long.valueOf(pingInfo.packetQueueMetric.getMin()), (Codec)Codec.LONG).register("PacketQueueAvg", pingInfo -> Double.valueOf(pingInfo.packetQueueMetric.getAverage()), (Codec)Codec.DOUBLE).register("PacketQueueMax", pingInfo -> Long.valueOf(pingInfo.packetQueueMetric.getMax()), (Codec)Codec.LONG);
/*     */   }
/* 689 */   public static final TimeUnit TIME_UNIT = TimeUnit.MICROSECONDS;
/*     */   
/*     */   public static final int ONE_SECOND_INDEX = 0;
/*     */   
/*     */   public static final int ONE_MINUTE_INDEX = 1;
/*     */   
/*     */   public static final int FIVE_MINUTE_INDEX = 2;
/*     */   public static final double PERCENTILE = 0.9900000095367432D;
/*     */   public static final int PING_FREQUENCY = 1;
/* 698 */   public static final TimeUnit PING_FREQUENCY_UNIT = TimeUnit.SECONDS;
/*     */   
/*     */   public static final int PING_FREQUENCY_MILLIS = 1000;
/*     */   
/*     */   public static final int PING_HISTORY_MILLIS = 15000;
/*     */   
/*     */   public static final int PING_HISTORY_LENGTH = 15;
/*     */   protected final PongType pingType;
/* 706 */   protected final Lock queueLock = new ReentrantLock();
/* 707 */   protected final IntPriorityQueue pingIdQueue = (IntPriorityQueue)new IntArrayFIFOQueue(15);
/* 708 */   protected final LongPriorityQueue pingTimestampQueue = (LongPriorityQueue)new LongArrayFIFOQueue(15);
/*     */   
/* 710 */   protected final Lock pingLock = new ReentrantLock();
/*     */   @Nonnull
/*     */   protected final HistoricMetric pingMetricSet;
/* 713 */   protected final Metric packetQueueMetric = new Metric();
/*     */   
/*     */   public PingInfo(PongType pingType) {
/* 716 */     this.pingType = pingType;
/*     */ 
/*     */     
/* 719 */     this
/*     */ 
/*     */ 
/*     */       
/* 723 */       .pingMetricSet = HistoricMetric.builder(1000L, TimeUnit.MILLISECONDS).addPeriod(1L, TimeUnit.SECONDS).addPeriod(1L, TimeUnit.MINUTES).addPeriod(5L, TimeUnit.MINUTES).build();
/*     */   }
/*     */   
/*     */   protected void recordSent(int id, long timestamp) {
/* 727 */     this.queueLock.lock();
/*     */     try {
/* 729 */       this.pingIdQueue.enqueue(id);
/* 730 */       this.pingTimestampQueue.enqueue(timestamp);
/*     */     } finally {
/* 732 */       this.queueLock.unlock();
/*     */     } 
/*     */   } protected void handlePacket(@Nonnull Pong packet) {
/*     */     int nextIdToHandle;
/*     */     long sentTimestamp;
/* 737 */     if (packet.type != this.pingType) throw new IllegalArgumentException("Got packet for " + String.valueOf(packet.type) + " but expected " + String.valueOf(this.pingType));
/*     */ 
/*     */ 
/*     */     
/* 741 */     this.queueLock.lock();
/*     */     try {
/* 743 */       nextIdToHandle = this.pingIdQueue.dequeueInt();
/* 744 */       sentTimestamp = this.pingTimestampQueue.dequeueLong();
/*     */     } finally {
/* 746 */       this.queueLock.unlock();
/*     */     } 
/*     */     
/* 749 */     if (packet.id != nextIdToHandle) throw new IllegalArgumentException(String.valueOf(packet.id));
/*     */ 
/*     */     
/* 752 */     long nanoTime = System.nanoTime();
/* 753 */     long pingValue = nanoTime - sentTimestamp;
/* 754 */     if (pingValue <= 0L) throw new IllegalArgumentException(String.format("Ping must be received after its sent! %s", new Object[] { Long.valueOf(pingValue) }));
/*     */     
/* 756 */     this.pingLock.lock();
/*     */     try {
/* 758 */       this.pingMetricSet.add(nanoTime, TIME_UNIT.convert(pingValue, TimeUnit.NANOSECONDS));
/* 759 */       this.packetQueueMetric.add(packet.packetQueueSize);
/*     */     } finally {
/* 761 */       this.pingLock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public PongType getPingType() {
/* 766 */     return this.pingType;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Metric getPacketQueueMetric() {
/* 771 */     return this.packetQueueMetric;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public HistoricMetric getPingMetricSet() {
/* 776 */     return this.pingMetricSet;
/*     */   }
/*     */   
/*     */   public void clear() {
/* 780 */     this.pingLock.lock();
/*     */     try {
/* 782 */       this.packetQueueMetric.clear();
/* 783 */       this.pingMetricSet.clear();
/*     */     } finally {
/* 785 */       this.pingLock.unlock();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\io\PacketHandler$PingInfo.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */