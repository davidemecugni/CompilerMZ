package org.compiler.token.dialects;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTrie {
    @Test
    void testTrie() {
        Trie trie = new Trie();
        Set<String> uniqueKeys = new HashSet<>(Set.of("let", "exit", "if", "elif", "while", "++"));
        for (String key : uniqueKeys) {
            trie.insert(key);
        }
        assertTrue(trie.containsPrefix("le"));
        assertFalse(trie.containsPrefix("true"));
        assertFalse(trie.containsPrefix("whilst"));
        assertTrue(trie.containsPrefix("+"));
    }
}
