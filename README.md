# CompilerMZ
![Compiler logo](READMESOURCES/logo.png) \
A full custom compiler for the .mz(Mecugni Zanasi) language! \
It generates an x86_64 assembly file. \
The program can then by run by using nasm and ld or the assembler and linker of your choice.

To compile the file the steps made by the compiler are:
- Tokenization  
  - transforms the chars present in the input file to tokens such as EXIT token
- Parsing        
  - transforms the token list into a tree with prog as root
- Generating ASM 
  - part where the proper assembly is written into an output file
  
CompilerMZ by default compiles the file `input.mz` and generates the file `input.asm`. \
The `input.asm` file is then assembled using NASM and linked using ld by default. \
The following flags are available:


Example:
```manz
@commento
let x = 1;
let y = 2;
let z = 3;

exit(x);
```
# Features
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
# Get the compiler working
```shell
sudo apt-get install nasm
sudo apt-get install ld
```
To add compilerMZ to your path(if the project is under IdeaProjects and the commons-cli is installed):
```shell
echo "alias compilerMZ='java -classpath ~/IdeaProjects/CompilerMZ/target/classes:~/.m2/repository/commons-cli/commons-cli/1.6.0/commons-cli-1.6.0.jar org.compiler.CompilerMZ'" >> ~/.bashrc
```
# The idea
![CompilerMZ](READMESOURCES/warning.png) \
The project was born during a OOP course at the University of Modena and Reggio Emilia. \
The idea was to create a compiler for a custom language, the .mz language. 
