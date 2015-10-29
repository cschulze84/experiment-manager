package experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;

import JVM.ReactisJVM;
import application.FileIoService;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.RuleG;
import ca.pfv.spmf.datastructures.redblacktree.RedBlackTree;
import experiment.dataExtraction.CoverageParser;
import experiment.dataExtraction.TestCaseParser;
import experiment.dataMining.AssociationRuleMining;
import experiment.models.ExperimentData;
import experiment.models.ExperimentManagerData;
import experiment.models.IterationData;
import experiment.models.StoppingCriterion;
import experiment.models.invariants.Implication;
import experiment.models.reactis.ReactisPort;
import experiment.reactis.utils.EditRSI;

public class Experiment implements Runnable {
	FileIoService fileIo = new FileIoService();
	private ExperimentData data = new ExperimentData();
	private ExperimentManagerData experimentManagerData;

	// Initialization

	public Experiment(ExperimentManagerData experimentData, int id) {
		this.experimentManagerData = experimentData;
		this.data.id.set(id);
		setStopping();
		setExperimentFolder();
		setTempFolder();
		setRsiFiles();
	}
	private void setStopping() {
		data.setStoppingCriterion(experimentManagerData.getStoppingCriterion());
		data.cutoff.set(experimentManagerData.getIterationEnd());
		data.iterationEnd.set(experimentManagerData.getIterationEnd());
	}

	public ExperimentData getData() {
		return data;
	}

	private void setExperimentFolder() {
		data.setExperimentFolderPath(experimentManagerData.getFolderPath() + File.separator + data.PATH_NAME + data.id.get() + File.separator);

	}

	private void setTempFolder() {
		data.setTempFolderPath(data.getExperimentFolderPath() + data.TEMP_NAME + File.separator);
	}

	private void setRsiFiles() {
		data.setRsiFile(data.getTempFolderPath() + data.RSI);
		data.setIterationRsiFile(data.getTempFolderPath() + data.ITERATION_RSI);
	}

	// Getters
	private String getAssociationRuleMiningoutputPath() {
		return getIterationFolderPath(data.iteration.get())
				+ data.ASSOCIATION_RULE_OUTPUT;
	}

	private String getAssociationRuleMiningInputPath() {
		return getIterationFolderPath(data.iteration.get()) + data.ASSOCIATION_RULE_INPUT;
	}

	private String getIterationFolderPath(int iteration) {
		String iterationFolderPath = data.getExperimentFolderPath() + data.ITERATION
				+ iteration + File.separator;
		return iterationFolderPath;
	}

	// Run Experiments

	@Override
	public void run() {
		setup();
		System.out.println("Start Experiment: " + data.getId());
		do {
			
			long timeInSeconds = runIteration();
			
			data.setTimeAll(data.getTimeAll() + timeInSeconds);
			data.time.set(timeInSeconds);
			data.overallTime.set(data.getTimeAll());
			
			data.iteration.set(data.iteration.get() + 1);
		} while (checkStatus());
		System.out.println("Finished Experiment: " + data.id.get());

	}

