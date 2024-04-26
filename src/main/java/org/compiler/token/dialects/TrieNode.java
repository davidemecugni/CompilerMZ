package org.compiler.token.dialects;

import java.util.HashMap;
import java.util.Map;

class TrieNode {
    Map<Character, TrieNode> children;

    public TrieNode() {
        this.children = new HashMap<>();
    }
}