package de.htw.BV.Ue04;


/**
 * @author Marten Sch�licke
 * @version 1.0
 */
public class Wavelet {
	private ExtendedView orig;
	private ExtendedView fehler;
	private ExtendedView recon;
	private double quant;
	/**
	 * used to manage the mode of the pr�diktor:<br/>
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
	
	public Wavelet(ExtendedView orig, ExtendedView fehler, ExtendedView recon){
		this.orig = orig;
		this.fehler = fehler;
		this.recon = recon;
		width = orig.getImgWidth();
		height = orig.getImgHeight();
		quant = 1;
		mode = 1;
	}
	
	public void setQuant(double q){
		quant = q;
	}
	
	public void setMode(byte m){
		if(m < 0 || m > 6){
			mode = 1;
		}
		else{
			mode = m;
		}
	}

	public void generateFailures(){
    	int[] failure = new int[orig.getPixels().length];
    	int[] reconPix = new int[orig.getPixels().length];
    	int[] origPix = orig.getPixels();
		int pos = 0;
		int s2 = 128;
		int error;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				switch(mode){
					case 1: 
						s2 = x==0 ? 128 : reconPix[pos-1];
					break;
					case 2: 
						s2 = y==0 ? 128 : reconPix[pos-width];
					break;
					case 3: 
						s2 = x==0 || y==0 ? 128 : reconPix[pos-width-1];
					break;
					case 4: 
						s2 = x==0 || y==0 ? 128 : reconPix[pos-1] + reconPix[pos-width] - reconPix[pos-width-1];
					break;
					case 5: 
						s2 = x==0 || y==0 ? 128 : (reconPix[pos-1] + reconPix[pos-width]) /2;
					break;
					case 6: 
						if(x == 0 || y == 0){
							s2 = 128;
						}
						else {
							s2 = Math.abs(reconPix[pos-1] - reconPix[pos-width-1]) < Math.abs(reconPix[pos-width] - reconPix[pos-width-1]) ? reconPix[pos-width] : reconPix[pos-1];
						}
					break;
				}
				error = (origPix[pos] & 0xFF) - s2;
				error = error >= 0 ? (int)(error/quant + 0.5) : (int)(error/quant - 0.5);
				failure[pos] = error;
				reconPix[pos] = error >= 0 ? s2 + (int)(error * quant +0.5) : s2 + (int)(error * quant -0.5);
				if(reconPix[pos] <   0)reconPix[pos] =   0;
				if(reconPix[pos] > 255)reconPix[pos] = 255;
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
					case 1: 
						s2 = x == 0 ? 128 : reconPix[pos-1] & 0xFF;
					break;
					case 2: 
						s2 = y == 0 ? 128 : reconPix[pos-width] & 0xFF;
					break;
					case 3: 
						s2 = x == 0 || y == 0 ? 128 : reconPix[pos-width-1] & 0xFF;
					break;
					case 4: 
						s2 = x == 0 || y == 0 ? 128 : (reconPix[pos-1] & 0xFF) + (reconPix[pos-width] & 0xFF) - (reconPix[pos-width-1] & 0xFF);
					break;
					case 5: 
						s2 = x == 0 || y == 0 ? 128 : ((reconPix[pos-1] & 0xFF) + (reconPix[pos-width] & 0xFF)) /2;
					break;
					case 6: 
						if(x == 0 || y == 0){
							s2 = 128;
						}
						else {
							s2 = Math.abs((reconPix[pos-1] & 0xFF) - (reconPix[pos-width-1] & 0xFF)) < Math.abs((reconPix[pos-width] & 0xFF) - (reconPix[pos-width-1] & 0xFF)) ? (reconPix[pos-width] & 0xFF) : (reconPix[pos-1] & 0xFF);
						}
					break;
				}
				int pix = failure[pos] >= 0 ?  s2 + (int) (failure[pos] * quant +0.5) : s2 + (int)(failure[pos] * quant -0.5);
				if(pix < 0)pix = 0;
				if(pix > 255)pix = 255;
				reconPix[pos] = 0xFF000000 + (pix << 16) + (pix << 8) + pix;
			}
		}
		recon.applyChanges();
	}
	
	public ExtendedView getFehler () {
		return fehler;
	}
	public ExtendedView getRecon () {
		return recon;
	}
	
	
}
