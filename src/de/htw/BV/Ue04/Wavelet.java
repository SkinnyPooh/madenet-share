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
	
	public Wavelet(ExtendedView orig, ExtendedView fehler, ExtendedView recon){
		this.orig = orig;
		this.fehler = fehler;
		this.recon = recon;
		pic = new double[orig.getPixels().length];
	}
	
	public void calcPic(int kaskaden){
		if(kaskaden < 1 || kaskaden > 5) kaskaden = 1;
		copyPixels();
		int width = orig.getImgWidth();
		int height = orig.getImgHeight();
		pic = calcKaskade(pic.clone(), width, height, kaskaden);
		showPic(width, height, kaskaden);
	}
	
    private double[] calcKaskade(double[] dA, final int width, final int height, final int kaskaden) {
	    double[] ll = dA.clone();
	    double[] lh;
	    double[] hl = dA.clone();
	    double[] hh;
	    ll = lPH(ll, width, height);
	    hl = hPH(hl, width, height);
	    ll = throwAwayEvery2nd(ll, 0);
	    hl = throwAwayEvery2nd(hl, 1);
	    lh = ll.clone();
	    hh = hl.clone();
	    ll = lPV(ll, width/2, height);
	    lh = hPV(lh, width/2, height);
	    hl = lPV(hl, width/2, height);
	    hh = hPV(hh, width/2, height);
	    ll = throwAwayEvery2nd(ll, 0);
	    lh = throwAwayEvery2nd(lh, 1);
	    hl = throwAwayEvery2nd(hl, 0);
	    hh = throwAwayEvery2nd(hh, 1);
	    if(kaskaden > 0){
	    	calcKaskade(ll, width/2, height/2, kaskaden-1);
	    }
	    dA = mergeBands(ll, lh, hl, hh, width, height);
	    return dA;
    }

    private double[] mergeBands(double[] ll, double[] lh, double[] hl, double[] hh, int width, int height) {
	    double[] dA = new double[width * height];
	    int[] pos = {0, 0, 0, 0, 0};
	    for(int y = 0; y < width; y++){
	    	for(int x = 0; x < height; x++, pos[0]++){
	    		if(y < height/2){
	    			if(x < width /2){
	    				dA[pos[0]] = ll[pos[1]];
	    				pos[1]++;
	    			}
	    			else{
	    				dA[pos[0]] = ll[pos[2]];
	    				pos[2]++;
	    			}
	    		}
	    		else{
	    			if(x < width /2){
	    				dA[pos[0]] = ll[pos[3]];
	    				pos[3]++;
	    			}
	    			else{
	    				dA[pos[0]] = ll[pos[4]];
	    				pos[4]++;
	    			}
	    		}
	    	}
	    }
	    return dA;
    }

	private double[] throwAwayEvery2nd(double[] dA, int start) {
    	double[] dA2 = new double[dA.length / 2];
    	if(start < 0 && start > 1){
    		start %= 2;
    	}
	    for(int i = 0; i < dA.length; i++){
	    	if(i+start % 2 == 0 && i != 0){
	    		dA2[(i-start)/2] = dA[i];
	    	}
	    }
		return dA2;
    }

	private void copyPixels() {
    	int[] pixel = orig.getPixels();
	    for(int i = 0; i < pic.length; i++){
	    	pic[i] = pixel[i];
	    }
    }

	private void showPic(final int width, final int height, final int kaskaden){
		double[] pixelDoubles = generatePixels(pic, width, height, kaskaden);
		int[] pixelInts = copyDPixToIPix(pixelDoubles);
		fehler.setPixels(pixelInts);
	}

	private double[] generatePixels(final double[] dA, final int width, final int height, final int kaskaden) {
    	for(int i = 0; i < dA.length; i++){
    		dA[i] /= 4;
    	}
    	double[] ll = getLL(dA, width, height);
	    if(kaskaden > 0){
	    	ll = generatePixels(ll, width/2, height/2, kaskaden-1);
	    }
	    int pos = 0;
	    int posLL = 0;
	    for(int y = 0; y < height; y++){
	    	for(int x = 0; x < width; x++, pos++){
	    		if(x < width/2 && y < height/2){
	    			dA[pos] = ll[posLL];
	    			posLL++;
	    		}
	    		else{
	    			dA[pos] += 128;
	    		}
	    	}
	    }
	    return dA;
    }

    private double[] getLL(double[] dA, int width, int height) {
	    double[] ll = new double[width * height / 4];
	    int pos = 0;
	    int posLL = 0;
	    for(int y = 0; y<height/2; y++){
	    	for(int x = 0; x<width/2; x++, pos++, posLL++){
	    		ll[posLL] = dA[pos];
	    	}
	    	pos += width/2;
	    }
	    return ll;
    }

    private int[] copyDPixToIPix(double[] pixelDoubles) {
	    int[] pixels = new int[pixelDoubles.length];
	    for(int i = 0; i < pixels.length; i++){
	    	if(pixelDoubles[i] < 0)pixels[i] = 0;
	    	if(pixelDoubles[i] > 255)pixels[i] = 255;
	    	else pixels[i] = (int) (pixelDoubles[i] + 0.5);
	    }
	    return pixels;
    }

	//Hochpass Horizontal usw.
	private double[] hPH(double[] ausgangswerte, int widht, int height){
		return null;
	}
	
	private double[] hPV(double[] ausgangswerte, int widht, int height){
		return null;
	}
	
	private double[] lPH(double[] ausgangswerte, int widht, int height){
		return null;
	}
	
	private double[] lPV(double[] ausgangswerte, int widht, int height){
		return null;
	}
}
