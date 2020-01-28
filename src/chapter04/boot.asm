org 0x7c00

LOAD_ADDR EQU 0x8000

entry:
    mov ax, 0
    mov ss, ax
    mov ds, ax
    mov es, ax
    mov si, ax

readFloppy:
    mov ch, 1
    mov dh, 0
    mov cl, 2

    mov bx, LOAD_ADDR

    mov ah, 0x02
    mov al, 1
    mov dl, 0

    int 0x13

    jc fin

    jmp LOAD_ADDR

fin:
    HLT
    jmp fin
