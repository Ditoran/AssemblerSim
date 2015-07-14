lcall initLCD
lcall loeschen
mov R4, #0
mov A, R4
lcall cursorpos

main:
mov A, R4
lcall zifferaus
lcall warte
inc R4
cjne R4, #10, main
mov R4, #0

sjmp main

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