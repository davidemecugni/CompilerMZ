global _start
_start:

     ;;value
     mov rax, 1
     push rax

     ;;value
     mov rax, 2
     push rax

     ;;value
     mov rax, 3
     push rax

     ;;value
     mov rax, 4
     push rax

     ;;value
     mov rax, 100
     push rax

     ;;identifier
     push QWORD [rsp + 0]

     ;;exit
     mov rax, 60
     pop rdi
     syscall
     ;;/exit

     ;;final exit
     mov rax, 60
     mov rdi, 0
     syscall
