package experiment.dataExtraction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import experiment.Experiment;
import experiment.models.ExperimentData;
import experiment.models.ExperimentManagerData;
import experiment.models.IterationData;

public class SpreadsheetCreator {
	
	private static final String[] settingsTitle = {
        "Number of Experiments", "Number of Tests", "Number of Test Steps", "Number of Targeted Steps", "Stopping Criterion", "", "Prune?"};
	
	private static final String[] overViewTitle = {
        "Model Name", "# of Inputs", "# of Outputs", "# In/Outputs", "# Invariants", "Average Iterations", "Average Time"};
	
	private static final String[] experimentTitle = {
        "Iteration",  "Invariants",	"Invalidated Reactis", "Net", "Acc", "Invalidated Golden", "Net", "Acc", "Time"};


	public static void main(String[] args) {
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Overview");
		
        //the header row: centered text in 48pt font
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(12.75f);
        for (int i = 0; i < overViewTitle.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(overViewTitle[i]);
        }
        
        try {
			String file = "test.xlsx";
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveData(String fileName, ExperimentManagerData experimentManagerData){
		Workbook workbook = new XSSFWorkbook();
		
		createOverview(workbook, experimentManagerData);
		
		createSettings(workbook, experimentManagerData);	
		
		int i=1;
		for (Experiment experiment : experimentManagerData.getExperiments()) {
			createExperiment(workbook, experiment.getData(), i++);
		}
		
		 try {
				FileOutputStream out = new FileOutputStream(fileName);
				workbook.write(out);
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private void createSettings(Workbook workbook,
			ExperimentManagerData experimentManagerData) {
		Sheet settings = workbook.createSheet("Settings");
		
        Row numberOfExperiments = settings.createRow(0);
        Cell cell = numberOfExperiments.createCell(0);
        cell.setCellValue(settingsTitle[0]);
        cell = numberOfExperiments.createCell(1);
        cell.setCellValue(experimentManagerData.getNumberOfExperiments().get());
        
        Row numberOfTests = settings.createRow(1);
        cell = numberOfTests.createCell(0);
        cell.setCellValue(settingsTitle[1]);
        cell = numberOfTests.createCell(1);
        cell.setCellValue(experimentManagerData.getNumberOfTests());
        
        Row numberOfTestSteps = settings.createRow(2);
        cell = numberOfTestSteps.createCell(0);
        cell.setCellValue(settingsTitle[2]);
        cell = numberOfTestSteps.createCell(1);
        cell.setCellValue(experimentManagerData.getNumberOfTestSteps());
        
        Row numberOfTargetedSteps = settings.createRow(3);
        cell = numberOfTargetedSteps.createCell(0);
        cell.setCellValue(settingsTitle[3]);
        cell = numberOfTargetedSteps.createCell(1);
        cell.setCellValue(experimentManagerData.getNumberOfTargetedSteps());
        
        Row stoppingCriterion = settings.createRow(4);
        cell = stoppingCriterion.createCell(0);
        cell.setCellValue(settingsTitle[4]);
        cell = stoppingCriterion.createCell(1);
        cell.setCellValue(experimentManagerData.getStoppingCriterion().getAbbreviation());
        
        Row stoppingCriterionValue = settings.createRow(5);
        cell = stoppingCriterionValue.createCell(0);
        cell.setCellValue(settingsTitle[5]);
        cell = stoppingCriterionValue.createCell(1);
        cell.setCellValue(experimentManagerData.getIterationEnd());
        
        Row prune = settings.createRow(6);
        cell = prune.createCell(0);
        cell.setCellValue(settingsTitle[6]);
        cell = prune.createCell(1);
        cell.setCellValue(experimentManagerData.isPrune());
        
		for(int colNum = 0; colNum<numberOfExperiments.getLastCellNum();colNum++){  
			settings.autoSizeColumn(colNum);
		}
	}

	private void createOverview(Workbook workbook,
			ExperimentManagerData experimentManagerData) {
		Sheet overview = workbook.createSheet("Overview");
		
		Row headerRow = overview.createRow(0);

        for (int i = 0; i < overViewTitle.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(overViewTitle[i]);
        }
        
        Row dataRow = overview.createRow(1);
        Cell cell = dataRow.createCell(0);
        cell.setCellValue(experimentManagerData.getModelName());
        
        cell = dataRow.createCell(1);
        cell.setCellValue(experimentManagerData.getNumberOfInputs());
        
        cell = dataRow.createCell(2);
        cell.setCellValue(experimentManagerData.getNumberOfOutputs());
        
        cell = dataRow.createCell(3);
        cell.setCellValue(experimentManagerData.getNumberOfInputs() + experimentManagerData.getNumberOfOutputs());
        
        cell = dataRow.createCell(4);
        cell.setCellValue(experimentManagerData.getNumberOfInvariants());
        
        cell = dataRow.createCell(5);
        cell.setCellValue(experimentManagerData.getNumberOfIterations());
        
        cell = dataRow.createCell(6);
        cell.setCellValue(experimentManagerData.getAverageTime());
        
		for(int colNum = 0; colNum<headerRow.getLastCellNum();colNum++){  
			overview.autoSizeColumn(colNum);
		}
	}
	
	//"Iteration", "Invariants", "Invalidated Reactis", "Net", "Acc", "Invalidated Golden", "Net", "Acc", "Time"
	private void createExperiment(Workbook workbook,
			ExperimentData experimentData, int number) {
		
		Sheet experiment = workbook.createSheet("Experiment " + number);
		
		Row headerRow = experiment.createRow(0);
		for (int i = 0; i < experimentTitle.length; i++) {
			Cell cell = headerRow.createCell(i);
            cell.setCellValue(experimentTitle[i]);
        }
		
		int i = 0;
		
		for (IterationData data : experimentData.getIterationData()) {
			Row dataRow = experiment.createRow(++i);
	        Cell cell = dataRow.createCell(0);
	        cell.setCellValue(i);
	        
	        cell = dataRow.createCell(1);
	        cell.setCellValue(data.getNumberOfPrunedInvariants());
	        
	        cell = dataRow.createCell(2);
	        cell.setCellValue(data.getNumbeOfInvalidatedInvariants());
	        
	        cell = dataRow.createCell(3);
	        cell.setCellValue(data.getNumberOfPrunedInvariants() - data.getNumbeOfInvalidatedInvariants());
	        
	        cell = dataRow.createCell(4);
	        cell.setCellValue((data.getNumberOfPrunedInvariants() - data.getNumbeOfInvalidatedInvariants()) / (double) data.getNumberOfPrunedInvariants());
	        
	        cell = dataRow.createCell(5);
	        cell.setCellValue("");
	        
	        cell = dataRow.createCell(6);
	        cell.setCellValue("");
	        
	        cell = dataRow.createCell(7);
	        cell.setCellValue("");
	        
	        cell = dataRow.createCell(8);
	        cell.setCellValue(data.getExecutionTimeTestGeneration());
		}
		
		for(int colNum = 0; colNum<headerRow.getLastCellNum();colNum++){  
			experiment.autoSizeColumn(colNum);
		}
		
	}

}
