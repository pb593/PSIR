package image;

/*
 * Comprises the actual Image and its calculated signature
 *
 */

import java.awt.Image;

class ProcessedImage {
	
	private Image raw;
	
	private ImageSignature signature;
	
	ProcessedImage(Image img){
		
		this.raw = img;
		signature = new ImageSignature(img);
		
	}
	
}