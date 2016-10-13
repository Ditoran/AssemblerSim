public class CodeChecker {
	// Pr�ft Befehle auf richtige Schreibweise und verbessert notfalls
	// Muss noch vervollst�ndigt werden

	private boolean validCode = false;
	private String[][] source;

	public CodeChecker(String[][] source) {

		this.setSource(source);
		
		//Befehle korrigieren, falls falsch geschrieben
		String[] operation = { "mov", "setb", "clr", "sjmp", "djnz", "cjne",
				"inc", "dec", "rl", "rr", "jz", "jnz", "jb", "jnb", "mul",
				"div", "cpl", "lcall", "movc", "add", "swap", "anl", "orl",
				"xrl", "xch", "xchd" };
		String[] target = { "P2", "R0", "R1", "R2", "R3", "R4", "R5", "R6",
				"R7", "P2.0", "P2.1", "P2.2", "P2.3", "P2.4", "P2.5", "P2.6",
				"P2.7", "@R0", "@R1", "A", "EA" , "EX0", "EX1", "ET0", "ET1",
				"TMOD", "TCON", "TH0", "TH1", "TL0", "TL1", "DATA", "EQU"};
		for (int i = 0; i < this.source.length; i++) {
			for (int j = 0; j < operation.length; j++) {
				if (this.source[i][0].toLowerCase().equals(operation[j])) {
					this.source[i][0] = operation[j];
				}
			}
		}
		for (int i = 0; i < this.source.length; i++) {
			for (int j = 0; j < target.length; j++) {
				if (this.source[i][1].toUpperCase().equals(target[j])) {
					this.source[i][1] = target[j];
				} else if (this.source[i][2].toUpperCase().equals(target[j])) {
					this.source[i][2] = target[j];
				}
			}
		}
		for (int i = 0; i < this.source.length; i++) {
		    if (this.source[i][1].equals("EQU") || this.source[i][1].equals("DATA")) {
			//this.source[i][0] = this.source[i][2];
			for (int j = 0; j < this.source.length; j++) {
			    if (this.source[j][1].equals(this.source[i][0])) {
				this.source[j][1] = this.source[i][2];
			    }
			    if (this.source[j][2].equals(this.source[i][0])) {
				this.source[j][2] = this.source[i][2];
			    }
			}
		    }
		    //System.out.println(this.source[i][0] + " " + this.source[i][1] + " " + this.source[i][2] + " " + this.source[i][3]);
		}
		this.validCode = true;

	}

	public String[][] getSource() {
		return source;
	}

	public void setSource(String[][] source) {
		this.source = source;
	}

	public boolean isValidCode() {
		return validCode;
	}

}