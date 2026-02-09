# Match System - Plugin Design Document

## Overview

A generalized, extensible match system for competitive PvP game modes. The system manages the core lifecycle of matches — creation, player assignment, state transitions, and cleanup — while remaining agnostic to specific game rules. Game mode developers extend the system through condition predicates, lifecycle handlers, and pluggable matchmaking strategies.

---

## Core Concepts

### Match States

A match progresses through three states:

```
PENDING ──→ ACTIVE ──→ ENDED
```

- **Pending**: The match exists and is accepting players. The start condition is evaluated each tick.
- **Active**: The match is in progress. The end condition is evaluated each tick.
- **Ended**: The match is over. Cleanup hooks fire, players are handled (typically returned to lobby), and the match instance is disposed.

There are no intermediate states at the system level. Game modes that need sub-phases (countdown, sudden death, post-game ceremony, etc.) manage those internally within the **Active** state.

### Player States (Within a Match)

Each player in a match has one of two statuses tracked by the system:

- **Connected**: The player is actively in the match.
- **Disconnected**: The player has lost connection or left. The game mode decides the policy (remove from match, allow reconnect window, substitute with NPC, etc.).

### Match State Object

Every match holds a state object that serves as the single source of truth for condition evaluation. It has two layers:

**Built-in fields** (managed automatically by the system):
- Current player list
- Player count
- Player statuses (connected/disconnected)
- Match creation timestamp
- Current match state (pending/active/ended)

**Custom fields** (managed by the developer / external systems):
- Arbitrary key-value pairs the developer defines
- Written to by external systems (timers, voting systems, score trackers, etc.)
- Examples: `isTimerElapsed`, `voteCount`, `roundsPlayed`, `lastPlayerStanding`

The match system never writes to custom fields. It only reads the full state object when evaluating conditions.

---

## Transition Conditions

### Mechanism

A condition is a **predicate function** over the match state object. The match system evaluates the appropriate condition once per tick:

- While **Pending**: evaluate the **start condition**
- While **Active**: evaluate the **end condition**

When a condition returns `true`, the transition fires immediately.

### Developer Responsibility

The developer defines one function per transition. Composition (AND/OR logic, multiple checks) is the developer's responsibility within that function. This keeps the system simple while allowing arbitrary complexity.

**Start condition examples:**
- Player count >= minimum threshold
- Player count >= minimum AND a ready timer has elapsed
- All players have voted to start
- Any combination of the above

**End condition examples:**
- Only one player remains connected
- A score threshold has been reached
- A match duration timer has expired
- A custom game-over flag has been set

### Important Behavior

- Conditions are evaluated every tick (30 TPS), so they should be lightweight.
- The condition receives a read-only view of the match state. It does not mutate state.
- External systems are responsible for updating custom state fields. The condition only checks current values.

---

## Match Type Definition

A match type bundles all configuration needed to create and run matches of a particular game mode. A developer registers a match type with:

| Component | Description |
|---|---|
| **Player limits** | Minimum and maximum player count for the match |
| **Start condition** | Predicate over match state that triggers pending → active |
| **End condition** | Predicate over match state that triggers active → ended |
| **Match handler** | Lifecycle hook implementation for game-specific behavior |
| **Custom state initializer** | (Optional) Sets up initial custom fields on the match state |

---

## Match Handler

The match handler is an interface the developer implements to define game-specific behavior. It has a **one-to-one** relationship with the match — it is the authority for that match's game logic.

### Lifecycle Methods

| Method | When Called | Purpose |
|---|---|---|
| `onPlayerAdded(player, matchState)` | A player is assigned to the match (pending or active) | Spawn the player, set up inventory, assign position, etc. |
| `onPlayerRemoved(player, matchState)` | A player leaves or is removed from the match | Clean up player-specific game state |
| `onPlayerDisconnected(player, matchState)` | A player's connection is lost | Decide policy: grace period, remove, replace with NPC |
| `onPlayerReconnected(player, matchState)` | A previously disconnected player returns | Restore player state, resume participation |
| `onMatchStart(matchState)` | Start condition met, match transitions to active | Initialize game world, teleport players, start timers |
| `onMatchEnd(matchState)` | End condition met, match transitions to ended | Determine winners, distribute rewards, announce results |
| `onCleanup(matchState)` | Match is being disposed after ending | Clean up game-specific resources (arenas, entities, scoreboards). Fires **before** match state is torn down so the handler still has access to all data. |

### Key Principles

- The handler is called **synchronously** during transitions. It runs before events are broadcast.
- The handler can read and write to custom match state fields.
- The handler does **not** control transitions — that's the condition system's job.

---

## Event Bus Integration

After the match handler processes a transition, events are fired on the server's event bus. These are for **cross-cutting observers** — systems that want to react to match activity without being the game mode itself.

### Events

| Event | Fired When |
|---|---|
| `MatchCreatedEvent` | A new match instance is created |
| `MatchStartedEvent` | Match transitions from pending to active |
| `MatchEndedEvent` | Match transitions from active to ended |
| `MatchDisposedEvent` | Match instance is cleaned up and removed |
| `MatchPlayerAddedEvent` | A player is added to a match |
| `MatchPlayerRemovedEvent` | A player is removed from a match |
| `MatchPlayerDisconnectedEvent` | A player disconnects during a match |
| `MatchPlayerReconnectedEvent` | A player reconnects to a match |

