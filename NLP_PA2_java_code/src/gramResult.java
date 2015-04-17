import java.util.ArrayList;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class gramResult {
	  Multimap<String, String> nerTagMap;
	  ArrayList<String> nounPhraseMap ;
	
	  gramResult()
	  {
		  
	  }
	  gramResult(ArrayListMultimap<String,String> nermap,ArrayList<String> phrasemap)
	  {
		  nerTagMap=nermap;
		  nounPhraseMap=phrasemap;
		  
	  }
	 
}
