package experiment.dataExtraction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import experiment.Experiment;
import experiment.models.ExperimentData;
import experiment.models.ExperimentManagerData;
import experiment.models.IterationData;
import experiment.models.tests.CoverageData;

public class SpreadsheetCreator {
	
	private static final String[] settingsTitle = {
        "Number of Experiments", "Number of Tests", "Number of Test Steps", "Number of Targeted Steps", "Stopping Criterion", "", "Prune?"};
	
	private static final String[] overViewTitle = {
        "Model Name", "# of Inputs", "# of Outputs", "# In/Outputs", "# Invariants", "Average Iterations", "Average Time"};
	
	private static final String[] experimentTitle = {
        "Iteration",  "Invariants",	"Invalidated Reactis", "Net", "Acc", "Invalidated Golden", "Net", "Acc", "Time"};
	
	private static final String[] coverageTitle = {"Branch", "Decision", "Condition", "MCDC", "MCC", "Boundary", "State", "Transition", "Condition Action"};

	private static final String[] coverageSubTitle = {"%", "#" , "#cov" , "#unc" , "#unr"};

	public static void main(String[] args) {
		Workbook workbook = new XSSFWorkbook();
		
		SpreadsheetCreator creator = new SpreadsheetCreator();

		creator.createExperiment(workbook, null, 1);
        
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
		
		Row headerRow1 = experiment.createRow(0);
		
		
		Row headerRow2 = experiment.createRow(1);
		for (int i = 0; i < experimentTitle.length; i++) {
			Cell cell = headerRow2.createCell(i);
            cell.setCellValue(experimentTitle[i]);
        }
		
		int field = experimentTitle.length;
		
		for (int i = 0; i < coverageTitle.length; i++) {
			for (int j = 0; j < coverageSubTitle.length; j++) {
				Cell cell = headerRow2.createCell(field++);
	            cell.setCellValue(coverageSubTitle[j]);
			}
        }
		field = experimentTitle.length;
		for (int i = 0; i < coverageTitle.length; i++) {
			Cell cell = headerRow1.createCell(field);
            cell.setCellValue(coverageTitle[i]);
			experiment.addMergedRegion(new CellRangeAddress(0, 0, field, field+coverageSubTitle.length-1));
			field = field+coverageSubTitle.length;
        }
		
		int i = 1;
		
		if(experimentData == null || experimentData.getIterationData() == null){
			return;
		}
		
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
	        
	        double acc = 100 * (data.getNumberOfPrunedInvariants() - data.getNumbeOfInvalidatedInvariants()) / (double) data.getNumberOfPrunedInvariants();
	        
	        cell = dataRow.createCell(4);
	        cell.setCellValue(round(acc,2));
	        
	        cell = dataRow.createCell(5);
	        cell.setCellValue("");
	        
	        cell = dataRow.createCell(6);
	        cell.setCellValue("");
	        
	        cell = dataRow.createCell(7);
	        cell.setCellValue("");
	        
	        cell = dataRow.createCell(8);
	        cell.setCellValue(data.getExecutionTimeTestGeneration());
	        
	        field = 9;
	        
	        field = printCoverage(dataRow, data.getCovarage().getBranch(), field);
	        field = printCoverage(dataRow, data.getCovarage().getDecision(), field);
	        field = printCoverage(dataRow, data.getCovarage().getCondition(), field);
	        field = printCoverage(dataRow, data.getCovarage().getMCDC(), field);
	        field = printCoverage(dataRow, data.getCovarage().getMCC(), field);
	        field = printCoverage(dataRow, data.getCovarage().getBoundary(), field);
	        field = printCoverage(dataRow, data.getCovarage().getState(), field);
	        field = printCoverage(dataRow, data.getCovarage().getTransitionAction(), field);
	        field = printCoverage(dataRow, data.getCovarage().getConditionAction(), field);  
	   
		}
		
		for(int colNum = 0; colNum<headerRow2.getLastCellNum();colNum++){  
			experiment.autoSizeColumn(colNum);
		}
		
	}

	private int printCoverage(Row dataRow, CoverageData data, int field) {
		Cell cell = dataRow.createCell(field++);
        cell.setCellValue(round(data.getPercent(), 2));
		cell = dataRow.createCell(field++);
        cell.setCellValue(data.getTotal());
        cell = dataRow.createCell(field++);
        cell.setCellValue(data.getCovered());
        cell = dataRow.createCell(field++);
        cell.setCellValue(data.getUncovered());
        cell = dataRow.createCell(field++);
        cell.setCellValue(data.getUnreachable());
		return field;
	}

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}
