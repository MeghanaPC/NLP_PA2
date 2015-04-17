package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 * Parses the top docs XML
 * 
 * @author Meghana
 *
 */
public class XmlParsingForRelevantDocs {

	/**
	 * Creates a new file with plain text of the top docs file passed as
	 * argument Loses distinction between the different documents within top
	 * docs.
	 * 
	 * text, leadpara, caption, headline, head, graphic, LP, HL
	 * 
	 * @param fileToBeParsed
	 * @param resultFileName
	 * @throws IOException
	 */
	public static void xmlParse(String fileToBeParsed, String resultFileName)
			throws IOException {

		BufferedWriter output = new BufferedWriter(new FileWriter(
				resultFileName));
		BufferedReader reader = new BufferedReader(new FileReader(
				fileToBeParsed));
		String input = null;
		List<String> tagList = new ArrayList<String>();
		tagList.add("HEAD");
		tagList.add("HEADLINE");
		tagList.add("HL");
		tagList.add("LEADPARA");
		tagList.add("LP");
		tagList.add("TEXT");
		tagList.add("CAPTION");
		tagList.add("GRAPHIC");

		while ((input = reader.readLine()) != null) {

			if (input.contains(("Qid"))) {
				BufferedWriter writerIntermed = new BufferedWriter(
						new FileWriter("intermedFile"));
				// get content between DOC and /DOC and write to intermed file
				while ((input = reader.readLine()) != null
						&& !input.equals("</DOC>")) {
					writerIntermed.write(input);
					// System.out.println(input);
					writerIntermed.newLine();
				}
				writerIntermed.write("</DOC>");

				writerIntermed.newLine();
				writerIntermed.close();
			}

			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document doc = docBuilder.parse("intermedFile");

				NodeList nList = doc.getElementsByTagName("DOC");

				for (int i = 0; i < nList.getLength(); i++) {
					Node nNode = nList.item(i);
					Element element = (Element) nNode;
					for (String tagtype : tagList) {
						NodeList childList = element
								.getElementsByTagName(tagtype);
						for (int j = 0; j < childList.getLength(); j++) {
							Node childNode = childList.item(j);
							String s = childNode.getTextContent();
							output.write(s);

						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		output.close();
		reader.close();
		
		 File file = new File("intermedFile"); file.delete();
		 

	}

	public static void main(String[] args){

		String pathToTopDocsDevDir = "/home/meghana/Documents/NLP_PA2/pa2_data/pa2-release/topdocs/dev";
		String pathToTopDocsTestDir = "/home/meghana/Documents/NLP_PA2/pa2_data/pa2-release/topdocs/test";
		
		File dev_parsed_dir = new File(pathToTopDocsDevDir + "_parsed/");
		dev_parsed_dir.mkdir();
		File test_parsed_dir = new File(pathToTopDocsTestDir + "_parsed/");
		test_parsed_dir.mkdir();

		File[] files = new File(pathToTopDocsDevDir + "/").listFiles();
		
		for(File file : files){
			try{
				xmlParse(file.getAbsolutePath(), pathToTopDocsDevDir + "_parsed/"
						+ file.getName());
				}catch(Exception e){
					e.printStackTrace();
				}
			
		}
		files = new File(pathToTopDocsTestDir + "/").listFiles();
		
		for(File file : files){
			try{
				xmlParse(file.getAbsolutePath(), pathToTopDocsTestDir + "_parsed/"
						+ file.getName());
				}catch(Exception e){
					e.printStackTrace();
				}
			
		}
	}
}
