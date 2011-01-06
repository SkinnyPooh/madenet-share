package de.htw.BV.Ue03;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.*;

/**
 * 
 */

/**
 * @author Marten Schälicke
 * @version 1.0
 */
public class DPCMmain extends JFrame {

	private ExtendedView orig;
	private ExtendedView fehler;
	private ExtendedView recon;
	private DPCM dpcm;

	private static final int maxImageWidth = 100;
	private static final int maxImageHeight = 100;

	private int width = 1200;
	private int height = 600;
	JLabel[] label = new JLabel[4]; 

	public static void main(String[] args) {
		new DPCMmain();
	}

	public DPCMmain() {

		super("DPCM");
		setSize(width, height);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		JPanel toolPanel = new JPanel();
		JPanel imagePanel = new JPanel(new GridLayout(1,3));
		JPanel outputPanel = new JPanel(new GridLayout(1,3));
		
		
		
	
		String[] string = { "Entropie:", "Entropie:", "Entropie: MSE:"};

		for (int i = 0; i < 3; i++) {
			label[i] = new JLabel(string[i]);
			outputPanel.add(label[i]);
		}

		File input = new File("test1.jpg");

		if (!input.canRead())
			input = openFile(); // file not found, choose another image

		JButton load = new JButton("Open Image");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File input = openFile();
				if (input != null) {
					orig.loadImage(input);
					fehler.loadImage(input);
					recon.loadImage(input);
					getAllImages();
				}
			}

		});

		final Choice choicePraediktor = new Choice();
		choicePraediktor.add("A (horizontal)");
		choicePraediktor.add("B (vertikal)");
		choicePraediktor.add("C (diagonal)");
		choicePraediktor.add("A+B-C");
		choicePraediktor.add("(A+b)/2");
		choicePraediktor.add("adaptiv");
		JLabel praediktor = new JLabel("Pr�diktor:");

		choicePraediktor.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (dpcm != null) {
					dpcm.setMode((byte) (choicePraediktor.getSelectedIndex() + 1));
					dpcm.generateFailures();
					getAllImages();
					updateText();
				}

			}

		});

		final JLabel slider = new JLabel("1.0");
		final JSlider slideQuant = new JSlider(10, 1000, 10);
		JLabel quant = new JLabel("Quantisierung");
		slideQuant.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent arg0) {
				slider.setText("" + (double) slideQuant.getValue() / 10.);
				if (dpcm != null) {
					dpcm.setQuant((double) slideQuant.getValue() / 10.);
					getAllImages();
					updateText();
				}
			}
		});
		
		
		toolPanel.add(load);
		toolPanel.add(praediktor);
		toolPanel.add(choicePraediktor);
		toolPanel.add(quant);
		toolPanel.add(slideQuant);
		toolPanel.add(slider);
		
		
		
		orig = new ExtendedView(input);
		fehler = new ExtendedView(input);
	    recon = new ExtendedView(input);
		dpcm = new DPCM(orig, fehler, recon);

		TitledBorder titleOrig = new TitledBorder(BorderFactory.createEtchedBorder(), "Eingabebild", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font("Sans", Font.PLAIN, 11));
		orig.setBorder(titleOrig);

		TitledBorder titleFehler = new TitledBorder(BorderFactory.createEtchedBorder(), "Pr�diktionsfehlerbild", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font("Sans", Font.PLAIN, 11));
		fehler.setBorder(titleFehler);

		TitledBorder titleRecon = new TitledBorder(BorderFactory.createEtchedBorder(), "Rekonstruiertes Bild", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font("Sans", Font.PLAIN, 11));
		recon.setBorder(titleRecon);

		getAllImages();
		updateText();


		imagePanel.add(orig);
		imagePanel.add(fehler);
		imagePanel.add(recon);
		
		add(toolPanel, BorderLayout.NORTH);
		add(imagePanel, BorderLayout.CENTER);
		add(outputPanel, BorderLayout.SOUTH);
		setVisible(true);
	}

	private File openFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "jpg", "png", "gif");
		chooser.setFileFilter(filter);
		int ret = chooser.showOpenDialog(this);
		if (ret == JFileChooser.APPROVE_OPTION)
			return chooser.getSelectedFile();
		return null;
	}

	private void getAllImages() {

		orig.convertToGray();
		orig.setMaxSize(new Dimension(maxImageWidth, maxImageHeight));
		dpcm.generateFailures();
		fehler = dpcm.getFehler();
		recon = dpcm.getRecon();		
	}
	private double getMSE() {
		int origpixels[] = orig.getPixels();
		int reconpixels[] = recon.getPixels();
		double sumOfSquares = 0;

		for (int i = 0; i < origpixels.length; i++) {
			sumOfSquares += ((origpixels[i] & 0xFF) - (reconpixels[i] & 0xFF)) * ((origpixels[i] & 0xFF) - (reconpixels[i] & 0xFF));
		}
		System.out.println(sumOfSquares);
		return sumOfSquares / origpixels.length;

	}
	
	private void updateText() {
		
		label[0].setText("Entropie: " + orig.getEntropie());
		label[1].setText("Entropie: " + fehler.getEntropie());
		label[2].setText("Entropie: " + recon.getEntropie() + "  MSE: " + getMSE());
	//	label[3].setText("MSE: " + getMSE());
		

		}

}
