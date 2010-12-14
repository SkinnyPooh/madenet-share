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
		int[] failure = null;
		switch(mode){
			case 1: failure = genM1();
			break;
			case 2: failure = genM2();
			break;
			case 3: failure = genM3();
			break;
			case 4: failure = genM4();
			break;
			case 5: failure = genM5();
			break;
			case 6: failure = genM1();
			break;
		}
		showFailures(failure);
		recon(failure);
	}

    private int[] genM1() {
    	int[] failure = new int[orig.getPixels().length];
    	int[] origPix = orig.getPixels();
		int pos = 0;
		int s2 = 128;
		int error;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				if(x == 0)s2 = 128;
				error = s2 - origPix[pos];
				error = (int) (error/(double) quant + 0.5) * quant;
				failure[pos] = error;
				s2 += error;
			}
		}
	    return failure;
    }

    private int[] genM2() {
    	int[] failure = new int[orig.getPixels().length];
    	int[] origPix = orig.getPixels();
		int pos = 0;
		int s2 = 128;
		int error;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				if(y == 0)s2 = 128;
				else{
					s2 = origPix[pos-width] + failure[pos-width];
				}
				error = s2 - origPix[pos];
				error = (int) (error/(double) quant + 0.5) * quant;
				failure[pos] = error;
			}
		}
	    return failure;
    }

    private int[] genM3() {
    	int[] failure = new int[orig.getPixels().length];
    	int[] origPix = orig.getPixels();
		int pos = 0;
		int s2 = 128;
		int error;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				if(x == 0 || y == 0)s2 = 128;
				else{
					s2 = origPix[pos-width-1] + failure[pos-width-1];
				}
				error = s2 - origPix[pos];
				error = (int) (error/(double) quant + 0.5) * quant;
				failure[pos] = error;
			}
		}
	    return failure;
    }

    private int[] genM4() {
    	int[] failure = new int[orig.getPixels().length];
    	int[] origPix = orig.getPixels();
		int pos = 0;
		int s2 = 128;
		int error;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				if(x == 0 || y == 0)s2 = 128;
				else{
					s2 = (origPix[pos] + failure[pos]) + (origPix[pos-width] + failure[pos-width]) - (origPix[pos-width-1] + failure[pos-width-1]);
				}
				error = s2 - origPix[pos];
				error = (int) (error/(double) quant + 0.5) * quant;
				failure[pos] = error;
			}
		}
	    return failure;
    }

    private int[] genM5() {
    	int[] failure = new int[orig.getPixels().length];
    	int[] origPix = orig.getPixels();
		int pos = 0;
		int s2 = 128;
		int error;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				if(x == 0 || y == 0)s2 = 128;
				else{
					s2 = (origPix[pos] + failure[pos]) + (origPix[pos-width] + failure[pos-width]) /2;
				}
				error = s2 - origPix[pos];
				error = (int) (error/(double) quant + 0.5) * quant;
				failure[pos] = error;
			}
		}
	    return failure;
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
		switch(mode){
			case 1: reconM1(failure);
			break;
			case 2: reconM2(failure);
			break;
			case 3: reconM3(failure);
			break;
			case 4: reconM4(failure);
			break;
			case 5: reconM5(failure);
			break;
			case 6: reconM1(failure);
			break;
		}
		recon.applyChanges();
	}

    private void reconM1(int[] failure) {
	    int[] reconPix = recon.getPixels();
		int pos = 0;
		int s2 = 128;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				if(x == 0)s2 = 128;
				reconPix[pos] = s2 + failure[pos];
				s2 += failure[pos];
			}
		}
    }

    private void reconM2(int[] failure) {
	    int[] reconPix = recon.getPixels();
		int pos = 0;
		int s2 = 128;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				if(x == 0 || y == 0)s2 = 128;
				else{
					s2 = reconPix[pos-width-1] + failure[pos-width-1];
				}
				reconPix[pos] = s2 + failure[pos];
			}
		}
    }

    private void reconM3(int[] failure) {
	    int[] reconPix = recon.getPixels();
		int pos = 0;
		int s2 = 128;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				if(x == 0 || y == 0)s2 = 128;
				else{
					s2 = reconPix[pos-width-1] + failure[pos-width-1];
				}
				reconPix[pos] = s2 + failure[pos];
			}
		}
    }

    private void reconM4(int[] failure) {
	    int[] reconPix = recon.getPixels();
		int pos = 0;
		int s2 = 128;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				if(x == 0 || y == 0)s2 = 128;
				else{
					s2 = (reconPix[pos] + failure[pos]) + (reconPix[pos-width] + failure[pos-width]) - (reconPix[pos-width-1] + failure[pos-width-1]);
				}
				reconPix[pos] = s2 + failure[pos];
			}
		}
    }

    private void reconM5(int[] failure) {
	    int[] reconPix = recon.getPixels();
		int pos = 0;
		int s2 = 128;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++, pos++){
				if(x == 0 || y == 0)s2 = 128;
				else{
					s2 = (reconPix[pos] + failure[pos]) + (reconPix[pos-width] + failure[pos-width]) /2;
				}
				reconPix[pos] = s2 + failure[pos];
			}
		}
    }
}
