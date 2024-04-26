package org.compiler.token.dialects;

class Trie {
    private final TrieNode root;

    Trie() {
        root = new TrieNode();
    }

    void insert(String word) {
        TrieNode current = root;
        for (char l : word.toCharArray()) {
            current = current.children.computeIfAbsent(l, c -> new TrieNode());
        }
    }

    boolean containsPrefix(String prefix) {
        TrieNode current = root;
        for (char l : prefix.toCharArray()) {
            TrieNode node = current.children.get(l);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return true;
    }
}