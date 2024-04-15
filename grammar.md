# Grammar according to the EBNF

- [prog] -> [stmt]*
- [stmt]
  - exit([expr])
  - let *ident* = [expr]
- [expr]
  - int_lit
  - ident
  
### Notes
*) Zero or more \
+) One or more
