# Grammar for CompilerMZ according to the EBNF ISO/IEC 14977:1996

- prog = stmt*
- stmt = scope | exit_stmt | let_stmt | if_stmt | comment_stmt
- scope = open_curly, stmt*, close_curly
- if_stmt = if, space*, #TBC
- exit_stmt = exit, space*, open_par, space*, expr, space*, close_par, semi
- let_stmt = let, space*, ident, space*, eq, space*, expr, semi
- expr = int_lit | ident | expr, space*, operator, space*, expr | open_par, space*, expr, space*, close_par
- operator = add | sub | mul | div
- int_lit = digit+
- ident = (valid_char - digit), valid_char*
- comment_stmt = comment_stmt_single | comment_stmt_multi
- comment_stmt_single = comment, ((? UNICODE ?) - comment)*,  (? UNICODE ?)*, new_line
- comment_stmt_multi = comment, comment, ((? UNICODE ?) - comment)*, comment, comment
# Terminals
- digit = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
- new_line = (? ISO 6429 character Line Feed ?)
- space = (? ISO 6429 character Whitespace ?) - new_line
- valid_char = ((? UNICODE ?) - (? Single char comment ?)) - space - (? Single char terminal ?)

(* Editable Terminals, might depend on dialect *)

- terminal = comment_terminal | exit | open_par | close_par | let | eq | add | sub | mul | div | semi
- comment = "@"
- exit = "exit"
- open_par = "("
- close_par = ")"
- let = "let"
- eq = "="
- add = "+"
- sub = "-"
- mul = "*"
- div = "/"
- semi = ";"
- open_curly = "{"
- close_curly = "}"
- if = "if"

### Notes
- * Zero or more 
- + One or more 
- - Removed
- [] Optional(Zero or one)
- | Either one or the other 
- "CHAR" The character CHAR or string CHAR
- (? CHAR ?) The special character/string CHAR
- (* COMMENT *) A comment

