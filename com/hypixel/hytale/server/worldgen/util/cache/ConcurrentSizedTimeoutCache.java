/*     */ package com.hypixel.hytale.server.worldgen.util.cache;
/*     */ 
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.worldgen.util.LogUtil;
/*     */ import it.unimi.dsi.fastutil.HashCommon;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.lang.invoke.MethodHandles;
/*     */ import java.lang.invoke.VarHandle;
/*     */ import java.lang.ref.Cleaner;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.StampedLock;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Function;
/*     */ import java.util.logging.Level;
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
/*     */ public class ConcurrentSizedTimeoutCache<K, V>
/*     */   implements Cache<K, V>
/*     */ {
/*     */   private static final int BUCKET_MIN_CAPACITY = 16;
/*     */   private static final float BUCKET_LOAD_FACTOR = 0.75F;
/*     */   private final int bucketMask;
/*     */   @Nonnull
/*     */   private final Bucket<K, V>[] buckets;
/*     */   @Nonnull
/*     */   private final Function<K, K> computeKey;
/*     */   @Nonnull
/*     */   private final Function<K, V> computeValue;
/*     */   @Nonnull
/*     */   private final BiConsumer<K, V> destroyer;
/*     */   @Nonnull
/*     */   private final ScheduledFuture<?> future;
/*     */   @Nonnull
/*     */   private final Cleaner.Cleanable cleanable;
/*     */   
/*     */   public ConcurrentSizedTimeoutCache(int capacity, int concurrencyLevel, long timeout, @Nonnull TimeUnit timeoutUnit, @Nonnull Function<K, K> computeKey, @Nonnull Function<K, V> computeValue, @Nullable BiConsumer<K, V> destroyer) {
/*  70 */     long timeout_ns = timeoutUnit.toNanos(timeout);
/*     */ 
/*     */     
/*  73 */     int bucketCount = HashCommon.nextPowerOfTwo(concurrencyLevel);
/*     */     
/*  75 */     int bucketCapacity = Math.max(16, HashCommon.nextPowerOfTwo(capacity / bucketCount));
/*     */ 
/*     */     
/*  78 */     this.bucketMask = bucketCount - 1;
/*     */ 
/*     */     
/*  81 */     this.buckets = (Bucket<K, V>[])new Bucket[bucketCount];
/*  82 */     for (int i = 0; i < bucketCount; i++) {
/*  83 */       this.buckets[i] = new Bucket<>(bucketCapacity, timeout_ns);
/*     */     }
/*     */     
/*  86 */     this.computeKey = computeKey;
/*  87 */     this.computeValue = computeValue;
/*  88 */     this.destroyer = (destroyer != null) ? destroyer : ConcurrentSizedTimeoutCache::noopDestroy;
/*     */ 
/*     */     
/*  91 */     this.future = HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(new CleanupRunnable<>(new WeakReference<>(this)), timeout, timeout, timeoutUnit);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  96 */     this.cleanable = CleanupFutureAction.CLEANER.register(this, new CleanupFutureAction(this.future));
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 101 */     this.cleanable.clean();
/*     */     
/* 103 */     for (Bucket<K, V> bucket : this.buckets) {
/* 104 */       bucket.clear(this.destroyer);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void cleanup() {
/* 110 */     for (Bucket<K, V> bucket : this.buckets) {
/* 111 */       bucket.cleanup(this.destroyer);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public V get(K key) {
/* 118 */     if (this.future.isCancelled()) throw new IllegalStateException("Cache has been shutdown!"); 
/* 119 */     int hash = HashCommon.mix(key.hashCode());
/* 120 */     return this.buckets[hash & this.bucketMask].compute(key, this.computeKey, this.computeValue, this.destroyer);
/*     */   }
/*     */   
/*     */   private static class Bucket<K, V> {
/*     */     private final int capacity;
/*     */     private final int trimThreshold;
/*     */     private final long timeout_ns;
/*     */     private final ArrayDeque<ConcurrentSizedTimeoutCache.CacheEntry<K, V>> pool;
/*     */     private final Object2ObjectOpenHashMap<K, ConcurrentSizedTimeoutCache.CacheEntry<K, V>> map;
/* 129 */     private final StampedLock lock = new StampedLock();
/*     */     
/*     */     public Bucket(int capacity, long timeout_ns) {
/* 132 */       this.capacity = capacity;
/* 133 */       this.trimThreshold = MathUtil.fastFloor(capacity * 0.75F);
/* 134 */       this.timeout_ns = timeout_ns;
/* 135 */       this.pool = new ArrayDeque<>(capacity);
/* 136 */       this.map = new Object2ObjectOpenHashMap(capacity, 0.75F);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public V compute(@Nonnull K key, @Nonnull Function<K, K> computeKey, @Nonnull Function<K, V> computeValue, @Nonnull BiConsumer<K, V> destroyer) {
/* 145 */       long timestamp = System.nanoTime();
/* 146 */       long readStamp = this.lock.readLock();
/*     */       
/*     */       try {
/* 149 */         ConcurrentSizedTimeoutCache.CacheEntry<K, V> entry = (ConcurrentSizedTimeoutCache.CacheEntry<K, V>)this.map.get(key);
/* 150 */         if (entry != null)
/*     */         {
/* 152 */           return entry.markAndGet(timestamp);
/*     */         }
/*     */       } finally {
/* 155 */         this.lock.unlockRead(readStamp);
/*     */       } 
/*     */       
/* 158 */       K newKey = computeKey.apply(key);
/* 159 */       V newValue = computeValue.apply(key);
/*     */       
/* 161 */       V resultValue = newValue;
/*     */       
/* 163 */       long writeStamp = this.lock.writeLock();
/*     */       
/*     */       try {
/* 166 */         ConcurrentSizedTimeoutCache.CacheEntry<K, V> newEntry = this.pool.isEmpty() ? new ConcurrentSizedTimeoutCache.CacheEntry<>() : this.pool.poll();
/* 167 */         Objects.requireNonNull(newEntry, "CacheEntry pool returned null entry!");
/* 168 */         newEntry.key = newKey;
/* 169 */         newEntry.value = newValue;
/* 170 */         newEntry.timestamp = timestamp;
/*     */ 
/*     */         
/* 173 */         ConcurrentSizedTimeoutCache.CacheEntry<K, V> currentEntry = (ConcurrentSizedTimeoutCache.CacheEntry<K, V>)this.map.putIfAbsent(newKey, newEntry);
/*     */ 
/*     */         
/* 176 */         if (currentEntry != null) {
/* 177 */           Objects.requireNonNull(currentEntry.value);
/*     */           
/* 179 */           resultValue = currentEntry.value;
/* 180 */           currentEntry.timestamp = timestamp;
/*     */ 
/*     */           
/* 183 */           newEntry.key = null;
/* 184 */           newEntry.value = null;
/*     */ 
/*     */           
/* 187 */           if (this.pool.size() < this.capacity) {
/* 188 */             this.pool.offer(newEntry);
/*     */           }
/*     */         } 
/*     */       } finally {
/* 192 */         this.lock.unlockWrite(writeStamp);
/*     */       } 
/*     */ 
/*     */       
/* 196 */       if (newValue != resultValue) {
/* 197 */         destroyer.accept(newKey, newValue);
/*     */       }
/*     */       
/* 200 */       return resultValue;
/*     */     }
/*     */     
/*     */     public void cleanup(@Nullable BiConsumer<K, V> destroyer) {
/* 204 */       long writeStamp = this.lock.writeLock();
/*     */       try {
/* 206 */         boolean needsTrim = (this.map.size() >= this.trimThreshold);
/* 207 */         long expireTimestamp = System.nanoTime() - this.timeout_ns;
/*     */         
/* 209 */         ObjectIterator<Object2ObjectMap.Entry<K, ConcurrentSizedTimeoutCache.CacheEntry<K, V>>> it = this.map.object2ObjectEntrySet().fastIterator();
/* 210 */         while (it.hasNext()) {
/* 211 */           ConcurrentSizedTimeoutCache.CacheEntry<K, V> entry = (ConcurrentSizedTimeoutCache.CacheEntry<K, V>)((Object2ObjectMap.Entry)it.next()).getValue();
/* 212 */           if (entry == null) {
/* 213 */             LogUtil.getLogger().at(Level.SEVERE).log("Found null entry in cache bucket during cleanup!");
/* 214 */             it.remove();
/*     */             
/*     */             continue;
/*     */           } 
/* 218 */           if (entry.timestamp < expireTimestamp) {
/*     */             continue;
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 224 */           it.remove();
/*     */           
/* 226 */           if (destroyer != null) {
/* 227 */             destroyer.accept(entry.key, entry.value);
/*     */           }
/*     */ 
/*     */           
/* 231 */           entry.key = null;
/* 232 */           entry.value = null;
/*     */ 
/*     */           
/* 235 */           if (this.pool.size() < this.capacity) {
/* 236 */             this.pool.offer(entry);
/*     */           }
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 244 */         if (needsTrim && this.map.size() < this.capacity) {
/* 245 */           this.map.trim(this.capacity);
/*     */         }
/*     */       } finally {
/* 248 */         this.lock.unlockWrite(writeStamp);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void clear(@Nonnull BiConsumer<K, V> destroyer) {
/* 253 */       long writeStamp = this.lock.writeLock();
/*     */       try {
/* 255 */         ObjectIterator<Object2ObjectMap.Entry<K, ConcurrentSizedTimeoutCache.CacheEntry<K, V>>> it = this.map.object2ObjectEntrySet().fastIterator();
/* 256 */         while (it.hasNext()) {
/* 257 */           ConcurrentSizedTimeoutCache.CacheEntry<K, V> entry = (ConcurrentSizedTimeoutCache.CacheEntry<K, V>)((Object2ObjectMap.Entry)it.next()).getValue();
/* 258 */           destroyer.accept(entry.key, entry.value);
/* 259 */           it.remove();
/*     */         } 
/*     */       } finally {
/* 262 */         this.lock.unlockWrite(writeStamp);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static <K, V> void noopDestroy(K key, V value) {}
/*     */ 
/*     */   
/*     */   private static class CacheEntry<K, V>
/*     */   {
/*     */     private static final VarHandle TIMESTAMP;
/*     */     
/*     */     @Nullable
/*     */     public K key;
/*     */     @Nullable
/*     */     public V value;
/*     */     public long timestamp;
/*     */     
/*     */     private CacheEntry() {
/* 282 */       this.key = null;
/*     */ 
/*     */       
/* 285 */       this.value = null;
/*     */ 
/*     */       
/* 288 */       this.timestamp = 0L;
/*     */     }
/*     */     @Nonnull
/*     */     protected V markAndGet(long timestamp) {
/* 292 */       Objects.requireNonNull(this.value);
/* 293 */       TIMESTAMP.setVolatile(this, timestamp);
/* 294 */       return this.value;
/*     */     }
/*     */     
/*     */     static {
/*     */       try {
/*     */         TIMESTAMP = MethodHandles.lookup().findVarHandle(CacheEntry.class, "timestamp", long.class);
/*     */       } catch (ReflectiveOperationException e) {
/*     */         throw new ExceptionInInitializerError(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldge\\util\cache\ConcurrentSizedTimeoutCache.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */