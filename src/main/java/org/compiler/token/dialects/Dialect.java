package org.compiler.token.dialects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.compiler.token.TokenType;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Dialect {
    private final String name;

    private Map<String, TokenType> wordToTokenMap;

    public Dialect(String name) {
        this.name = name;
        retrieveDialect();
    }

    private void retrieveDialect() {
        Map<String, TokenType> data = retrieveJson();
        Set<String> uniqueKeys = new HashSet<>(data.keySet());
        if (uniqueKeys.size() < data.size()) {
            throw new IllegalArgumentException("Duplicate keys for tokens found in JSON");
        }
        Set<TokenType> uniqueValues = new HashSet<>(data.values());
        if (uniqueValues.size() < data.size()) {
            throw new IllegalArgumentException("Duplicate values for tokens found in JSON");
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
        if(data.size() != 25){
            throw new IllegalArgumentException("Dialect must contain 25 tokens");
        }
        wordToTokenMap = new HashMap<>(data);
        checkDialectCompleteness();
    }

    private Map<String, TokenType> retrieveJson() {
        Gson gson = new Gson();
        JsonReader reader;
        try {
            reader = new JsonReader(new InputStreamReader(
                    Objects.requireNonNull(getClass().getResourceAsStream("/" + name + ".json")), StandardCharsets.UTF_8));
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Dialect not found: " + name);
        }
        Type type = new TypeToken<Map<String, TokenType>>() {
        }.getType();
        return gson.fromJson(reader, type);
    }

    private void checkDialectCompleteness() {
        Map<String, TokenType> defaultDialect = retrieveJson();

        for (TokenType tokenType : defaultDialect.values()) {
            if (!wordToTokenMap.containsValue(tokenType)) {
                throw new IllegalArgumentException("Dialect does not contain TokenType: " + tokenType);
            }
        }
    }
    public Map<String, TokenType> getWordToTokenMap() {
        return wordToTokenMap;
    }

    @Override
    public String toString() {
        return "Dialect{" + "name='" + name + '\'' + ", wordToTokenMap=" + wordToTokenMap + '}';
    }
}
