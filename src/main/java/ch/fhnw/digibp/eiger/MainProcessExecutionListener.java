package ch.fhnw.digibp.eiger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class MainProcessExecutionListener implements JavaDelegate {
	
	public void execute(DelegateExecution execution) throws Exception {
	      
		execution.setVariable("applicantsInvited", false);
		System.out.println("applicantsInvited: " + execution.getVariable("applicantsInvited"));
		
	    }
	
}