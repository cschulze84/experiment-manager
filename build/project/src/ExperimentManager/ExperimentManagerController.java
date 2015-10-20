package ExperimentManager;

import java.io.File;

import experiment.Experiment;
import experiment.ExperimentManager;
import experiment.models.ReactisPort;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class ExperimentManagerController {
	// Toolbar
	@FXML
	private Button saveButton;

	@FXML
	private Button newButton;

	@FXML
	private Button openButton;

	@FXML
	private Button setupButton;

	@FXML
	private Button startButton;

	// Path Tab
	@FXML
	private Button experimentPathButton;

	@FXML
	private Button modelFileButton;

	@FXML
	private Button rsiFileButton;

	@FXML
	private TextField experimentPathText;

	@FXML
	private TextField modelFileText;

	@FXML
	private TextField rsiFileText;

	// Settings Tab
	@FXML
	private Slider numberOfExperiments;

	@FXML
	private ComboBox<String> stoppingCriteria;

	@FXML
	private Slider iterationEnd;

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
	private TableColumn<Experiment, String> timeColumn;

	@FXML
	private TableColumn<Experiment, String> overallTimeColumn;

	// FXML
	private Stage primaryStage;

	// Model
	private ExperimentManager manager;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		manager = new ExperimentManager();

		inportList.setItems(manager.getReactisData().getInports());
		outportList.setItems(manager.getReactisData().getOutports());

		experimentPathText
				.setText("C:\\Users\\Christoph\\OneDrive\\newAnalysis\\experiment1");
		modelFileText
				.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Emergency_Blinking.mdl");
		rsiFileText
				.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Emergency_Blinking.rsi");
		// modelFileText.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Rear_Fog_Light.mdl");
		// rsiFileText.setText("C:\\Programs\\ReactisV2015\\examples\\test\\Rear_Fog_Light.rsi");

		setupExperimentTable();

		ObservableList<String> options = FXCollections.observableArrayList(
				"No new invariants", "Number of iterations");
		stoppingCriteria.setItems(options);
		stoppingCriteria.getSelectionModel().select(0);
	}

	

	private void setupExperimentTable() {
		experimentTable.setItems(manager.getExperiments());

		numberColumn
				.setCellValueFactory(new PropertyValueFactory<Experiment, Number>(
						"id"));
		IterationColumn
				.setCellValueFactory(new PropertyValueFactory<Experiment, Number>(
						"iteration"));
		invariantColumn
				.setCellValueFactory(new PropertyValueFactory<Experiment, Number>(
						"invariants"));

		numberColumn.setCellValueFactory(cellData -> cellData.getValue()
				.idProperty());
		IterationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.iterationProperty());
		invariantColumn.setCellValueFactory(cellData -> cellData.getValue()
				.invariantsProperty());
		invalidatedColumn.setCellValueFactory(cellData -> cellData.getValue()
				.invalidatedInvariantsProperty());
		oldInvariantColumn.setCellValueFactory(cellData -> cellData.getValue()
				.oldInvariantsProperty());
		iterationEndColumn.setCellValueFactory(cellData -> cellData.getValue()
				.iterationEndProperty());
		timeColumn.setCellValueFactory(cellData -> cellData.getValue()
				.timeProperty());
		overallTimeColumn.setCellValueFactory(cellData -> cellData.getValue()
				.overallTimeProperty());
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
			manager.setFolderPath(selectedDirectory.getAbsolutePath());
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
			manager.setModelFile(selectedFile.getAbsolutePath());
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
			manager.setRsiFile(selectedFile.getAbsolutePath());
		}
	}

	@FXML
	private void handleSetupButton() {
		manager.setFolderPath(experimentPathText.getText());
		manager.setModelFile(modelFileText.getText());
		manager.setRsiFile(rsiFileText.getText());
		manager.initialSetup();
	}

	@FXML
	private void handleSwitchToOutputButton() {
		ReactisPort port = inportList.getSelectionModel().getSelectedItem();

		if (port == null) {
			return;
		}

		manager.getReactisData().switchPortToOutput(port);
	}

	@FXML
	private void handleSwitchToInputButton() {
		ReactisPort port = outportList.getSelectionModel().getSelectedItem();

		if (port == null) {
			return;
		}

		manager.getReactisData().switchPortToInput(port);
	}

	@FXML
	private void handleStartButton() {
		Double d = numberOfExperiments.getValue();
		manager.setNumberOfExperiments(d.intValue());
		manager.setStoppingCriterion(stoppingCriteria.getSelectionModel()
				.getSelectedItem());
		d = iterationEnd.getValue();
		manager.setStopingNumber(d.intValue());
		manager.run();
	}
}
