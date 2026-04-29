# The Cursed Village

A Java text-based RPG. The player wakes at the edge of a forgotten village trapped under a dark curse and has three in-game days to gather three sacred relics, defeat or evade the cursed creatures that roam the world, and break the curse before the village is lost forever.

The game runs entirely in the terminal: every action is typed as a short command and every event is described in prose. The world keeps moving while the player thinks because the time system, the monster, and the merchant all run on independent threads.


---

## Run

After compiling, launch the game with:

```
java Main
```

---

## How to play

At the `>` prompt, type one command per line. The full command list is:

| Command | What it does |
|---|---|
| `look` | Re-print the current scene description and what is in it |
| `go <scene>` | Travel to one of the listed exits |
| `take <item>` | Pick up an item from the current scene |
| `drop <item>` | Drop a carried item in the current scene |
| `inventory` | List the player's carried items |
| `status` | Show HP, gold, and current location |
| `talk <character>` | Interact with a character standing in the same scene |
| `attack <character>` | Attack the forest monster (or anything else) |
| `use <item>` | Use an item in a context-dependent way |
| `give <item> to <character>` | Hand an item to a character |
| `light <item>` | Light the torch |
| `help` | Show the full command list |
| `quit` | Flee the village (counts as a loss) |

### Winning

To win the game, the player must collect three relics and use them in the right place:

1. Acquire a fresh **Apple** from the Abandoned House and give it to the Village Elder, who rewards the player with the **Protective Charm**.
2. Take the **Ancient Scroll** from the same house.
3. Travel through the Dark Forest into the Cave and take the **Magic Key**.
4. Return to the Old Church carrying all three relics. The Charm calms the Cursed Spirit and the Guard recognises the player as worthy and unlocks the chamber.
5. Enter the Hidden Underground Chamber and use the Ancient Scroll on the altar to break the curse.

### Losing

The game ends in failure if any of the following occur:

- The player's HP reaches zero (killed by the monster, the cursed spirit, or the dark forest at night).
- Three full in-game days pass without the curse being broken.
- The player flees the village (the `quit` command).

