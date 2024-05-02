package org.compiler.token.tokens;

import org.compiler.token.TokenType;

public class TokenComment extends Token {
    private final String comment;
    private final boolean multiline;

    public TokenComment(String comment, boolean multiline) {
        super(TokenType.comment);
        this.comment = comment;
        this.multiline = multiline;
    }

    public String getComment() {
        return comment;
    }

    public boolean isMultiline() {
        return multiline;
    }

    @Override
    public String toString() {
        return "TokenComment{" + "comment='" + comment + '\'' + ", l=" + getLine() + ", col_s=" + getColumnStart()
                + ", col_e=" + getColumnEnd() + '}';
    }
}
