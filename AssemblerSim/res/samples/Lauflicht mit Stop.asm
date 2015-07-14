begin:
mov P2, #1
lcall wait
rotateleft:

jb P2.7, rotateright
mov A, P2
rl A
mov P2, A
lcall wait
sjmp rotateleft

rotateright:
jb P2.1, begin
mov A, P2
rr A
mov P2, A
lcall wait
sjmp rotateright

stopp:
abfrage:
jnb P3.2, abfrage
ret

wait:
mov R2, #1
schleife_3:
mov R1, #255
schleife_2:
mov R0, #255
schleife_1:
jnb P3.3, weiter
lcall stopp
weiter:
djnz R0, schleife_1
djnz R1, schleife_2
djnz R2, schleife_3
ret