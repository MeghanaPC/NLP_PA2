import java.util.ArrayList;

import com.google.common.collect.Multimap;

public class gramResult {
	  public Multimap<String, String> nerTagMap;
	  public ArrayList<String> nounPhraseList;
	
	  gramResult()
	  {
		  
	  }
	  gramResult(Multimap<String, String> myMultimap,ArrayList<String> phrasemap)
	  {
		  nerTagMap=myMultimap;
		  nounPhraseList=phrasemap;
		  
	  }
	 
}
