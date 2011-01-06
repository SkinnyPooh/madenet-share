package de.htw.BV.Ue04;


/**
 * @author Marten Schälicke
 * @version 1.0
 */
public class Wavelet {
	private ExtendedView orig;
	private ExtendedView fehler;
	private ExtendedView recon;
	private double[] pic;
	private int width;
	private int height;
	
	public Wavelet(ExtendedView orig, ExtendedView fehler, ExtendedView recon){
		this.orig = orig;
		this.fehler = fehler;
		this.recon = recon;
		pic = new double[orig.getPixels().length];
		width = orig.getImgWidth();
		height = orig.getImgHeight();
	}
	
	public void calcPic(int kaskaden){
		
	}
	
	public void showPic(){
		
	}
	
	private double[] HochpassHorizontal(double[] ausgangswerte, int widht, int height){
		return null;
	}
	
	private double[] HochpassVertikal(double[] ausgangswerte, int widht, int height){
		return null;
	}
	
	private double[] TiefpassHorizontal(double[] ausgangswerte, int widht, int height){
		return null;
	}
	
	private double[] TiefpassVertikal(double[] ausgangswerte, int widht, int height){
		return null;
	}
}
