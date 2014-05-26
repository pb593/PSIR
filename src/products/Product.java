package products;

import image.*;
import java.awt.Image;

class Product {
	private String name;
	private String description;

	private ArrayList<String> offerKeys;

	private ArrayList<ProcessedImage> images;
	private ArrayList<Image> displayImages;

	private String key;

	public Product (String name, String description, ArrayList<String> offerKeys, ArrayList<ProcessedImage> images, ArrayList<Image> displayImages){
		this.name = name;
		this.description = description;
		this.offerKeys = offerKeys;
		this.images = images;
		this.displayImages = displayImages;
	}

	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }

	public ArrayList<String> getOfferKeys() { return offerKeys; }

	public ArrayList<ProcessedImage> getImages() { return images; }
	public ArrayList<Image> getDisplayImages() { return displayImages; }

	public void addImage(ProcessedImage image) { images.add(image); }
	public void deleteImage(ProcessedImage image) throws ImageNotFound {
		if !(images.remove(image))
			throw new ImageNotFound();
	}

	public void addDisplayImage(Image image) { displayImages.add(image); }
	public void deleteDisplayImage(Image image) throws ImageNotFound {
		if !(displayImages.remove(image))
			throw new ImageNotFound();
	}

	public void addOfferKey(String key) { offerKeys.add(key); }
	public void deleteOfferKey(String key) throws KeyNotFound {
		if !(offerKeys.remove(key))
			throw new KeyNotFound();
	}

}