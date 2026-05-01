package io.github.Earth1283.blockEx.engine.compiled

import io.github.Earth1283.blockEx.api.BlockProvider
import io.github.Earth1283.blockEx.api.BlockVector3
import io.github.Earth1283.blockEx.engine.ast.*

/** A rigid pattern that uses O(1) lookups for extreme performance. */
class CompiledPattern private constructor(val offsets: Map<BlockVector3, String>) {
    
    fun matches(provider: BlockProvider, origin: BlockVector3): Boolean {
        for ((offset, expectedMaterial) in offsets) {
            val target = origin + offset
            val actual = provider.getMaterialAt(target)
            if (expectedMaterial != "*" && !expectedMaterial.equals(actual, ignoreCase = true)) {
                return false
            }
        }
        return true
    }

    companion object {
        fun compile(root: MatcherNode): CompiledPattern {
            val map = mutableMapOf<BlockVector3, String>()
            walk(root, BlockVector3(0, 0, 0), map)
            return CompiledPattern(map)
        }

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
    }
}
