package experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ExperimentManager.ExperimentManagerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import experiment.dataExtraction.SpreadsheetCreator;
import experiment.models.ExecutionOrder;
import experiment.models.ExperimentData;
import experiment.models.ExperimentManagerData;
import experiment.models.reactis.ReactisPort;

public class ExperimentManager implements Runnable {
	
	private ExperimentManagerController controller;

	private ExperimentManagerData data = new ExperimentManagerData();
	
	private ObservableList<ReactisPort> inports;

	private ObservableList<ReactisPort> outports;
	
	private ObservableList<ExperimentManagerData> experiments = FXCollections.observableArrayList();

	private ListView<ExperimentManagerData> experimentList;
	
	public void setData(ExperimentManagerData data) {
		this.data = data;
	}
	
	public void setController(ExperimentManagerController controller) {
		this.controller = controller;
	}
	
	public ObservableList<ReactisPort> getInports() {
		return inports;
	}
	
	public ObservableList<ReactisPort> getOutports() {
		return outports;
	}
	
	public ExperimentManager() {
		inports = FXCollections.observableList(data.getReactisData().getInports());
		outports = FXCollections.observableList(data.getReactisData().getOutports());
	}
	
	public ExperimentManagerData getData() {
		return data;
	}
	
	public ObservableList<ExperimentManagerData> getExperiments() {
		return experiments;
	}

	@Override
	public void run(){
		List<ExperimentManagerData> runExperiments = new ArrayList<>();
		runExperiments.addAll(experimentList.getSelectionModel().getSelectedItems());
		
		for (ExperimentManagerData experimentManagerData : runExperiments) {
			experimentList.getSelectionModel().clearSelection();
			setData(experimentManagerData);
			experimentList.getSelectionModel().select(experimentManagerData);;
			controller.getExperimentTable().setItems(data.getExperiments());
			
			List<Thread> threads = new ArrayList<>();
			setup();
			
			if(data.getExecutionOrder() == ExecutionOrder.PARALLEL){
				int i =0;
				Thread myExperimentRunner = null;
				for (Experiment experiment : data.getExperiments()) {
					myExperimentRunner = new Thread(experiment,"E" + i++);
					threads.add(myExperimentRunner);
					myExperimentRunner.start();
					try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				for (Thread thread : threads) {
					try {
						thread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else if(data.getExecutionOrder() == ExecutionOrder.SEQUENTIAL){
				Thread myExperimentRunner = null;
				int i =0;
				for (Experiment experiment : data.getExperiments()) {
					myExperimentRunner = new Thread(experiment,"E" + i++);
					threads.add(myExperimentRunner);
					myExperimentRunner.start();
					try {
						myExperimentRunner.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			SpreadsheetCreator spreadsheetCreator = new SpreadsheetCreator();
			
			spreadsheetCreator.saveData(data.getFolderPath() + File.separator + ExperimentData.DATA, data);
		}
		
	}
	
	public void initialSetup(){
		data.getReactisData().loadData(data.getRsiFile());
		
		
		inports = FXCollections.observableList(data.getReactisData().getInports());
		outports = FXCollections.observableList(data.getReactisData().getOutports());
/*		inports.clear();
		outports.clear();
		inports.addAll(data.getReactisData().getInports());
		outports.addAll(data.getReactisData().getOutports());*/
	}
	
	public void loadInOutPortsFromGUI(){
		data.getReactisData().getInports().clear();
		data.getReactisData().getOutports().clear();
		data.getReactisData().getInports().addAll(inports);
		data.getReactisData().getOutports().addAll(outports);
		
	}

	private void setup() {
		data.getExperiments().clear();
		setupFolders();
		setupExperiments();
	}

	private void setupExperiments() {
		System.out.println("Setup Experiments");
		for (int i = 0; i < data.getNumberOfExperiments().get(); i++) {
			Experiment experiment = new Experiment(data, i);
			data.getExperiments().add(experiment);
		}
	}

	private void setupFolders() {
		System.out.println("Create Main Data Folder");
		File folder = new File(data.getFolderPath());
		FileIoService fileIo = new FileIoService();
		if(folder.exists()){
			fileIo.deleteFolder(folder);
		}
		FileIoService.createFolder(folder);
	}

	public void runExperiment(ExperimentManagerData data) {
		// TODO Auto-generated method stub
		
	}

	public void switchPortToInput(ReactisPort port) {
		//data.getReactisData().switchPortToInput(port);
		outports.remove(port);
		inports.add(port);
		
	}
	
	public void switchPortToOutput(ReactisPort port) {
		//data.getReactisData().switchPortToOutput(port);
		outports.add(port);
		inports.remove(port);
		
	}

	public void saveExperiment(File file) {
		getData().setName(file.getName());
		FileIoService.toXML(getData(), file);
		
		ExperimentManagerData newData = (ExperimentManagerData) FileIoService.fromXML(file);
		
		experiments.add(newData);
	}

	public void runExperiments(ListView<ExperimentManagerData> experiments) {
		this.experimentList = experiments;
		Thread myExperimentRunner =  new Thread(this, "manager thread");
		myExperimentRunner.start();
	}
	
}
