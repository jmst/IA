package up.jt.ia;





public class Caso {
	
	private double inp;
	private double out;
	
	public Caso() {
		inp = 0;
		out = 0;
	}
	
	public Caso( double i, double o) {
		inp = i;
		out = o;
	}
	
	public double getInp() {
		return inp;
	}
	
	public double getOut() {
		return out;
	}
	
	public void setInp( double v) {
		inp = v;
	}
	
	public void setOut( double v) {
		out = v;
	}

}

