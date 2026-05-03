# BlockEx 🧱⚡

**Regex for Blocks in Minecraft**

BlockEx is a modern, high-performance engine for matching 3D block structures. Whether you are building custom multiblock machines, magic portals, or decorative triggers, BlockEx provides a powerful "Regex-like" way to define shapes.

## ✨ Features

- **Directional DSL**: Define paths like `up(3).east(1).branch { ... }`.
- **Dual Matching Engine**:
  - **NFA AST Engine**: High-performance execution using NFA simulation with memoization to prevent catastrophic backtracking (no "Regex DoS").
  - **Compiled Engine**: Pre-calculated $O(1)$ relative coordinate maps for maximum performance on static shapes.
- **Hardened Security**: Tokenizer limits protect against malicious or oversized configuration files.
- **Easy Configuration**: Server owners can create `.blockex` files to add new structures without any coding.
- **Interactive Errors**: Leveraging [Adventure](https://docs.advntr.dev/), the plugin provides clear, hoverable error messages in your console.
- **Reactive Triggers**: Automatically check for patterns when blocks are placed, broken, or interacted with.

## 🛠 For Developers (Kotlin DSL)

Add BlockEx to your project and start defining patterns effortlessly:

```kotlin
val structure = blockEx {
    start("minecraft:diamond_block")
        .up(2, "minecraft:iron_block")
        .branch { north(1, "minecraft:gold_block") }
}

val isMatch = structure.matches(BukkitProvider(world), location)
```

## 📝 For Server Owners (.blockex)

Create custom structures in `plugins/BlockEx/patterns/`:

```text
@trigger {
  event: BlockPlaceEvent
  filter: { material: "minecraft:diamond_block" }
}

start("minecraft:diamond_block")
  -> up(1..3, "minecraft:glass")

@action {
  type: command
  run: "broadcast %player% built a magic structure!"
}
```

## 🚀 Getting Started

1. Drop the `BlockEx.jar` into your `plugins` folder.
2. Start the server.
3. Define your patterns in the `patterns/` folder or via the API.
4. Enjoy performant 3D structural matching!

---

**Built with Kotlin & Performance in mind.**
