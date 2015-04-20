import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Pattern;

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
	
	public static final int linesToRead=1900;
	public static MaxentTagger tagger;
	public static StanfordCoreNLP pipeline;
	
	private static final int numberAnswers=10;
	private static final int countNER=7;
	//private static final int countPhrase=0;     numberAnswers-countNER
	
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
	     Path resultFilePath = Paths.get("Answers_Test.txt");
	     BufferedWriter writer=new BufferedWriter(new FileWriter(resultFilePath.toString()));
	     
	     
	     
		 Path filepath = Paths.get("/home/trupti/Desktop/pa2-release/qadata/test/questions.txt");
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
	    		 ArrayList<String> finalAnswerList=new ArrayList<String>();  //need to be instantiated only once
	    		 ArrayList<gramResult> resultList=new ArrayList<gramResult>();
	    		 resultList=processQuestions(qno);
	    		 
	    		 String taggedquestion = tagger.tagString(question);
	    		 String answerType=Answertype.findAnswerType(question,taggedquestion);
	    		 
	    		 	if(Pattern.compile("(DATE|LOCATION|PERSON|TIME|ORGANIZATION|TIME|MONEY)",Pattern.CASE_INSENSITIVE).matcher(answerType).find())
	    			{
	    		 		//taking 7 NERS and 3 phrases. Else if no NERS found, then rest of them noun phrases.
	    		 		//int countAns=0;
	    				boolean doneNER=false;
	    				for(gramResult rl:resultList)
	    				{
	    					Multimap<String, String> map=ArrayListMultimap.create(rl.nerTagMap);	
	    	 	 			
	    	 	 		
	    	 	 			Collection<String> taggerWords=map.get(answerType);
	    	 	 			for(String s:taggerWords)
	    	 	 			{
	    	 	 				if(finalAnswerList.size()>=countNER)
	    	 	 				{
	    	 	 					doneNER=true;
	    	 	 					break;
	    	 	 				}
	    	 	 				String filtered = filterAnswer(s, question);
	    	 	 				if(filtered != null && !(finalAnswerList.contains(filtered))){
	    	 	 					finalAnswerList.add(filtered);
	    	 	 				}
	    	 	 			}

	    				}
	    				int remaining=numberAnswers-finalAnswerList.size();
	    				for(gramResult rl:resultList)
	    				{
	    					ArrayList<String> arr=new ArrayList<String>(rl.nounPhraseList);
	    					for(String phrase:arr)
	    					{
	    						if(remaining>0)
	    						{
	    							String filtered = filterAnswer(phrase, question);
		    	 	 				if(filtered != null && !(finalAnswerList.contains(filtered))){
		    	 	 					finalAnswerList.add(filtered);
		    	 	 					--remaining;
		    	 	 				}
	    							
	    						}
	    						else
	    							break;
	    					}
	    				}
	    				
	    		 		System.out.println("ner tag found");
	    			}
	    			else if(Pattern.compile("(NOUNPHRASE)",Pattern.CASE_INSENSITIVE).matcher(answerType).find())
	    			{
	    				//just taking top 10 noun phrases for now 
	    				System.out.println("noun phrase found");
	    				int countAns=0;
	    				boolean doneFlag=false;
	    				for(gramResult rl:resultList)
	    				{
	    					ArrayList<String> arr=new ArrayList<String>(rl.nounPhraseList);
	    					for(String nounp:arr)
	    	 	 			{
	    						
	    						String filtered = filterAnswer(nounp, question);
	    	 	 				if(filtered != null && !(finalAnswerList.contains(filtered))){
	    	 	 					countAns=countAns+1;
	    	 	 					finalAnswerList.add(filtered);
	    	 	 				}	    						
	    	 	 				if(countAns>=numberAnswers)
	    						{
	    							doneFlag=true;
	    							break;
	    						}
	    	 	 			}
	    					if(doneFlag)
    							break;	
	    				}
	    					
	    			}
	    			else if(Pattern.compile("(PROPERNOUN)",Pattern.CASE_INSENSITIVE).matcher(answerType).find())
	    			{
	    				//just taking top 10 noun phrases for now 
	    				System.out.println("proper noun phrase found");
	    				int countAns=0;
	    				boolean doneFlag=false;
	    				for(gramResult rl:resultList)
	    				{
	    					ArrayList<String> arrp=new ArrayList<String>(rl.properNounPhraseList);
	    					for(String pnounp:arrp)
	    	 	 			{
	    						
	    						String filtered = filterAnswer(pnounp, question);
	    	 	 				if(filtered != null && !(finalAnswerList.contains(filtered))){
	    	 	 					countAns=countAns+1;
	    	 	 					finalAnswerList.add(filtered);
	    	 	 				}	    						
	    	 	 				if(countAns>=numberAnswers)
	    						{
	    							doneFlag=true;
	    							break;
	    						}
	    	 	 			}
	    					if(doneFlag)
    							break;	
	    				}
	    					
	    			}
	    			else if(Pattern.compile("(NUMBER)",Pattern.CASE_INSENSITIVE).matcher(answerType).find())
	    			{
	    				//might need to keep list of POS tags for date.
	    				 System.out.println("number found");
	    			}
	    			else
	    			{
	    				System.out.println("none of the question types match...error");
	    			}
	    		 
	    		 	writer.write("qid"+" "+qno);
	    		 	writer.newLine();
	    		 	for(int i=0;i<finalAnswerList.size();i++)
	    		 	{
	    		 		if(i >= numberAnswers){
	    		 			break;
	    		 		}
	    		 		writer.write(i+1+" "+finalAnswerList.get(i));
	    		 		writer.newLine();
	    		 		
	    		 	}

	    		 
	    	
	    	 }
	    	 // answer processing
	    	 
	    	
	     }
	     quereader.close();
	     writer.close();
	}
	private static String filterAnswer(String s, String question) {
		
		String result = "";
		Boolean found = false;
		
		//for each word in the answer which is not present in the question, add it to result
		String[] answerWords = s.split(" ");
		for(String answerWord : answerWords){
			if(!question.toLowerCase().contains(answerWord.toLowerCase())){
				found = true;
				result = result + " "  + answerWord + " ";
				//result = result+answerWord;
			}
		}
		if(found){
			return result.trim();			
		}else{
			return null;
		}
	}
	public static ArrayList<gramResult> processQuestions(String qno) throws IOException
	{
		 System.out.println(qno+"in the process method");
		 ArrayList<gramResult> gramResultList=new ArrayList<gramResult>();
		 Path filepath = Paths.get("d_ngrams_keywordOverlap/Ngrams_"+qno);
	     BufferedReader reader = new BufferedReader(new FileReader(filepath.toString()));
	     String line1 = null;
	     int numberLines=0;
		 while ((line1 = reader.readLine()) != null&&numberLines<=linesToRead)
		 {
			numberLines=numberLines+1;
			Multimap<String, String> myMultimap = ArrayListMultimap.create();
			ArrayList<String> myPhraseList = new ArrayList<String>();
			ArrayList<String> myNounPhraseList = new ArrayList<String>();
			 
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
		       boolean properFound=false;
		       String[] wordsTags=tagged.split(" ");
		       StringBuilder sbPhrase=new StringBuilder();
		       StringBuilder sbProperNoun=new StringBuilder();
		       boolean firstwordJJ=false;
		       for(String wordTag:wordsTags)
		       {
		      	 
		    	   if(wordTag.contains("_JJ")&&firstwordJJ==false)
		    	   { 
			    		   firstwordJJ=true;
			    		   String[] separatedWordJJ=wordTag.split("_");
				      	   sbPhrase.append(separatedWordJJ[0]).append(" "); 
		    	   }
		    	   else if(wordTag.contains("_NN")&&firstwordJJ)
			      {
			      		 String[] separatedWord=wordTag.split("_");
			      		 sbPhrase.append(separatedWord[0]).append(" "); 
			      		 found=true;
			      		 firstwordJJ=false;
			       }
		    	   else if(wordTag.contains("_NN"))
		    	   {
		    		   String[] separatedWordn=wordTag.split("_");
			      		sbPhrase.append(separatedWordn[0]).append(" "); 
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
		      		 firstwordJJ=false;
		      		 sbPhrase.setLength(0);	 
		      		 
		      		 
		      		if(wordTag.contains("_JJ")&&firstwordJJ==false)
			    	{ 
				    		   firstwordJJ=true;
				    		   String[] separatedWordJJnew=wordTag.split("_");
					      	   sbPhrase.append(separatedWordJJnew[0]).append(" "); 
			    	}
		      	 }
		      	 if(wordTag.contains("_NNP"))
		      	 {
		      		 String[] separatedWordnoun=wordTag.split("_");
		      		 sbProperNoun.append(separatedWordnoun[0]).append(" "); 
		      		 properFound=true;
		      		 
		      	 }
		      	 else
		      	 {
		      		 if(properFound)
		      		 {
		      			sbProperNoun.setLength(sbProperNoun.length()-1);
		      			myNounPhraseList.add(sbProperNoun.toString()); 
		      		 }
		      		properFound=false;
		      		sbProperNoun.setLength(0);
		      	 }
		       }
		       gramResult gramResultObject=new gramResult(myMultimap,myPhraseList,myNounPhraseList);
		       gramResultList.add(gramResultObject);
		       
		} //while each sentence
		reader.close();
		
		return gramResultList;
		
	}
}
