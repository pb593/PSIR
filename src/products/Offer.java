package products;

class Offer {
	private int price;
	private int availability;

	private String productKey;
	private String vendorKey;

	private String key;

	public Offer (int price, int availability, String productKey, String vendorKey) {
		this.price = price;
		this.availability = availability;
		this.productKey = productKey;
		this.vendorKey = vendorKey;
	}

	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }

	public String getProductKey() { return productKey; }
	public String getVendorKey() { return vendorKey; }

	public void setPrice(int price) { this.price = price; }
	public int getPrice() { return price; }

	public void setAvailability(int availability) { this.availability = availability; }
	public int getAvailability() { return availability; }

	public void purchase(int quantity) {}
}