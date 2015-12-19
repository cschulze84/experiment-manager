package experiment.modelAnalyzis;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

import org.conqat.lib.commons.collections.UnmodifiableCollection;
import org.conqat.lib.commons.logging.SimpleLogger;
import org.conqat.lib.simulink.builder.SimulinkModelBuilder;
import org.conqat.lib.simulink.builder.SimulinkModelBuildingException;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkInPort;
import org.conqat.lib.simulink.model.SimulinkLine;
import org.conqat.lib.simulink.model.SimulinkModel;
import org.conqat.lib.simulink.model.SimulinkOutPort;
import org.fraunhofer.modeling.model.stateModel.State;
import org.fraunhofer.modeling.model.stateModel.StateModel;
import org.fraunhofer.modeling.model.stateModel.StateModelElement;
import org.fraunhofer.modeling.model.stateModel.SuperState;
import org.fraunhofer.modeling.model.stateModel.Transition;
import org.fraunhofer.visualization.graphml.CreateGraphML;

import experiment.models.modelAnalyzis.Connection;
import experiment.models.modelAnalyzis.Input;
import experiment.models.modelAnalyzis.Output;

public class SimulinkParser {
	private static final String INPORT_LABEL = "INPORT";
	private static final String OUTPORT_LABEL = "OUTPORT";
	private static final String OUT_ACTIONPORT_LABEL = "OUT_ACTION_PORT";
	private static final String ACTIONPORT_LABEL = "ACTION_PORT";
	private static final String ACTIONPORT_NAME = "Action Port";
	private static final String IF_ACTION_NAME = "ifaction";
	private static final String OUT_INPORT_LABEL = "OUT_IN_PORT";
	private static final CharSequence INPORT_NAME = "Inport";
	private static final CharSequence OUTPORT_NAME = "Outport";
	
	Map<SimulinkBlock, List<State>> inputMap = new HashMap<>();
	
