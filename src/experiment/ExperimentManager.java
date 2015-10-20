package experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import application.FileIoService;
import experiment.dataExtraction.SpreadsheetCreator;
import experiment.models.ExecutionOrder;
import experiment.models.ExperimentData;
import experiment.models.ExperimentManagerData;

public class ExperimentManager implements Runnable {

	private ExperimentManagerData data = new ExperimentManagerData();
	
	public ExperimentManager() {
	}
	
	public ExperimentManagerData getData() {
		return data;
	}

	@Override
	public void run(){
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
	
	public void initialSetup(){
		data.getReactisData().loadData(data.getRsiFile());
		//reactisData.
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
	
}
