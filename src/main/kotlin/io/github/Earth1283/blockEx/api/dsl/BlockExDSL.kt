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
