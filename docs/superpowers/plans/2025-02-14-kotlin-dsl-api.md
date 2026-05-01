# Kotlin DSL API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a Kotlin DSL for creating BlockEx patterns ergonomically.

**Architecture:** Use the Builder pattern with Kotlin DSL features (trailing lambdas, receiver types) to construct an AST (MatcherNode tree) that can be optionally compiled into a CompiledPattern.

**Tech Stack:** Kotlin, JUnit 5

---

### Task 1: DSL Infrastructure and Start Node

**Files:**
- Create: `src/main/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSL.kt`
- Test: `src/test/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSLTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package io.github.Earth1283.blockEx.api.dsl

import io.github.Earth1283.blockEx.engine.ast.StartNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BlockExDSLTest {
    @Test
    fun testStartNodeDSL() {
        val pattern = blockEx {
            start("minecraft:diamond_block")
        }
        
        val ast = pattern.astRoot
        assertTrue(ast is StartNode)
        assertEquals("minecraft:diamond_block", (ast as StartNode).expectedMaterial)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests io.github.Earth1283.blockEx.api.dsl.BlockExDSLTest`
Expected: Compilation error (unresolved reference `blockEx`)

- [ ] **Step 3: Write minimal implementation**

```kotlin
package io.github.Earth1283.blockEx.api.dsl

import io.github.Earth1283.blockEx.api.Direction
import io.github.Earth1283.blockEx.engine.ast.*
import io.github.Earth1283.blockEx.engine.compiled.CompiledPattern

class Pattern(val astRoot: MatcherNode, val useCompiled: Boolean) {
    private val compiled by lazy { CompiledPattern.compile(astRoot) }
    
    fun matches(provider: io.github.Earth1283.blockEx.api.BlockProvider, pos: io.github.Earth1283.blockEx.api.BlockVector3): Boolean {
        return if (useCompiled) compiled.matches(provider, pos) else astRoot.matches(provider, pos)
    }
}

class PatternBuilder {
    var useCompiled = false
    private var headNode: MatcherNode? = null
    
    fun start(material: String): ChainBuilder {
        val cb = ChainBuilder(material)
        headNode = cb.build()
        return cb
    }
    
    fun build(): Pattern {
        return Pattern(headNode ?: throw IllegalStateException("Pattern must have a start node"), useCompiled)
    }
}

class ChainBuilder(private val startMat: String) {
    private val steps = mutableListOf<(MatcherNode?) -> MatcherNode>()
    
    fun buildStepsOnly(): MatcherNode? {
        var current: MatcherNode? = null
        for (step in steps.reversed()) {
            current = step(current)
        }
        return current
    }

    fun build(): StartNode = StartNode(startMat, buildStepsOnly())
}

fun blockEx(block: PatternBuilder.() -> Unit): Pattern {
    val builder = PatternBuilder()
    builder.block()
    return builder.build()
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew test --tests io.github.Earth1283.blockEx.api.dsl.BlockExDSLTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSL.kt src/test/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSLTest.kt
git commit -m "feat: add DSL infrastructure and start node"
```

### Task 2: Directional Steps (Up, Down, North, South, East, West)

**Files:**
- Modify: `src/main/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSL.kt`
- Modify: `src/test/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSLTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
    @Test
    fun testDirectionalDSL() {
        val pattern = blockEx {
            start("minecraft:diamond_block")
                .up(3, "minecraft:glass")
                .down(1, "minecraft:stone")
                .north(2, "minecraft:oak_log")
                .south(1..2, "minecraft:leaves")
        }
        
        val ast = pattern.astRoot as StartNode
        val upNode = ast.next as DirectionNode
        assertEquals(Direction.UP, upNode.direction)
        assertEquals(3..3, upNode.range)
        
        val downNode = upNode.next as DirectionNode
        assertEquals(Direction.DOWN, downNode.direction)
        
        val northNode = downNode.next as DirectionNode
        assertEquals(Direction.NORTH, northNode.direction)
        
        val southNode = northNode.next as DirectionNode
        assertEquals(Direction.SOUTH, southNode.direction)
        assertEquals(1..2, southNode.range)
    }
```

- [ ] **Step 2: Run test to verify it fails**

