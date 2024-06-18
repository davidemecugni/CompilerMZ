# CompilerMZ :computer:
<div align="left">
  <img src="https://visitor-badge.laobi.icu/badge?page_id=DavideMecugni.CompilerMZ.&" alt="badge" />
</div>
![Compiler logo](READMESOURCES/logo.png)

CompilerMZ is a custom compiler and cross-compiler for multiple languages! \
It generates an x86_64 assembly file. The program is then executed by using nasm(assembler) \
and gcc(linker).

Fast AF! \
![C vs MZ](READMESOURCES/CvsMZ.png)

## Table of Contents

- [Features](#features-sparkles)
- [Inner workings](#inner-workings-gear)
- [Getting Started](#getting-started-rocket)
- [Usage](#usage-hammer_and_wrench)
- [Grammar](#grammar-book)
- [The Idea](#the-idea-bulb)
- [Examples](examples.md)
- [UML](#UML)

## Features :sparkles:

- Multi dialect support :earth_africa:
    - Create your custom dialect
    - Current dialects supported:
        - default_dialect : a C vibe dialect :keyboard:
        - zanna : an italian dialect :it:
        - emilian[^1] : a dialect from Emilia-Romagna :spaghetti:
        - emoji : an emoji dialect, for the brave ones :smiley:
    - Full support for UNICODE characters, feel free to add yours!
- Comments :speech_balloon:
    - Single line comments
    - Multi line comments
- Variables :abacus:
    - All variables are 64bit signed numbers
    - Initialization
    - Declaration
    - Automatic check for already declared variables
- Exit :door:
    - Exit with variable
    - Exit by default(0)
- Arithmetic operations :heavy_plus_sign:
    - Addition
    - Subtraction
    - Multiplication
    - Division
    - Modulus
    - Parenthesis operations
    - Negative numbers
- Logical operations :mag:
    - Equal
    - Not equal
    - Greater than
    - Greater or equal
    - Less than
    - Less or equal
    - And
    - Or
- If statement :triangular_flag_on_post:
    - Condition expression
    - Scope
    - Elif and Else
- Variable Reassignment :arrows_counterclockwise:
    - Reassign a variable
- While loop :repeat:
    - Condition expression
    - Scope
- Print :printer:
    - Print a variable
    - Print a string
    - Print a number
- Read :blue_book:
    - Read a number
    - Store the number in a variable
    - Returns -1 if the input is not a number

## Inner workings :gear:

To compile the .mz file, the steps made by the compiler are:

- Tokenization :scissors:
    - reads the input file and creates a list of tokens according to the dialect chosen
    - transforms the chars present in the input file to tokens such as EXIT token
    - checks for unclosed comments or strings
- Parsing :deciduous_tree:
    - transforms the tokens list into a list of trees that represent the code
    - checks for syntax errors
    - checks for semantic errors
- Generating ASM :hammer:
    - part where the proper assembly is written into an output .asm file
    - checks for already declared variables
    - garbage collector for variables(removes variables out of scope)

CompilerMZ by default compiles the .asm file provided(-i) and generates the executable file. \
If not specified it will also generate the intermediate .asm assembly file(with comments!) and the object .o file.

## Getting Started :rocket:

To get the compiler working, you need to install `nasm` and `gcc`:

```shell
sudo apt-get install nasm
sudo apt-get install gcc
```

## Usage :hammer_and_wrench:

Here is the full man page:

```shell
MZ Compiler by Davide Mecugni, Andrea Zanasi
(C) 2024

usage: CompilerMZ
 -c,--compile            compile only, no assembly and linking
 -d,--dialect <arg>      dialect to be used
 -e,--executable <arg>   final executable file
 -f,--format             format the code, specify the dialect with -d flag
 -h,--help               print this message
 -i,--input <arg>        input .mz manz file
 -o,--output <arg>       output .asm assembly file
 -O,--object <arg>       .o object file(assembled .asm file)
 -t,--translate <arg>    cross-compiles a dialect to another one, requires
                         "dialectIn,dialectOut"
 -V,--version            print version
 -v,--verbose            verbose output
 -x,--execute            executes the newly created file
```

## Grammar :book:

For more details about the [EBNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form) grammar of the
language, please refer to the [grammar.md](grammar.md) file.

## The Idea :bulb:

The project was born during a OOP course at the University of Modena and Reggio Emilia. The idea was to create a
compiler for a custom language, the .mz language.  
<img src="READMESOURCES/warning.png" alt="CompilerMZ"></img>

## UML
![UML Image](READMESOURCES/CompilerMZUML.jpg)
## Footnotes

[^1]: Emilian is a group of dialects of the Emilian language spoken in the region of Emilia-Romagna, Italy. The specific
dialect followed is specified in "Dizionario del dialetto carpigiano" by Graziano Malagoli and Anna Maria Ori (Modena,
2011).
