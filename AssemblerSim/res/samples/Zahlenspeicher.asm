mov P2, #0
mov R0, #30h
;Eingabe
begin:
lcall warte_eingabe
cjne R0, #53, begin
mov P2, #255
;Ausgabe
jumper:
mov R0, #30h
begin1:
lcall warte_ausgabe
cjne R0, #53, begin1
mov R0, #48
sjmp begin1

 warte_eingabe:
 mov R3, #50
 sprung1:
 lcall taster_eingabe
 djnz R3, sprung1
 ret
 
 warte_ausgabe:
 mov R3, #50
 sprung11:
 lcall taster_ausgabe
 djnz R3, sprung11
 ret
 
 taster_eingabe:
 jnb P3.2, ende1
 mov A, P1
 mov @R0, A
 inc R0
 tasterlos:
 jb P3.2, tasterlos
 ende1:
 ret
 
 taster_ausgabe:
 jnb P3.2, ende2
 mov A, @R0
 inc R0
 mov P2, A
 tasterlos1:
 jb P3.2, tasterlos1
 ende2:
 ret