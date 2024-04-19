# Grammar for CompilerMZ according to the EBNF ISO/IEC 14977:1996

- prog = stmt*
- stmt = exit, open_par, space*, expr, space*, close_par, semi | let, space*, ident, space*, eq, space*, expr, semi | comment
- expr = int_lit | ident | expr, space*, operator, space*, expr | open_par, space*, expr, space*, close_par
- operator = add | sub | mul | div
- int_lit = digit+
- ident = valid_char+
- comment = comment_single | comment_multi
- comment_single = comment_terminal, any_char*, [new_line]
- comment_multi = comment_terminal, comment_terminal, any_char*, comment_terminal, comment_terminal
# Terminals
- digit = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
- new_line = (? ISO 6429 character Line Feed ?)
- space = (? ISO 6429 character Whitespace ?) - new_line
- any_char = (? ISO 6429 character ?) - comment_terminal
- valid_char = any_char+ - space+  - [terminal](? Single character terminal ?)+ 

(* Editable Terminals, might depend on dialect *)

- terminal = comment_terminal | exit | open_par | close_par | let | eq | add | sub | mul | div | semi
- comment_terminal = "@"
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


### Notes
- * Zero or more 
- + One or more 
- - Removed
- [] Optional(Zero or one)
- | Either one or the other 
- "CHAR" The character CHAR or string CHAR
- (? CHAR ?) The special character/string CHAR
- (* COMMENT *) A comment

