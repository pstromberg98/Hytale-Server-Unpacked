/*     */ package com.hypixel.hytale.builtin.beds.sleep.systems.player;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
/*     */ import com.hypixel.hytale.builtin.beds.sleep.components.SleepTracker;
/*     */ import com.hypixel.hytale.builtin.beds.sleep.systems.world.StartSlumberSystem;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.packets.world.SleepMultiplayer;
/*     */ import com.hypixel.hytale.protocol.packets.world.UpdateSleepState;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.time.Duration;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UpdateSleepPacketSystem
/*     */   extends DelayedEntitySystem<EntityStore>
/*     */ {
/*  32 */   public static final Query<EntityStore> QUERY = (Query<EntityStore>)Query.and(new Query[] { (Query)PlayerRef.getComponentType(), (Query)PlayerSomnolence.getComponentType(), (Query)SleepTracker.getComponentType() });
/*     */   
/*  34 */   public static final Duration SPAN_BEFORE_BLACK_SCREEN = Duration.ofMillis(1200L);
/*     */   
/*     */   public static final int MAX_SAMPLE_COUNT = 5;
/*  37 */   private static final UUID[] EMPTY_UUIDS = new UUID[0];
/*  38 */   private static final UpdateSleepState PACKET_NO_SLEEP_UI = new UpdateSleepState(false, false, null, null);
/*     */ 
/*     */   
/*     */   public Query<EntityStore> getQuery() {
/*  42 */     return QUERY;
/*     */   }
/*     */   
/*     */   public UpdateSleepPacketSystem() {
/*  46 */     super(0.25F);
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  51 */     UpdateSleepState packet = createSleepPacket(store, index, archetypeChunk);
/*  52 */     SleepTracker sleepTrackerComponent = (SleepTracker)archetypeChunk.getComponent(index, SleepTracker.getComponentType());
/*  53 */     assert sleepTrackerComponent != null;
/*     */     
/*  55 */     packet = sleepTrackerComponent.generatePacketToSend(packet);
/*     */     
/*  57 */     if (packet != null) {
/*  58 */       PlayerRef playerRefComponent = (PlayerRef)archetypeChunk.getComponent(index, PlayerRef.getComponentType());
/*  59 */       assert playerRefComponent != null;
/*     */       
/*  61 */       playerRefComponent.getPacketHandler().write((Packet)packet);
/*     */     } 
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
/*     */   private UpdateSleepState createSleepPacket(@Nonnull Store<EntityStore> store, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk) {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: invokevirtual getExternalData : ()Ljava/lang/Object;
/*     */     //   4: checkcast com/hypixel/hytale/server/core/universe/world/storage/EntityStore
/*     */     //   7: invokevirtual getWorld : ()Lcom/hypixel/hytale/server/core/universe/world/World;
/*     */     //   10: astore #4
/*     */     //   12: aload_1
/*     */     //   13: invokestatic getResourceType : ()Lcom/hypixel/hytale/component/ResourceType;
/*     */     //   16: invokevirtual getResource : (Lcom/hypixel/hytale/component/ResourceType;)Lcom/hypixel/hytale/component/Resource;
/*     */     //   19: checkcast com/hypixel/hytale/builtin/beds/sleep/resources/WorldSomnolence
/*     */     //   22: astore #5
/*     */     //   24: aload #5
/*     */     //   26: invokevirtual getState : ()Lcom/hypixel/hytale/builtin/beds/sleep/resources/WorldSleep;
/*     */     //   29: astore #6
/*     */     //   31: aload_3
/*     */     //   32: iload_2
/*     */     //   33: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   36: invokevirtual getComponent : (ILcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   39: checkcast com/hypixel/hytale/builtin/beds/sleep/components/PlayerSomnolence
/*     */     //   42: astore #7
/*     */     //   44: getstatic com/hypixel/hytale/builtin/beds/sleep/systems/player/UpdateSleepPacketSystem.$assertionsDisabled : Z
/*     */     //   47: ifne -> 63
/*     */     //   50: aload #7
/*     */     //   52: ifnonnull -> 63
/*     */     //   55: new java/lang/AssertionError
/*     */     //   58: dup
/*     */     //   59: invokespecial <init> : ()V
/*     */     //   62: athrow
/*     */     //   63: aload #7
/*     */     //   65: invokevirtual getSleepState : ()Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep;
/*     */     //   68: astore #8
/*     */     //   70: aload #6
/*     */     //   72: instanceof com/hypixel/hytale/builtin/beds/sleep/resources/WorldSlumber
/*     */     //   75: ifeq -> 93
/*     */     //   78: aload #6
/*     */     //   80: checkcast com/hypixel/hytale/builtin/beds/sleep/resources/WorldSlumber
/*     */     //   83: astore #10
/*     */     //   85: aload #10
/*     */     //   87: invokevirtual createSleepClock : ()Lcom/hypixel/hytale/protocol/packets/world/SleepClock;
/*     */     //   90: goto -> 94
/*     */     //   93: aconst_null
/*     */     //   94: astore #9
/*     */     //   96: aload #8
/*     */     //   98: dup
/*     */     //   99: invokestatic requireNonNull : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   102: pop
/*     */     //   103: astore #10
/*     */     //   105: iconst_0
/*     */     //   106: istore #11
/*     */     //   108: aload #10
/*     */     //   110: iload #11
/*     */     //   112: <illegal opcode> typeSwitch : (Ljava/lang/Object;I)I
/*     */     //   117: tableswitch default -> 148, 0 -> 158, 1 -> 171, 2 -> 184, 3 -> 289
/*     */     //   148: new java/lang/MatchException
/*     */     //   151: dup
/*     */     //   152: aconst_null
/*     */     //   153: aconst_null
/*     */     //   154: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   157: athrow
/*     */     //   158: aload #10
/*     */     //   160: checkcast com/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$FullyAwake
/*     */     //   163: astore #12
/*     */     //   165: getstatic com/hypixel/hytale/builtin/beds/sleep/systems/player/UpdateSleepPacketSystem.PACKET_NO_SLEEP_UI : Lcom/hypixel/hytale/protocol/packets/world/UpdateSleepState;
/*     */     //   168: goto -> 308
/*     */     //   171: aload #10
/*     */     //   173: checkcast com/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$MorningWakeUp
/*     */     //   176: astore #13
/*     */     //   178: getstatic com/hypixel/hytale/builtin/beds/sleep/systems/player/UpdateSleepPacketSystem.PACKET_NO_SLEEP_UI : Lcom/hypixel/hytale/protocol/packets/world/UpdateSleepState;
/*     */     //   181: goto -> 308
/*     */     //   184: aload #10
/*     */     //   186: checkcast com/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$NoddingOff
/*     */     //   189: astore #14
/*     */     //   191: aload #4
/*     */     //   193: invokestatic check : (Lcom/hypixel/hytale/server/core/universe/world/World;)Lcom/hypixel/hytale/builtin/beds/sleep/systems/world/CanSleepInWorld$Result;
/*     */     //   196: invokeinterface isNegative : ()Z
/*     */     //   201: ifeq -> 210
/*     */     //   204: getstatic com/hypixel/hytale/builtin/beds/sleep/systems/player/UpdateSleepPacketSystem.PACKET_NO_SLEEP_UI : Lcom/hypixel/hytale/protocol/packets/world/UpdateSleepState;
/*     */     //   207: goto -> 308
/*     */     //   210: aload #14
/*     */     //   212: invokevirtual realTimeStart : ()Ljava/time/Instant;
/*     */     //   215: invokestatic now : ()Ljava/time/Instant;
/*     */     //   218: invokestatic between : (Ljava/time/temporal/Temporal;Ljava/time/temporal/Temporal;)Ljava/time/Duration;
/*     */     //   221: invokevirtual toMillis : ()J
/*     */     //   224: lstore #15
/*     */     //   226: lload #15
/*     */     //   228: getstatic com/hypixel/hytale/builtin/beds/sleep/systems/player/UpdateSleepPacketSystem.SPAN_BEFORE_BLACK_SCREEN : Ljava/time/Duration;
/*     */     //   231: invokevirtual toMillis : ()J
/*     */     //   234: lcmp
/*     */     //   235: ifle -> 242
/*     */     //   238: iconst_1
/*     */     //   239: goto -> 243
/*     */     //   242: iconst_0
/*     */     //   243: istore #17
/*     */     //   245: aload_3
/*     */     //   246: iload_2
/*     */     //   247: invokevirtual getReferenceTo : (I)Lcom/hypixel/hytale/component/Ref;
/*     */     //   250: astore #18
/*     */     //   252: aload_1
/*     */     //   253: aload #18
/*     */     //   255: invokestatic isReadyToSleep : (Lcom/hypixel/hytale/component/ComponentAccessor;Lcom/hypixel/hytale/component/Ref;)Z
/*     */     //   258: istore #19
/*     */     //   260: new com/hypixel/hytale/protocol/packets/world/UpdateSleepState
/*     */     //   263: dup
/*     */     //   264: iload #17
/*     */     //   266: iconst_0
/*     */     //   267: aload #9
/*     */     //   269: iload #19
/*     */     //   271: ifeq -> 282
/*     */     //   274: aload_0
/*     */     //   275: aload_1
/*     */     //   276: invokevirtual createSleepMultiplayer : (Lcom/hypixel/hytale/component/Store;)Lcom/hypixel/hytale/protocol/packets/world/SleepMultiplayer;
/*     */     //   279: goto -> 283
/*     */     //   282: aconst_null
/*     */     //   283: invokespecial <init> : (ZZLcom/hypixel/hytale/protocol/packets/world/SleepClock;Lcom/hypixel/hytale/protocol/packets/world/SleepMultiplayer;)V
/*     */     //   286: goto -> 308
/*     */     //   289: aload #10
/*     */     //   291: checkcast com/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$Slumber
/*     */     //   294: astore #15
/*     */     //   296: new com/hypixel/hytale/protocol/packets/world/UpdateSleepState
/*     */     //   299: dup
/*     */     //   300: iconst_1
/*     */     //   301: iconst_1
/*     */     //   302: aload #9
/*     */     //   304: aconst_null
/*     */     //   305: invokespecial <init> : (ZZLcom/hypixel/hytale/protocol/packets/world/SleepClock;Lcom/hypixel/hytale/protocol/packets/world/SleepMultiplayer;)V
/*     */     //   308: areturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #66	-> 0
/*     */     //   #67	-> 12
/*     */     //   #68	-> 24
/*     */     //   #70	-> 31
/*     */     //   #71	-> 44
/*     */     //   #73	-> 63
/*     */     //   #75	-> 70
/*     */     //   #77	-> 96
/*     */     //   #78	-> 158
/*     */     //   #79	-> 171
/*     */     //   #80	-> 184
/*     */     //   #81	-> 191
/*     */     //   #83	-> 210
/*     */     //   #84	-> 226
/*     */     //   #86	-> 245
/*     */     //   #87	-> 252
/*     */     //   #89	-> 260
/*     */     //   #91	-> 289
/*     */     //   #77	-> 308
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   85	8	10	slumber	Lcom/hypixel/hytale/builtin/beds/sleep/resources/WorldSlumber;
/*     */     //   165	6	12	ignored	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$FullyAwake;
/*     */     //   178	6	13	ignored	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$MorningWakeUp;
/*     */     //   226	63	15	elapsedMs	J
/*     */     //   245	44	17	grayFade	Z
/*     */     //   252	37	18	ref	Lcom/hypixel/hytale/component/Ref;
/*     */     //   260	29	19	readyToSleep	Z
/*     */     //   191	98	14	noddingOff	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$NoddingOff;
/*     */     //   296	12	15	ignored	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$Slumber;
/*     */     //   0	309	0	this	Lcom/hypixel/hytale/builtin/beds/sleep/systems/player/UpdateSleepPacketSystem;
/*     */     //   0	309	1	store	Lcom/hypixel/hytale/component/Store;
/*     */     //   0	309	2	index	I
/*     */     //   0	309	3	archetypeChunk	Lcom/hypixel/hytale/component/ArchetypeChunk;
/*     */     //   12	297	4	world	Lcom/hypixel/hytale/server/core/universe/world/World;
/*     */     //   24	285	5	worldSomnolence	Lcom/hypixel/hytale/builtin/beds/sleep/resources/WorldSomnolence;
/*     */     //   31	278	6	worldSleepState	Lcom/hypixel/hytale/builtin/beds/sleep/resources/WorldSleep;
/*     */     //   44	265	7	playerSomnolenceComponent	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSomnolence;
/*     */     //   70	239	8	playerSleepState	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep;
/*     */     //   96	213	9	clock	Lcom/hypixel/hytale/protocol/packets/world/SleepClock;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   252	37	18	ref	Lcom/hypixel/hytale/component/Ref<Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;>;
/*     */     //   0	309	1	store	Lcom/hypixel/hytale/component/Store<Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;>;
/*     */     //   0	309	3	archetypeChunk	Lcom/hypixel/hytale/component/ArchetypeChunk<Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;>;
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
/*     */   @Nullable
/*     */   private SleepMultiplayer createSleepMultiplayer(@Nonnull Store<EntityStore> store) {
/*  97 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*  98 */     List<PlayerRef> playerRefs = new ArrayList<>(world.getPlayerRefs());
/*  99 */     playerRefs.removeIf(playerRef -> (playerRef.getReference() == null));
/* 100 */     if (playerRefs.size() <= 1) {
/* 101 */       return null;
/*     */     }
/*     */     
/* 104 */     playerRefs.sort(Comparator.comparingLong(ref -> (ref.getUuid().hashCode() + world.hashCode())));
/*     */     
/* 106 */     int sleepersCount = 0;
/* 107 */     int awakeCount = 0;
/*     */     
/* 109 */     List<UUID> awakeSampleList = new ArrayList<>(playerRefs.size());
/* 110 */     for (PlayerRef playerRef : playerRefs) {
/* 111 */       Ref<EntityStore> ref = playerRef.getReference();
/* 112 */       boolean readyToSleep = StartSlumberSystem.isReadyToSleep((ComponentAccessor)store, ref);
/* 113 */       if (readyToSleep) {
/* 114 */         sleepersCount++; continue;
/*     */       } 
/* 116 */       awakeCount++;
/* 117 */       awakeSampleList.add(playerRef.getUuid());
/*     */     } 
/*     */ 
/*     */     
/* 121 */     UUID[] awakeSample = (awakeSampleList.size() > 5) ? EMPTY_UUIDS : (UUID[])awakeSampleList.toArray(x$0 -> new UUID[x$0]);
/*     */     
/* 123 */     return new SleepMultiplayer(sleepersCount, awakeCount, awakeSample);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\beds\sleep\systems\player\UpdateSleepPacketSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */