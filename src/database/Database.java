package database;

/**
 *	Author: P. Berkovich
 *
 *This is the most generic abstract class for a database.  
**/

import java.util.List;
import java.util.LinkedList;

abstract class Database<K extends Comparable<K>, V> {
	
	protected List<V> items = new LinkedList<V>();
	
	public void add(V item) throws KeyCollision {
		
		if(items.contains(item.getKey()))
			throw new KeyCollision();
		else
			items.add(item);
				
	}
	
	public boolean contains(K key){
		/*
		 * return true iff the db contains an item with the given key
		 */
			
		for(V i: items)
			if(key.compareTo(i.getKey())==0)
				return true;
		
		return false;
		
	}
	
	public V find(K key) throws ItemNotFound {
		
		/*
		 *returns the item with specified key, throws ItemNotFound if
		 *does not exist
		 */
			
		for(V i: items)
			if(key.compareTo(i.getKey())==0)
				return i;
		
		throw new ItemNotFound();
		
	}
	
	public void delete(K key) throws ItemNotFound{
		/*
		 * deletes item with the specified key, or throws ItemNotFound if 
		 * does not exist		
		 */	
		for(V i: items)
			if(key.compareTo(i.getKey())==0)
				items.remove(i);
		
		throw new ItemNotFound();
	}
	
}