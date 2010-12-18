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
		
	private static final int maxImageWidth = 600;
	private static final int maxImageHeight = 600;
	private static final int layoutBorder = 10;
	
	private int width = 800;
	private int height = 600;
	
	
	public static void main(String[] args) {
		new DPCMmain();
	}
	
	public DPCMmain(){
		
		super ("DPCM");
		setSize(width,height);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
				
		JPanel toolPanel =  new JPanel();
		JPanel imagePanel =  new JPanel();
		
		// load the default image
		File input = new File("test1.jpg");

		if (!input.canRead())
			input = openFile(); // file not found, choose another image

		orig = new ExtendedView(input);
		orig.convertToGray();
		orig.setMaxSize(new Dimension(maxImageWidth, maxImageHeight));
		
		fehler = new ExtendedView(input);
		fehler.convertToGray();
		fehler.setMaxSize(new Dimension(maxImageWidth, maxImageHeight));
		
		recon = new ExtendedView(input);
		recon.convertToGray();
		recon.setMaxSize(new Dimension(maxImageWidth, maxImageHeight));
		
		dpcm = new DPCM(orig, fehler, recon);
		
		final Choice choicePraediktor = new Choice ();
		choicePraediktor.add("A (horizontal)");
		choicePraediktor.add("B (vertikal)");
		choicePraediktor.add("C (diagonal)");
		choicePraediktor.add("A+B-C");
		choicePraediktor.add("(A+b)/2");
		choicePraediktor.add("adaptiv");
			
		
		JButton load = new JButton("Open Image");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File input = openFile();
				if (input != null) {
					orig.loadImage(input);
					orig.convertToGray();
					orig.setMaxSize(new Dimension(maxImageWidth, maxImageHeight));
					pack();
				}
			}
			
		});
		
		
		JLabel praediktor = new JLabel("Prädiktor:");
		choicePraediktor.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (dpcm != null) {
					dpcm.setMode((byte)(choicePraediktor.getSelectedIndex()+1));
					dpcm.generateFailures();
				}
								
			}
			
		});
		
		final JLabel slider = new JLabel("1.0"); 
		final JSlider slideQuant =  new JSlider(10, 1000, 10);
		JLabel quant =  new JLabel("Quantisierung");
		slideQuant.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent arg0) {
				slider.setText("" + (double) slideQuant.getValue() / 10.); 	
				if (dpcm != null) {
					dpcm.setQuant((double) slideQuant.getValue() / 10.);
					dpcm.generateFailures();
				}
			}
		});
		
		
		toolPanel.add(load);
		toolPanel.add(praediktor);
		toolPanel.add(choicePraediktor);
		toolPanel.add(quant);
		toolPanel.add(slideQuant);
		toolPanel.add(slider);
		
		
		TitledBorder titleOrig = new TitledBorder(BorderFactory.createEtchedBorder(), "Eingabebild",  TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font ("Sans", Font.PLAIN, 11));
		orig.setBorder(titleOrig);
		imagePanel.add(orig);
		
		TitledBorder titleFehler = new TitledBorder(BorderFactory.createEtchedBorder(), "Prädiktionsfehlerbild",  TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font ("Sans", Font.PLAIN, 11));
		fehler.setBorder(titleFehler);
		imagePanel.add(fehler);
		
		TitledBorder titleRecon = new TitledBorder(BorderFactory.createEtchedBorder(), "Rekonstruiertes Bild",  TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font ("Sans", Font.PLAIN, 11));
		recon.setBorder(titleRecon);
		imagePanel.add(recon);
		
		dpcm.generateFailures();
		
//		if (dpcm != null) {
//			dpcm.generateFailures();
//			imagePanel.add(fehler);
//			imagePanel.add(recon);
//		}
		
	//	setLayout(new GridLayout(2,1));
		add(toolPanel, BorderLayout.NORTH);
		add(imagePanel, BorderLayout.CENTER);
		
	
		
	
		
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
			
}