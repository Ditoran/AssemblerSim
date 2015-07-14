;P3.3 zählt hoch und Taster P3.2 schaltet P3.3 Interrupt aus

sjmp init

org 0003h
cpl EX1
clr IE1
reti

org 0013h
inc P2
reti


init:
mov P2, #0
setb IT0
setb IT1
clr IE0
clr IE1
setb EX0
setb EX1
setb EA


main:
sjmp main