package de.htw.BV.Ue03;
/**
 * 
 */

/**
 * @author Marten Schälicke
 * @version 1.0
 */
public class DPCM {
	private ExtendedView orig;
	private ExtendedView fehler;
	private ExtendedView recon;
	private byte quant;
	/**
	 * used to manage the mode of the prädiktor:
	 * 1 - P = A
	 * 2 - P = B
	 * 3 - P = C
	 * 4 - P = A+B-C
	 * 5 - P = (A+B)/2
	 * 6 - adaptiv
	 */
	private byte mode;
	
	public DPCM(ExtendedView orig, ExtendedView fehler, ExtendedView recon){
		this.orig = orig;
		this.fehler = fehler;
		this.recon = recon;
		quant = 1;
		mode = 1;
	}
	
	public void setQuant(byte q){
		quant = q;
	}
	
	public void setModde(byte m){
		mode = m;
	}

	public void generateFailures(){
		int[] failure = new int[orig.getPixels().length];
	}
	
	private void showFailures(int[] failure){
		int[] pixFehler = fehler.getPixels();
		int pix;
		for(int i = 0; i < pixFehler.length; i++){
			pix = failure[i] + 128;
			if(pix < 0)pix = 0;
			if(pix > 255) pix = 255;
			pixFehler[i] = 0xFF000000 + ((pix & 0xff) << 16) + ((pix & 0xff) << 8) + (pix & 0xff);
		}
		fehler.applyChanges();
	}
	
	private void recon(int[] failure){
		recon.applyChanges();
	}
}
