package de.htw.BV.Ue04;


/**
 * @author Marten Schälicke
 * @version 1.0
 */
public class Wavelet {
	private ExtendedView orig;
	private ExtendedView fehler;
	private ExtendedView recon;
	private final static int[] ANALYSE_TIEFPASS = {-1,2,6,2,-1};
	private final static int[] ANALYSE_HOCHPASS = {-1,2,-1};
	private final static int[] SYNTHESE_TIEFPASS = {1,2,1};
	private final static int[] SYNTHESE_HOCHPASS = {-1,-2,6,-2,-1};
	
	public Wavelet(ExtendedView orig, ExtendedView fehler, ExtendedView recon){
		this.orig = orig;
		this.fehler = fehler;
		this.recon = recon;
	}
	
	public void calcPic(int kaskaden){
		if(kaskaden < 1 || kaskaden > 5) kaskaden = 1;
		double[] pixValues = copyPixels();
		int width = orig.getImgWidth();
		int height = orig.getImgHeight();
		double[] wave = calcKaskade(pixValues, width, height, kaskaden);
		showWavelet(wave, width, height, kaskaden);
		showRecon(wave, width, height, kaskaden);
	}
	
	private double[] calcKaskade(final double[] dA, final int width, final int height, final int kaskaden) {
		double[] lowpass = filterHorizontal(dA, width, height, ANALYSE_TIEFPASS, 4);
		lowpass = throwAwayEvery2ndHorizontal(lowpass, 0, width, height);
		double[] highpass = filterHorizontal(dA, width, height, ANALYSE_HOCHPASS, 4);
		highpass = throwAwayEvery2ndHorizontal(highpass, 1, width, height);
		double[] lowlow = filterVertikal(lowpass, width/2, height, ANALYSE_TIEFPASS, 4);
		lowlow = throwAwayEvery2ndVertikal(lowlow, 0, width/2, height);
		double[] lowhigh = filterVertikal(lowpass, width/2, height, ANALYSE_HOCHPASS, 4);
		lowhigh = throwAwayEvery2ndVertikal(lowhigh, 0, width/2, height);
		double[] highlow = filterVertikal(highpass, width/2, height, ANALYSE_TIEFPASS, 4);
		highlow = throwAwayEvery2ndVertikal(highlow, 0, width/2, height);
		double[] highhigh = filterVertikal(highpass, width/2, height, ANALYSE_HOCHPASS, 4);
		highhigh = throwAwayEvery2ndVertikal(highhigh, 0, width/2, height);
		if(kaskaden > 1){
			lowlow = calcKaskade(lowlow, width/2, height/2, kaskaden-1);
		}
		double[] kaskadenResult = mergeBands(lowlow, lowhigh, highlow, highhigh, width, height);
		return kaskadenResult;
    }

    private double[] mergeBands(final double[] ll, final double[] lh, final double[] hl, final double[] hh, final int width, final int height) {
	    double[] merged = new double[width * height];
	    int pos = 0, posLL = 0, posHL = 0, posLH = 0, posHH = 0;
		for(int y = 0; y <height; y++){
			for(int x = 0; x < width; x++, pos++){
				if(y < height/2){
					if(x < width/2){
						merged[pos] = ll[posLL];
						posLL++;
					}
					else{
						merged[pos] = hl[posHL];
						posHL++;
					}
				}
				else{
					if(x < width/2){
						merged[pos] = lh[posLH];
						posLH++;
					}
					else{
						merged[pos] = hh[posHH];
						posHH++;
					}
				}
			}
		}
	    return merged;
    }

	private double[] throwAwayEvery2ndHorizontal(final double[] dA, int start, final int width, final int height) {
    	double[] dA2 = new double[dA.length / 2];
    	if(start < 0 && start > 1){
    		start %= 2;
    	}
    	int posDA = 0;
    	int posDA2 = 0;
    	for(int x = 0+start; x < width; x+=2){
    		posDA = x;
    		posDA2 = x/2;
    		for(int y = 0; y < height; y++, posDA+=width, posDA2 += width/2){
    			dA2[posDA2] = dA[posDA];
    		}
    	}
		return dA2;
    }

	private double[] throwAwayEvery2ndVertikal(final double[] dA, int start, final int width, final int height) {
    	double[] dA2 = new double[dA.length / 2];
    	if(start < 0 && start > 1){
    		start %= 2;
    	}
    	int posDA = 0+start*width;
    	int posDA2 = 0;
	    for(int y = 0+start; y < height; y+=2, posDA+=width){
	    	for(int x = 0; x < width; x++, posDA++, posDA2++){
	    		dA2[posDA2] = dA[posDA];
	    	}
	    }
		return dA2;
    }

