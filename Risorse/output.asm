global _start
_start:

     ;;value
     mov rax, 7
     push rax

     ;;value
     mov rax, 16
     push rax

     ;;value
     mov rax, 8
     push rax

     ;;identifier
     push QWORD [rsp + 16]

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
