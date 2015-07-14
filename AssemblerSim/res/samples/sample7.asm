mov P2, #0
mov R0, #0
mov R1, #0

inrunde:

cjne R0, #100, weiter1
sjmp win_player1

weiter1:

cjne R1, #100, weiter2
sjmp win_player2

weiter2:

jb P3.3, up_spieler1
jb P3.2, up_spieler2
sjmp inrunde

up_spieler1:
jb P3.3, up_spieler1
inc R0
sjmp inrunde

up_spieler2:

jb P3.2, up_spieler2
inc R1
sjmp inrunde

win_player1:

setb P2.7
setb P2.6
endlos1:
sjmp endlos1

win_player2:

setb P2.0
setb P2.1
endlos2:
sjmp endlos2