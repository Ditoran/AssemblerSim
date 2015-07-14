sjmp init

org 000Bh
mov TH0, #3Ch
mov TL0, #B0h
lcall test
reti

init:
mov P2, #0
mov R0, #0
mov TMOD, #00000001b
mov TH0, #3Ch
mov TL0, #B0h
mov TCON, #00010000b
clr TF0
setb ET0
setb EA

ende:
sjmp ende

test:
cjne R0, #20, weiter1
inc P2
mov R0, #0
sjmp weiter2
weiter1:
inc R0
weiter2:
ret