	public Map<String, Connection> whichInAndOutputsAreConnected(File simulinkModel, List<String> inputs, List<String> outputs){
		Map<String, Connection> resultMap = new HashMap<>();
		
		try (SimulinkModelBuilder builder = new SimulinkModelBuilder(simulinkModel, new SimpleLogger())) {
			SimulinkParser parser = new SimulinkParser();
			SimulinkModel model = builder.buildModel();
			
			StateModel stateModel = new StateModel();
			
			parser.getAllStates(model, stateModel);
			
			parser.getAllLines(model.getContainedLines(), stateModel);
			
			StateModel flattenedModel = parser.flatten(stateModel);
			
			int i = 0;
			Map<String, Integer> stateMap = new HashMap<>();
			
			for (State state : flattenedModel.getStates()) {
				stateMap.put(state.getId(), i++);
			}
			
			Graph g = new Graph(flattenedModel.getStates().size());
			
			for (Transition transition : flattenedModel.getTransitions()) {
				 g.addEdge(stateMap.get(transition.getSourceId()), stateMap.get(transition.getTargetId()));
			}
			
			
			for (String input : inputs) {
				Input inputConnection = new Input();
				inputConnection.setName(input);
				resultMap.put(input, inputConnection);
				
				for (String output : outputs) {
					Output outputConnection = null;
					if(resultMap.containsKey(output)){
						outputConnection = (Output) resultMap.get(output);
					}
					else{
						outputConnection = new Output();
						resultMap.put(output, outputConnection);
					}
					
					State sourceState = getStateByName(flattenedModel, input);
					State targetState = getStateByName(flattenedModel, output);
					
					if(g.isReachable(stateMap.get(sourceState.getId()), stateMap.get(targetState.getId()))){
						inputConnection.getConnectedTo().add(outputConnection);
						outputConnection.getConnectedTo().add(inputConnection);
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return resultMap;
		
	}
	
	public static void main(String[] args) throws SimulinkModelBuildingException, ZipException, IOException {
		File file = new File(args[0]);
		try (SimulinkModelBuilder builder = new SimulinkModelBuilder(file, new SimpleLogger())) {
			SimulinkParser parser = new SimulinkParser();
			SimulinkModel model = builder.buildModel();
			
			StateModel stateModel = new StateModel();
			
			parser.getAllStates(model, stateModel);
			
			parser.getAllLines(model.getContainedLines(), stateModel);
			
			StateModel flattenedModel = parser.flatten(stateModel);
			
			/*for (StateModelElement element : stateModel.getAllModelElements()) {
				System.out.println("NAME: " + element.getName());
				System.out.println("ID: " + element.getData());
				System.out.println("TYPE: " + element.getUrl());
			}*/
			
			CreateGraphML creator = new CreateGraphML();
			
			/*File graphMLFile = new File("model.graphml");
			creator.newGraphMlFile(graphMLFile, flattenedModel);*/
			
			// list all blocks in the model
			/*for (SimulinkBlock block : model.getSubBlocks()) {
				if(block.getType().equalsIgnoreCase("OUTPORT")){
					outBlocks.add(block);
				}
			}
			
			for (SimulinkBlock block : outBlocks) {
				List<SimulinkBlock> origin = new ArrayList<>();
				traceBlockToOrigin(block, origin);
				System.out.println(block.getType());
			}*/

			/*// render a block or model as PNG image
			SimulinkBlockRenderer simulinkBlockRenderer = new SimulinkBlockRenderer();
			BufferedImage image = simulinkBlockRenderer.renderBlock(model);
			ImageIO.write(image, "PNG", new File(file.getPath() + ".png"));
			*/
			int i = 0;
			Map<String, Integer> stateMap = new HashMap<>();
			
			for (State state : flattenedModel.getStates()) {
				stateMap.put(state.getId(), i++);
			}
			
			Graph g = new Graph(flattenedModel.getStates().size());
			
			for (Transition transition : flattenedModel.getTransitions()) {
				 g.addEdge(stateMap.get(transition.getSourceId()), stateMap.get(transition.getTargetId()));
			}
			
			String source = "p_TFL_anzeige";
			String target = "s_tagfahrlicht_anzeige";
			
			State sourceState = getStateByName(flattenedModel, source);
			State targetState = getStateByName(flattenedModel, target);
			
			if(g.isReachable(stateMap.get(sourceState.getId()), stateMap.get(targetState.getId()))){
				System.out.println("Can be reached");
			}
			else{
				System.out.println("Cannot be reached");
			}
		}
	}

	private static State getStateByName(StateModel model, String name) {
		for (State state : model.getStates()) {
			if(state.getName().equals(name)){
				return state;
			}
		}
		return null;
	}

	private StateModel flatten(StateModel model) {
		StateModel flatModel = new StateModel();
		
		for (StateModelElement state : model.getAllModelElements()) {
			if(state instanceof Transition){
				continue;
			}
			if(state instanceof SuperState){
				continue;
			}
			
			State newState = flatModel.newState();
			
			newState.setName(state.getName());
			newState.setData(state.getData());
			newState.setUrl(state.getUrl());
		}
		
		for (Transition transition : model.getTransitions()) {
			State sourceState = (State) model.getModelElementById(transition.getSourceId(), true);
			State targetState = (State) model.getModelElementById(transition.getTargetId(), true);
			
			/*System.out.println(transition.getName());
			System.out.flush();*/
			
			if(transition.getUrl().equals(INPORT_LABEL)){
				//System.out.println("Inport");
				for (State subState : ((SuperState) targetState).getModel().getStates()) {
					if(subState.getUrl().contains(INPORT_NAME)){
						if(portsAreEqual(transition.getName(), subState.getUrl())){
							targetState = subState;
							break;
						}
					}
				}
			}
			else if(transition.getUrl().equals(ACTIONPORT_LABEL)){
				//System.out.println("Action Port");
				for (State subState : ((SuperState) targetState).getModel().getStates()) {
					if(subState.getName().contains(ACTIONPORT_NAME)){
						connectActionPortToOutport(flatModel, ((SuperState) targetState).getModel(), subState);
						targetState = subState;
						break;
					}
				}
			}
			else if(transition.getUrl().equals(OUTPORT_LABEL)){
				//System.out.println("Outport");
				for (State subState : ((SuperState) sourceState).getModel().getStates()) {
					if(subState.getUrl().contains(OUTPORT_NAME)){
						if(portsAreEqual(transition.getName(), subState.getUrl())){
							sourceState = subState;
							//System.out.println(transition.getName() + " : " + subState.getUrl());
							break;
						}
					}
				}
			}
			else if(transition.getUrl().equals(OUT_INPORT_LABEL)){
				for (State subState : ((SuperState) sourceState).getModel().getStates()) {
					if(subState.getUrl().contains(OUTPORT_NAME)){
						if(portsAreEqual(transition.getName().split("###")[0], subState.getUrl())){
							sourceState = subState;
							break;
						}
					}
				}
				for (State subState : ((SuperState) targetState).getModel().getStates()) {
					if(subState.getUrl().contains(INPORT_NAME)){
						if(portsAreEqual(transition.getName().split("###")[1], subState.getUrl())){
							targetState = subState;
							break;
						}
					}
				}
			}
			else if(transition.getUrl().equals(OUT_ACTIONPORT_LABEL)){
				for (State subState : ((SuperState) sourceState).getModel().getStates()) {
					if(subState.getUrl().contains(OUTPORT_LABEL)){
						if(portsAreEqual(transition.getName().split("###")[0], subState.getUrl())){
							sourceState = subState;
							break;
						}
					}
				}
				for (State subState : ((SuperState) targetState).getModel().getStates()) {
					if(subState.getName().contains(ACTIONPORT_NAME)){
						connectActionPortToOutport(flatModel, ((SuperState) targetState).getModel(), subState);
						targetState = subState;
						break;
					}
				}
			}
			State sourceStateFlat = findStateByData(flatModel, sourceState.getData());
			State targetStateFlat = findStateByData(flatModel, targetState.getData());
				
			flatModel.newTransition(sourceStateFlat, targetStateFlat);
			
		}
		
		return flatModel;
	}

	private void connectActionPortToOutport(StateModel flatModel, StateModel model, State sourceState) {
		for (State state : model.getStates()) {
			if(state.getUrl().startsWith(OUTPORT_LABEL)){
				State sourceStateFlat = findStateByData(flatModel, sourceState.getData());
				State targetStateFlat = findStateByData(flatModel, state.getData());
					
				flatModel.newTransition(sourceStateFlat, targetStateFlat);
			}
		}
	}

	private boolean portsAreEqual(String firstPort, String secondPort) {
		boolean areEqual = firstPort.split("@")[0].equals(secondPort.split("@")[1]);
		return areEqual;
	}

	private void getAllLines(UnmodifiableCollection<SimulinkLine> containedLines, StateModel stateModel) {
		List<SimulinkBlock> subBlocks = new ArrayList<>();
		
		for (SimulinkLine line : containedLines) {
			String sourceID = line.getSrcPort().getBlock().getId();
			String targetID = line.getDstPort().getBlock().getId();
			
			State sourceState = findStateByData(stateModel, sourceID);
			State targetState = findStateByData(stateModel, targetID);
			
			Transition transition = stateModel.newTransition(sourceState, targetState);
			
			if(!(sourceState instanceof SuperState) && targetState instanceof SuperState){
				SimulinkInPort port = line.getDstPort().getBlock().getInPort(line.getDstPort().getIndex());
				transition.setName(port.toString());
				transition.setLabel(port.toString());
				
				if(port.toString().startsWith(IF_ACTION_NAME)){
					transition.setUrl(ACTIONPORT_LABEL);
				}
				else{
					transition.setUrl(INPORT_LABEL);	
				}
			}
			else if(sourceState instanceof SuperState && !(targetState instanceof SuperState)){	
				SimulinkOutPort port = line.getSrcPort().getBlock().getOutPort(line.getSrcPort().getIndex());
				if(port != null){
					transition.setName(port.toString());
					transition.setLabel(port.toString());
					transition.setUrl(OUTPORT_LABEL);
					
				}
				else if(sourceState instanceof SuperState){
					transition.setUrl(ACTIONPORT_LABEL);
				}
				
			}
			else if(sourceState instanceof SuperState && targetState instanceof SuperState){	
				SimulinkOutPort outPort = line.getSrcPort().getBlock().getOutPort(line.getSrcPort().getIndex());
				SimulinkInPort inPort = line.getDstPort().getBlock().getInPort(line.getDstPort().getIndex());
				if(outPort != null && inPort != null){
					transition.setName(outPort.toString() + "###" + inPort.toString());
					transition.setLabel(outPort.toString() + "###" + inPort.toString());
					
					transition.setUrl(OUT_INPORT_LABEL);
					
				}
				else{
					transition.setName(outPort.toString());
					transition.setUrl(OUT_ACTIONPORT_LABEL);
				}
				
			}
	
			if(line.getDstPort().getBlock().hasSubBlocks() && isNotInList(line.getDstPort().getBlock(), subBlocks)){
				subBlocks.add(line.getDstPort().getBlock());
			}
		}
		
		for (SimulinkBlock block : subBlocks) {
			getAllLines(block.getContainedLines(), stateModel);
		}
	}

	private boolean isNotInList(SimulinkBlock block, List<SimulinkBlock> subBlocks) {
		for (SimulinkBlock simulinkBlock : subBlocks) {
			if(simulinkBlock.equals(block)){
				return false;
			}
		}
		return true;
	}
	
	private void getAllStates(SimulinkModel model, StateModel stateModel) {
		for (SimulinkBlock block : model.getSubBlocks()) {
			if(block.hasSubBlocks()){
				SuperState state = stateModel.newSuperState();
				state.setName(block.getName());
				state.setLabel(block.getName());
				state.setData(block.getId());
				state.setUrl(block.getType());
				getAllStates(block, state.getModel());
			}
			else{
				State state = stateModel.newState();
				state.setName(block.getName());
				state.setLabel(block.getName());
				state.setData(block.getId());
				state.setUrl(block.getType());
			}
		}
	}


	private void getAllStates(SimulinkBlock superBlock, StateModel stateModel) {
		for (SimulinkBlock block : superBlock.getSubBlocks()) {
			if(block.hasSubBlocks()){
				SuperState state = stateModel.newSuperState();
				state.setName(block.getName());
				state.setLabel(block.getName());
				state.setData(block.getId());
				state.setUrl(block.getType());
				getAllStates(block, state.getModel());
			}
			else{
				State state = stateModel.newState();
				state.setName(block.getName());
				state.setLabel(block.getName());
				state.setData(block.getId());
				state.setUrl(block.getType());
				
				if(block.getType().equals("Inport") || block.getType().equals("Outport")){
					String portNumber = block.getDeclaredParameter("Port");
					
					if(portNumber==null){
						portNumber = "1";
					}
					
					state.setUrl(state.getUrl() + "@" + portNumber);
					state.setName(portNumber + "@" + state.getName());

					/*for (SimulinkInPort inport : block.getModel().getInPorts()) {
						System.out.println(inport.getIndex());
					}*/
				}
			}
		}
		
	}

	private State findStateByData(StateModel stateModel, String id) {
		for (StateModelElement element : stateModel.getAllModelElements()) {
			if(element.getData().equals(id)){
				return (State) element;
			}
		}
		return null;
	}

}
