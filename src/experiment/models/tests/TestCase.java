package experiment.models.tests;

import java.util.ArrayList;
import java.util.List;

public class TestCase {
	List<TestData> testCaseData = new ArrayList<>();
	
	public TestData addNewTestData(){
		TestData testData = new TestData();
		testCaseData.add(testData);
		return testData;
	}
	
	public List<TestData> getTestCaseData() {
		return testCaseData;
	}
}
