# CompilerMZ
![Compiler logo](READMESOURCES/logo.png) \
A full custom compiler for the .mz(Mecugni Zanasi) language!

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
@commento@
exit 42;
```

  
