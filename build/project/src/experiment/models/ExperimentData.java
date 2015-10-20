package experiment.models;

public class ExperimentData {
	private TestSuiteCoverage covarage = new TestSuiteCoverage();
	private int iteration;
	private int numberOfInvariants = 0;
	private int numberOfPrunedInvariants = 0;
	private int numbeOfInvalidatedInvariants = 0;
	private long executionTimeTestGeneration = 0;
	private long executionTimePruning = 0;
	private String experimentFolder;
	private TestSuiteCoverage coverage = new TestSuiteCoverage();
	
	public TestSuiteCoverage getCoverage() {
		return coverage;
	}
	
	public void setCoverage(TestSuiteCoverage coverage) {
		this.coverage = coverage;
	}
	
	public ExperimentData(int iteration, String experimentFolder){
		this.iteration = iteration;
		this.experimentFolder = experimentFolder;
	}
	
	public TestSuiteCoverage getCovarage() {
		return covarage;
	}
	
	public void setCovarage(TestSuiteCoverage covarage) {
		this.covarage = covarage;
	}
	
	public int getNumberOfInvariants() {
		return numberOfInvariants;
	}
	
	public void setNumberOfInvariants(int numberOfInvariants) {
		this.numberOfInvariants = numberOfInvariants;
	}
	
	public int getNumberOfPrunedInvariants() {
		return numberOfPrunedInvariants;
	}
	
	public void setNumberOfPrunedInvariants(int numberOfPrunedInvariants) {
		this.numberOfPrunedInvariants = numberOfPrunedInvariants;
	}
	
	public int getNumbeOfInvalidatedInvariants() {
		return numbeOfInvalidatedInvariants;
	}
	
	public void setNumbeOfInvalidatedInvariants(int numbeOfInvalidatedInvariants) {
		this.numbeOfInvalidatedInvariants = numbeOfInvalidatedInvariants;
	}
	
	public long getExecutionTimeTestGeneration() {
		return executionTimeTestGeneration;
	}
	
	public void setExecutionTimeTestGeneration(long executionTimeTestGeneration) {
		this.executionTimeTestGeneration = executionTimeTestGeneration;
	}
	
	public long getExecutionTimePruning() {
		return executionTimePruning;
	}
	
	public void setExecutionTimePruning(long executionTimePruning) {
		this.executionTimePruning = executionTimePruning;
	}
	
	public String getExperimentFolder() {
		return experimentFolder;
	}
}
