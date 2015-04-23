import java.util.*;



public class NgramTiler {
	
	public static ArrayList<String> tileGrams(ArrayList<String> ngrams){
		
		//In pass 1, try merging n grams from end without deleting
		for(int i=ngrams.size()-1; i > 0; i--){
			String[] parts = ngrams.get(i).split("\\s+");
			//System.out.println(parts[0]+":"+parts[1]);
			String result = "";
			for(int k=1; k<parts.length; k++){
				if(result == ""){
					result = parts[k];
				}
				else{
				result = result+" "+ parts[k];
				}
				
			}
			for(int j=i-1; j >= 0; j--){
				if(ngrams.get(j).endsWith(parts[0])){
					ngrams.set(j,ngrams.get(j)+" "+result);
				}
			}
		}
		int tracker = 0;
		int cur = 1;
		
		System.out.println(ngrams.toString());
		//remove redundant elements after stage 1
		while(cur < ngrams.size()){
			boolean flag = false;
				for(int j = tracker; j >= 0; j--){
					if(ngrams.get(j).endsWith(ngrams.get(cur))){
						ngrams.remove(cur);
						flag = true;
						break;
					}
				
				}
				if(!flag){
					cur++;
					tracker++;
			}
		}
		
		//pass 2
		for(int i=ngrams.size()-1; i > 0; i--){
			String[] parts = ngrams.get(i).split("\\s+");
			//System.out.println(parts[0]+":"+parts[1]);
			String result = "";
			for(int k=0; k<parts.length-1; k++){
				if(result == ""){
					result = parts[k];
				}
				else{
				result = result+" "+ parts[k];
				}
				
			}
			for(int j=i-1; j >= 0; j--){
				if(ngrams.get(j).startsWith(parts[parts.length-1])){
					ngrams.set(j,result+" "+ngrams.get(j));
				}
			}
		}
		tracker = 0;
		cur = 1;
		
		System.out.println(ngrams.toString());
		//remove redundant elements after stage 1
		while(cur < ngrams.size()){
			boolean flag = false;
				for(int j = tracker; j >= 0; j--){
					if(ngrams.get(j).startsWith(ngrams.get(cur))){
						ngrams.remove(cur);
						flag = true;
						break;
					}
				
				}
				if(!flag){
					cur++;
					tracker++;
			}
		}
		
		
		
		
		
		return ngrams;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> test = new ArrayList<String>();
		test.add("Charles Dickens");
		test.add("Sherlock Holmes");
		test.add("Dickens Junior");
		test.add("Charles Xavier");
		test.add("Mr. Charles");
		test.add("Bruce Way");
		test.add("Agent X");
		test.add("Holmes I");
		test.add("Junior 1");
		System.out.println(test.toString());
		tileGrams(test);
		System.out.println(test.toString());
	}

}
