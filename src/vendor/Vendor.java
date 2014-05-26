package vendor;

class Vendor {
	private String name;
	private String password;

	private List<String> offerKeys;

	private String key;

	public Vendor () {
	}

	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }

	public List<String> getOfferKeys() { return offerKeys; }

	public String getName() { return name; }

}