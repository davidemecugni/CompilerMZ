package org.compiler.token;

/**
 * The types of allowed tokens
 */
public enum TokenType {
    _exit, int_lit, semi, open_paren, close_paren, ident, let, eq, plus, star, minus, slash, comment, open_curly,
    close_curly, _if, _else, elif, _while, percent, not, logic_not_eq, logic_eq, logic_gt, logic_lt, logic_ge, logic_le,
    logic_and, logic_or, _true, _false, print, quotes, string_lit
}
