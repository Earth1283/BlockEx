package io.github.Earth1283.blockEx.api.dsl

import io.github.Earth1283.blockEx.api.BlockProvider
import io.github.Earth1283.blockEx.api.BlockVector3
import io.github.Earth1283.blockEx.api.Direction
import io.github.Earth1283.blockEx.engine.ast.*
import io.github.Earth1283.blockEx.engine.compiled.CompiledPattern

import io.github.Earth1283.blockEx.engine.WorklistEngine

class Pattern(val astRoot: MatcherNode, val useCompiled: Boolean) {
    private val compiled by lazy { CompiledPattern.compile(astRoot) }
    
    fun matches(provider: BlockProvider, pos: BlockVector3): Boolean {
        return if (useCompiled) {
            compiled.matches(provider, pos)
        } else {
            WorklistEngine(provider).matches(astRoot, pos)
        }
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

fun blockEx(block: PatternBuilder.() -> Unit): Pattern {
    val builder = PatternBuilder()
    builder.block()
    return builder.build()
}
