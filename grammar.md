# Grammar for CompilerMZ according to the EBNF

- [prog] -> [stmt]*
- [stmt] ::= "exit(" [expr] ")" | "let" [ident] "=" [expr]
- [expr] ::= [int_lit] | [ident] | \[expr] \[operator] [expr]
- [operator] ::= "+"
- [int_lit] ::= [digit]+
- [ident] ::= [letter]+
- [digit] ::= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9
- [letter] ::= "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" | "j" | "k" | "l" | "m" | "n" | "o" | "p" | "q" | "r" | "s" | "t" | "u" | "v" | "w" | "x" | "y" | "z" | "A" | "B" | "C" | "D" | "E" | "F" | "G" | "H" | "I" | "J" | "K" | "L" | "M" | "N" | "O" | "P" | "Q" | "R" | "S" | "T" | "U" | "V" | "W" | "X" | "Y" | "Z

### Notes
- *) Zero or more 
- +) One or more 
- |) Either one or the other 
- "CHAR") The character CHAR or string CHAR

