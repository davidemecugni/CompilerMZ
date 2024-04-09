# CompilerMZ
![Compiler logo](READMESOURCES/logo.png) \
A full custom compiler for the .mz(Mecugni Zanasi) language! \
It generates an x86_64 assembly file. \
The program can then by run by using nasm and ld or the assembler and linker of your choice.\

To compile the file the steps made by the compiler are:
- Tokenization  
  - transforms the chars present in the input file to tokens such as EXIT token
- Parsing        
  - transforms the token list into a tree TBC
- Generating ASM 
  - part where the proper assembly is written into an output file
  
Added tester.sh utility to go from a .mz file to an executable file through:
- Compiling         
  - Using CompilerMZ.java
- Assembling      
  - Using nasm for the elf64 architecture
- Linking
  - Using ld linker

Example:
```manz
@commento
exit 42;
```
# The idea
![CompilerMZ](READMESOURCES/warning.png) \
The project was born during a OOP course at the University of Modena and Reggio Emilia. \
The idea was to create a compiler for a custom language, the .mz language. 