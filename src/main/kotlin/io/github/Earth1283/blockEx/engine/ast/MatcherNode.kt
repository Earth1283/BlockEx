package io.github.Earth1283.blockEx.engine.ast

import io.github.Earth1283.blockEx.api.BlockProvider
import io.github.Earth1283.blockEx.api.BlockVector3
import io.github.Earth1283.blockEx.api.Direction

sealed class MatcherNode {
    var next: MatcherNode? = null
}

class StartNode(val expectedMaterial: String, next: MatcherNode? = null) : MatcherNode() {
    init { this.next = next }
}

class DirectionNode(
    val direction: Direction,
    val range: IntRange,
    val expectedMaterial: String,
    next: MatcherNode? = null
) : MatcherNode() {
    init { this.next = next }
}

class BranchNode(val branches: List<MatcherNode>, next: MatcherNode? = null) : MatcherNode() {
    init { this.next = next }
}