	private boolean checkStatus() {
		if (data.getStoppingCriterion() == StoppingCriterion.NO_INVARIANTS) {
			data.oldInvariants.set(data.getOldInvariantsTemp());
			if (data.invalidatedInvariants.get() == 0
					&& data.invariants.get() == data.oldInvariants.get()) {
				data.iterationEnd.set(data.iterationEnd.get() - 1);
				if (data.iterationEnd.get() == 0) {
					return false;
				} else {
					data.setOldInvariantsTemp(data.invariants.get());
					return true;
				}

			}
			data.setOldInvariantsTemp(data.invariants.get());
			data.iterationEnd.set(data.cutoff.get());
			return true;
		} else if (data.getStoppingCriterion() == StoppingCriterion.NUMBER_OF_ITERATIONS) {
			data.cutoff.set(data.cutoff.get() - 1);
			if (data.cutoff.get() == 0) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	// Experiment Setup
	private void setup() {
		System.out.println("Setup Folders for Experiment: " + data.id.get());
		setupFolders();
		System.out.println("Copy Files for Experiment: " + data.id.get());
		copyFiles();
		setupTestSuite();
		setupRSIFile();
	}

	private void setupRSIFile() {
		EditRSI readRsi = new EditRSI();
		readRsi.cleanAssertions(data.getBaseRsiFile(), data.getRsiFile());
	}

	private void setupTestSuite() {
		for (ReactisPort port : experimentManagerData.getReactisData().getInports()) {
			data.getTestSuite().addInput(port.getName());
		}

		for (ReactisPort port : experimentManagerData.getReactisData().getOutports()) {
			data.getTestSuite().addOutput(port.getName());
		}
	}

	private void copyFiles() {
		File temp = new File(data.getTempFolderPath());
		File model = new File(experimentManagerData.getModelFile());
		
		
		File rsi = new File(experimentManagerData.getRsiFile());

		fileIo.copyFileToFolder(model, temp);
		fileIo.copyFileToFolder(rsi, temp);

		data.setModelFile(data.getTempFolderPath() + model.getName());
		data.setBaseRsiFile(data.getTempFolderPath() + rsi.getName());
		
		if(!experimentManagerData.getModelSupport().isEmpty()){
			File support = new File(experimentManagerData.getModelSupport());
			fileIo.copyFileToFolder(support, temp);
		}
	}

	private void setupFolders() {
		File folder = new File(data.getExperimentFolderPath());
		if (folder.exists()) {
			fileIo.deleteFolder(folder);
		}
		FileIoService.createFolder(folder);
	}

	// Run Iteration

	private long runIteration() {
		final long startTime = System.currentTimeMillis();
		System.out.println("Setting Up Itertation " + data.iteration.get()
				+ " Of Experiment " + data.id.get());
		IterationData iterationData = setupIteration();
		System.out.println("Start Reactis Iteration " + data.iteration.get()
				+ " Of Experiment " + data.id.get());
		runReactis(iterationData);

		System.out.println("Start Data Mining Iteration " + data.iteration.get()
				+ " Of Experiment " + data.id.get());
		runDataMining(iterationData);
		System.out.println("Finished Itertation " + data.iteration.get()
				+ " Of Experiment " + data.id.get());
		data.setLastIterationData(iterationData);
		final long endTime = System.currentTimeMillis();

		long timeInSeconds = (endTime - startTime) / 1000;
		
		iterationData.setExecutionTimeTestGeneration(timeInSeconds);
		
		return timeInSeconds;
	}
	
/*	private void runDataMining(IterationData iterationData) {
		AssociationRuleMining miner = new AssociationRuleMining();
		miner.printForAssociationRuleMining(data.getTestSuite(),
				getAssociationRuleMiningInputPath());
		
		RedBlackTree<RuleG> rules = miner.mineRulesPruned(getAssociationRuleMiningInputPath(),
				getAssociationRuleMiningoutputPath(), experimentManagerData.getMaxInvariants());

		List<RuleG> prunedInvariants = miner.pruneRuleGComplete(rules);
		List<Implication> implications = miner.parseMiningOutputs(prunedInvariants);
		saveDataMiningData(implications, implications);
		iterationData.setNumberOfInvariants(implications.size());
		iterationData.setNumberOfPrunedInvariants(implications.size());
		
		CoverageParser parser = new CoverageParser();
		parser.parseCoverage(
				getIterationFolderPath(data.iteration.get()) + data.COVERAGE,
				iterationData.getCovarage());

		data.invalidatedInvariants.set(iterationData.getCovarage().getAssertion()
				.getCovered());
		
		if(data.getLastIterationData() != null){
			data.getLastIterationData().setNumbeOfInvalidatedInvariants(data.invalidatedInvariants.get());
		}

		
		iterationData.setNumbeOfInvalidatedInvariants(data.invalidatedInvariants.get());
	}*/

	private void runDataMining(IterationData iterationData) {
		AssociationRuleMining miner = new AssociationRuleMining();
		miner.printForAssociationRuleMining(data.getTestSuite(),
				getAssociationRuleMiningInputPath());
		
		PriorityQueue<RuleG> rules = miner.mineRules(
				getAssociationRuleMiningInputPath(),
				getAssociationRuleMiningoutputPath(), experimentManagerData.getMaxInvariants());

		if(experimentManagerData.isPrune()){
			List<RuleG> prunedInvariants = miner.pruneRuleGComplete(rules);
			List<Implication> implications = miner.parseMiningOutputs(prunedInvariants);
			saveDataMiningData(implications, implications);
			iterationData.setNumberOfInvariants(implications.size());
			iterationData.setNumberOfPrunedInvariants(implications.size());
		}
		else{
			List<Implication> implications = miner.parseMiningOutputs(rules);
			saveDataMiningData(implications, null);
			iterationData.setNumberOfInvariants(implications.size());
			iterationData.setNumberOfPrunedInvariants(0);
		}	
		CoverageParser parser = new CoverageParser();
		parser.parseCoverage(
				getIterationFolderPath(data.iteration.get()) + data.COVERAGE,
				iterationData.getCovarage());

		data.invalidatedInvariants.set(iterationData.getCovarage().getAssertion()
				.getCovered());
		
		if(data.getLastIterationData() != null){
			data.getLastIterationData().setNumbeOfInvalidatedInvariants(data.invalidatedInvariants.get());
		}

		
		iterationData.setNumbeOfInvalidatedInvariants(data.invalidatedInvariants.get());
	}

	private void saveDataMiningData(List<Implication> implications,
			List<Implication> prunedImplications) {
		if (experimentManagerData.isPrune()) {
			data.invariants.set(prunedImplications.size());
			saveImplications(prunedImplications,
					getIterationFolderPath(data.iteration.get()) + data.PRUNED_INVARIANTS);
			data.getTestSuite().setImplicationsPruned(prunedImplications);
		} else {
			data.invariants.set(implications.size());
			saveImplications(implications, getIterationFolderPath(data.iteration.get())
					+ data.INVARIANTS);
			data.getTestSuite().setImplications(implications);

		}

	}

	private void saveImplications(List<Implication> implications,
			String filePath) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,
					false));
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
		parser.parseCSV(getIterationFolderPath(data.iteration.get()) + data.CSV,
				data.getTestSuite());

	}

	private void runReactis(IterationData iterationData) {
		ReactisJVM testGenerator = new ReactisJVM();
		String testSuite = getIterationFolderPath(data.iteration.get()) + data.TEST_SUITE;
		String csv = getIterationFolderPath(data.iteration.get()) + data.CSV;
		String cov = getIterationFolderPath(data.iteration.get()) + data.COVERAGE;
		
		String settings = "";
		
		if(experimentManagerData.getNumberOfTargetedSteps() == 0){
			settings = "-r " + experimentManagerData.getNumberOfTests() + ";" + experimentManagerData.getNumberOfTestSteps() + " -t 0 ";
		}
		else{
			settings = "-r " + experimentManagerData.getNumberOfTests() + ";" + experimentManagerData.getNumberOfTestSteps() + " -t " + experimentManagerData.getNumberOfTargetedSteps() + " -g Subsystem;Branch;State;ConditionAction;TransitionAction;Condition;Decision;MCDC;MCC;Boundary;UserTarget;Assertion;CStatement";
		}

		int result = testGenerator.generateTestCases(data.getModelFile(),
				data.getIterationRsiFile(), testSuite, csv, cov, settings);
		
		System.out.println("Result: " + result);

		parseReactisData();
	}

	// Setup Iteration

	private IterationData setupIteration() {
		String iterationFolder = setupIterationFolders();
		setupIterationFiles();
		IterationData iterationData = new IterationData(data.iteration.get(),
				iterationFolder);
		data.getIterationData().add(iterationData);
		return iterationData;
	}

	private void setupIterationFiles() {
		EditRSI edit = new EditRSI();

		edit.addAssertions(data.getRsiFile(), data.getIterationRsiFile(), data.getTestSuite(), experimentManagerData.getReactisData(),
				experimentManagerData.isPrune());
	}

	private String setupIterationFolders() {
		String iterationFolder = getIterationFolderPath(data.iteration.get());

		File folder = new File(iterationFolder);
		/*
		 * if(folder.exists()){ fileIo.deleteFolder(folder); }
		 */
		FileIoService.createFolder(folder);

		return iterationFolder;
	}

}
