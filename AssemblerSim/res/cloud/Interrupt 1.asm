; Hauptprogramm zählt P2 hoch. Bei Interruptauslösung
; wird R0 um eins erhöht

sjmp init

org 0003h
inc R0
reti

init:
mov R0, #0
mov P2, #0
setb IT0
clr IE0
setb EX0
setb EA

main:
inc P2
sjmp main