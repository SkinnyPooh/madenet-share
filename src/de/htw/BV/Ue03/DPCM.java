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
	 * used to manage the mode of the prädiktor:<br/>
	 * 1 - P = A<br/>
	 * 2 - P = B<br/>
	 * 3 - P = C<br/>
	 * 4 - P = A+B-C<br/>
	 * 5 - P = (A+B)/2<br/>
	 * 6 - adaptiv
	 */
	private byte mode;
	private int width;
	private int height;
	
	public DPCM(ExtendedView orig, ExtendedView fehler, ExtendedView recon){
		this.orig = orig;
		this.fehler = fehler;
		this.recon = recon;
		width = orig.getImgWidth();
		height = orig.getImgHeight();
		quant = 1;
		mode = 1;
	}
	
	public void setQuant(byte q){
		quant = q;
	}
	
	public void setModde(byte m){
		if(m < 0 || m > 6){
			mode = 1;
		}
		else{
			mode = m;
		}
	}

	public void generateFailures(){
    	int[] failure = new int[orig.getPixels().length];
    	int[] origPix = orig.getPixels();
		int pos = 0;
		int s2 = 128;
		int error;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				switch(mode){
					case 1: s2 = x==0 ? 128 : s2;
					break;
					case 2: s2 = y==0 ? 128 : origPix[pos-width] + failure[pos-width];;
					break;
					case 3: s2 = x==0 || y==0 ? 128 : origPix[pos-width-1] + failure[pos-width-1];
					break;
					case 4: s2 = x==0 || y==0 ? 128 : (origPix[pos] + failure[pos]) + (origPix[pos-width] + failure[pos-width]) - (origPix[pos-width-1] + failure[pos-width-1]);
					break;
					case 5: s2 = x==0 || y==0 ? 128 : (origPix[pos] + failure[pos]) + (origPix[pos-width] + failure[pos-width]) /2;
					break;
					case 6: s2 = x==0 || y==0 ? 128 : s2;
					break;
				}
				error = s2 - origPix[pos];
				error = (int) (error/(double) quant + 0.5);
				failure[pos] = error;
				s2 += error * quant;
			}
		}
		showFailures(failure);
		recon(failure);
	}
	
	public void showFailures(int[] failure){
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
	
	public void recon(int[] failure){
	    int[] reconPix = recon.getPixels();
		int pos = 0;
		int s2 = 128;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				switch(mode){
					case 1: s2 = x == 0 ? 128 : s2;
					break;
					case 2: s2 = y == 0 ? 128 : reconPix[pos-width-1] + failure[pos-width-1];
					break;
					case 3: s2 = x == 0 || y == 0 ? 128 : reconPix[pos-width-1] + failure[pos-width-1];
					break;
					case 4: s2 = x == 0 || y == 0 ? 128 : (reconPix[pos] + failure[pos]) + (reconPix[pos-width] + failure[pos-width]) - (reconPix[pos-width-1] + failure[pos-width-1]);
					break;
					case 5: s2 = x == 0 || y == 0 ? 128 : (reconPix[pos] + failure[pos]) + (reconPix[pos-width] + failure[pos-width]) /2;
					break;
					case 6: s2 = x == 0 || y == 0 ? 128 : reconPix[pos-width-1] + failure[pos-width-1];
					break;
				}
				int pix = s2 + failure[pos]*quant;
				reconPix[pos] = 0xFF000000 + ((pix & 0xff) << 16) + ((pix & 0xff) << 8) + (pix & 0xff);
			}
		}
		recon.applyChanges();
	}
}
