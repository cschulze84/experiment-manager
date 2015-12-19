package experiment.dataExtraction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import experiment.models.tests.TestCase;
import experiment.models.tests.TestData;
import experiment.models.tests.TestSuite;

public class TestCaseParser {

	public TestCaseParser() {

	}

	/*public static void main(String[] args) {
		TestCaseParser parser = new TestCaseParser();
		//TestSuite suite = parser.parseCSV(".//testSuite.csv");
		suite.addInput("rb_IndicatorsRequested");
		suite.addInput("rb_indicators_HzrdSwtchEdgesIsPressedPlaus");
		suite.addInput("rb_state");
		suite.addOutput("rb_IndicatorsNew");
		
		AssociationRuleMining miner = new AssociationRuleMining();
		miner.printForAssociationRuleMining(suite, ".//input.txt");
		
		miner.mineRules(".//input.txt", ".//output.txt");
		
		List<Implication> implications = miner.parseMiningOutputs(suite, ".//output.txt");
		
		
		System.out.println("ALL");
		for (Implication implication : implications) {
			System.out.println(implication.toString());
		}
		
		System.out.println("PRUNED");
		
		for (Implication implication : miner.prune(implications)) {
			System.out.println(implication.toString());
		}
	}*/

	public TestSuite parseCSV(String filePath, TestSuite suite) {
		File csvData = new File(filePath);
		CSVParser parser = null;
		try {
			parser = CSVParser.parse(csvData, Charset.defaultCharset(),	CSVFormat.EXCEL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean header = true;
		
		if(suite.getHeader().size() > 0){
			header = false;
		}
		
		TestCase testCase = suite.addNewTestCase();

		boolean newTestCase = false;
		boolean lastLineBlank = false;
		boolean firstPart = true;
		
		for (CSVRecord csvRecord : parser) {
			if(csvRecord.size() == 0 || (csvRecord.size() == 1 && csvRecord.get(0).length() == 0)){
				if(!lastLineBlank){
					newTestCase = true;
				}
				continue;
			}
			if(header){
				for (String headerString : csvRecord) {
					suite.getHeader().add(headerString.replace("|", ""));
				}
				header = false;
				firstPart = false;
			}
			else if(!header && firstPart){
				firstPart=false;
				continue;
			}
			else{
				lastLineBlank = false;
				if(newTestCase){
					testCase = suite.addNewTestCase();
					newTestCase = false;
				}
				TestData data = testCase.addNewTestData();
				for (String dataString : csvRecord) {
					data.getData().add(dataString);
				}
			}
		}
		return suite;
	}
}
