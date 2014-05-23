package image;

/*
 * For now, we'll just recognise identical images -- using hashcodes
 * to compute image signatures
 *
 */

import java.awt.Image;

class ImageSignature {
	
	private final int signature;
	
	ImageSignature(Image raw){
		
		signature = RawToBits(raw);
		
	}
	
	public int getSignature(){
		return signature;
	}
	
	private int RawToBits(Image raw){
		
		return raw.hashCode();
		//so, for now, we'll just recognise IDENTICAL images
		
	}
	
	public int compare(ImageSignature with){
		return this.signature - with.getSignature();
		
		// >0 if this > with
		// 0 if this == with
		// <0 if this < with
		
	}
	
}