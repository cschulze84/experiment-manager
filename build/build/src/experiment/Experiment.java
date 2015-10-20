package experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.RuleG;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import experiment.dataExtraction.CoverageParser;
import experiment.dataExtraction.TestCaseParser;
import experiment.dataMining.AssociationRuleMining;
import experiment.models.ExperimentData;
import experiment.models.Implication;
import experiment.models.ReactisData;
import experiment.models.ReactisPort;
import experiment.models.TestSuite;
import experiment.reactis.utils.EditRSI;
import JVM.ReactisJVM;
import application.FileIoService;

public class Experiment implements Runnable{
	FileIoService fileIo = new FileIoService();
	private static final String PATH_NAME = "experiment";
	private static final String TEMP_NAME = "temp";
	private static final String ITERATION = "iteration";
	private static final String TEST_SUITE = "testSuite.rst";
	private static final String CSV = "testSuite.csv";
	private static final String COVERAGE = "coverage.txt";
	private static final String ASSOCIATION_RULE_INPUT = "dataMiningInput.txt";
	private static final String ASSOCIATION_RULE_OUTPUT = "dataMiningOutput.txt";
	private static final String RSI = "info.rsi";
	private static final String ITERATION_RSI = "iteration.rsi";
	private static final String INVARIANTS = "invariants.txt";
	private static final String PRUNED_INVARIANTS = "invariantsPruned.txt";
	
	private String baseFolderPath;
	private String experimentFolderPath;
	private String tempFolderPath;
	private String modelFile;
	private String baseRsiFile;
	private String rsiFile;
	private String iterationRsiFile;
	
	private IntegerProperty  iteration = new SimpleIntegerProperty(1);
	private IntegerProperty  id = new SimpleIntegerProperty(0);
	private IntegerProperty  invariants = new SimpleIntegerProperty(0);
	private IntegerProperty  invalidatedInvariants = new SimpleIntegerProperty(0);
	private IntegerProperty  oldInvariants = new SimpleIntegerProperty(0);
	private int oldInvariantsTemp = 0;
	
	private IntegerProperty cutoff = new SimpleIntegerProperty(3);;
	
	private List<ExperimentData> iterationData = new ArrayList<>();
	private ReactisData reactisData;
	private TestSuite testSuite = new TestSuite();
	private StringProperty time = new SimpleStringProperty("");
	private StringProperty overallTime = new SimpleStringProperty("");
	private long timeAll = 0;
	
	//Initialization
	
	public Experiment(String baseFolderPath, String modelFile, String rsiFile, ReactisData reactisData, int id){
		this.baseFolderPath = baseFolderPath;
		this.modelFile = modelFile;
		this.baseRsiFile = rsiFile;
		this.reactisData = reactisData;
		this.id.set(id);
		
		setExperimentFolder();
		setTempFolder();
		setRsiFiles();
	}
	
	public int getId() {
		return id.get();
	}
	
	public void setId(int id) {
		this.id.set(id);
	}
	
	public IntegerProperty idProperty(){
		return this.id;
	}
	
	public int getIteration() {
		return iteration.get();
	}
	
	public void setIteration(int iteration) {
		this.iteration.set(iteration);
	}
	
	public IntegerProperty iterationProperty(){
		return this.iteration;
	}
	
	public int getInvariants() {
		return invariants.get();
	}
	
	public void setInvariants(int invariants) {
		this.invariants.set(invariants);
	}
	
	public IntegerProperty invariantsProperty(){
		return this.invariants;
	}
	
	public IntegerProperty invalidatedInvariantsProperty(){
		return this.invalidatedInvariants;
	}
	
	public IntegerProperty oldInvariantsProperty() {
		return this.oldInvariants;
	}

	public IntegerProperty iterationEndProperty() {
		return this.cutoff;
	}

	public StringProperty timeProperty() {
		return this.time;
	}
	
	public StringProperty overallTimeProperty() {
		return this.overallTime;
	}


	
	private void setExperimentFolder(){
		File folder = new File(baseFolderPath);
		
		String folderPath = folder.getAbsolutePath() + File.separator + PATH_NAME + id.get() + File.separator;
		
		experimentFolderPath = folderPath;
		
	}
	
	private void setTempFolder(){
		tempFolderPath = experimentFolderPath + TEMP_NAME + File.separator;
	}
	

	private void setRsiFiles() {
		rsiFile = tempFolderPath + RSI;
		iterationRsiFile = tempFolderPath + ITERATION_RSI;
	}
	
	//Getters
	private String getAssociationRuleMiningoutputPath() {
		return getIterationFolderPath(iteration.get()) + ASSOCIATION_RULE_OUTPUT;
	}

	private String getAssociationRuleMiningInputPath() {
		return getIterationFolderPath(iteration.get()) + ASSOCIATION_RULE_INPUT;
	}
	
	private String getIterationFolderPath(int iteration) {
		String iterationFolderPath = experimentFolderPath + ITERATION + iteration + File.separator;
		return iterationFolderPath;
	}
	
	//Run Experiments

		@Override
		public void run() {
			setup();	
			System.out.println("Start Experiment: " + id);
			do{
				final long startTime = System.currentTimeMillis();
				runIteration();
				final long endTime = System.currentTimeMillis();
				
				long timeInSeconds = (endTime-startTime)/1000;
				timeAll += timeInSeconds;
				
				time.set(timeInSeconds+ " Seconds");
				overallTime.set(timeAll+ " Seconds");

				iteration.set(iteration.get()+1);;
			}while(checkStatus());
			System.out.println("Finished Experiment: " + id);
		}
		