### Ordering

For any transition:
1. Match state is updated
2. Match handler method is called
3. Event is fired on the bus

This guarantees the handler (the authority) processes the transition before any observers react.

---

## Matchmaking System

The matchmaking system is responsible for:

1. **Tracking ready players** — Players mark themselves as ready; the system maintains a pool of ready players.
2. **Creating matches** — When needed, instantiate new pending matches from a match type definition.
3. **Assigning players** — Place ready players into pending matches using a pluggable strategy.

### Matchmaking Strategy

The strategy is a pluggable component that determines how ready players are assigned to matches. The system ships with a default strategy and supports custom implementations.

**Default strategy (simple fill):**
- Iterate through pending matches with available slots
- Assign ready players to the first match with room
- If no pending matches have room, create a new one
- No sorting, no skill rating, no preference matching

**Custom strategy examples:**
- Skill-based matchmaking (rank/MMR grouping)
- Party-aware matching (keep friends together)
- Region-based matching (latency optimization)
- Mode preference routing (when multiple match types are active)

### Player Readiness

- A player can **ready up** to enter the matchmaking pool.
- A player can **unready** to leave the pool before being assigned.
- Once assigned to a match, they are removed from the ready pool.
- A player can only be in **one match at a time** (enforced by the system).

---

## Match Lifecycle (End-to-End)

```
1. Player readies up
       │
       ▼
2. Matchmaking strategy assigns player to a pending match
   (or creates a new pending match first)
       │
       ▼
3. Match is PENDING
   - Accepting players
   - Start condition evaluated each tick
   - Players can leave/unready → match keeps waiting
       │
       ▼  (start condition returns true)
4. Match transitions to ACTIVE
   - Handler.onMatchStart() fires
   - MatchStartedEvent broadcast
   - End condition now evaluated each tick
   - Players play the game
       │
       ▼  (end condition returns true)
5. Match transitions to ENDED
   - Handler.onMatchEnd() fires
   - MatchEndedEvent broadcast
   - Players handled (typically sent to lobby)
       │
       ▼
6. Cleanup
   - Handler.onCleanup() fires (match state still accessible)
   - Match state torn down
   - MatchDisposedEvent broadcast
   - Match instance removed from the system
```

---

## Edge Cases & Considerations

### Empty Pending Matches

If all players leave a pending match before it starts, the system should **automatically dispose** the match. There is no reason to keep an empty pending match alive. The matchmaking system will create a new one when players are available.

### Stale Pending Matches

A pending match that never reaches its start condition could linger indefinitely. The match type definition should support an **optional pending timeout**. If set, the system disposes the match after the timeout elapses without the start condition being met, returning any remaining players to the ready pool.

### Player Uniqueness

The system **enforces** that a player can only be in one match at a time. Attempting to add a player who is already in a match is rejected. A player must leave or be removed from their current match before joining another.

### Disconnection During Pending State

If a player disconnects while in a pending match (before it starts), the system should **remove them from the match** and return them to an unready state. The game mode handler is notified but this is distinct from mid-match disconnection where the game mode might want to preserve their slot.

### Cleanup Ordering

When a match ends and cleanup fires:
1. `Handler.onMatchEnd()` — game logic finalization (winners, rewards)
2. Players are processed (sent to lobby, unready, etc.)
3. `Handler.onCleanup()` — game-specific resource teardown
4. Match state is torn down
5. Match instance is removed

The handler has access to the full match state (including player list and custom fields) during both `onMatchEnd` and `onCleanup`. This allows the developer to reference final game data during teardown.

### Condition Evaluation Timing

Since conditions are evaluated every tick, there is a potential for a condition to become true and then false within the same tick if external systems are modifying state concurrently. The system evaluates conditions at a **fixed point** in the tick cycle, reading a consistent snapshot of the match state.

### Rapid Transitions

If a start condition is met and then the end condition is *also* immediately true on the first active tick, the match will transition from pending → active → ended within two ticks. This is valid behavior — the handler methods still fire in order. Game mode developers should be aware that their end condition could technically be satisfied immediately.

---

## System Boundaries

The match system **owns**:
- Match lifecycle state machine (pending/active/ended)
- Player tracking within matches (add/remove/status)
- Condition evaluation loop
- Match creation and disposal
- Player uniqueness enforcement
- Ready pool management
- Matchmaking orchestration

The match system **does not own**:
- Team composition or team logic
- Game-specific rules, scoring, or win conditions (beyond the end predicate)
- World/arena management
- Player spawning, teleportation, or inventory
- Sub-phases within the active state
- What constitutes a "ready" player beyond the flag (e.g., level requirements)

---

## Extension Summary

A developer building a game mode on this system provides:

1. **Match type registration** — player limits, state initializer
2. **Start condition predicate** — when should the match begin?
3. **End condition predicate** — when should the match end?
4. **Match handler implementation** — what happens at each lifecycle event?
5. **(Optional) Custom matchmaking strategy** — how should players be grouped?
6. **(Optional) External systems** — timers, voting, scoring that write to custom match state fields
