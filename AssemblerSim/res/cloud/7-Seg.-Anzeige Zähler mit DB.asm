mov R4, #0
main:
mov A, R4
lcall leseTabelle
mov P2, A
inc R4
lcall warte
cjne R4, #16, main
mov R4, #0
sjmp main

leseTabelle:
mov dptr, #tabelle
movc A, @A+dptr
ret


tabelle:
DB 7Eh, 00010010b, 10111100b, 10110110b, 11010010b, 11100110b, 11101110b, 00110010b, 11111110b, 11110110b, 11111010b, 11001110b, 01101100b, 10011110b, 11101100b, 11101000b

warte:
mov R2, #1
warte3:
mov R1, #255
warte2:
mov R0, #255
warte1:
djnz R0, warte1
djnz R1, warte2
djnz R2, warte3
ret