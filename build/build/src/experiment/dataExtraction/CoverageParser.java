package experiment.dataExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import experiment.models.CoverageData;
import experiment.models.TestSuiteCoverage;

public class CoverageParser {

	public void parseCoverage(String coverageFile, TestSuiteCoverage coverage){
		File file = new File(coverageFile);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			for(String line; (line = br.readLine()) != null; ) {
		    	if(line.trim().isEmpty()){
		    		continue;
		    	}
		    	CoverageData data = parseLine(line, coverage);
		    	
		    	if(data.getName().equals(TestSuiteCoverage.ASSERTION)){
					coverage.setAssertion(data);
				}
				else if(data.getName().equals(TestSuiteCoverage.BOUNDARY)){
					coverage.setBoundary(data);
				}
				else if(data.getName().equals(TestSuiteCoverage.BRANCH)){
					coverage.setBranch(data);
				}		
				else if(data.getName().equals(TestSuiteCoverage.CONDITION)){
					coverage.setCondition(data);
				}
				else if(data.getName().equals(TestSuiteCoverage.CONDITION_ACTION)){
					coverage.setConditionAction(data);
				}
				else if(data.getName().equals(TestSuiteCoverage.CSTATEMENT)){
					coverage.setCStatement(data);
				}
				else if(data.getName().equals(TestSuiteCoverage.DECISION)){
					coverage.setDecision(data);
				}
				else if(data.getName().equals(TestSuiteCoverage.MCC)){
					coverage.setMCC(data);
				}
				else if(data.getName().equals(TestSuiteCoverage.MCDC)){
					coverage.setMCDC(data);
				}
				else if(data.getName().equals(TestSuiteCoverage.STATE)){
					coverage.setState(data);
				}
				else if(data.getName().equals(TestSuiteCoverage.SUBSYSTEM)){
					coverage.setSubsystem(data);
				}
				else if(data.getName().equals(TestSuiteCoverage.TRANSITION_ACTION)){
					coverage.setTransitionAction(data);
				}
				else if(data.getName().equals(TestSuiteCoverage.USER_TARGET)){
					coverage.setUserTarget(data);
				}
		    }
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private CoverageData parseLine(String line, TestSuiteCoverage coverage) {
		String fullLineArray[] = line.split(":");
		
		CoverageData data = new CoverageData();
		
		data.setName(fullLineArray[0].trim());
		
		String dataArray[] = fullLineArray[1].trim().split(" ");
		
		int i=0;
		for (String dataString : dataArray) {
			dataString = dataString.replace(",", "");
			dataString = dataString.replace("%", "");
			
			String dataValueArray[] = dataString.split("=");
			
			if(dataValueArray[0].trim().equals(CoverageData.TOTAL)){
				data.setTotal(Integer.parseInt(dataValueArray[1].trim()));
			}
			else if(dataValueArray[0].trim().equals(CoverageData.COVERED)){
				data.setCovered(Integer.parseInt(dataValueArray[1].trim()));
			}
			else if(dataValueArray[0].trim().equals(CoverageData.UNREACHABLE)){
				data.setUnreachable(Integer.parseInt(dataValueArray[1].trim()));
			}		
			else if(dataValueArray[0].trim().equals(CoverageData.UNCOVERED)){
				data.setUncovered(Integer.parseInt(dataValueArray[1].trim()));
			}
			else if(dataValueArray[0].trim().equals(CoverageData.PERCENT)){
				data.setPercent(Double.parseDouble(dataValueArray[1].trim()));
			}
			i++;
		}
		return data;
		
	}
}
