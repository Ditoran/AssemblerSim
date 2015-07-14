mov R0, #30h
mov A, #3
mov @R0, A

mov R0, #31h
mov A, #0
mov @R0, A

mov R0, #32h
mov A, #1
mov @R0, A

mov R0, #30h
mov A, @R0
mov R3, A

mov R0, #31h

berechne:
mov A, @R0

inc R0

add A, @R0

inc R0

mov @R0, A
dec R0

djnz R3, berechne
ende:
sjmp ende