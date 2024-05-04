package org.compiler;

import org.compiler.token.TokenType;
import org.compiler.token.dialects.Dialect;
import org.compiler.token.tokens.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CrossCompiler {
    private final ArrayList<Token> tokens;
    private final Dialect dialect;
    private String crossCompiledCode;

    public CrossCompiler(ArrayList<Token> tokens, String dialect) {
        this.tokens = tokens;
        this.dialect = new Dialect(dialect);
        generateCrossCompiledCode();
    }

    /**
     * Generates the cross-compiled code from the tokens(used as an intermediate language).
     */
    private void generateCrossCompiledCode() {
        int indent = 0;
        Map<String, TokenType> wordToTokenMap = dialect.getWordToTokenMap();
        Map<TokenType, String> tokenToWordMap = new HashMap<>();
        boolean openingQuotes = true;
        for (String word : wordToTokenMap.keySet()) {
            tokenToWordMap.put(wordToTokenMap.get(word), word);
        }
        addMultiTokenTokens(tokenToWordMap);
        StringBuilder crossCompiledCodeSB = new StringBuilder();
        System.out.println(tokens);
        for (Token token : tokens) {
            TokenType type = token.getType();
            if (type == TokenType.open_curly || type == TokenType.close_curly) {
                if (type == TokenType.close_curly) {
                    crossCompiledCodeSB.delete(crossCompiledCodeSB.length() - 4, crossCompiledCodeSB.length());
                }
                crossCompiledCodeSB.append(tokenToWordMap.get(type)).append("\n");
                indent = (type == TokenType.open_curly) ? indent + 1 : indent - 1;
                crossCompiledCodeSB.append(getIndentation(indent));
                continue;
            }
            if (tokenToWordMap.containsKey(type)) {
                if (type == TokenType.comment) {
                    String commentSymbol = tokenToWordMap.get(TokenType.comment);
                    String commentText = ((TokenComment) token).getComment();
                    if (!((TokenComment) token).isMultiline()) {
                        crossCompiledCodeSB.append(commentSymbol).append(commentText);
                    } else {
                        crossCompiledCodeSB.append(commentSymbol).append(commentSymbol).append("\n");
                        crossCompiledCodeSB.append(correctIndentation(commentText, indent));
                        crossCompiledCodeSB.append(commentSymbol).append(commentSymbol);
                    }
                    crossCompiledCodeSB.append("\n");
                    crossCompiledCodeSB.append(getIndentation(indent));
                    continue;
                }
                crossCompiledCodeSB.append(tokenToWordMap.get(type));
                if (type == TokenType.semi) {
                    crossCompiledCodeSB.append("\n");
                    crossCompiledCodeSB.append(getIndentation(indent));
                    continue;
                }
                if (type == TokenType.quotes) {
                    if (openingQuotes) {
                        openingQuotes = false;
                    } else {
                        crossCompiledCodeSB.append(" ");
                        openingQuotes = true;
                    }
                    continue;
                }
                crossCompiledCodeSB.append(" ");
            } else {
                if (type == TokenType.int_lit) {
                    crossCompiledCodeSB.append(((TokenIntLit) token).getValue());
                }
                if (type == TokenType.string_lit) {
                    crossCompiledCodeSB.append(((TokenString) token).getContent());
                    continue;
                }
                if (type == TokenType.ident) {
                    crossCompiledCodeSB.append(((TokenIdent) token).getName());
                }

                crossCompiledCodeSB.append(" ");
            }
        }
        crossCompiledCode = crossCompiledCodeSB.toString();
    }

    /**
     * Adds multi-token tokens to the tokenToWordMap. They are not specified in the dialects, so they need to be added
     * manually.
     *
     * @param tokenToWordMap
     *            the map of tokens to words
     */
    private void addMultiTokenTokens(Map<TokenType, String> tokenToWordMap) {
        tokenToWordMap.put(TokenType.logic_not_eq,
                tokenToWordMap.get(TokenType.not) + tokenToWordMap.get(TokenType.eq));
        tokenToWordMap.put(TokenType.logic_eq, tokenToWordMap.get(TokenType.eq) + tokenToWordMap.get(TokenType.eq));
        tokenToWordMap.put(TokenType.logic_ge,
                tokenToWordMap.get(TokenType.logic_gt) + tokenToWordMap.get(TokenType.eq));
        tokenToWordMap.put(TokenType.logic_le,
                tokenToWordMap.get(TokenType.logic_lt) + tokenToWordMap.get(TokenType.eq));
    }

    public String getCrossCompiledCode() {
        return crossCompiledCode;
    }

    private String getIndentation(int indentation) {
        return "    ".repeat(Math.max(0, indentation));
    }

    private String correctIndentation(String code, int indentation) {
        return code.replaceAll("(?m)^ +", getIndentation(indentation));
    }
}
