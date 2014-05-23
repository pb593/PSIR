package database;


/*
 * Author: P. Berkovich
 *
 * This singleton class is the OfferDatabase
 */


class VendorDatabase extends Database<String, Vendor>{
	
	private static VendorDatabase instance = null;
	
	public static VendorDatabase getInstance(){
		if(instance == null)
			instance = new VendorDatabase();
		
		return instance;
		
	}
	
	private VendorDatabase(){
		
	}
	
}