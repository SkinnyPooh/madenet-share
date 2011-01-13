package de.htw.BV.Ue04;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 */

/**
 * @author Marten Schälicke
 * @version 1.0
 */
@SuppressWarnings("serial")
public class WaveletMain extends JFrame {

	private ExtendedView orig;
	private ExtendedView fehler;
	private ExtendedView recon;
	private Wavelet wave;

	private static final int maxImageWidth = 100;
	private static final int maxImageHeight = 100;

	private int width = 1200;
	private int height = 600;
	JLabel[] label = new JLabel[4]; 

	public static void main(String[] args) {
		new WaveletMain();
	}

	public WaveletMain() {

		super("Wavelet");
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

		File input = new File("test2.jpg");

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
		
		final JLabel sliderK = new JLabel("1");
		final JSlider slideKaskade = new JSlider(1, 5, 1);
		JLabel kaskade = new JLabel(" Kaskaden:");
		slideKaskade.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent arg0) {
				sliderK.setText("" + slideKaskade.getValue());
				wave.calcPic(slideKaskade.getValue());
			}
			
					
		});

		
		
		toolPanel.add(load);
		toolPanel.add(kaskade);
		toolPanel.add(slideKaskade);
		toolPanel.add(sliderK);
		
		
		
		orig = new ExtendedView(input);
		fehler = new ExtendedView(input);
	    recon = new ExtendedView(input);
		wave = new Wavelet(orig, fehler, recon);

		TitledBorder titleOrig = new TitledBorder(BorderFactory.createEtchedBorder(), "Eingabebild", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font("Sans", Font.PLAIN, 11));
		orig.setBorder(titleOrig);

		TitledBorder titleFehler = new TitledBorder(BorderFactory.createEtchedBorder(), "Wavelet Transformation", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font("Sans", Font.PLAIN, 11));
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
		
		label[0].setText("Entropie: " + format(orig.getEntropie(), 3));
		label[1].setText("Entropie: " + format(fehler.getEntropie(), 3));
		label[2].setText("Entropie: " + format(recon.getEntropie(), 3) + "  MSE: " + format(getMSE(), 3));

	}
	
	public String format(double x, int len) {
		double d = 1;
		  
		for (int i = 0; i < len; i++) d *= 10;
		  
		x = Math.round(x * d) / d;
		  
		return Double.toString(x);
	}

}
