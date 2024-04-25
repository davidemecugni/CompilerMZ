# CompilerMZ
![Compiler logo](READMESOURCES/logo.png) \
A full custom compiler for the .mz(Mecugni Zanasi) language! \
It generates an x86_64 assembly file. \
The program is then run by using nasm and ld(supports also the assembler and linker of your choice using only .asm compilation).

To compile the file the steps made by the compiler are:
- Tokenization  
  - transforms the chars present in the input file to tokens such as EXIT token
  - checks for unclosed multiline comments
- Parsing        
  - transforms the tokens list into a list of trees that represent the code
  - checks for syntax errors
  - checks for semantic errors
- Generating ASM 
  - part where the proper assembly is written into an output .asm file
  - checks for already declared variables
  
CompilerMZ by default compiles the file `input.mz` and generates the assembly file `output.asm`. \
If not specified it will also generate the object file `output.o` and the executable `output`. 
Here is the full man page:
```text
MZ Compiler by Davide Mecugni, Andrea Zanasi
(C) 2024

usage: CompilerMZ
 -c,--compile            compile only, no assembly and linking
 -d,--dialect <arg>      dialect to be used
 -e,--executable <arg>   final executable file
 -h,--help               print this message
 -i,--input <arg>        input .mz manz file
 -o,--output <arg>       output .asm assembly file
 -O,--object <arg>       .o object file(assembled .asm file)
 -t,--time               print time for given procedure
 -v,--verbose            verbose output
 -V,--version            print version
```


Standard dialect code example:
```manz
@@
Finds the nth prime(considering 2 the first prime number)
@@
{
    @Starts the count at 1
    let count = 1;
    let number = 2;
    let nThPrime = 54; @Should be 251

    while(count < nThPrime + 1) {
    let is_prime = true;
    let divisor = 2;

    @ Checks whether a number is prime
    while (divisor * divisor <= number & is_prime) {
        if (number % divisor == 0) {
            is_prime = false;
        }
        divisor = divisor + 1;
    }

    if (is_prime) {
        if (count == nThPrime) {
            @@
            If the nTh prime is found it exits with that number
            Note that 251 is the last prime before 255(max exit number on UNIX systems
            @@
            exit(number);
        }
        count = count + 1;
    }
    number = number + 1;
    }
}
```
# Features
- [x] Multi dialect support
  - [x] Create your custom dialect
  - [x] Full support for UNICODE characters
- [x] Comments
  - [x] Single line comments
  - [x] Multi line comments
- [x] Variables
  - [x] Initialization
  - [x] Declaration
  - [x] Automatic check for already declared variables
- [x] Exit
  - [x] Exit with variable
  - [x] Exit by default(0)
- [x] Arithmetic operations
  - [x] Addition
  - [x] Subtraction
  - [x] Multiplication
  - [x] Division
  - [x] Modulus
  - [x] Parenthesis operations
- [x] Logical operations
  - [x] Equal
  - [x] Not equal
  - [x] Greater than
  - [x] Greater or equal
  - [x] Less than
  - [x] Less or equal
  - [x] And
  - [x] Or
- [x] If statement
  - [x] Condition expression
  - [x] Scope
  - [x] Elif and Else
- [x] Variable Reassignment
- [x] While loop
  - [x] Condition expression
  - [x] Scope

# Get the compiler working
```shell
sudo apt-get install nasm
sudo apt-get install ld
```
To add compilerMZ to your path(if the project is under IdeaProjects and the commons-cli is installed):
```shell
echo "alias compilerMZ='java -classpath ~/IdeaProjects/CompilerMZ/target/classes:~/.m2/repository/commons-cli/commons-cli/1.6.0/commons-cli-1.6.0.jar org.compiler.CompilerMZ'" >> ~/.bashrc
```

To run the compiler from .jar file:
```shell
java -jar CompilerMZ-VERSION-jar-with-dependencies.jar -V
```

# The idea
![CompilerMZ](READMESOURCES/warning.png) \
The project was born during a OOP course at the University of Modena and Reggio Emilia. \
The idea was to create a compiler for a custom language, the .mz language. 
