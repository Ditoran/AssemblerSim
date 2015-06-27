public class InputToSource {
	
	public static String[][] get(String[] input, int lines) {
		String[][] source = new String[lines][4];

		// Array mit Leerzeichen füllen, um Fehlermeldungen zu vermeiden
		source = fillArray(source);

		String temp = "";
		// Wird benötigt für die Teilung der einzelnen Wörter
		String[] tempwords = { "", "", "", "" };
		// Zur Überprüfung auf Leerzeichen, siehe unten
		char tempchar;
		int countspaces = 0;
		
		//Kommas korrigieren, falls zwischen den Befehlen kein Leerzeichen ist
		
		for (int i = 0; i < input.length; i++) {
			input[i] = input[i].replaceAll(", ", ",");
			input[i] = input[i].replaceAll(",", ", ");
		}

		for (int j = 0; j < lines; j++) {
			temp.trim();
			temp = input[j].replaceAll(",", "");

			// Es wird durch die Leerzeichen geprüft, aus wie vielen Elementen
			// der Befehl besteht
			for (int i = 0; i < temp.length(); i++) {
				tempchar = temp.charAt(i);
				if (tempchar == ' ') {
					countspaces++;
				} // end of if
			} // end of for
			
			//TODO: Fix für Wortmarken, die mit einem Befehl in derselben Zeile stehen
			/*for (int i = 0; i < lines; i++) {
				for (int k = 0; k < tempwords.length; k++) {
					if (temp.charAt(k) == ':') {
						
					}
					String[] temp1;
					
				}
			}*/
			
			// DB Zeilen im Quelltext formatieren (Bsp.: DB 123,12345,12345)
			if (temp.startsWith("DB ")) {
				if (temp.startsWith("DB '")) {
					//Bsp.: DB 'Zahlenspiel',0
					temp = temp.substring(4);
					if (temp.substring(temp.length()-2, temp.length()).equals("'0")) {
						temp = temp.substring(0, temp.length()-2);
					} else {
						temp = temp.substring(0, temp.length()-3);
					}
					source[j][0] = "DB ";
					source[j][1] = temp;
					countspaces = 0;
					continue;
				} else {
					temp = temp.replace(' ', ',');
					temp = temp.replace("DB,", "DB ");
					countspaces = 1;
				}
								
			}

			// Zerteilung in temporäres Array und Einordnung in 2D Array
			tempwords = temp.split(" ", 0);

			source[j][0] = tempwords[0];

			if (countspaces == 1) {
				source[j][1] = tempwords[1];
			}
			if (countspaces == 2) {
				source[j][1] = tempwords[1];
				source[j][2] = tempwords[2];
			}
			if (countspaces == 3) {
				source[j][1] = tempwords[1];
				source[j][2] = tempwords[2];
				source[j][3] = tempwords[3];
			} // end of if

			countspaces = 0;
		}

		return source;
	}

	// Array mit leeren Strings füllen, um Fehlermeldungen zu verhindern
	public static String[][] fillArray(String[][] get) {
		int length = get.length;
		for (int y = 0; y < length; y++) {
			for (int x = 0; x < 4; x++) {
				get[y][x] = "";
			} // end of for
		} // end of for
		return get;
	}

	public static String[] getLine(String[][] source, int line) {
		String[] temp = { "", "", "", "" };
		for (int i = 0; i < 4; i++) {
			temp[i] = source[line][i];
		} // end of for
		return temp;
	}
}