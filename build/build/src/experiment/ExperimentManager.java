package experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import experiment.models.ReactisData;
import application.FileIoService;

public class ExperimentManager{
	private String folderPath;
	private String modelFile;
	private String rsiFile;
	private IntegerProperty  numberOfExperiments = new SimpleIntegerProperty(0);
	private ObservableList<Experiment> experiments = FXCollections.observableArrayList();
	
	private ReactisData reactisData = new ReactisData();
	
	public ExperimentManager(int numberOfExperiments, String folderPath, String modelFile, String rsiFile) {
		this.numberOfExperiments.set(numberOfExperiments);
		this.folderPath = folderPath;
		this.modelFile = modelFile;
		this.rsiFile = rsiFile;
	}
	
	public ExperimentManager() {
		
	}
	
	public ObservableList<Experiment> getExperiments() {
		return experiments;
	}
	
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	
	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}
	
	public void setRsiFile(String rsiFile) {
		this.rsiFile = rsiFile;
	}
	
	public void setNumberOfExperiments(int numberOfExperiments) {
		this.numberOfExperiments.set(numberOfExperiments);
	}

	public void run(){
		setup();
		
		int i =0;
		for (Experiment experiment : experiments) {
			Thread myExperimentRunner = new Thread(experiment,"E" + i++);
			myExperimentRunner.start();
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ReactisData getReactisData() {
		return reactisData;
	}
	
	public void initialSetup(){
		reactisData.loadData(rsiFile);
		//reactisData.
	}

	private void setup() {
		
		setupFolders();
		setupExperiments();
	}

	private void setupExperiments() {
		System.out.println("Setup Experiments");
		for (int i = 0; i < numberOfExperiments.get(); i++) {
			Experiment experiment = new Experiment(folderPath, modelFile, rsiFile, reactisData, i);
			experiments.add(experiment);
		}
	}

	private void setupFolders() {
		System.out.println("Create Main Data Folder");
		File folder = new File(folderPath);
		FileIoService fileIo = new FileIoService();
		if(folder.exists()){
			fileIo.deleteFolder(folder);
		}
		FileIoService.createFolder(folder);
	}
	
	public static void main(String[] args){
		ExperimentManager manager = new ExperimentManager(3, ".\\tester\\", "C:\\Programs\\ReactisV2015\\examples\\R2009a\\Emergency_Blinking.mdl", "C:\\Programs\\ReactisV2015\\examples\\R2009a\\Emergency_Blinking.rsi");
		manager.run();
	}

	public void setStoppingCriterion(String selectedItem) {
		// TODO Auto-generated method stub
		
	}

	public void setStopingNumber(int parseInt) {
		// TODO Auto-generated method stub
		
	}
}
