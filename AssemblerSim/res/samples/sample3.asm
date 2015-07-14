mov R0, #00000010b ;2
mov R1, #00000011b ;3

mov A, R0
RL A
RL A
RL A
RL A
orl A, R1
mov P2, A

ende:
sjmp ende