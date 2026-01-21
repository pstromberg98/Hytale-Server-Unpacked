/*     */ package com.hypixel.hytale.builtin.beds.sleep.systems.world;
/*     */ import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSleep;
/*     */ import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
/*     */ import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSleep;
/*     */ import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSomnolence;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.ZoneOffset;
/*     */ import java.util.Collection;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class StartSlumberSystem extends DelayedSystem<EntityStore> {
/*  26 */   public static final Duration NODDING_OFF_DURATION = Duration.ofMillis(3200L);
/*  27 */   public static final Duration WAKE_UP_AUTOSLEEP_DELAY = Duration.ofHours(1L);
/*     */   
/*     */   public StartSlumberSystem() {
/*  30 */     super(0.3F);
/*     */   }
/*     */ 
/*     */   
/*     */   public void delayedTick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
/*  35 */     checkIfEveryoneIsReadyToSleep(store);
/*     */   }
/*     */   
/*     */   private void checkIfEveryoneIsReadyToSleep(Store<EntityStore> store) {
/*  39 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*  40 */     Collection<PlayerRef> playerRefs = world.getPlayerRefs();
/*  41 */     if (playerRefs.isEmpty())
/*     */       return; 
/*  43 */     if (CanSleepInWorld.check(world).isNegative()) {
/*     */       return;
/*     */     }
/*     */     
/*  47 */     float wakeUpHour = world.getGameplayConfig().getWorldConfig().getSleepConfig().getWakeUpHour();
/*     */     
/*  49 */     WorldSomnolence worldSomnolenceResource = (WorldSomnolence)store.getResource(WorldSomnolence.getResourceType());
/*     */     
/*  51 */     WorldSleep worldState = worldSomnolenceResource.getState();
/*  52 */     if (worldState != WorldSleep.Awake.INSTANCE) {
/*     */       return;
/*     */     }
/*     */     
/*  56 */     if (isEveryoneReadyToSleep((ComponentAccessor<EntityStore>)store)) {
/*  57 */       WorldTimeResource timeResource = (WorldTimeResource)store.getResource(WorldTimeResource.getResourceType());
/*     */       
/*  59 */       Instant now = timeResource.getGameTime();
/*  60 */       Instant target = computeWakeupInstant(now, wakeUpHour);
/*  61 */       float irlSeconds = computeIrlSeconds(now, target);
/*     */       
/*  63 */       worldSomnolenceResource.setState((WorldSleep)new WorldSlumber(now, target, irlSeconds));
/*     */       
/*  65 */       store.forEachEntityParallel((Query)PlayerSomnolence.getComponentType(), (index, archetypeChunk, commandBuffer) -> {
/*     */             Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/*     */             commandBuffer.putComponent(ref, PlayerSomnolence.getComponentType(), (Component)PlayerSleep.Slumber.createComponent(timeResource));
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   private Instant computeWakeupInstant(@Nonnull Instant now, float wakeUpHour) {
/*  73 */     LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneOffset.UTC);
/*     */     
/*  75 */     int hours = (int)wakeUpHour;
/*  76 */     float fractionalHour = wakeUpHour - hours;
/*  77 */     LocalDateTime wakeUpTime = ldt.toLocalDate().atTime(hours, (int)(fractionalHour * 60.0F));
/*     */     
/*  79 */     if (!ldt.isBefore(wakeUpTime)) {
/*  80 */       wakeUpTime = wakeUpTime.plusDays(1L);
/*     */     }
/*     */     
/*  83 */     return wakeUpTime.toInstant(ZoneOffset.UTC);
/*     */   }
/*     */   
/*     */   private static float computeIrlSeconds(Instant startInstant, Instant targetInstant) {
/*  87 */     long ms = Duration.between(startInstant, targetInstant).toMillis();
/*  88 */     long hours = TimeUnit.MILLISECONDS.toHours(ms);
/*  89 */     double seconds = Math.max(3.0D, hours / 6.0D);
/*  90 */     return (float)Math.ceil(seconds);
/*     */   }
/*     */   
/*     */   private boolean isEveryoneReadyToSleep(ComponentAccessor<EntityStore> store) {
/*  94 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*  95 */     Collection<PlayerRef> playerRefs = world.getPlayerRefs();
/*  96 */     if (playerRefs.isEmpty()) return false;
/*     */     
/*  98 */     for (PlayerRef playerRef : playerRefs) {
/*  99 */       if (!isReadyToSleep(store, playerRef.getReference())) {
/* 100 */         return false;
/*     */       }
/*     */     } 
/* 103 */     return true;
/*     */   }
/*     */   
/*     */   public static boolean isReadyToSleep(ComponentAccessor<EntityStore> store, Ref<EntityStore> ref) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: aload_1
/*     */     //   2: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   5: invokeinterface getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   10: checkcast com/hypixel/hytale/builtin/beds/sleep/components/PlayerSomnolence
/*     */     //   13: astore_2
/*     */     //   14: aload_2
/*     */     //   15: ifnonnull -> 20
/*     */     //   18: iconst_0
/*     */     //   19: ireturn
/*     */     //   20: aload_2
/*     */     //   21: invokevirtual getSleepState : ()Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep;
/*     */     //   24: astore_3
/*     */     //   25: aload_3
/*     */     //   26: dup
/*     */     //   27: invokestatic requireNonNull : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   30: pop
/*     */     //   31: astore #4
/*     */     //   33: iconst_0
/*     */     //   34: istore #5
/*     */     //   36: aload #4
/*     */     //   38: iload #5
/*     */     //   40: <illegal opcode> typeSwitch : (Ljava/lang/Object;I)I
/*     */     //   45: tableswitch default -> 76, 0 -> 86, 1 -> 97, 2 -> 144, 3 -> 175
/*     */     //   76: new java/lang/MatchException
/*     */     //   79: dup
/*     */     //   80: aconst_null
/*     */     //   81: aconst_null
/*     */     //   82: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   85: athrow
/*     */     //   86: aload #4
/*     */     //   88: checkcast com/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$FullyAwake
/*     */     //   91: astore #6
/*     */     //   93: iconst_0
/*     */     //   94: goto -> 183
/*     */     //   97: aload #4
/*     */     //   99: checkcast com/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$MorningWakeUp
/*     */     //   102: astore #7
/*     */     //   104: aload_0
/*     */     //   105: invokestatic getResourceType : ()Lcom/hypixel/hytale/component/ResourceType;
/*     */     //   108: invokeinterface getResource : (Lcom/hypixel/hytale/component/ResourceType;)Lcom/hypixel/hytale/component/Resource;
/*     */     //   113: checkcast com/hypixel/hytale/server/core/modules/time/WorldTimeResource
/*     */     //   116: astore #8
/*     */     //   118: aload #7
/*     */     //   120: invokevirtual gameTimeStart : ()Ljava/time/Instant;
/*     */     //   123: getstatic com/hypixel/hytale/builtin/beds/sleep/systems/world/StartSlumberSystem.WAKE_UP_AUTOSLEEP_DELAY : Ljava/time/Duration;
/*     */     //   126: invokevirtual plus : (Ljava/time/temporal/TemporalAmount;)Ljava/time/Instant;
/*     */     //   129: astore #9
/*     */     //   131: aload #8
/*     */     //   133: invokevirtual getGameTime : ()Ljava/time/Instant;
/*     */     //   136: aload #9
/*     */     //   138: invokevirtual isAfter : (Ljava/time/Instant;)Z
/*     */     //   141: goto -> 183
/*     */     //   144: aload #4
/*     */     //   146: checkcast com/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$NoddingOff
/*     */     //   149: astore #8
/*     */     //   151: aload #8
/*     */     //   153: invokevirtual realTimeStart : ()Ljava/time/Instant;
/*     */     //   156: getstatic com/hypixel/hytale/builtin/beds/sleep/systems/world/StartSlumberSystem.NODDING_OFF_DURATION : Ljava/time/Duration;
/*     */     //   159: invokevirtual plus : (Ljava/time/temporal/TemporalAmount;)Ljava/time/Instant;
/*     */     //   162: astore #9
/*     */     //   164: invokestatic now : ()Ljava/time/Instant;
/*     */     //   167: aload #9
/*     */     //   169: invokevirtual isAfter : (Ljava/time/Instant;)Z
/*     */     //   172: goto -> 183
/*     */     //   175: aload #4
/*     */     //   177: checkcast com/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$Slumber
/*     */     //   180: astore #9
/*     */     //   182: iconst_1
/*     */     //   183: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #107	-> 0
/*     */     //   #108	-> 14
/*     */     //   #109	-> 20
/*     */     //   #111	-> 25
/*     */     //   #112	-> 86
/*     */     //   #113	-> 97
/*     */     //   #114	-> 104
/*     */     //   #115	-> 118
/*     */     //   #116	-> 131
/*     */     //   #118	-> 144
/*     */     //   #119	-> 151
/*     */     //   #120	-> 164
/*     */     //   #122	-> 175
/*     */     //   #111	-> 183
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   93	4	6	fullyAwake	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$FullyAwake;
/*     */     //   118	26	8	worldTimeResource	Lcom/hypixel/hytale/server/core/modules/time/WorldTimeResource;
/*     */     //   131	13	9	readyTime	Ljava/time/Instant;
/*     */     //   104	40	7	morningWakeUp	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$MorningWakeUp;
/*     */     //   164	11	9	sleepStart	Ljava/time/Instant;
/*     */     //   151	24	8	noddingOff	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$NoddingOff;
/*     */     //   182	1	9	slumber	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep$Slumber;
/*     */     //   0	184	0	store	Lcom/hypixel/hytale/component/ComponentAccessor;
/*     */     //   0	184	1	ref	Lcom/hypixel/hytale/component/Ref;
/*     */     //   14	170	2	somnolence	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSomnolence;
/*     */     //   25	159	3	sleepState	Lcom/hypixel/hytale/builtin/beds/sleep/components/PlayerSleep;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	184	0	store	Lcom/hypixel/hytale/component/ComponentAccessor<Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;>;
/*     */     //   0	184	1	ref	Lcom/hypixel/hytale/component/Ref<Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;>;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\beds\sleep\systems\world\StartSlumberSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */