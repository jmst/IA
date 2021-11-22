package up.jt.ia;

public class Serie {

	private double[] x = new double[51];
	private double[] y = new double[51];
	private int sup;

	public Serie() {
		for (int i = 0; i < 51; i++) {
			x[i] = 0;
			y[i] = 0;
		}
		sup = -1;
	}

	public double getX(int i) {
		if (i <= sup)
			return x[i];
		return Double.MIN_VALUE;
	}
//
//	public double getY(double vx) {
//		for (int i = 0; i < 51 && i <= sup; i++) {
//			if (x[i] == vx)
//				return y[i];
//		}
//		return Double.MIN_VALUE;
//	}

	public double getY(int i) {
		if (i <= sup)
			return y[i];
		return Double.MIN_VALUE;
	}
	
	public void setXY( double vx, double vy) {
		sup++;
		x[sup] = vx;
		y[sup] = vy;
	}
}
