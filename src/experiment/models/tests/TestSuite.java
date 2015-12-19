package experiment.models.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import experiment.models.invariants.Implication;

public class TestSuite {
	
	private static final String TIME_HEADER = "___t___";
	
	List<String> header = new ArrayList<>();
	List<TestCase> testCases = new ArrayList<>();
	List<String> inputs = new ArrayList<>();
	List<String> outputs = new ArrayList<>();
	
	List<Implication> implications = new ArrayList<>();
	List<Implication> implicationsPruned = new ArrayList<>();
	
	public void setImplications(List<Implication> implications) {
		this.implications = implications;
	}
	
	public List<Implication> getImplications() {
		return implications;
	}
	
	public void setImplicationsPruned(List<Implication> implicationsPruned) {
		this.implicationsPruned = implicationsPruned;
	}
	
	public List<Implication> getImplicationsPruned() {
		return implicationsPruned;
	}
	
	public TestCase addNewTestCase(){
		TestCase testCase = new TestCase();
		testCases.add(testCase);
		return testCase;
	}
	
	public List<String> getHeader() {
		return header;
	}
	
	public List<TestCase> getTestCases() {
		return testCases;
	}
	
	public void addInput(String input){
		inputs.add(input);
	}
	
	public void addOutput(String output){
		outputs.add(output);
	}
	
	public void printToConsole(boolean header){
		if(header){
			for (String headerString : this.header) {
				System.out.print(headerString + " ");
			}
			System.out.println();
		}
		
		for (TestCase tc : getTestCases()) {
			for (TestData testData : tc.getTestCaseData()) {
				for (String dataString : testData.getData()) {
					System.out.print(dataString + " ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	
	
	public int getTimeColumn() {
		int i = 0;
		for (String headerString : header) {
			if(headerString.equalsIgnoreCase(TIME_HEADER)){
				return i;
			}
			i++;
		}
		return -1;
	}

	public void printToFile(boolean header, boolean lines, String filePath){
		File file = new File(filePath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			if(header){
				boolean first = true;
				for (String headerString : this.header) {
					if(first){
						writer.write(headerString);
						first = false;
					}
					else{
						writer.write(" " + headerString);
					}
					
				}
				writer.write(System.lineSeparator());
			}
			
			for (TestCase tc : getTestCases()) {
				for (TestData testData : tc.getTestCaseData()) {
					boolean first = true;
					for (String dataString : testData.getData()) {
						if(first){
							writer.write(dataString);
							first = false;
						}
						else{
							writer.write(" " + dataString);
						}
					}
					writer.write(System.lineSeparator());
				}
				if(lines){
					writer.write(System.lineSeparator());
				}
			}
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public boolean isInput(String variableName) {
		for (String input : inputs) {
			if(input.equalsIgnoreCase(variableName)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isOutput(String variableName) {
		for (String output : outputs) {
			if(output.equalsIgnoreCase(variableName)){
				return true;
			}
		}
		return false;
	}
	
	public List<String> getInputs() {
		return inputs;
	}
}
