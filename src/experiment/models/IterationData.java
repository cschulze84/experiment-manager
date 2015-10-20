package experiment.models;

import experiment.models.tests.TestSuite;
import experiment.models.tests.TestSuiteCoverage;

public class IterationData {
	private TestSuite testSuite = new TestSuite();
	private int iteration = 0;
	private int numberOfInvariants = 0;
	private int numberOfPrunedInvariants = 0;
	private int numbeOfInvalidatedInvariants = 0;
	private long executionTimeTestGeneration = 0;
	private TestSuiteCoverage coverage = new TestSuiteCoverage();

	public IterationData(int iteration, String iterationFolder) {
		this.iteration = iteration;
	}

	public int getNumberOfPrunedInvariants() {
		return numberOfPrunedInvariants;
	}

	public int getNumbeOfInvalidatedInvariants() {
		return numbeOfInvalidatedInvariants;
	}

	public long getExecutionTimeTestGeneration() {
		return executionTimeTestGeneration;
	}
	
	public void setExecutionTimeTestGeneration(long timeInSeconds) {
		this.executionTimeTestGeneration = timeInSeconds;
	}

	public TestSuite getTestSuite() {
		return testSuite;
	}

	public void setNumberOfInvariants(int numberOfInvariants) {
		this.numberOfInvariants = numberOfInvariants;
	}

	public void setNumberOfPrunedInvariants(int numberOfPrunedInvariants) {
		this.numberOfPrunedInvariants = numberOfPrunedInvariants;
	}

	public int getIteration() {
		return iteration;
	}

	public TestSuiteCoverage getCovarage() {
		return coverage;
	}

	public void setNumbeOfInvalidatedInvariants(int numbeOfInvalidatedInvariants) {
		this.numbeOfInvalidatedInvariants = numbeOfInvalidatedInvariants;
	}

}
