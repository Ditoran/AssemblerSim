lcall initLCD
lcall loeschen

mov dptr, #text1
lcall textzeile1

mov dptr, #text2
lcall textzeile2

ende:
sjmp ende

text1:
DB 'Example Text',0

text2:
DB 'It works!',0