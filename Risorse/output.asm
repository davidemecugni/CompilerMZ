global _start
_start:

     mov rax, 7
     push rax

     ;;exit
     mov rax, 60
     pop rdi

     syscall
     ;;/exit

     mov rax, 60
     mov rdi, 0
     syscall
