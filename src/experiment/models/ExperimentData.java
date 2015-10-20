package experiment.models;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import experiment.models.reactis.ReactisData;
import experiment.models.tests.TestSuite;

public class ExperimentData {
	// Constants
	public static final String PATH_NAME = "experiment";
	public static final String TEMP_NAME = "temp";
	public static final String ITERATION = "iteration";
	public static final String TEST_SUITE = "testSuite.rst";
	public static final String CSV = "testSuite.csv";
	public static final String COVERAGE = "coverage.txt";
	public static final String DATA = "data.xlsx";
	public static final String ASSOCIATION_RULE_INPUT = "dataMiningInput.txt";
	public static final String ASSOCIATION_RULE_OUTPUT = "dataMiningOutput.txt";
	public static final String RSI = "info.rsi";
	public static final String ITERATION_RSI = "iteration.rsi";
	public static final String INVARIANTS = "invariants.txt";
	public static final String PRUNED_INVARIANTS = "invariantsPruned.txt";

	// Paths
	private String experimentFolderPath;
	private String tempFolderPath;
	private String modelFile;
	private String baseRsiFile;
	private String rsiFile;
	private String iterationRsiFile;

	// UI Data
	public IntegerProperty iteration = new SimpleIntegerProperty(1);
	public IntegerProperty id = new SimpleIntegerProperty(0);
	public IntegerProperty invariants = new SimpleIntegerProperty(0);
	public IntegerProperty invalidatedInvariants = new SimpleIntegerProperty(0);
	public IntegerProperty oldInvariants = new SimpleIntegerProperty(0);
	private int oldInvariantsTemp = 0;
	public IntegerProperty cutoff = new SimpleIntegerProperty(3);
	public LongProperty time = new SimpleLongProperty(0);
	public LongProperty overallTime = new SimpleLongProperty(0);
	public IntegerProperty iterationEnd = new SimpleIntegerProperty(0);

	private List<IterationData> iterationData = new ArrayList<>();
	private ReactisData reactisData;
	private TestSuite testSuite = new TestSuite();

	private long timeAll = 0;

	private StoppingCriterion stoppingCriterion;
	private boolean prune;
	private int maxInvariants;
	private IterationData currentIterationData;
	
	public ExperimentData(){
		
	}


	public String getExperimentFolderPath() {
		return experimentFolderPath;
	}

	public void setExperimentFolderPath(String experimentFolderPath) {
		this.experimentFolderPath = experimentFolderPath;
	}

	public String getTempFolderPath() {
		return tempFolderPath;
	}

	public void setTempFolderPath(String tempFolderPath) {
		this.tempFolderPath = tempFolderPath;
	}

	public String getModelFile() {
		return modelFile;
	}

	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}

	public String getBaseRsiFile() {
		return baseRsiFile;
	}

	public void setBaseRsiFile(String baseRsiFile) {
		this.baseRsiFile = baseRsiFile;
	}

	public String getRsiFile() {
		return rsiFile;
	}

	public void setRsiFile(String rsiFile) {
		this.rsiFile = rsiFile;
	}

	public String getIterationRsiFile() {
		return iterationRsiFile;
	}

	public void setIterationRsiFile(String iterationRsiFile) {
		this.iterationRsiFile = iterationRsiFile;
	}

	public IntegerProperty getIteration() {
		return iteration;
	}

	public void setIteration(IntegerProperty iteration) {
		this.iteration = iteration;
	}

	public IntegerProperty getId() {
		return id;
	}

	public void setId(IntegerProperty id) {
		this.id = id;
	}

	public IntegerProperty getInvariants() {
		return invariants;
	}

	public void setInvariants(IntegerProperty invariants) {
		this.invariants = invariants;
	}

	public IntegerProperty getInvalidatedInvariants() {
		return invalidatedInvariants;
	}

	public void setInvalidatedInvariants(IntegerProperty invalidatedInvariants) {
		this.invalidatedInvariants = invalidatedInvariants;
	}

	public IntegerProperty getOldInvariants() {
		return oldInvariants;
	}

	public void setOldInvariants(IntegerProperty oldInvariants) {
		this.oldInvariants = oldInvariants;
	}

	public int getOldInvariantsTemp() {
		return oldInvariantsTemp;
	}

	public void setOldInvariantsTemp(int oldInvariantsTemp) {
		this.oldInvariantsTemp = oldInvariantsTemp;
	}

	public IntegerProperty getCutoff() {
		return cutoff;
	}

	public void setCutoff(IntegerProperty cutoff) {
		this.cutoff = cutoff;
	}

	public LongProperty getTime() {
		return time;
	}

	public void setTime(LongProperty time) {
		this.time = time;
	}

	public LongProperty getOverallTime() {
		return overallTime;
	}

	public void setOverallTime(LongProperty overallTime) {
		this.overallTime = overallTime;
	}

	public List<IterationData> getIterationData() {
		return iterationData;
	}

	public void setIterationData(List<IterationData> iterationData) {
		this.iterationData = iterationData;
	}

	public ReactisData getReactisData() {
		return reactisData;
	}

	public void setReactisData(ReactisData reactisData) {
		this.reactisData = reactisData;
	}

	public TestSuite getTestSuite() {
		return testSuite;
	}

	public void setTestSuite(TestSuite testSuite) {
		this.testSuite = testSuite;
	}

	public long getTimeAll() {
		return timeAll;
	}

	public void setTimeAll(long timeAll) {
		this.timeAll = timeAll;
	}

	public StoppingCriterion getStoppingCriterion() {
		return stoppingCriterion;
	}

	public void setStoppingCriterion(StoppingCriterion stoppingCriterion) {
		this.stoppingCriterion = stoppingCriterion;
	}

	public boolean isPrune() {
		return prune;
	}

	public void setPrune(boolean prune) {
		this.prune = prune;
	}

	public int getMaxInvariants() {
		return maxInvariants;
	}

	public void setMaxInvariants(int maxInvariants) {
		this.maxInvariants = maxInvariants;
	}

	public IterationData getLastIterationData() {
		return currentIterationData;
	}

	public void setLastIterationData(IterationData currentIterationData) {
		this.currentIterationData = currentIterationData;
	}
}
