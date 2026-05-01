package io.github.Earth1283.blockEx.engine.ast

import io.github.Earth1283.blockEx.api.BlockProvider
import io.github.Earth1283.blockEx.api.BlockVector3
import io.github.Earth1283.blockEx.api.Direction

sealed class MatcherNode {
    abstract fun matches(provider: BlockProvider, currentPos: BlockVector3): Boolean
    
    protected fun matchesMaterial(expected: String, actual: String): Boolean {
        if (expected == "*") return true
        return expected.equals(actual, ignoreCase = true)
    }
}

class StartNode(val expectedMaterial: String, val next: MatcherNode? = null) : MatcherNode() {
    override fun matches(provider: BlockProvider, currentPos: BlockVector3): Boolean {
        if (!matchesMaterial(expectedMaterial, provider.getMaterialAt(currentPos))) return false
        return next?.matches(provider, currentPos) ?: true
    }
}
