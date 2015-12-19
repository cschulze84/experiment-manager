package ExperimentManager;

import java.io.File;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import experiment.Experiment;
import experiment.ExperimentManager;
import experiment.FileIoService;
import experiment.models.ExecutionOrder;
import experiment.models.ExperimentData;
import experiment.models.ExperimentManagerData;
import experiment.models.StoppingCriterion;
import experiment.models.reactis.ReactisPort;

public class ExperimentManagerController {
	// Toolbar
	@FXML
	private Button saveButton;
	
	@FXML
	private Button loadButton;

	@FXML
	private Button setupButton;

	@FXML
	private Button startButton;
	
	//Tab Pane
	@FXML
	private TabPane tabPane;
	
	@FXML
	private Tab tabExperiment;

	// Path Tab
	@FXML
	private Button experimentPathButton;

	@FXML
	private Button modelFileButton;
	
	@FXML
	private Button modelSupportButton;

	@FXML
	private Button rsiFileButton;
	
	@FXML
	private Button saveFolderButton;

	@FXML
	private TextField experimentPathText;

	@FXML
	private TextField modelFileText;
	
	@FXML
	private TextField modelSupportText;

	@FXML
	private TextField rsiFileText;
	
	@FXML
	private TextField saveFolderText;


	// Settings Tab
	@FXML
	private Slider numberOfExperiments;
	
	@FXML
	private ComboBox<ExecutionOrder> executionOrder;
	
	@FXML
	private Slider numberOfTests;
	
	@FXML
	private Slider numberOfTestSteps;
	
	@FXML
	private Slider numberOfTargetedSteps;

	@FXML
	private ComboBox<StoppingCriterion> stoppingCriteria;

	@FXML
	private Slider iterationEnd;
	
	@FXML
	private CheckBox usePrunedInvariants;
	
	@FXML
	private Slider maxInvariants;
	
	@FXML
	private CheckBox analyzeModel;

	// Model Tab
	@FXML
	private Button switchToOutputButton;

	@FXML
	private Button switchToInputButton;

	@FXML
	private ListView<ReactisPort> inportList;

	@FXML
	private ListView<ReactisPort> outportList;

	// Experiment
	@FXML
	private TableView<Experiment> experimentTable;

	@FXML
	private TableColumn<Experiment, Number> numberColumn;

	@FXML
	private TableColumn<Experiment, Number> IterationColumn;

	@FXML
	private TableColumn<Experiment, Number> invariantColumn;

	@FXML
	private TableColumn<Experiment, Number> invalidatedColumn;

	@FXML
	private TableColumn<Experiment, Number> oldInvariantColumn;

	@FXML
	private TableColumn<Experiment, Number> iterationEndColumn;

	@FXML
	private TableColumn<Experiment, Number> timeColumn;

	@FXML
	private TableColumn<Experiment, Number> overallTimeColumn;
	
	//LEFT
	@FXML
	private ListView<ExperimentManagerData> experiments;

	// FXML
	private Stage primaryStage;

	// Model
	private ExperimentManager manager;


	public TableView<Experiment> getExperimentTable() {
		return experimentTable;
	}
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		manager = new ExperimentManager();
		
		manager.setController(this);

		experiments.setItems(manager.getExperiments());
		
		experiments.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		experimentPathText
				.setText("C:\\Users\\Christoph\\OneDrive\\newAnalysis\\data\\pl_coverage");
		saveFolderText.setText("C:\\Users\\Christoph\\OneDrive\\newAnalysis\\data\\experimentList\\pl_coverage");
		
		//modelFileText.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Emergency_Blinking.mdl");
		//rsiFileText.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Emergency_Blinking.rsi");
		//modelFileText.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Rear_Fog_Light.mdl");
		//rsiFileText.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Rear_Fog_Light.rsi");
		//modelFileText.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Daytime_Driving_Light.slx");
		//rsiFileText.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Daytime_Driving_Light.rsi");
		modelFileText.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Parking_Light.mdl");
		rsiFileText.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Parking_Light.rsi");
		modelSupportText.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Constants.m");
		
