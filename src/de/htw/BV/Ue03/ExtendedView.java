package de.htw.BV.Ue03;
import java.io.File;

/**
 * 
 */

/**
 * @author Marten SchÃ¤licke
 * @version 1.0
 */
public class ExtendedView extends ImageView {
	
	private double[] histo = new double[256];

    public ExtendedView(File file) {
	    super(file);
    }
    
    public ExtendedView(int x, int y){
    	super(x, y);
    }
    
    public void convertToGray() {
    	int[] pixels = getPixels();
    	
    	for (int pos = 0; pos < pixels.length; pos++) {

			// get pixel
			int colour = pixels[pos];

			// get RGB values
			int r = (colour & 0xff0000) >> 16;
			int g = (colour & 0x00ff00) >> 8;
			int b = (colour & 0x0000ff);
    	
			double Y =  (0.299 * r + 0.587 * g + 0.114 * b + 0.5);

			// Rücktransformation von YCbCr nach RGB
			r = (int) Y;
			g = (int) Y;
			b = (int) Y;

			pixels[pos] = (0xFF << 24) | (r << 16) | (g << 8) | b;  	
    	}
    	applyChanges();
    }
    
    private double getEntropie(){
        double entropie = 0;
        for(int i = 0; i < histo.length; i++){
            if(histo[i] != 0)entropie += histo[i] * 
            (Math.log10(histo[i])/Math.log10(2));
        }
        return -entropie;
    }
    
    private void drawHistogramm() {
        int pixels[] = getPixels();

        int width = getImgWidth();
        int height = getImgHeight();

        double highest = 0;
        for(int i = 0; i < histo.length; i++){
            if(histo[i] > highest)highest = histo[i];
        }
        double f = (height-15) / highest;
        int pos = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, pos++) {
                if((y < (height - 15 - (f*histo[x]))) && (y < height-15)){
                    pixels[pos] = 0xff000000;
                }
                else
                    pixels[pos] = 0xffffffff;    // white
                if (y > height-15){
                    int grau = (int) ((x / (double) width) * 255);
                    pixels[pos] = (0xff << 24) | (grau << 16) | (grau << 8) | grau;
                }
            }
        }

        applyChanges();
    }
    
    
}