package up.jt.ia;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;

public class PrincipalFuncao extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4171119970741915087L;
	private CampoTexto tfNCiclos;
	private CampoTexto tfErroMaximo;
	private CampoTexto tfErro;
	private CampoTexto tfAlfa;
	private JButton bAprender;
	private JButton b5ciclos;
	private JButton b50ciclos;
	private JButton b500ciclos;
	private JButton bParar;
	private JButton bReiniciar;
	private JButton bSair;
	private JPanel pGeral;
	private JPanel pBotoes;
	private JPanel jspo;

	protected static final int NIN = 1;
	protected static final int NHID = 16;

	private EnEstado estado;
	private int nCiclos;
	private int nCiclosMax;
	private double erroMaximo;
	private double erro;
	private double alfa;
	private boolean fim;
	private double[] inp = new double[NIN + 1]; // input + teta
	private double[] phIn = new double[NHID]; // o somatorio dos inputs * w dos
												// percep da 1a. camada (hid)
	private double[] phOut = new double[NHID + 1]; // os outputs dos percep da
													// 1a.
													// camada (hid)
	private double poIn; // o somatorio dos inputs * w dos
							// percep da 2a. camada (out)
	private double poOut; // os outputs dos percep da 2a.
							// camada (out)
	private double[][] wI = new double[NIN + 1][NHID]; // os pesos dos inputs
														// para os percep da 1a.
														// camada
	private double[] wO = new double[NHID + 1]; // os pesos dos percep da
	// 1a. camada para os percep
	// da 2a. (que é só um)
	private double out; // os outputs desejados
	private double vOut; // o valor de output a mostrar
	private ArrayList<Caso> casos = new ArrayList<>();
	// private DecimalFormat df = new DecimalFormat("###.##");
	private Aprendiz apre;
	private Serie obj;
	private Serie ss;

	// private boolean alterando = false;

	public PrincipalFuncao() {
		super("Rede Neuronal");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int res = saiOuNao();
				if (res == JOptionPane.YES_OPTION) { // confirmado ?
					System.exit(0);
				}
			}
		};
		addWindowListener(l);

		ss = new Serie();
		obj = new Serie();
		for (double v = 0; v <= 5; v += 0.1) {
			obj.setXY(v, f(v));
		}
		Caso c = new Caso(0.5, f(0.5));
		casos.add(c);
		c = new Caso(1, f(1));
		casos.add(c);
		c = new Caso(1.5, f(1.5));
		casos.add(c);
		c = new Caso(2.5, f(2.5));
		casos.add(c);
		c = new Caso(3, f(3));
		casos.add(c);
