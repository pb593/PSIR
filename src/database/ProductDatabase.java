package database;


import java.awt.Image;

/*
 * Author: P. Berkovich
 *
 * This singleton class is the ProductDatabase
 */

class ProductDatabase extends Database<String, Product>{
	
	private static ProductDatabase instance = null;
	
	public static ProductDatabase getInstance(){
		if(instance == null)
			instance = new ProductDatabase();
		
		return instance;
		
	}
	
	private ProductDatabase(){
		
	}
	
	public List<Product> find_similar(Image im){
		//TODO
	}
	
}