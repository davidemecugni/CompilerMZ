# Grammar for CompilerMZ according to the EBNF ISO/IEC 14977:1996

- prog = stmt*
- stmt = exit, open_par, space*, expr, space*, close_par | let, space*, ident, space*, eq, space* expr
- expr = int_lit | ident | expr, space*, operator, space*, expr | open_par, space*, expr, space*, close_par
- operator = add | sub | mul | div
- int_lit = digit+
- ident = letter+
- comment = comment_single | comment_multi
- comment_single = comment_terminal, any_char*, [new_line]
- comment_multi = comment_terminal, comment_terminal, any_char*, comment_terminal, comment_terminal
# Terminals
- digit = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
- letter = "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" | "j" | "k" | "l" | "m" | "n" | "o" | "p" | "q" | "r" | "s" | "t" | "u" | "v" | "w" | "x" | "y" | "z" | "A" | "B" | "C" | "D" | "E" | "F" | "G" | "H" | "I" | "J" | "K" | "L" | "M" | "N" | "O" | "P" | "Q" | "R" | "S" | "T" | "U" | "V" | "W" | "X" | "Y" | "Z
- new_line = ? ISO 6429 character Line Feed ?
- space = ? ISO 6429 character Space ?
- any_char = ? Any ASCII Char ? - comment_terminal
### Editable Terminals
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


### Notes
- * Zero or more 
- + One or more 
- - Removed
- [] Optional(Zero or one)
- | Either one or the other 
- "CHAR" The character CHAR or string CHAR
- ? CHAR ? The special character/string CHAR

