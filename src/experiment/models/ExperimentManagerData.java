package experiment.models;

import java.io.File;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import experiment.Experiment;
import experiment.models.reactis.ReactisData;

public class ExperimentManagerData {
	//Paths
	private String folderPath;
	private String modelFile;
	private String modelSupport;
	private String rsiFile;
	
	//Settings
	private StoppingCriterion stoppingCriterion;
	private ExecutionOrder executionOrder;
	private int iterationEnd;
	private boolean prune;
	private int maxInvariants;
	private int numberOfTests;
	private int numberOfTestSteps;
	private int numberOfTargetedSteps;
	private IntegerProperty numberOfExperiments = new SimpleIntegerProperty(0);
	
	//Model
	private ReactisData reactisData = new ReactisData();
	
	//Experiment Data
	private ObservableList<Experiment> experiments = FXCollections
			.observableArrayList();
	private String name;
	private boolean analyze;


	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getModelFile() {
		return modelFile;
	}

	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}
	

	public void setModelSupport(String modelSupport) {
		this.modelSupport = modelSupport;
	}
	
	public String getModelSupport() {
		return modelSupport;
	}

	public String getRsiFile() {
		return rsiFile;
	}

	public void setRsiFile(String rsiFile) {
		this.rsiFile = rsiFile;
	}

	public IntegerProperty getNumberOfExperiments() {
		return numberOfExperiments;
	}

	public void setNumberOfExperiments(int numberOfExperiments) {
		this.numberOfExperiments.set(numberOfExperiments);
	}

	public ObservableList<Experiment> getExperiments() {
		return experiments;
	}

	public void setExperiments(ObservableList<Experiment> experiments) {
		this.experiments = experiments;
	}

	public ReactisData getReactisData() {
		return reactisData;
	}

	public void setReactisData(ReactisData reactisData) {
		this.reactisData = reactisData;
	}

	public StoppingCriterion getStoppingCriterion() {
		return stoppingCriterion;
	}

	public void setStoppingCriterion(StoppingCriterion stoppingCriterion) {
		this.stoppingCriterion = stoppingCriterion;
	}

	public int getIterationEnd() {
		return iterationEnd;
	}

	public void setIterationEnd(int iterationEnd) {
		this.iterationEnd = iterationEnd;
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

	public int getNumberOfTests() {
		return numberOfTests;
	}

	public void setNumberOfTests(int numberOfTests) {
		this.numberOfTests = numberOfTests;
	}

	public int getNumberOfTestSteps() {
		return numberOfTestSteps;
	}

	public void setNumberOfTestSteps(int numberOfTestSteps) {
		this.numberOfTestSteps = numberOfTestSteps;
	}

	public int getNumberOfTargetedSteps() {
		return numberOfTargetedSteps;
	}

	public void setNumberOfTargetedSteps(int numberOfTargetedSteps) {
		this.numberOfTargetedSteps = numberOfTargetedSteps;
	}

	public String getModelName() {
		File file = new File(modelFile);
		return file.getName();
	}
	
	public int getNumberOfInputs() {
		return reactisData.getInports().size();
	}
	
	public int getNumberOfOutputs() {
		return reactisData.getOutports().size();
	}

	public double getNumberOfInvariants() {
		double average = 0;
		for (Experiment experiment : experiments) {
			average += experiment.getData().getInvariants().get();
		}
		return average / experiments.size();
	}

	public double getNumberOfIterations() {
		double average = 0;
		for (Experiment experiment : experiments) {
			average += experiment.getData().getIteration().get();
		}
		return average / experiments.size();
	}

	public double getAverageTime() {
		double average = 0;
		for (Experiment experiment : experiments) {
			average += experiment.getData().getTimeAll();
		}
		return average / experiments.size();
	}

	public void setExecutionOrder(ExecutionOrder executionOrder) {
		this.executionOrder = executionOrder;
	}
	
	public ExecutionOrder getExecutionOrder() {
		return executionOrder;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public void setAnalyze(boolean analyze) {
		this.analyze = analyze;
	}
	
	public boolean isAnalyze() {
		return analyze;
	}
}