//		c = new Caso(3.5, f(3.5));
//		casos.add(c);
		c = new Caso(4, f(4));
		casos.add(c);
		c = new Caso(4.5, f(4.5));
		casos.add(c);

		estado = EnEstado.Limpo;
		enche();
		add(pGeral);
		iniciar();
		refresh();
	}

	private double f(double v) {
//		return (1 / (0.2 + (v - 1) * (v - 1.5))) * ((1 / (0.25 + (v - 4) * (v - 4.5))));
		return  ds(3*v-2) + ds(3*v-10);
//		return 1-2*ds(2*v);
//		return 0.9 * Math.exp(-(v-1)*(v-1)) + 0.8 * Math.exp(-(0.8*v-4)*(0.7*v-4));
	}

	private double ds( double v) {
		return 2 * s(v) * (1 - s(v));
	}

	private double s( double v) {
		return 1/(1+Math.exp(-v));
	}
	
	private int saiOuNao() {
		return JOptionPane.showConfirmDialog(this, " Confirma o fim do programa ? ",
				" Rede Neuronal - Fim do Programa ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
	}

	private void enche() {
		pGeral = new JPanel(new BorderLayout());
		pBotoes = new JPanel(new GridBagLayout());
		// pTabelas = new JPanel(new GridLayout(2, 1));
		// pGeral.add(pTabelas, BorderLayout.CENTER);

		pGeral.add(pBotoes, BorderLayout.PAGE_START);
		enchePBotoes();
		// enchePTabelas();

		jspo = new JPanelGraf();
		jspo.setPreferredSize(new Dimension(800, 600));
		jspo.setBackground(Color.white);
		jspo.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		pGeral.add(jspo, BorderLayout.CENTER);
	}

	private void enchePBotoes() {
		bAprender = new Botao("Aprender");
		pBotoes.add(bAprender, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 5, 5));
		bAprender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nCiclosMax = 1000000;
				apre = new Aprendiz();
				apre.execute();
				refresh();
			}
		});

		bParar = new Botao("Parar");
		pBotoes.add(bParar, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 5, 5));
		bParar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fim = true;
				refresh();
			}
		});

		bReiniciar = new Botao("Reiniciar");
		pBotoes.add(bReiniciar, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 5, 5));
		bReiniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iniciar();
				refresh();
			}
		});

		pBotoes.add(new JLabel("Alfa"), new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, new Insets(1, 20, 1, 1), 5, 5));
		tfAlfa = new CampoTexto();
		tfAlfa.setText("1");
		alfa = tfAlfa.getValor();
		pBotoes.add(tfAlfa, new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 5, 5));

		pBotoes.add(new JLabel("Erro max."), new GridBagConstraints(6, 0, 1, 1, 0, 0, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, new Insets(1, 20, 1, 1), 5, 5));
		tfErroMaximo = new CampoTexto();
		tfErroMaximo.setText("0.02");
		erroMaximo = tfErroMaximo.getValor();
		pBotoes.add(tfErroMaximo, new GridBagConstraints(7, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 5, 5));

		pBotoes.add(new JLabel("Erro"), new GridBagConstraints(8, 0, 1, 1, 0, 0, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, new Insets(1, 20, 1, 1), 5, 5));
		tfErro = new CampoTexto();
		tfErro.setText(" ");
		erro = tfErro.getValor();
		tfErro.setEnabled(false);
		pBotoes.add(tfErro, new GridBagConstraints(9, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 5, 5));

		pBotoes.add(new JLabel("No. Ciclo"), new GridBagConstraints(10, 0, 1, 1, 0, 0, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, new Insets(1, 20, 1, 1), 5, 5));
		tfNCiclos = new CampoTexto();
		tfNCiclos.setText(" ");
		tfNCiclos.setEnabled(false);
		nCiclos = 0;
		pBotoes.add(tfNCiclos, new GridBagConstraints(11, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 5, 5));

		pBotoes.add(new JLabel(" "), new GridBagConstraints(12, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 5, 5));

		bSair = new Botao("Sair");
		pBotoes.add(bSair, new GridBagConstraints(14, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 5, 5));
		bSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int res = saiOuNao();
				if (res == JOptionPane.YES_OPTION) { // confirmado ?
					System.exit(0);
				}
			}
		});
		b5ciclos = new Botao("5 ciclos");
		pBotoes.add(b5ciclos, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 5, 5));
		b5ciclos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nCiclosMax = 5;
				apre = new Aprendiz();
				apre.execute();
				refresh();
			}
		});
		b50ciclos = new Botao("50 ciclos");
		pBotoes.add(b50ciclos, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 5, 5));
		b50ciclos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nCiclosMax = 50;
				apre = new Aprendiz();
				apre.execute();
				refresh();
			}
		});
		b500ciclos = new Botao("500 ciclos");
		pBotoes.add(b500ciclos, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 5, 5));
		b500ciclos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nCiclosMax = 500;
				apre = new Aprendiz();
				apre.execute();
				refresh();
			}
		});
	}

	private void iniciar() {
		for (int i = 0; i <= NIN; i++) {
			inp[i] = 0;
			for (int j = 0; j < NHID; j++)
				wI[i][j] = Math.random() * 2 - 1;
		}
		inp[NIN] = -1; // teta
		out = 0;
		vOut = 0;
		poOut = 0;
		for (int j = 0; j < NHID + 1; j++) {
			wO[j] = Math.random() * 2 - 1;
		}
		nCiclos = 0;
		alfa = 0.5;
		erroMaximo = 0.01;
		estado = EnEstado.Limpo;
		limpar();
		repaint();
	}

	private void aprende() {
		nCiclos = 0;
		boolean treinado = false;
		estado = EnEstado.A_Aprender;
		try {
			erroMaximo = Double.parseDouble(tfErroMaximo.getText());
		} catch (Exception ex) {
			erroMaximo = 0.01;
		}
		if (erroMaximo < 0.01)
			erroMaximo = 0.01;
		fim = false;
		while (treinado == false && !fim && estado == EnEstado.A_Aprender && nCiclos < nCiclosMax) {
			double erroMax = 0;
			treinado = true;
			for (Caso c : casos) {
				usaCaso(c);
				calcula();
				double erroAux = Math.abs(poOut - out);
				if (erroAux > erroMax)
					erroMax = erroAux;
				ajustaPesos();
			}
			nCiclos++;
			if (erroMax > erroMaximo)
				treinado = false;
			erro = erroMax;
			if (nCiclos % 5 == 0) {
				Serie s = new Serie();
				for (double x = 0; x <= 5; x += 0.1) {
					inp[0] = x;
					calcula();
					s.setXY(x, vOut);
				}
				synchronized (ss) {
					ss = s;
				}
				refresh();
			}
			try {
				Thread.sleep(1);
			} catch (Exception ex) {
			}
			;
		}
		fim = true;
		estado = EnEstado.Treinado;
		refresh();
	}

	private void ajustaPesos() {
		double delta = (out - poOut) * poOut * (1 - poOut);
		for (int j = 0; j < NHID + 1; j++) {
			wO[j] += alfa * phOut[j] * delta;
		}
		for (int j = 0; j < NHID; j++) {
			double e = wO[j] * delta;
			double d = phOut[j] * (1 - phOut[j]) * e;
			for (int i = 0; i < NIN + 1; i++) {
				wI[i][j] += alfa * d * inp[i];
			}
		}
	}

	private void calcula() {
		for (int i = 0; i < NHID; i++) {
			phIn[i] = 0;
			for (int j = 0; j < NIN + 1; j++) {
				phIn[i] += wI[j][i] * inp[j];
			}
			phOut[i] = sig(phIn[i]);
		}
		phOut[NHID] = -1; // teta para a saida
		poIn = 0;
		for (int i = 0; i < NHID + 1; i++) {
			poIn += wO[i] * phOut[i];
		}
		poOut = sig(poIn);
		vOut = poOut;
	}

	private void usaCaso(Caso c) {
		for (int i = 0; i < NIN; i++) {
			setIn(c.getInp());
			setOut(c.getOut());
		}
	}

	private void limpar() {
		setIn(0);
		setOut(0);
	}

	private void setIn(double v) {
		inp[0] = v;
	}

	private void setOut(double v) {
		out = v;
	}

	private void refresh() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				alfa = tfAlfa.getValor();
				erroMaximo = tfErroMaximo.getValor();
				tfNCiclos.setText("" + nCiclos);
				tfErro.setText(new DecimalFormat("#0.#####").format(erro));
				repaint();
			}
		});
	}

	private double sig(double x) {
		return 1 / (1 + Math.exp(-x));
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PrincipalFuncao p = new PrincipalFuncao();
				p.pack();
				p.setVisible(true);
			}
		});
	}

	// ----------------------------------------------------

	private class Botao extends JButton {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1817757875656034356L;

		protected Botao(String texto) {
			super(texto);
			setPreferredSize(new Dimension(90, 20));
		}
	}

	// ----------------------------------------------------

	private class CampoTexto extends JTextField {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3273275831684666522L;

		protected CampoTexto() {
			super(6);
			setPreferredSize(new Dimension(20, 16));
			setDisabledTextColor(Color.black);
		}

		public void setEnabled(boolean estado) {
			super.setEnabled(estado);
			setBackground(new Color(248, 248, 248));
		}

		protected double getValor() {
			try {
				return Double.parseDouble(getText().trim());
			} catch (Exception ex) {
				return 0;
			}
		}
	}

	// ---------------------------------------------------------

	private class Aprendiz extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() {
			aprende();
			return null;
		}
	}

	private class JPanelGraf extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public JPanelGraf() {
			super();
		}

		public void paint(Graphics gc) {
			super.paint(gc);
			int xant = 0;
			int yant = 0;
			double escX = (getWidth() - 60) / 5.0;
			double escY = (getHeight() - 20) / 4.0;
			int zeroY = getHeight() / 2;
			int zeroX = 50;
			int larg = getWidth();
			int alt = getHeight();
			gc.setColor(Color.white);
			gc.fillRect(0, 0, larg, alt);
			gc.setColor(Color.black);
			gc.drawLine(10, zeroY, larg - 10, zeroY);
			gc.drawLine(zeroX, 10, zeroX, alt - 10);
			for (double v = 2; v >= -2; v -= 0.5) {
				int y = alt - zeroY - (int) (v * escY);
				gc.drawLine(zeroX - 3, y, zeroX + 3, y);
			}
			for (double v = 0.5; v <= 5; v += 0.5) {
				int x = zeroX + (int) (v * escX);
				gc.drawLine( x, zeroY-3, x, zeroY + 3);
			}
			boolean cont = false;
			gc.setColor(Color.red);
			for (int i = 0; i < 51; i++) {
				if (obj.getX(i) == Double.MIN_VALUE)
					continue;
				int x = (int) (obj.getX(i) * escX) + zeroX;
				int y = alt - zeroY - (int) (obj.getY(i) * escY);
				if (cont) {
					gc.drawLine(xant, yant, x, y);
				}
				cont = true;
				xant = x;
				yant = y;
			}
			cont = false;
			gc.setColor(new Color(32,128,32));
			synchronized (ss) {
				for (int i = 0; i < 51; i++) {
					if (ss.getX(i) == Double.MIN_VALUE)
						continue;
					int x = (int) (ss.getX(i) * escX) + zeroX;
					int y = alt - zeroY - (int) (ss.getY(i) * escY);
					if (cont) {
						gc.drawLine(xant, yant, x, y);
					}
					cont = true;
					xant = x;
					yant = y;
				}

			}
			gc.setColor(Color.blue);
			for (Caso c : casos) {
				int x = (int) (c.getInp() * escX) + zeroX;
				int y = alt - zeroY - (int) (c.getOut() * escY);
				gc.fillOval(x - 2, y - 2, 5, 5);
			}
		}
	}
}
