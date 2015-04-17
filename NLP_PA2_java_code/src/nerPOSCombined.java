import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class nerPOSCombined {
	public static ArrayList<gramResult> gramResultObject=new ArrayList<gramResult>();
	
	public static void Main(String[] args) throws IOException
	{
		Properties props = new Properties();
	     boolean useRegexner = true;
	     if (useRegexner) {
	       props.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner");
	      // props.put("regexner.mapping", "locations.txt");
	     } else {
	       props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
	     }
	     StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	     //POS tagger
	     MaxentTagger tagger = new MaxentTagger(
	             "english-left3words-distsim.tagger");
	     
	     Path filepath = Paths.get("-----filepath-----");
	     BufferedReader reader = new BufferedReader(new FileReader(filepath.toString()));
	     String line = null;
		while ((line = reader.readLine()) != null)
		{
			
		}
	}
}