		setupExperimentTable();

		ObservableList<StoppingCriterion> options = FXCollections.observableArrayList(
				StoppingCriterion.values());
		stoppingCriteria.setItems(options);
		stoppingCriteria.getSelectionModel().select(0);
		
		ObservableList<ExecutionOrder> optionsOrder = FXCollections.observableArrayList(
				ExecutionOrder.values());
		executionOrder.setItems(optionsOrder);
		executionOrder.getSelectionModel().select(1);
	}

	

	private void setupExperimentTable() {
		experimentTable.setItems(manager.getData().getExperiments());

		numberColumn
				.setCellValueFactory(new PropertyValueFactory<Experiment, Number>(
						"id"));
		IterationColumn
				.setCellValueFactory(new PropertyValueFactory<Experiment, Number>(
						"iteration"));
		invariantColumn
				.setCellValueFactory(new PropertyValueFactory<Experiment, Number>(
						"invariants"));

		numberColumn.setCellValueFactory(cellData -> cellData.getValue().getData().id);
		IterationColumn.setCellValueFactory(cellData -> cellData.getValue().getData().iteration);
		invariantColumn.setCellValueFactory(cellData -> cellData.getValue().getData().invariants);
		invalidatedColumn.setCellValueFactory(cellData -> cellData.getValue().getData().invalidatedInvariants);
		oldInvariantColumn.setCellValueFactory(cellData -> cellData.getValue().getData().oldInvariants);
		iterationEndColumn.setCellValueFactory(cellData -> cellData.getValue().getData().iterationEnd);
		timeColumn.setCellValueFactory(cellData -> cellData.getValue().getData().time);
		overallTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getData().overallTime);
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param primaryStage
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	@FXML
	private void handlePathButton() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Choose Experiment Folder");

		File selectedDirectory = chooser.showDialog(primaryStage);
		if (selectedDirectory != null) {
			experimentPathText.setText(selectedDirectory.getAbsolutePath());
			manager.getData().setFolderPath(selectedDirectory.getAbsolutePath());
		}
	}

	@FXML
	private void handleModelFileButton() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Model File");
		/*
		 * fileChooser.getExtensionFilters().addAll( new
		 * ExtensionFilter("Text Files", "*.txt"));
		 */

		File selectedFile = fileChooser.showOpenDialog(primaryStage);
		if (selectedFile != null) {
			modelFileText.setText(selectedFile.getAbsolutePath());
			manager.getData().setModelFile(selectedFile.getAbsolutePath());
		}
	}

	@FXML
	private void handleModelSupportButton() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Model Support File");
		/*
		 * fileChooser.getExtensionFilters().addAll( new
		 * ExtensionFilter("Text Files", "*.txt"));
		 */

		File selectedFile = fileChooser.showOpenDialog(primaryStage);
		if (selectedFile != null) {
			modelSupportText.setText(selectedFile.getAbsolutePath());
			manager.getData().setModelSupport(selectedFile.getAbsolutePath());
		}
	}
	
	@FXML
	private void handleRSIFileButton() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Model File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Reactis Info Files", "*.rsi"));

		File selectedFile = fileChooser.showOpenDialog(primaryStage);
		if (selectedFile != null) {
			rsiFileText.setText(selectedFile.getAbsolutePath());
			manager.getData().setRsiFile(selectedFile.getAbsolutePath());
		}
	}
	
	@FXML
	private void handleSaveFolderButton() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Choose Experiment Folder");

		File selectedDirectory = chooser.showDialog(primaryStage);
		if (selectedDirectory != null) {
			saveFolderText.setText(selectedDirectory.getAbsolutePath());
		}
	}
	
	@FXML
	private void handleSaveButton() {
		File file = new File(saveFolderText.getText() + ".experiment");
		
		manager.getData().setFolderPath(experimentPathText.getText());
		manager.getData().setModelFile(modelFileText.getText());
		manager.getData().setModelSupport(modelSupportText.getText());
		manager.getData().setRsiFile(rsiFileText.getText());
		
		Double d = numberOfExperiments.getValue();
		manager.getData().setNumberOfExperiments(d.intValue());
		manager.getData().setExecutionOrder(executionOrder.getSelectionModel().getSelectedItem());
		manager.getData().setStoppingCriterion(stoppingCriteria.getSelectionModel()
				.getSelectedItem());
		d = iterationEnd.getValue();
		manager.getData().setIterationEnd(d.intValue());
		
		manager.getData().setPrune(usePrunedInvariants.isSelected());
		
		d = maxInvariants.getValue();
		manager.getData().setMaxInvariants(d.intValue());
		
		d = numberOfTests.getValue();
		manager.getData().setNumberOfTests(d.intValue());
		
		d = numberOfTestSteps.getValue();
		manager.getData().setNumberOfTestSteps(d.intValue());
		
		d = numberOfTargetedSteps.getValue();
		manager.getData().setNumberOfTargetedSteps(d.intValue());	
		
		manager.getData().setAnalyze(analyzeModel.isSelected());
		
		manager.saveExperiment(file);
		
		
	}
	
	@FXML
	private void handleLoadButton() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Choose Experiment Folder");

		File selectedDirectory = chooser.showDialog(primaryStage);
		if (selectedDirectory != null) {
			String extensions[] = {"experiment"};
			List<File> files = FileIoService.getFilesInFolder(selectedDirectory, extensions);
			
			for (File file : files) {
				ExperimentManagerData data = (ExperimentManagerData) FileIoService.fromXML(file);
				manager.getExperiments().add(data);
			}
			
		}
	}

	@FXML
	private void handleSetupButton() {
		manager.getData().setFolderPath(experimentPathText.getText());
		manager.getData().setModelFile(modelFileText.getText());
		manager.getData().setModelSupport(modelSupportText.getText());
		manager.getData().setRsiFile(rsiFileText.getText());
		manager.initialSetup();
		

		inportList.setItems(manager.getInports());
		
		outportList.setItems(manager.getOutports());
		//outports = FXCollections.observableList(manager.getData().getReactisData().getOutports());

		

	}

	@FXML
	private void handleSwitchToOutputButton() {
		ReactisPort port = inportList.getSelectionModel().getSelectedItem();

		if (port == null) {
			return;
		}

		manager.switchPortToOutput(port);
	}

	@FXML
	private void handleSwitchToInputButton() {
		ReactisPort port = outportList.getSelectionModel().getSelectedItem();

		if (port == null) {
			return;
		}

		manager.switchPortToInput(port);
	}

	@FXML
	private void handleStartButton() {
		switchToExperimentView();
		manager.runExperiments(experiments);
		
		/*Double d = numberOfExperiments.getValue();
		manager.getData().setNumberOfExperiments(d.intValue());
		manager.getData().setExecutionOrder(executionOrder.getSelectionModel().getSelectedItem());
		manager.getData().setStoppingCriterion(stoppingCriteria.getSelectionModel()
				.getSelectedItem());
		d = iterationEnd.getValue();
		manager.getData().setIterationEnd(d.intValue());
		
		manager.getData().setPrune(usePrunedInvariants.isSelected());
		
		d = maxInvariants.getValue();
		manager.getData().setMaxInvariants(d.intValue());
		
		d = numberOfTests.getValue();
		manager.getData().setNumberOfTests(d.intValue());
		
		d = numberOfTestSteps.getValue();
		manager.getData().setNumberOfTestSteps(d.intValue());
		
		d = numberOfTargetedSteps.getValue();
		manager.getData().setNumberOfTargetedSteps(d.intValue());	*/
		
		//manager.loadInOutPortsFromGUI();
		
		
		
		

	}



	private void switchToExperimentView() {
		tabPane.getSelectionModel().select(tabExperiment);
	}
}
