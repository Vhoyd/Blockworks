

# Event Flow

## Table of Contents
[Block Damage](event-flow.md#block-damage)<br>
[Block Break](event-flow.md#block-break)<br>
[Block Break Abort](event-flow.md#block-break-abort)

## Legend
Parallelogram: External Bukkit event <br>
Rectangle: Process <br>
Diamond: Condition <br>
Oval: End of flow

## Block Damage
```mermaid
flowchart TD
A[/BlockDamageEvent/]
A --> Z{Event cancelled?}
Z --> |Yes|C
Z --> |No|B{Valid BlockInstance?}
B --> |No|C([End])
B --> |Yes|D{Valid BlockBreaker?}
D --> |No|C
D --> |Yes|F[Fire BlockInstanceCreateEvent]
F --> G{Event cancelled?}
G -->|Yes|C
G --> |No|I[Fire BlockInstanceStartBreakEvent]
I --> J{Event cancelled?}
J --> |Yes|C
J --> |No|L[Associate BlockBreaker <br> with new BlockInstance]
L --> C
```
## Block Break
Note: this event is only listened to for handling completely-vanilla blocks; BlockBreakEvents 
by default only fire when a *client* breaks a block.
```mermaid
flowchart TD
    A[/BlockBreakEvent/]
    A --> B{Event cancelled?}
    B -->|Yes|C([End])
    B --> |No|D{Valid BlockInstance?}
    D --> |No|C
    D --> |Yes|E{Valid BlockBreaker?}
    E --> |No|C
    E --> |Yes|J[Cancel BlockBreakEvent]
    J --> F[Fire VanillaInstanceBrokenEvent]
    F --> G{Event cancelled?}
    G -->|Yes|C
    G --> |No|H[Break block]
    H --> C
    H --- I["Note: calls the <br> breakNaturally method with <br> the player's held ItemStack <br> as the argument"]
```
## Block Break Abort
```mermaid
flowchart TD
    A[/BlockDamageAbortEvent/]
    A --> B{Valid BlockBreaker?}
    B --> |No|C([End])
    B --> |Yes|D{BlockBreaker is <br> breaking something?}
    D --> |No|C
    D --> |Yes|E[Fire BlockInstanceBreakAbortEvent]
    E --> F{Event cancelled?}
    F --> |Yes|C
    F --> |No|H[Unsubscribe BlockInstance]
    H --> C
    H --- I["Note: cancellation preserves <br> current BlockInstance"]
```