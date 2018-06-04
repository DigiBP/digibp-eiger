package ch.fhnw.digibp.eiger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public class ProcessExecutionListener implements ExecutionListener {
	
	public void notify(DelegateExecution execution) throws Exception {
	      //execution.setVariable("variableSetInExecutionListener", "firstValue");
	      //execution.setVariable("eventReceived", execution.getEventName());
		//execution.setVariable("applicantsInvited", false);
		execution.setVariableLocal("applicationComplete", false);
		execution.setVariableLocal("experienceSufficient", false);
		execution.setVariableLocal("eligibleDegree", false);
		execution.setVariableLocal("languageRequriementsMatch", false);
		execution.setVariableLocal("candidateDecision", false);
		execution.setVariableLocal("applicationEligible", false);
		
	    }
	
}