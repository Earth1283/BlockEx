========
BlockEx
========

**Regex for Blocks in Minecraft**

BlockEx is a high-performance Minecraft plugin engine that allows developers and server owners to define and match 3D block structures using a "Regex-like" directional path system.

Features
========

* **Directional Path DSL**: Define structures using an ergonomic Kotlin DSL or a simple text-based config.
* **Dual-Engine Architecture**:
    * **AST State Machine**: Supports flexible patterns with variable quantifiers (e.g., 1 to 5 blocks) and greedy backtracking.
    * **Compiled Engine**: Ultra-fast $O(1)$ lookup for rigid, static structures.
* **.blockex Configuration**: Server owners can define patterns and triggers in text files without touching code.
* **Adventure Integration**: Rich, color-coded error reporting in the console with hoverable suggestions.
* **Action Dispatcher**: Automatically execute commands or send messages when a structure is matched.

Developer Usage (Kotlin DSL)
============================

Define a pattern in your plugin:

.. code-block:: kotlin

    val magicPortal = blockEx {
        useCompiled = true // Optional: optimize for O(1) matching
        start("minecraft:obsidian")
            .up(3, "minecraft:obsidian")
            .branch {
                east(2, "minecraft:obsidian")
                down(3, "minecraft:obsidian")
            }
    }

    // Match against a location
    if (magicPortal.matches(BukkitProvider(world), origin)) {
        // Success!
    }

Server Owner Configuration (.blockex)
======================================

Place files in ``plugins/BlockEx/patterns/portal.blockex``:

.. code-block:: text

    @useCompiled
    @trigger {
      event: BlockPlaceEvent
      filter: { material: "minecraft:diamond_block" }
    }

    start("minecraft:diamond_block")
      -> up(1..3, "minecraft:glass")
      -> branch { north(1, "minecraft:gold_block") }

    @action {
      type: command
      run: "say %player% has constructed a magic pillar!"
    }

Installation
============

1. Download the latest jar.
2. Place it in your server's ``plugins`` folder.
3. Restart the server to generate the ``patterns/`` directory.

License
=======

BlockEx is licensed under the MIT License.
