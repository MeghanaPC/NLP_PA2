import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


public class MultiMap_HowToUse {

	 public static void main(String[] args) {
		  Multimap<String, String> myMultimap = ArrayListMultimap.create();
		   
		  // Adding some key/value
		  myMultimap.put("Fruits", "Bannana");
		  myMultimap.put("Fruits", "Apple");
		  myMultimap.put("Fruits", "Pear");
		  myMultimap.put("Vegetables", "Carrot");
		   
		  // Getting the size
		  int size = myMultimap.size();
		  System.out.println(size);  // 4
		   
		  // Getting values
		  Collection<String> fruits = myMultimap.get("Fruits");
		  System.out.println(fruits);
		  
		  Collection<String> vegetables = myMultimap.get("Vegetables");
		  System.out.println(vegetables); // [Carrot]
		   
		  System.out.println("printing values");
		  // Iterating over entire Mutlimap
		  for(String value : myMultimap.values()) {
			  
		   System.out.println(value);
		  }
		   
		  // Removing a single value
		  myMultimap.remove("Fruits","Pear");
		  System.out.println(myMultimap.get("Fruits")); // [Bannana, Apple]
		  // Remove all values for a key
		  myMultimap.removeAll("Fruits");
		  System.out.println(myMultimap.get("Fruits")); //[]
	 }
}
