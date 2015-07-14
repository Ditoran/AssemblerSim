;Dieses Programm sortiert zwei Zahlen nach der
;Größe. Die kleinere wird in R0 gespeichert,
;die größere in R1

;Zahlen festlegen:
mov R0, #134
mov R1, #133

;Zahlen sichern
mov A, R0
mov R6, A
mov A, R1
mov R7, A

;Beide Zahlen werden nacheinander um 1
;verkleinert. Die Zahl, die zuerst 0 erreicht, ist
;die kleinere.

anfang:
djnz R0, weiter
sjmp erstekleiner
weiter:
djnz R1, anfang
sjmp zweitekleiner
;weiter2:
;sjmp anfang

erstekleiner:
mov A, R6
mov R0, A
mov A, R7
mov R1, A
sjmp ende

zweitekleiner:
mov A, R6
mov R1, A
mov A, R7
mov R0, A
sjmp ende

ende:
sjmp ende