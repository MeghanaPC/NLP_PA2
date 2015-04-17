import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;


public class MainClass {
	
	public static final int linesToRead=200;
	public static MaxentTagger tagger;
	public static StanfordCoreNLP pipeline;
	
	public static void main(String[] args) throws IOException
	{
		/* ---------- loading pos tagger and ner ----- */
		 Properties props = new Properties();
	     boolean useRegexner = true;
	     if (useRegexner) {
	       props.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner");
	     } else {
	       props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
	     }
	     pipeline = new StanfordCoreNLP(props);
	    
	     tagger = new MaxentTagger("english-left3words-distsim.tagger");
	     
		/* -------main flow starts here ------------ */
		 Path filepath = Paths.get("/home/trupti/Desktop/pa2-release/qadata/dev/questions.txt");
	     BufferedReader quereader = new BufferedReader(new FileReader(filepath.toString()));
	     String line = null;
	     boolean flag=false;
	     String qno=null;
	     while ((line = quereader.readLine()) != null)
	     {
	    	
	    	 String question=null;
	    	 if(flag)
	    	 {
	    		 question=line;
	    		 System.out.println(question);
	    		 flag=false;
	    	 }
	    	
	    	 if(line.contains("Number"))
	    	 {
	    		 flag=true;
	    		 String[] temp=line.split(" ");
	    		 qno=temp[1];
	    		 System.out.println(qno);
	    		 
	    	 }
	    	 if(question!=null)
	    	 {
	    		 ArrayList<gramResult> resultList=new ArrayList<gramResult>();
	    		 resultList=processQuestions(qno);
	    		 
	    		 for(gramResult g:resultList)
	 	 		{
	 	 			Multimap<String, String> m=g.nerTagMap;
	 	 			ArrayList<String> a=g.nounPhraseList;
	 	 			System.out.println("---------------------------------new line-------------------------------------");
	 	 			for(Entry<String, String> entry:m.entries())
	 	 			{
	 	 				System.out.println(entry.getKey()+" - "+entry.getValue());
	 	 			}
	 	 			for(String nounp:a)
	 	 			{
	 	 				System.out.println(nounp);
	 	 			}
	 	 		}
	    	 }
	    	 // answer processing
	    	 
	    	
	     }
	     quereader.close();
	}
	public static ArrayList<gramResult> processQuestions(String qno) throws IOException
	{
		 ArrayList<gramResult> gramResultList=new ArrayList<gramResult>();
		 Path filepath = Paths.get("Ngram_"+qno);
	     BufferedReader reader = new BufferedReader(new FileReader(filepath.toString()));
	     String line1 = null;
	     int numberLines=0;
		 while ((line1 = reader.readLine()) != null&&numberLines<=linesToRead)
		 {
			numberLines=numberLines+1;
			Multimap<String, String> myMultimap = ArrayListMultimap.create();
			ArrayList<String> myPhraseList = new ArrayList<String>();
			 
				Annotation document = new Annotation(line1);
		        pipeline.annotate(document);

		       List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		       StringBuilder sb = new StringBuilder();
		       
		       for (CoreMap sentence : sentences) {
		         // traversing the words in the current sentence, "O" is a sensible default to initialise
		         // tokens to since we're not interested in unclassified / unknown things..
		         String prevNeToken = "O";
		         String currNeToken = "O";
		         boolean newToken = true;
		         for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
		           currNeToken = token.get(NamedEntityTagAnnotation.class);
		           String word = token.get(TextAnnotation.class);
		           // Strip out "O"s completely, makes code below easier to understand
		           if (currNeToken.equals("O")) {
		             // LOG.debug("Skipping '{}' classified as {}", word, currNeToken);
		             if (!prevNeToken.equals("O") && (sb.length() > 0)) {
		               //handleEntity(prevNeToken, sb, tokens);
		            	 myMultimap.put(prevNeToken,sb.toString());
		            	// System.out.println(prevNeToken+" "+sb+" ");
		            	 sb.setLength(0);
		               newToken = true;
		             }
		             continue;
		           }

		           if (newToken) {
		             prevNeToken = currNeToken;
		             newToken = false;
		             sb.append(word);
		             continue;
		           }

		           if (currNeToken.equals(prevNeToken)) {
		             sb.append(" " + word);
		           } else {
		             // We're done with the current entity - print it out and reset
		        	   myMultimap.put(prevNeToken,sb.toString());
		        	 //  System.out.println(prevNeToken+" "+sb+" ");
		        	   sb.setLength(0);
		             newToken = true;
		           }
		           prevNeToken = currNeToken;
		         }
		       }
		       /* -------POS -------*/
		       String tagged = tagger.tagString(line1);
		       boolean found=false;
		       String[] wordsTags=tagged.split(" ");
		       StringBuilder sbPhrase=new StringBuilder();
		       for(String wordTag:wordsTags)
		       {
		      	 if(wordTag.contains("_NN"))
		      	 {
		      		 String[] separatedWord=wordTag.split("_");
		      		 sbPhrase.append(separatedWord[0]).append(" "); 
		      		 found=true;
		      	 }
		      	 else
		      	 {
		      		 if(found)
		      		 {
		      			sbPhrase.setLength(sbPhrase.length()-1);
		      			myPhraseList.add(sbPhrase.toString()); 
		      		 }
		      		 found=false;
		      		 sbPhrase.setLength(0);	 
		      	 }
		       }
		       gramResult gramResultObject=new gramResult(myMultimap,myPhraseList);
		       gramResultList.add(gramResultObject);
		       
		} //while each sentence
		reader.close();
		
		return gramResultList;
		
	}
}
