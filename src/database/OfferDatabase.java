package database;

/*
 * Author: P. Berkovich
 *
 * This singleton class is the OfferDatabase
 */


class OfferDatabase extends Database<Integer, Offer>{
	
	private static OfferDatabase instance = null;
	
	public static OfferDatabase getInstance(){
		if(instance == null)
			instance = new OfferDatabase();
		
		return instance;
		
	}
	
	private OfferDatabase(){
		
	}
	
	@Override
	public void add(Offer offer) throws KeyCollision {
		
		offer.setID(items.size()); //set offerID to point at location in the list
		items.add(offer); //add offer to the OfferDatabase
		offer.getProduct().getOffers().add(offer);
		//add offer to the list of offers in the approp. Product
		
	}
	
}