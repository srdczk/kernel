org 0x7c00; 程序从0x7c00 处开始加载

; 寄存器设置
entry:
    mov ax, 0
    mov ss, ax
    mov ds, ax
    mov es, ax
    mov si, msg
; 将 si 中依次输出
putloop:
    mov al, [si]
    add si, 1
    cmp al, 0
    je fin
    mov ah, 0x0e
    mov bx, 15
    int 0x10
    jmp putloop

fin:
    HLT
    jmp fin

msg:
    DB  0x0a, 0x0a
    db  "nimasile";屏幕上显示
    db  0x0a
    db  0

