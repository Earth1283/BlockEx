# Kotlin DSL API Completion Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete the Kotlin DSL API by adding all directional methods and branching support to `ChainBuilder`, and fix a bug in `PatternBuilder`.

**Architecture:** 
- `ChainBuilder` will have methods for `up`, `down`, `north`, `south`, `east`, `west`.
- Each directional method will support `count: Int` and `range: IntRange`.
- `branch` method will add a `BranchNode` to the chain.
- `PatternBuilder` will delay building the AST until its own `build()` method is called.

**Tech Stack:** Kotlin

---

### Task 1: Update BlockExDSL.kt

**Files:**
- Modify: `src/main/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSL.kt`

- [ ] **Step 1: Add imports and fix PatternBuilder**

```kotlin
package io.github.Earth1283.blockEx.api.dsl

import io.github.Earth1283.blockEx.api.BlockProvider
import io.github.Earth1283.blockEx.api.BlockVector3
import io.github.Earth1283.blockEx.api.Direction
import io.github.Earth1283.blockEx.engine.ast.*
import io.github.Earth1283.blockEx.engine.compiled.CompiledPattern

class Pattern(val astRoot: MatcherNode, val useCompiled: Boolean) {
    private val compiled by lazy { CompiledPattern.compile(astRoot) }
    
    fun matches(provider: BlockProvider, pos: BlockVector3): Boolean {
        return if (useCompiled) compiled.matches(provider, pos) else astRoot.matches(provider, pos)
    }
}

class PatternBuilder {
    var useCompiled = false
    private var chainBuilder: ChainBuilder? = null
    
    fun start(material: String): ChainBuilder {
        val cb = ChainBuilder(material)
        chainBuilder = cb
        return cb
    }
    
    fun build(): Pattern {
        val root = chainBuilder?.build() ?: throw IllegalStateException("Pattern must have a start node")
        return Pattern(root, useCompiled)
    }
}
// ...
```

- [ ] **Step 2: Add directional methods and branch to ChainBuilder**

```kotlin
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

    fun up(count: Int, material: String) = up(count..count, material)
    fun up(range: IntRange, material: String) = addStep { next -> DirectionNode(Direction.UP, range, material, next) }

    fun down(count: Int, material: String) = down(count..count, material)
    fun down(range: IntRange, material: String) = addStep { next -> DirectionNode(Direction.DOWN, range, material, next) }

    fun north(count: Int, material: String) = north(count..count, material)
    fun north(range: IntRange, material: String) = addStep { next -> DirectionNode(Direction.NORTH, range, material, next) }

    fun south(count: Int, material: String) = south(count..count, material)
    fun south(range: IntRange, material: String) = addStep { next -> DirectionNode(Direction.SOUTH, range, material, next) }

    fun east(count: Int, material: String) = east(count..count, material)
    fun east(range: IntRange, material: String) = addStep { next -> DirectionNode(Direction.EAST, range, material, next) }

    fun west(count: Int, material: String) = west(count..count, material)
    fun west(range: IntRange, material: String) = addStep { next -> DirectionNode(Direction.WEST, range, material, next) }

    fun branch(block: ChainBuilder.() -> Unit): ChainBuilder {
        val cb = ChainBuilder("*")
        cb.block()
        val branchStep = cb.buildStepsOnly() ?: throw IllegalArgumentException("Branch cannot be empty")
        return addStep { next -> BranchNode(listOf(branchStep), next) }
    }

    private fun addStep(step: (MatcherNode?) -> MatcherNode): ChainBuilder {
        steps.add(step)
        return this
    }
}
```

### Task 2: Update BlockExDSLTest.kt

**Files:**
- Modify: `src/test/kotlin/io/github/Earth1283/blockEx/api/dsl/BlockExDSLTest.kt`

- [ ] **Step 1: Add test for branching**

```kotlin
    @Test
    fun testBranchingDSL() {
        val pattern = blockEx {
            start("minecraft:stone")
                .branch {
                    up(1, "minecraft:glass")
                }
                .branch {
                    down(1, "minecraft:glass")
                }
        }
        
        val ast = pattern.astRoot as StartNode
        assertTrue(ast.next is io.github.Earth1283.blockEx.engine.ast.BranchNode)
        val branch1 = ast.next as io.github.Earth1283.blockEx.engine.ast.BranchNode
        assertTrue(branch1.branches[0] is io.github.Earth1283.blockEx.engine.ast.DirectionNode)
        
        assertTrue(branch1.next is io.github.Earth1283.blockEx.engine.ast.BranchNode)
        val branch2 = branch1.next as io.github.Earth1283.blockEx.engine.ast.BranchNode
        assertTrue(branch2.branches[0] is io.github.Earth1283.blockEx.engine.ast.DirectionNode)
    }
```

### Task 3: Verification

- [ ] **Step 1: Run tests**

Run: `./gradlew test`
Expected: ALL tests pass.

### Task 4: Finalize

- [ ] **Step 1: Commit and update TODO.md**

Run: `git add . && git commit -m "feat: complete Kotlin DSL API and fix PatternBuilder bug"`
Modify: `TODO.md` mark Task 6 as complete.
