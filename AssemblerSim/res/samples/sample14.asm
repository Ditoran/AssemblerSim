sjmp init

org 000Bh
mov TH0, #3Ch
mov TL0, #B0h
inc R0
lcall min
reti

init:
lcall initLCD
lcall loeschen

mov A, #03h
lcall cursorpos
mov A, #00111010b
lcall charaus

mov A, #06h
lcall cursorpos
mov A, #00111010b
lcall charaus

mov TH0, #3Ch
mov TL0, #B0h

mov R0, #0
mov R1, #0
mov R2, #8
mov R3, #12
mov TMOD, #00000001b
setb TR0
clr TF0

setb ET0
setb EA
main:
sjmp main



min:
cjne R0, #20, sekunden
mov R0, #0
inc R1
sekunden:
cjne R1, #60, minuten
mov R1, #0
inc R2
minuten:
cjne R2, #60, Stunden
mov R2, #0
inc R3
Stunden:
cjne R3, #24, Clear
mov R3, #0
Clear:

mov A, #01h
lcall cursorpos
mov A, R3
mov B, #10
div AB
lcall zifferaus
mov A, #02h
lcall cursorpos
mov R7, B
mov A, R7
lcall zifferaus

mov A, #04h
lcall cursorpos
mov A, R2
mov B, #10
div AB
lcall zifferaus
mov A, #05h
lcall cursorpos
mov R7, B
mov A, R7
lcall zifferaus

mov A, #07h
lcall cursorpos
mov A, R1
mov B, #10
div AB
lcall zifferaus
mov A, #08h
lcall cursorpos
mov R7, B
mov A, R7
lcall zifferaus

ret