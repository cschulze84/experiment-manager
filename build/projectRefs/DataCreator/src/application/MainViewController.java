package application;

import java.io.File;
import java.util.List;

import org.myphd.parser.CompareImplications;
import org.myphd.parser.Data;
import org.myphd.parser.ImplicationParser;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainViewController {
	@FXML
    private TextField goldenFileField;
    @FXML
    private TextField dataFolderField;
    
    private Stage primaryStage;
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
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
    private void handleGoldenButton(){
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Choose Golden Data File");
    	fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("Text Files", "*.txt"));
    	
    	File selectedFile = fileChooser.showOpenDialog(primaryStage);
    	if (selectedFile != null) {
    		goldenFileField.setText(selectedFile.getAbsolutePath());
    	}
    	
    	
    }
    
    @FXML
    private void handleDataButton(){
    	DirectoryChooser chooser = new DirectoryChooser();
    	chooser.setTitle("Choose Data Folder");
    	
    	File selectedDirectory = chooser.showDialog(primaryStage);
    	if (selectedDirectory != null) {
    		dataFolderField.setText(selectedDirectory.getAbsolutePath());
    	}
    }
    
    @FXML
    private void handleGenerateButton(){
    	FileIoService fileIoService = new FileIoService();
    	ImplicationParser parser = new ImplicationParser();
    	
    	if(dataFolderField.getText().isEmpty() || goldenFileField.getText().isEmpty()){
    		return;
    	}
    	File goldenFile = new File(goldenFileField.getText());
    	File dataFolder = new File(dataFolderField.getText());
    	
    	if(!goldenFile.isFile() || !dataFolder.isDirectory()){
    		return;
    	}
    	
    	String extensions[] = {"txt"};
    	List<File>  dataFiles = fileIoService.getFilesInFolder(dataFolder, extensions);
		for (File dataFile : dataFiles) {
			
			List<Data> toCompare = parser.parseZ3(dataFile.getAbsolutePath());
			List<Data> golden = parser.parseZ3(goldenFile.getAbsolutePath());
			
			CompareImplications comparison = new CompareImplications();
			comparison.compare(toCompare, golden);
		}
    }
}
