mov A,#0
mov dptr,#zeichen1
lcall definiereZeichen

mov A,#1
mov dptr,#zeichen2
lcall definiereZeichen

mov A, #01h
lcall cursorpos

mov A, #00h
lcall charaus

mov A, #05h
lcall cursorpos

mov A, #01h
lcall charaus

ende:
sjmp ende

zeichen1:
DB 01010b, 01010b, 01010b, 00000b, 10001b, 01110b, 00000b, 00000b

zeichen2:
DB 01000b, 00100b, 00010b, 11111b, 00010b, 00100b, 01000b, 00000b