- [ ] **Step 3: Write minimal implementation**

Update `ChainBuilder` in `src/main/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSL.kt`:

```kotlin
class ChainBuilder(private val startMat: String) {
    private val steps = mutableListOf<(MatcherNode?) -> MatcherNode>()

    private fun addStep(direction: Direction, range: IntRange, material: String) = apply {
        steps.add { next -> DirectionNode(direction, range, material, next) }
    }

    fun up(count: Int, material: String) = addStep(Direction.UP, count..count, material)
    fun up(range: IntRange, material: String) = addStep(Direction.UP, range, material)
    
    fun down(count: Int, material: String) = addStep(Direction.DOWN, count..count, material)
    fun down(range: IntRange, material: String) = addStep(Direction.DOWN, range, material)

    fun north(count: Int, material: String) = addStep(Direction.NORTH, count..count, material)
    fun north(range: IntRange, material: String) = addStep(Direction.NORTH, range, material)

    fun south(count: Int, material: String) = addStep(Direction.SOUTH, count..count, material)
    fun south(range: IntRange, material: String) = addStep(Direction.SOUTH, range, material)

    fun east(count: Int, material: String) = addStep(Direction.EAST, count..count, material)
    fun east(range: IntRange, material: String) = addStep(Direction.EAST, range, material)

    fun west(count: Int, material: String) = addStep(Direction.WEST, count..count, material)
    fun west(range: IntRange, material: String) = addStep(Direction.WEST, range, material)

    // ... build methods ...
}
```

- [ ] **Step 4: Run test to verify it passes**

- [ ] **Step 5: Commit**

### Task 3: Branching Support

**Files:**
- Modify: `src/main/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSL.kt`
- Modify: `src/test/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSLTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
    @Test
    fun testBranchingDSL() {
        val pattern = blockEx {
            start("minecraft:diamond_block")
                .branch {
                    up(1, "minecraft:glass")
                }
                .down(1, "minecraft:stone")
        }
        
        val ast = pattern.astRoot as StartNode
        val branchNode = ast.next as BranchNode
        assertEquals(1, branchNode.branches.size)
        assertTrue(branchNode.branches[0] is DirectionNode)
        
        val downNode = branchNode.next as DirectionNode
        assertEquals(Direction.DOWN, downNode.direction)
    }
```

- [ ] **Step 2: Run test to verify it fails**

- [ ] **Step 3: Write minimal implementation**

Add `branch` to `ChainBuilder`:

```kotlin
    fun branch(block: ChainBuilder.() -> Unit) = apply {
        val subChain = ChainBuilder("ignored") // material ignored for steps only
        subChain.block()
        val branchRoot = subChain.buildStepsOnly()
        steps.add { next -> BranchNode(listOfNotNull(branchRoot), next) }
    }
```

- [ ] **Step 4: Run test to verify it passes**

- [ ] **Step 5: Commit**

### Task 4: Compiled Pattern Support

**Files:**
- Modify: `src/main/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSL.kt`
- Modify: `src/test/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSLTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
    @Test
    fun testCompiledDSL() {
        val pattern = blockEx {
            useCompiled = true
            start("minecraft:diamond_block")
                .up(1, "minecraft:glass")
        }
        
        assertTrue(pattern.useCompiled)
        // We can't easily check if it's using the compiled engine internally without mocking,
        // but we can check if it matches correctly.
        val provider = io.github.Earth1283.blockEx.api.BlockProvider.createEmpty()
        provider.setBlock(0, 0, 0, "minecraft:diamond_block")
        provider.setBlock(0, 1, 0, "minecraft:glass")
        
        assertTrue(pattern.matches(provider, io.github.Earth1283.blockEx.api.BlockVector3(0, 0, 0)))
    }
```

- [ ] **Step 2: Run test to verify it fails**

- [ ] **Step 3: Write minimal implementation**

(Implementation should already handle `useCompiled` if using the snippet provided by the user).

- [ ] **Step 4: Run test to verify it passes**

- [ ] **Step 5: Commit**

### Task 5: Final Cleanup and TODO Update

- [ ] **Step 1: Update TODO.md**
- [ ] **Step 2: Final Verification**
- [ ] **Step 3: Commit**