	private double[] copyPixels() {
    	int[] pixel = orig.getPixels();
    	double[] pixValues = new double[pixel.length];
	    for(int i = 0; i < pixel.length; i++){
	    	pixValues[i] = (pixel[i] & 0xFF);
	    }
	    return pixValues;
    }

	private void showWavelet(final double[] wave, final int width, final int height, final int kaskaden){
		double[] pixelDoubles = generatePixels(wave, width, height, kaskaden);
		int[] pixelInts = copyDPixToIPix(pixelDoubles);
		fehler.setPixels(pixelInts);
	}

	private double[] generatePixels(final double[] dA, final int width, final int height, final int kaskaden) {
		double[] dA2 = new double[dA.length];
    	for(int i = 0; i < dA.length; i++){
    		dA[i] = dA[i] /4;
    	}
    	double[] ll = getLL(dA, width, height);
	    if(kaskaden > 1){
	    	ll = generatePixels(ll, width/2, height/2, kaskaden-1);
	    }
	    int pos = 0;
	    int posLL = 0;
	    for(int y = 0; y < height; y++){
	    	for(int x = 0; x < width; x++, pos++){
	    		if(x < width/2 && y < height/2){
	    			dA2[pos] = ll[posLL];
	    			posLL++;
	    		}
	    		else{
	    			dA2[pos] = dA[pos]+128;
	    		}
	    	}
	    }
	    return dA2;
    }

    private int[] copyDPixToIPix(final double[] pixelDoubles) {
	    int[] pixels = new int[pixelDoubles.length];
	    for(int i = 0; i < pixels.length; i++){
	    	int wert;
	    	if(pixelDoubles[i] < 0)wert = 0;
	    	if(pixelDoubles[i] > 255)wert = 255;
	    	else wert = (int) (pixelDoubles[i] + 0.5);
	    	pixels[i]= 0xFF000000 + ((wert & 0xff) << 16) + ((wert & 0xff) << 8) + (wert & 0xff);
	    }
	    return pixels;
    }
    
    private void showRecon(double[] wave, final int width, final int height, final int kaskaden){
    	double[] pixelDoubles = calcRecon(wave, width, height, kaskaden);
		int[] pixelInts = copyDPixToIPix(pixelDoubles);
		recon.setPixels(pixelInts);
    }
    
    private double[] calcRecon(final double[] band, final int width, final int height, final int kaskaden){
    	double[] dA = new double[width*height];
    	double[] ll = getLL(band, width, height);
    	if(kaskaden > 1){
    		ll = calcRecon(ll, width/2, height/2, kaskaden-1);
    	}
    	double[] lh = getLH(band, width, height);
    	double[] hl = getHL(band, width, height);
    	double[] hh = getHH(band, width, height);
    	ll = upscaleVertikal(ll, 0, width/2, height/2);
    	lh = upscaleVertikal(lh, 1, width/2, height/2);
    	hl = upscaleVertikal(hl, 0, width/2, height/2);
    	hh = upscaleVertikal(hh, 1, width/2, height/2);
    	ll = filterVertikal(ll, width/2, height, SYNTHESE_TIEFPASS, 4);
    	lh = filterVertikal(lh, width/2, height, SYNTHESE_HOCHPASS, 4);
    	hl = filterVertikal(hl, width/2, height, SYNTHESE_TIEFPASS, 4);
    	hh = filterVertikal(hh, width/2, height, SYNTHESE_HOCHPASS, 4);
    	double[] lowpass = mergeBands(ll,lh);
    	double[] highpass = mergeBands(hl,hh);
    	lowpass = upscaleHorizontal(lowpass, 0, width/2, height);
    	highpass = upscaleHorizontal(highpass, 1, width/2, height);
    	lowpass = filterHorizontal(lowpass, width, height, SYNTHESE_TIEFPASS, 4);
    	highpass = filterHorizontal(highpass, width, height, SYNTHESE_HOCHPASS, 4);
    	dA = mergeBands(lowpass, highpass);
    	return dA;
    }
    
    private double[] mergeBands(final double[] band1, final double[] band2){
    	double[] merged = new double[band1.length];
    	for(int i = 0; i < merged.length; i++){
    		merged[i] = band1[i] + band2[i];
    	}
    	return merged;
    }
    
