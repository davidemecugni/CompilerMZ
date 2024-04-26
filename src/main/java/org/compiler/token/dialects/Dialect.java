package org.compiler.token.dialects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.compiler.token.TokenType;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class Dialect {
    private final String name;

    private Map<String, TokenType> wordToTokenMap;

    public Dialect(String name) {
        this.name = name;
        retrieveDialect();
    }

    private void retrieveDialect() {
        Gson gson = new Gson();
        JsonReader reader;
        try {
            reader = new JsonReader(new InputStreamReader(
                    Objects.requireNonNull(getClass().getResourceAsStream("/" + name + ".json"))));
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Dialect not found: " + name);
        }
        Type type = new TypeToken<Map<String, TokenType>>() {
        }.getType();
        Map<String, TokenType> data = gson.fromJson(reader, type);
        Set<String> uniqueKeys = new HashSet<>(data.keySet());
        if (uniqueKeys.size() < data.size()) {
            throw new IllegalArgumentException("Duplicate keys for tokens found in JSON");
        }
        for(String key : data.keySet()) {
            if (key.matches(".*\\s.*")) {
                throw new IllegalArgumentException("Key " + key + " contains whitespace");
            }
        }
        Trie trie = new Trie();
        for (String key : data.keySet()) {
            if (trie.containsPrefix(key)) {
                throw new IllegalArgumentException("Key " + key + " is a substring of another key");
            }
            trie.insert(key);
        }
        wordToTokenMap = new HashMap<>(data);
    }

    public Map<String, TokenType> getWordToTokenMap() {
        return wordToTokenMap;
    }

    @Override
    public String toString() {
        return "Dialect{" + "name='" + name + '\'' + ", wordToTokenMap=" + wordToTokenMap + '}';
    }
}
