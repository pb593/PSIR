package database;

/*
 * Author: P. Berkovich
 *
 * This singleton class is the UserDatabase
 */


class UserDatabase extends Database<String, User>{
	
	private static UserDatabase instance = null;
	
	public static UserDatabase getInstance(){
		if(instance == null)
			instance = new UserDatabase();
		
		return instance;
		
	}
	
	private UserDatabase(){
		
	}
}