    private double[] upscaleHorizontal(final double[] dA, int start, final int width, final int height){
    	double[] dA2 = new double[width*height*2];
    	if(start < 0 && start > 1){
    		start %= 2;
    	}
    	int pos = 0, pos2 = 0+start;
    	for(int y = 0; y < height; y++){
        	for(int x = 0; x < width; x++, pos++, pos2 +=2){
        		dA2[pos2] = dA[pos];
	    		if(start != 0){
		    		dA2[pos2-1] = 0;
	    		}
	    		else{
	    			dA2[pos2+1] = 0;
	    		}
        	}
    	}
    	return dA2;
    }

    private double[] upscaleVertikal(final double[] dA, int start, final int width, final int height) {
    	double[] dA2 = new double[width*height*2];
    	if(start < 0 && start > 1){
    		start %= 2;
    	}
    	int pos = 0, pos2 = 0+start*width;
	    for(int y = 0; y < height; y++, pos2 += width){
	    	for(int x = 0; x < width; x++, pos++, pos2++){
	    		dA2[pos2] = dA[pos];
	    		if(start != 0){
		    		dA2[pos2-width] = 0;
	    		}
	    		else{
	    			dA2[pos2+width] = 0;
	    		}
	    	}
	    }
	    return dA2;
    }

	private double[] getLL(final double[] band, final int width, final int height) {
	    double[] ll = new double[width * height / 4];
	    int pos = 0, posBand = 0;
	    for(int y = 0; y<height/2; y++, posBand += width/2){
	    	for(int x = 0; x<width/2; x++, posBand++, pos++){
	    		ll[pos] = band[posBand];
	    	}
	    }
	    return ll;
    }
    
    private double[] getLH(final double[] band, final int width, final int height){
    	double[] lh = new double[width*height/4];
    	int pos = 0, posBand = height/2 * width;
    	for(int y = height/2; y < height; y++, posBand += width/2){
    		for(int x = 0; x < width/2; x++, posBand++, pos++){
    			lh[pos] = band[posBand];
    		}
    	}
    	return lh;
    }
    
    private double[] getHL(final double[] band, final int width, final int height){
    	double[] hl = new double[width*height/4];
    	int pos = 0, posBand = width/2;
    	for(int y = 0; y < height/2; y++, posBand += width/2){
    		for(int x = width/2; x < width; x++, pos++, posBand++){
    			hl[pos] = band[posBand];
    		}
    	}
    	return hl;
    }
    
    private double[] getHH(final double[] band, final int width, final int height){
    	double[] hh = new double[width*height/4];
    	int pos = 0, posBand = height/2 * width + width/2;
    	for(int y = height/2; y < height; y++, posBand += width/2){
    		for(int x = width/2; x < width; x++, pos++, posBand++){
    			hh[pos] = band[posBand];
    		}
    	}
    	return hh;
    }

    private double[] filterHorizontal(final double[] ausgangswerte, final int width, final int height, final int[] kernel, int nenner) {
		double[] resultat = new double[ausgangswerte.length];
		int pos = 0;
		int kMitteX = kernel.length / 2;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++, pos++) {
				double sum = 0;
				for (int i = 0; i < kernel.length; i++) {
					int xN = i-kMitteX;
					if (x + xN < 0) {
						sum += ausgangswerte[width * y - xN - x] * kernel[i];
						// nachbarwert
					} else if (x + xN >= width) {
						sum += ausgangswerte[width * y + width - 2 - x - xN + width] * kernel[i];
					} else {
						sum += ausgangswerte[pos + xN] * kernel[i];
					}
				}
				resultat[pos] = sum/nenner;
			}
		}
		return resultat;
	}

	private double[] filterVertikal(final double[] ausgangswerte, final int width, final int height, final int[] kernel, int nenner) {
		double[] resultat = new double[ausgangswerte.length];
		int pos = 0;
		int kMitteY = kernel.length/2;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double sum = 0;
				pos = width * y + x;
				for (int i = 0; i < kernel.length; i++) {
					int yN = i-kMitteY;
					if (y + yN < 0) {
						sum += ausgangswerte[-pos - width*yN] * kernel[i];
					} 
					else if (y + yN >= height) {
						sum += ausgangswerte[x + height - 2 - y + width*yN + height] * kernel[i];
					} 
					else {
						sum += ausgangswerte[pos + width*yN] * kernel[i];
					}
				}
				resultat[pos] = sum /nenner;
			}
		}
		return resultat;

	}
    
}