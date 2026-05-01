# Compiled Engine Improvement Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Improve `CompiledPattern` to correctly handle material collisions during compilation and expand test coverage to ensure robustness.

**Architecture:** Update the `walk` function in `CompiledPattern.kt` to merge materials when multiple branches or nodes specify the same coordinate. Introduce collision detection that throws `IllegalStateException` for conflicting specific materials. Expand tests to cover complex patterns and wildcards.

**Tech Stack:** Kotlin, JUnit 5

---

### Task 1: Expand Tests in CompiledPatternTest.kt

**Files:**
- Modify: `.worktrees/feat-blockex/src/test/kotlin/io/github/Earth1283/blockEx/engine/compiled/CompiledPatternTest.kt`

- [ ] **Step 1: Add tests for BranchNode, Wildcards, and Long Chains**
- [ ] **Step 2: Add test for conflicting materials (expected to fail until Task 2 is implemented)**

```kotlin
    @Test
    fun testCompileBranchNode() {
        val branch1 = DirectionNode(Direction.UP, 1..1, "minecraft:glass", null)
        val branch2 = DirectionNode(Direction.NORTH, 1..1, "minecraft:stone", null)
        val root = StartNode("minecraft:diamond_block", BranchNode(listOf(branch1, branch2), null))
        
        val compiled = CompiledPattern.compile(root)
        val provider = object : BlockProvider {
            override fun getMaterialAt(position: BlockVector3): String {
                return when (position) {
                    BlockVector3(0, 0, 0) -> "minecraft:diamond_block"
                    BlockVector3(0, 1, 0) -> "minecraft:glass"
                    BlockVector3(0, 0, -1) -> "minecraft:stone"
                    else -> "minecraft:air"
                }
            }
        }
        assertTrue(compiled.matches(provider, BlockVector3(0, 0, 0)))
    }

    @Test
    fun testCompileWildcard() {
        val root = StartNode("*", DirectionNode(Direction.UP, 1..1, "minecraft:glass", null))
        val compiled = CompiledPattern.compile(root)
        val provider = object : BlockProvider {
            override fun getMaterialAt(position: BlockVector3): String = if (position == BlockVector3(0, 1, 0)) "minecraft:glass" else "any"
        }
        assertTrue(compiled.matches(provider, BlockVector3(0, 0, 0)))
    }

    @Test
    fun testCompileConflictingMaterials() {
        val branch1 = DirectionNode(Direction.UP, 1..1, "minecraft:glass", null)
        val branch2 = DirectionNode(Direction.UP, 1..1, "minecraft:stone", null)
        val root = StartNode("minecraft:diamond_block", BranchNode(listOf(branch1, branch2), null))
        
        assertThrows<IllegalStateException> {
            CompiledPattern.compile(root)
        }
    }
    
    @Test
    fun testCompileLongChain() {
        var node: MatcherNode = DirectionNode(Direction.UP, 1..1, "stone", null)
        for (i in 0 until 10) {
            node = DirectionNode(Direction.UP, 1..1, "stone", node)
        }
        val root = StartNode("diamond_block", node)
        val compiled = CompiledPattern.compile(root)
        // Check if it has 12 entries (StartNode + 11 DirectionNodes)
        assertTrue(compiled.offsets.size == 12)
    }
```

- [ ] **Step 3: Run tests to verify failure**

Run: `./gradlew test --tests io.github.Earth1283.blockEx.engine.compiled.CompiledPatternTest`
Expected: `testCompileConflictingMaterials` FAIL (it won't throw exception currently)

### Task 2: Improve Material Merging in CompiledPattern.kt

**Files:**
- Modify: `.worktrees/feat-blockex/src/main/kotlin/io/github/Earth1283/blockEx/engine/compiled/CompiledPattern.kt`

- [ ] **Step 1: Implement `mergeMaterial` helper or inline logic in `walk`**

```kotlin
        private fun mergeMaterial(map: MutableMap<BlockVector3, String>, pos: BlockVector3, newMaterial: String) {
            val existing = map[pos]
            if (existing == null || existing == "*") {
                map[pos] = newMaterial
            } else if (newMaterial != "*") {
                if (!existing.equals(newMaterial, ignoreCase = true)) {
                    throw IllegalStateException("Conflicting materials at $pos: $existing vs $newMaterial")
                }
            }
        }
```

- [ ] **Step 2: Update `walk` function to use material merging**

```kotlin
        private fun walk(node: MatcherNode?, currentPos: BlockVector3, map: MutableMap<BlockVector3, String>) {
            if (node == null) return
            when (node) {
                is StartNode -> {
                    mergeMaterial(map, currentPos, node.expectedMaterial)
                    walk(node.next, currentPos, map)
                }
                is DirectionNode -> {
                    if (node.range.first != node.range.last) {
                        throw IllegalArgumentException("Cannot compile dynamic range: ${node.range}")
                    }
                    val dist = node.range.first
                    var pos = currentPos
                    for (i in 1..dist) {
                        pos += node.direction.vector
                        mergeMaterial(map, pos, node.expectedMaterial)
                    }
                    walk(node.next, pos, map)
                }
                is BranchNode -> {
                    for (branch in node.branches) {
                        walk(branch, currentPos, map)
                    }
                    walk(node.next, currentPos, map)
                }
            }
        }
```

- [ ] **Step 3: Run tests to verify success**

Run: `./gradlew test --tests io.github.Earth1283.blockEx.engine.compiled.CompiledPatternTest`
Expected: ALL PASS

- [ ] **Step 4: Commit changes**

```bash
git add .worktrees/feat-blockex/src/main/kotlin/io/github/Earth1283/blockEx/engine/compiled/CompiledPattern.kt .worktrees/feat-blockex/src/test/kotlin/io/github/Earth1283/blockEx/engine/compiled/CompiledPatternTest.kt
git commit -m "fix: improve compiled pattern material merging and expand test coverage"
```
