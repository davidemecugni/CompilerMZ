# Grammar for CompilerMZ according to the EBNF ISO/IEC 14977:1996

- prog = stmt*
- stmt = scope | exit_stmt | let_stmt | if_stmt | comment_stmt | assignment | while_stmt | func_built_in
- scope = open_curly, ws*, stmt*, ws*, close_curly
- if_stmt = if, ws*, open_par, ws*, expr, ws*, close_par, ws*, scope
- elif_stmt = elif, ws*, open_par, ws*, expr, ws*, close_par, ws*, scope, ws*, elif_stmt, ws*, [else_stmt]
- else_stmt = else, ws*, scope
- while_stmt = while, ws*, open_par, ws*, expr, ws*, close_par, ws*, scope
- exit_stmt = exit, ws*, open_par, ws*, expr, ws*, close_par, semi
- let_stmt = let, ws*, ident, ws*, eq, ws*, expr, semi
- func_built_in = print_stmt | read_stmt
- print_stmt = print, ws*, open_par, ws*, (ident | string_lit | int_lit), ws*, close_par, semi
- read_stmt = read, ws*, open_par, ws*, ident, ws*, close_par, semi
- expr = int_lit | ident | expr, ws*, operator, ws*, expr | open_par, ws*, expr, ws*, close_par
- operator = add | sub | mul | div | mod | logic_gt | logic_ge | logic_lt | logic_ge | logic_and | logic_or
- int_lit = digit+ | true | false
- string_lit = quote, ((? UNICODE ?) - quote)*, quote
- ident = (valid_char - digit), valid_char*
- assignment = ident, ws*, eq, vws*, expr, ws*, semi
- comment_stmt = comment_stmt_single | comment_stmt_multi
- comment_stmt_single = comment, ((? UNICODE ?) - comment)*,  (? UNICODE ?)*, new_line
- comment_stmt_multi = comment, comment, ((? UNICODE ?) - comment)*, comment, comment

### Terminals

- digit = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
- new_line = (? ISO 6429 character Line Feed ?)
- ws = (? HORIZONTAL TABULATION ?) | (? NEW LINE ?) | (? FORM FEED ?) | (? CARRIAGE RETURN ?) | (? SPACE ?)
- valid_char = (? UNICODE ?) - (? Single char comment ?) - ws - (? Single char terminal ?)

### (* Editable Terminals, might depend on dialect *)

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
- mod = "%"
- semi = ";"
- open_curly = "{"
- close_curly = "}"
- if = "if"
- elif = "elif"
- else = "else"
- while = "while"
- not = "!"
- logic_gt = ">"
- logic_ge = ">="
- logic_lt = "<"
- logic_le = "<="
- logic_and = "&"
- logic_or = "|"
- true = "true"
- false = "false"
- print = "print"
- quote = "\""

### Notes

- \* Zero or more
- \+ One or more
- \- Removed
- [] Optional(Zero or one)
- | Either one or the other
- "CHAR" The character CHAR or string CHAR
- (? CHAR ?) The special character/string CHAR
- (* COMMENT *) A comment