		private boolean checkStatus(){
			oldInvariants.set(oldInvariantsTemp);
			if(invalidatedInvariants.get() == 0 && invariants.get() == oldInvariants.get()){
				cutoff.set(cutoff.get() - 1);
				if(cutoff.get()==0){
					return false;
				}
				else{
					oldInvariantsTemp = invariants.get();
					return true;
				}
				
			}
			oldInvariantsTemp = invariants.get();
			cutoff.set(3);
			return true;
		}
		
	
	//Experiment Setup
	private void setup() {
		System.out.println("Setup Folders for Experiment: " + id.get());
		setupFolders();
		System.out.println("Copy Files for Experiment: " + id.get());
		copyFiles();
		setupTestSuite();
		setupRSIFile();
	}
				
	private void setupRSIFile() {
		EditRSI readRsi = new EditRSI();
		readRsi.cleanAssertions(baseRsiFile, rsiFile);
	}

	private void setupTestSuite() {
		for (ReactisPort port : reactisData.getInports()) {
			testSuite.addInput(port.getName());
		}
		
		for (ReactisPort port : reactisData.getOutports()) {
			testSuite.addOutput(port.getName());
		}
	}

	private void copyFiles() {
		File temp = new File(tempFolderPath);
		File model = new File(modelFile);
		File rsi = new File(baseRsiFile);
		
		fileIo.copyFileToFolder(model, temp);
		fileIo.copyFileToFolder(rsi, temp);
		
		modelFile = tempFolderPath + model.getName();
		baseRsiFile = tempFolderPath + rsi.getName();
	}

	private void setupFolders() {
		File folder = new File(experimentFolderPath);
		if(folder.exists()){
			fileIo.deleteFolder(folder);
		}
		FileIoService.createFolder(folder);
	}
	
	
	//Run Iteration
	
	private void runIteration(){
		System.out.println("Setting Up Itertation " + iteration.get() + " Of Experiment " + id.get());
		ExperimentData data = setupIteration();
		System.out.println("Start Reactis Iteration " + iteration.get() + " Of Experiment " + id.get());
		runReactis(data);

		System.out.println("Start Data Mining Iteration " + iteration.get() + " Of Experiment " + id.get());
		runDataMining(data);
		System.out.println("Finished Itertation " + iteration.get() + " Of Experiment " + id.get());
	}
	

	private void runDataMining(ExperimentData data) {
		AssociationRuleMining miner = new AssociationRuleMining();
		miner.printForAssociationRuleMining(testSuite, getAssociationRuleMiningInputPath());
		
		PriorityQueue<RuleG> rules = miner.mineRules(getAssociationRuleMiningInputPath(), getAssociationRuleMiningoutputPath());
		
		List<RuleG> ruleListPruned = miner.pruneRuleGComplete(rules);
		
		List<Implication> implications = miner.parseMiningOutputs(ruleListPruned);
		
		int previousNumberOfInvariants = -1;
		
		//List<Implication> prunedImplications = miner.prune(implications);
		
		/*do{
			prunedImplications = miner.prune(implications);
		}while(previousNumberOfInvariants > prunedImplications.size());*/

		saveDataMiningData(implications, null);
		
		CoverageParser parser = new CoverageParser();
		parser.parseCoverage(getIterationFolderPath(iteration.get()) + COVERAGE, data.getCovarage());
		
		invalidatedInvariants.set(data.getCovarage().getAssertion().getCovered());
	}

	private void saveDataMiningData(List<Implication> implications,
			List<Implication> prunedImplications) {
		saveImplications(implications, getIterationFolderPath(iteration.get()) + INVARIANTS);
		//saveImplications(prunedImplications, getIterationFolderPath(iteration.get()) + PRUNED_INVARIANTS);
		
		testSuite.setImplications(implications);
		
		//testSuite.setImplicationsPruned(prunedImplications);
		
		invariants.set(implications.size());
	}

	private void saveImplications(List<Implication> implications, String filePath) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter(filePath, false));
			for (Implication implication : implications) {
				writer.write(implication.toString() + System.lineSeparator());
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	private void parseReactisData() {
		TestCaseParser parser = new TestCaseParser();
		parser.parseCSV(getIterationFolderPath(iteration.get()) + CSV, testSuite);
		
	}

	private void runReactis(ExperimentData data) {
		ReactisJVM testGenerator = new ReactisJVM();
		String testSuite = getIterationFolderPath(iteration.get()) + TEST_SUITE;
		String csv = getIterationFolderPath(iteration.get()) + CSV;
		String cov = getIterationFolderPath(iteration.get()) + COVERAGE;
		
		int result = testGenerator.generateTestCases(modelFile, iterationRsiFile, testSuite, csv, cov);
		
		parseReactisData();
	}
	
	//Setup Iteration

	private ExperimentData setupIteration() {
		String iterationFolder = setupIterationFolders();
		setupIterationFiles();
		ExperimentData data = new ExperimentData(iteration.get(), iterationFolder);
		iterationData.add(data);
		return data;
	}

	private void setupIterationFiles() {
		EditRSI edit = new EditRSI();
		
		edit.addAssertions(rsiFile, iterationRsiFile, testSuite, reactisData);
	}

	private String setupIterationFolders() {
		String iterationFolder = getIterationFolderPath(iteration.get());
		
		File folder = new File(iterationFolder);
/*		if(folder.exists()){
			fileIo.deleteFolder(folder);
		}*/
		FileIoService.createFolder(folder);
		
		return iterationFolder;
	}

	

}
