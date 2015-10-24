package JVM;

public class ReactisJVM {
	/*public native int generateTestCases(String model, String info, String testSuite, String csv, String coverage);
	
	public native int generateTestCasesRandom(String model, String info, String testSuite, String csv, String coverage);
	*/
	public native int generateTestCases(String model, String info, String testSuite, String csv, String coverage, String settings);
	
	static {
		System.load("C:\\Users\\Christoph\\OneDrive\\newAnalysis\\ReactisTestGeneration\\x64\\Debug\\ReactisTestGeneration.dll");
	}
}
