package org.compiler.token.dialects;

import java.util.HashMap;
import java.util.Map;

/**
 * A trie node for the Trie data structure.
 */
class TrieNode {
    final Map<Character, TrieNode> children;

    public TrieNode() {
        this.children = new HashMap<>();
    }
}