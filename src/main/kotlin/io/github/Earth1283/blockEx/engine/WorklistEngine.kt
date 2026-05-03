package io.github.Earth1283.blockEx.engine

import io.github.Earth1283.blockEx.api.BlockProvider
import io.github.Earth1283.blockEx.api.BlockVector3
import io.github.Earth1283.blockEx.engine.ast.*

class WorklistEngine(private val provider: BlockProvider) {

    fun matches(root: MatcherNode, origin: BlockVector3): Boolean {
        val worklist = ArrayDeque<Pair<MatcherNode, BlockVector3>>()
        val visited = mutableSetOf<Pair<MatcherNode, BlockVector3>>()

        worklist.add(root to origin)
        visited.add(root to origin)

        while (worklist.isNotEmpty()) {
            val (node, pos) = worklist.removeFirst()

            when (node) {
                is StartNode -> {
                    if (matchesMaterial(node.expectedMaterial, provider.getMaterialAt(pos))) {
                        val next = node.next
                        if (next == null) return true
                        if (visited.add(next to pos)) {
                            worklist.add(next to pos)
                        }
                    }
                }
                is DirectionNode -> {
                    var testPos = pos
                    for (dist in 1..node.range.last) {
                        testPos += node.direction.vector
                        if (!matchesMaterial(node.expectedMaterial, provider.getMaterialAt(testPos))) {
                            break // Blocked, cannot go further
                        }
                        if (dist >= node.range.first) {
                            val next = node.next
                            if (next == null) return true
                            if (visited.add(next to testPos)) {
                                worklist.add(next to testPos)
                            }
                        }
                    }
                    // Handle 0 range if applicable (though DirectionNode usually implies at least 1 move if range.first > 0)
                    if (node.range.first == 0) {
                        val next = node.next
                        if (next == null) return true
                        if (visited.add(next to pos)) {
                            worklist.add(next to pos)
                        }
                    }
                }
                is BranchNode -> {
                    // All branches must match from the CURRENT pos.
                    // Branches are lookarounds, they don't consume the main chain's progress.
                    val allBranchesMatch = node.branches.all { branch ->
                        WorklistEngine(provider).matches(branch, pos)
                    }
                    if (allBranchesMatch) {
                        val next = node.next
                        if (next == null) return true
                        if (visited.add(next to pos)) {
                            worklist.add(next to pos)
                        }
                    }
                }
            }
        }

        return false
    }

    private fun matchesMaterial(expected: String, actual: String): Boolean {
        if (expected == "*") return true
        return expected.equals(actual, ignoreCase = true)
    }
}
