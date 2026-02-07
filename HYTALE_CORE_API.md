# Hytale Server Core API Documentation

This document contains detailed descriptions of important classes and methods in the `com.hypixel.hytale.server.core` package and its sub-packages. This information is prepared to serve as a reference for server management and mod development processes.

## Table of Contents
1. [Core (Main Package)](#core-main-package)
2. [Auth (Authentication)](#auth-authentication)
3. [Command](#command)
4. [Event](#event)
5. [Plugin](#plugin)
6. [Permissions](#permissions)
7. [Util (Tools)](#util-tools)
8. [Blocks](#blocks)

---

## Core (Main Package)
**Package:** `com.hypixel.hytale.server.core`

### `HytaleServer`
The central management class of the server (Singleton). It coordinates the server lifecycle (startup, loop, shutdown), managers (plugin, command, event), and modules.

**Important Public Methods:**
*   `static HytaleServer get()`: Returns the single instance of the running server.
*   `EventBus getEventBus()`: Returns the `EventBus` object where server-wide events are managed.
*   `PluginManager getPluginManager()`: Returns the plugin manager.
*   `CommandManager getCommandManager()`: Returns the command manager.
*   `HytaleServerConfig getConfig()`: Returns the object representing the server configuration file (`hytale-server.json`).
*   `void shutdownServer(ShutdownReason reason)`: Shuts down the server for the specified reason (`ShutdownReason`).
*   `String getServerName()`: Returns the server name set in the configuration.
*   `boolean isBooted()`: Indicates whether the server has fully started.
*   `boolean isShuttingDown()`: Indicates whether the server is in the process of shutting down.
*   `Instant getBoot()`: Returns the time when the server was started.

### `HytaleServerConfig`
Holds and manages server settings. Changes can be saved to the file on disk.

**Important Public Methods:**
*   `static HytaleServerConfig load()`: Loads the configuration from the default path.
*   `static CompletableFuture<Void> save(HytaleServerConfig config)`: Saves the configuration to disk.
*   `void setMotd(String motd)`: Sets the message of the day (MOTD) visible in the server list.
*   `int getMaxPlayers()`: Returns the maximum number of players.
*   `void setMaxPlayers(int maxPlayers)`: Sets the maximum number of players.
*   `Module getModule(String moduleName)`: Retrieves the settings of the named module (e.g., "WorldModule").

---

## Auth (Authentication)
**Package:** `com.hypixel.hytale.server.core.auth`

### `ServerAuthManager`
Manages authentication processes on the server. Checks the validity of players.

**Important Public Methods:**
*   `static ServerAuthManager getInstance()`: Returns the manager instance.
*   `void initialize()`: Prepares authentication keys and structures.
*   `AuthMode getAuthMode()`: Returns the server's authentication mode (ONLINE, OFFLINE, etc.).

### `SessionServiceClient`
Communicates with Hytale session services (Backend API). Used to verify player sessions and retrieve profile information.

**Important Public Methods:**
*   `CompletableFuture<String> requestAuthorizationGrantAsync(...)`: Requests authorization grant.
*   `CompletableFuture<String> exchangeAuthGrantForTokenAsync(...)`: Exchanges the grant for an access token.
*   `GameProfile[] getGameProfiles(String oauthAccessToken)`: Retrieves player profiles using the access token.
*   `GameSessionResponse createGameSession(...)`: Starts a new game session.

### `PlayerAuthentication`
A data class holding a player's authentication information (UUID, Username).

---

## Command
**Package:** `com.hypixel.hytale.server.core.command.system`

### `CommandManager`
The heart of the command system. It registers, parses, and routes commands to the relevant processor.

**Important Public Methods:**
*   `void registerCommands()`: Registers default system commands.
*   `CommandRegistration register(AbstractCommand command)`: Registers a new command object to the system. Used to add custom commands in mods.
*   `CompletableFuture<Void> handleCommand(CommandSender sender, String commandString)`: Executes a command line on behalf of the sender.
*   `Map<String, AbstractCommand> getCommandRegistration()`: Returns a map of all registered commands.

### `CommandSender`
An interface representing the entity executing the command. Can be `Player` or `ConsoleSender`.

**Methods:**
*   `void sendMessage(Message message)`: Sends a message to the sender.
*   `String getName()`: Returns the name of the sender.
*   `boolean hasPermission(String permission)`: Checks if the sender has specific permission.

---

## Event
**Package:** `com.hypixel.hytale.event` (and `com.hypixel.hytale.server.core.event`)

### `EventBus`
The center of the event-based system. Enables dispatching and listening to events.

**Important Public Methods:**
*   `EventRegistration register(Class<T> eventClass, Consumer<T> consumer)`: Registers a listener for a specific event class.
*   `IEventDispatcher dispatchFor(Class<T> eventClass)`: Returns a publisher for an event class.

### Example Events (`server.core.event.events`)
*   `BootEvent`: Triggered when the server starts.
*   `ShutdownEvent`: Triggered when the server begins to shut down.

---

## Plugin
**Package:** `com.hypixel.hytale.server.core.plugin`

### `PluginManager`
Manages plugins (Mods/Plugins) loaded on the server.

**Important Public Methods:**
*   `List<PluginBase> getPlugins()`: Lists all loaded and active plugins.
*   `PluginBase getPlugin(PluginIdentifier identifier)`: Retrieves the plugin with the specified ID.
*   `void setup()`: Starts the setup phase of plugins.
*   `void start()`: Starts (enables) the plugins.
*   `void shutdown()`: Safely stops the plugins.

### `PluginBase`
The base class for all plugins. This class is inherited when developing mods (usually via `JavaPlugin`).

---

## Permissions
**Package:** `com.hypixel.hytale.server.core.permissions`

### `HytalePermissions`
Contains constants defining standard permissions (permission nodes) within the server.

**Important Constants:**
*   `COMMAND_BASE`: Basic command permission (`hytale.command`).
*   `ASSET_EDITOR`: Asset editor permission.
*   `FLY_CAM`: Fly camera usage permission.
*   `fromCommand(String name)`: Creates a permission string for a command name (e.g., `hytale.command.give`).

---

## Util (Tools)
**Package:** `com.hypixel.hytale.server.core.util`

### `MessageUtil`
Contains helper methods for formatting, coloring, and sending messages to players.

**Important Public Methods:**
*   `AttributedString toAnsiString(Message message)`: Converts a message object to ANSI format to appear colored in the console.
*   `formatText(String text, ...)`: Replaces parameters (like {0}, {name}) in the text with their values.

### `NotificationUtil` (Other tool examined)
Simplifies sending notifications.

---

## Blocks

Blocks are the fundamental building units of the Hytale world. They are managed through a layered storage hierarchy, participate in the Entity Component System (ECS), and support events, ticking, rotation, connected textures, and multi-block structures.

### Storage Hierarchy

The world stores blocks in a hierarchical structure:

```
World
 └─ ChunkStore (component registry for all chunk data)
     └─ WorldChunk (32×256×32 block column, manages chunk lifecycle)
         ├─ BlockChunk (raw block grid data)
         │    └─ BlockSection[10] (each 32×32×32 = 32,768 blocks)
         │         ├─ chunkSection  (ISectionPalette — block type IDs)
         │         ├─ fillerSection  (ISectionPalette — filler offsets for multi-block structures)
         │         └─ rotationSection (ISectionPalette — rotation indices)
         ├─ BlockComponentChunk (block entity/component references by block index)
         ├─ EntityChunk (mobile entities within the chunk)
         └─ ChunkColumn (deprecated container for 10 section holders)
```

A chunk column covers 32 blocks on the X and Z axes and 320 blocks on Y (10 sections × 32). Global coordinates map to chunk-local coordinates via `x & 0x1F` / `z & 0x1F`. Chunks are indexed with `ChunkUtil.indexChunkFromBlock(x, z)` and blocks within a section with `ChunkUtil.indexBlock(x, y, z)`.

### Palette-Based Block Storage

Each `BlockSection` stores its block IDs using **palette compression** through `ISectionPalette` implementations. The palette maps a small set of internal indices to external block type IDs, keeping memory usage proportional to the number of *unique* block types in a section rather than the total block count.

**Package:** `com.hypixel.hytale.server.core.universe.world.chunk.section.palette`

| Implementation | Bits Per Block | Max Unique Types | Array Type | Notes |
|---|---|---|---|---|
| `EmptySectionPalette` | 0 | 1 (air only) | None (singleton) | Optimized for empty sections |
| `HalfByteSectionPalette` | 4 | 16 | `byte[16384]` (nibble-packed) | Demotes to Empty when all air |
| `ByteSectionPalette` | 8 | 256 | `byte[32768]` | Demotes to HalfByte when ≤14 unique types |
| `ShortSectionPalette` | 16 | 65,536 | `short[32768]` | Demotes to Byte when ≤251 unique types; max level (cannot promote) |

Palettes **automatically promote** when a `set()` call introduces a new unique block type that exceeds capacity (`SetResult.REQUIRES_PROMOTE`), and **automatically demote** when unique type count falls below a threshold (`shouldDemote()`). All access is guarded by a `StampedLock` for thread safety — reads use optimistic locking and fall back to read locks, while writes acquire write locks.

**`ISectionPalette` Key Methods:**
*   `int get(int index)`: Returns the external block type ID at the given index.
*   `SetResult set(int index, int id)`: Sets a block type ID. Returns `UNCHANGED`, `ADDED_OR_REMOVED`, or `REQUIRES_PROMOTE`.
*   `ISectionPalette promote()` / `ISectionPalette demote()`: Transitions to a larger or smaller palette.
*   `boolean contains(int id)`: Checks whether a block type exists in the section.
*   `int count()` / `int count(int id)`: Counts unique types or occurrences of a specific type.
*   `boolean isSolid(int id)`: Checks if the entire section contains only one block type.
*   `void serializeForPacket(ByteBuf buf)`: Serializes for network transmission.
*   `void serialize(KeySerializer, ByteBuf)` / `void deserialize(ToIntFunction<ByteBuf>, ByteBuf, int)`: Disk serialization/deserialization.

### Core Block Classes

#### `BlockType`
**Package:** `com.hypixel.hytale.server.core.asset.type.blocktype.config`

The central **asset definition** for a block. Each block type in the game (dirt, stone, chest, etc.) has a `BlockType` entry in the global asset map. It defines visual, physical, and gameplay properties.

**Key Fields:**
*   `String id` — Unique asset identifier (e.g. `"Dirt"`, `"Chest"`).
*   `String item` — Associated item ID for inventory.
*   `DrawType drawType` — Rendering mode (cube, model, etc.).
*   `BlockMaterial material` — Material class (`Empty`, solid, liquid, etc.).
*   `Opacity opacity` — Transparency level.
*   `int hitbox` / `int interactionHitbox` — Indices into `BlockBoundingBoxes` asset map.
*   `String model` / `float modelScale` / `String modelAnimation` — Custom model configuration.
*   `BlockTextures[] cubeTextures` — Per-face textures.
*   `ColorLight light` — Light emission data.
*   `BlockFlags flags` — Behavioral flags.
*   `BlockGathering gathering` — Tool requirements and drop tables.
*   `BlockPlacementSettings placementSettings` — Placement rotation modes and preview visibility.
*   `BlockMovementSettings movementSettings` — Solid, liquid, and passthrough flags.
*   `ConnectedBlockRuleSet connectedBlockRuleSet` — Connected texture rules.
*   `Map<InteractionType, String> interactions` — Available player interactions.
*   `Map<String, Integer> states` — Named state variants (e.g. open/closed door).

**Key Methods:**
*   `static BlockTypeAssetMap<String, BlockType> getAssetMap()`: Returns the global block type registry.
*   `BlockType getBlockForState(String state)`: Resolves a named state variant to its `BlockType`.
*   `String getDefaultStateKey()`: Returns the default state name.
*   `BlockGathering getGathering()`: Returns gathering/harvesting configuration.
*   `BlockPlacementSettings getPlacementSettings()`: Returns placement rules.
*   `boolean isUnknown()`: Whether this is an unknown/placeholder type.
*   `boolean hasSupport()`: Whether this block requires support.
*   `static BlockType getUnknownFor(String key)`: Creates a placeholder for an unrecognized block key.

Blocks are looked up by string key or integer index via `BlockType.getAssetMap()`.

#### `BlockState` (Deprecated)
**Package:** `com.hypixel.hytale.server.core.universe.world.meta`

An abstract class representing **persistent, per-block state data** — for example, the inventory contents of a chest or the progress of a furnace. Implements `Component<ChunkStore>`, meaning it is stored in the chunk's ECS store.

**Key Fields:**
*   `Vector3i position` — Position relative to the chunk (X/Z masked to 0–31, Y absolute).
*   `Ref<ChunkStore> reference` — ECS reference into the `ChunkStore`.
*   `AtomicBoolean initialized` — Tracks initialization status.

**Key Methods:**
*   `boolean initialize(BlockType blockType)`: Called when the block state is created or loaded. Return `false` to discard.
*   `void onUnload()`: Called when the chunk containing this block state is unloaded.
*   `int getIndex()`: Returns `ChunkUtil.indexBlockInColumn(x, y, z)`.
*   `BlockType getBlockType()`: Resolves the block type at this position.
*   `int getRotationIndex()`: Returns the rotation index from the chunk data.
*   `void markNeedsSave()`: Flags the chunk as dirty for persistence.
*   `BsonDocument saveToDocument()`: Serializes to BSON via codec.
*   `static BlockState load(BsonDocument, WorldChunk, Vector3i)`: Deserializes and initializes a `BlockState`.
*   `static BlockState ensureState(WorldChunk, int x, int y, int z)`: Creates a `BlockState` for a position if the block type defines one.
*   `Holder<ChunkStore> toHolder()`: Converts this state to a component holder for ECS operations.

`BlockState` subclasses are registered through `BlockStateModule` and use `CodecMapCodec` for polymorphic serialization (keyed by `"Type"`).

#### `BlockEntity`
**Package:** `com.hypixel.hytale.server.core.entity.entities`

Represents a block **as a mobile entity** in the world — for example, a dropped block item or a falling block. Implements `Component<EntityStore>` (stored in the *entity* store, not the chunk store).

**Key Fields:**
*   `String blockTypeKey` — Reference to the `BlockType` asset.
*   `SimplePhysicsProvider simplePhysicsProvider` — Physics simulation.
*   `boolean isBlockIdNetworkOutdated` — Dirty flag for network sync.

**Key Methods:**
*   `static Holder<EntityStore> assembleDefaultBlockEntity(TimeResource, String blockTypeKey, Vector3d position)`: Factory that creates a full entity holder with `BlockEntity`, `DespawnComponent` (120s default), `TransformComponent`, `Velocity`, and `UUIDComponent`.
*   `SimplePhysicsProvider initPhysics(BoundingBox)`: Initializes the physics provider.
*   `BoundingBox createBoundingBoxComponent()`: Creates a bounding box from the block type's hitbox.
*   `void addForce(float x, float y, float z)` / `void addForce(Vector3d)`: Applies force to the entity.

### BlockSection

**Package:** `com.hypixel.hytale.server.core.universe.world.chunk.section`

A `BlockSection` is a 32×32×32 cube of blocks within a chunk. It implements `Component<ChunkStore>` and is the primary unit of block data storage.

**Key Fields:**
*   `ISectionPalette chunkSection` — Block type IDs.
*   `ISectionPalette fillerSection` — Filler data for multi-block structures.
*   `ISectionPalette rotationSection` — Rotation indices.
*   `ChunkLightData localLight` / `ChunkLightData globalLight` — Lighting data.
*   `BitSet tickingBlocks` — Blocks that require tick updates.
*   `IntOpenHashSet changedPositions` — Positions modified since last network sync.
*   `StampedLock chunkSectionLock` — Thread-safety lock.

**Key Methods:**
*   `int get(int x, int y, int z)`: Returns the block type ID at a position.
*   `boolean set(int blockIdx, int blockId, int rotation, int filler)`: Sets block data (ID, rotation, filler) atomically. Handles palette promotion/demotion. Returns `true` if any data changed.
*   `int getFiller(int x, int y, int z)`: Returns the filler offset at a position.
*   `int getRotationIndex(int x, int y, int z)`: Returns the rotation index at a position.
*   `boolean setTicking(int blockIdx, boolean ticking)`: Marks a block for tick updates.
*   `void scheduleTick(int index, Instant gameTime)`: Schedules a future tick.
*   `void preTick(Instant gameTime)`: Processes scheduled ticks and copies the ticking set for iteration.
*   `<T, V> int forEachTicking(...)`: Iterates over ticking blocks and executes a `BlockTickStrategy`.
*   `IntOpenHashSet getAndClearChangedPositions()`: Atomically returns and clears the changed position set (for network delta updates).
*   `void invalidateLocalLight()` / `void invalidateGlobalLight()`: Marks lighting as stale.
*   `double getMaximumHitboxExtent()`: Computes the largest hitbox extent across all blocks in the section.
*   `void serializeForPacket(ByteBuf)`: Serializes all three palettes for network transmission.
*   `void serialize(KeySerializer, ByteBuf)` / `void deserialize(ToIntFunction<ByteBuf>, ByteBuf, int)`: Disk persistence including block migration support.

### WorldChunk

**Package:** `com.hypixel.hytale.server.core.universe.world.chunk`

The top-level chunk class that implements both `BlockAccessor` and `Component<ChunkStore>`. It ties together block storage, block components, entities, and chunk lifecycle flags.

**Key Fields:**
*   `World world` — The owning world.
*   `BlockChunk blockChunk` — Raw block grid data.
*   `BlockComponentChunk blockComponentChunk` — Per-block component holders.
*   `EntityChunk entityChunk` — Mobile entities in this chunk.
*   `Flags<ChunkFlag> flags` — Lifecycle flags (`INIT`, `LOADED`, `DIRTY`, etc.).
*   `int keepAlive` / `int activeTimer` — Chunk unloading timers.
*   `boolean needsSaving` / `boolean isSaving` — Persistence state.

### BlockAccessor Interface

**Package:** `com.hypixel.hytale.server.core.universe.world.accessor`

The primary interface for reading and writing block data. `WorldChunk` implements this.

**Key Methods:**
*   `int getBlock(int x, int y, int z)`: Returns the block type ID.
*   `BlockType getBlockType(int x, int y, int z)`: Resolves the ID to a `BlockType` asset.
*   `boolean setBlock(int x, int y, int z, int id, BlockType, int rotation, int filler, int settings)`: Core setter — all other `setBlock` overloads delegate here.
*   `boolean setBlock(int x, int y, int z, String blockTypeKey)`: Sets by key string.
*   `boolean setBlock(int x, int y, int z, BlockType blockType)`: Sets by `BlockType` reference.
*   `boolean breakBlock(int x, int y, int z)`: Removes a block (sets to air/`BlockType.EMPTY`). Handles filler offset adjustment.
*   `boolean placeBlock(int x, int y, int z, String key, Rotation yaw, Rotation pitch, Rotation roll, int settings)`: Places with rotation, validates placement space.
*   `boolean testPlaceBlock(int x, int y, int z, BlockType, int rotationIndex)`: Tests whether a block can be placed at the position.
*   `int getRotationIndex(int x, int y, int z)`: Returns the rotation index.
*   `int getFiller(int x, int y, int z)`: Returns the filler offset.
*   `boolean setTicking(int x, int y, int z, boolean ticking)`: Marks a block for tick updates.
*   `void setState(int x, int y, int z, BlockState state)`: Attaches a `BlockState` component.
*   `BlockState getState(int x, int y, int z)`: Retrieves the `BlockState` at a position.
*   `void setBlockInteractionState(int x, int y, int z, BlockType, String state, boolean force)`: Transitions a block to a named state variant (e.g. opening a door).

**Settings Bitmask Flags:**
| Bit | Hex | Effect |
|---|---|---|
| 1 | `0x2` | Silent placement (suppresses sound/particles) |
| 4 | `0x10` | Filler override (skip filler offset adjustment on break) |
| 8 | `0x100` | Network update suppression |

### Relationship Between Blocks, Entities, and Components

Hytale uses an **Entity Component System (ECS)** architecture with two distinct stores:

| Store | Purpose | Key Component Examples |
|---|---|---|
| `ChunkStore` | Stores chunk-scoped data (block grids, block states, sections) | `BlockSection`, `BlockState`, `WorldChunk`, `ChunkColumn` |
| `EntityStore` | Stores world-scoped mobile entities | `BlockEntity`, `TransformComponent`, `Velocity`, `BoundingBox` |

**Blocks as grid data (ChunkStore):**
Most blocks exist purely as integer IDs in the `BlockSection` palette. They have no per-instance component overhead. Only blocks that require persistent state (chests, furnaces, etc.) get a `BlockState` component attached via `BlockComponentChunk`, which maps block indices to ECS `Holder<ChunkStore>` references.

**Blocks as entities (EntityStore):**
When a block becomes a mobile object (dropped item, falling sand), a `BlockEntity` component is created in the `EntityStore` along with `TransformComponent`, `Velocity`, `BoundingBox`, `DespawnComponent`, and `UUIDComponent`. This entity is independent of the chunk grid.

```
┌─────────────────────────────────────────────┐
│                  BlockType                  │  Asset definition (shared, read-only)
│   id, material, hitbox, gathering, etc.     │
└──────────┬───────────────────┬──────────────┘
           │                   │
    ┌──────▼──────┐    ┌───────▼───────┐
    │ Block Grid  │    │  BlockEntity  │
    │ (ChunkStore)│    │ (EntityStore) │
    ├─────────────┤    ├───────────────┤
    │ BlockSection│    │ blockTypeKey  │
    │  palette ID │    │ physics       │
    │  rotation   │    │ velocity      │
    │  filler     │    │ boundingBox   │
    │             │    │ transform     │
    │ BlockState? │    │ despawn       │
    │ (optional)  │    │ uuid          │
    └─────────────┘    └───────────────┘
```

### Block Events

**Package:** `com.hypixel.hytale.server.core.event.events.ecs`

Block interactions fire events through the `EventBus`. All cancellable events extend `CancellableEcsEvent`.

#### `PlaceBlockEvent`
Fired when a block is placed.
*   `Vector3i getTargetBlock()` / `void setTargetBlock(Vector3i)` — Block position (mutable).
*   `RotationTuple getRotation()` / `void setRotation(RotationTuple)` — Placement rotation (mutable).
*   `ItemStack getItemInHand()` — The item used to place the block (nullable).
*   `void setCancelled(boolean)` — Prevents placement.

#### `BreakBlockEvent`
Fired when a block is broken.
*   `BlockType getBlockType()` — The type of block being broken (immutable).
*   `Vector3i getTargetBlock()` / `void setTargetBlock(Vector3i)` — Block position (mutable).
*   `ItemStack getItemInHand()` — The item used to break the block (nullable).
*   `void setCancelled(boolean)` — Prevents breaking.

#### `DamageBlockEvent`
Fired each tick while a player is mining a block.
*   `BlockType getBlockType()` — The type of block being damaged (immutable).
*   `float getCurrentDamage()` — Cumulative damage before this tick.
*   `float getDamage()` / `void setDamage(float)` — Damage being applied this tick (mutable).
*   `void setCancelled(boolean)` — Prevents this tick's damage.

#### `UseBlockEvent.Pre` / `UseBlockEvent.Post`
Fired before and after a player uses (interacts with) a block.
*   `InteractionType getInteractionType()` — The type of interaction.
*   `InteractionContext getContext()` — Interaction context.
*   `BlockType getBlockType()` — The block being used.
*   `Vector3i getTargetBlock()` — Block position.
*   Pre variant: `void setCancelled(boolean)` — Prevents the interaction.

### Multi-Block Structures (Filler Blocks)

Blocks larger than 1×1×1 (e.g. doors, beds) use a **filler system**. The primary block occupies one position, and surrounding positions are filled with filler references that encode an offset back to the primary block.

*   `FillerBlockUtil.pack(x, y, z)`: Packs a 3D offset into a single integer.
*   `FillerBlockUtil.unpackX/Y/Z(filler)`: Extracts offset components.
*   When breaking a filler block, the offset is subtracted from the position to find the primary block, which is then removed (unless `0x10` settings flag is set).
*   Filler data is stored in the `fillerSection` palette of `BlockSection`.

### Block Ticking

Blocks that require periodic updates (e.g. crops, redstone) use the tick system managed by `BlockSection`:

1. `setTicking(index, true)` marks a block for the next tick cycle.
2. `scheduleTick(index, gameTime)` queues a tick for a future time (priority queue sorted by `Instant`).
3. `preTick(gameTime)` dequeues scheduled ticks whose time has arrived and copies the ticking set.
4. `forEachTicking(...)` iterates over ticking blocks and executes a `BlockTickStrategy` callback. The strategy returns `CONTINUE` (keep ticking), `STOP` (done), or `WAIT_FOR_ADJACENT_CHUNK_LOAD` (defer until neighbors are loaded).

### Block Rotation

Blocks support rotation via a `RotationTuple` (yaw, pitch, roll), each being a `Rotation` enum value. The tuple is stored as a single integer index in the `rotationSection` palette.

*   `RotationTuple.of(yaw, pitch, roll)`: Creates a rotation tuple.
*   `RotationTuple.index(yaw, pitch, roll)`: Computes the integer index.
*   `RotationTuple.get(int index)`: Resolves an index back to a tuple.
*   `BlockType.getRotationYawPlacementOffset()`: Defines a yaw offset applied on placement.

### Thread Safety

`BlockSection` uses a `StampedLock` to protect all palette access:
*   **Reads** attempt an optimistic read first (`tryOptimisticRead`), falling back to a read lock if validation fails.
*   **Writes** always acquire a write lock.
*   Changed positions are tracked in an `IntOpenHashSet` that is atomically swapped on `getAndClearChangedPositions()` for network delta sync.
*   `WorldChunk` uses a separate `StampedLock` for its lifecycle flags.
