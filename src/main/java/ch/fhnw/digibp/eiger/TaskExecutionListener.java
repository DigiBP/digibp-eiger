package ch.fhnw.digibp.eiger;


import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.TaskListener;



public class TaskExecutionListener implements TaskListener {
	
	private Expression candidateDecision;
	
	public void notify(DelegateTask delegateTask) {
		System.out.println("candidateDecision: " + candidateDecision.getValue(delegateTask).toString());
		System.out.println("applicationEligible: " + delegateTask.getExecution().getVariable("applicationEligible"));
	      if("true".equals(candidateDecision.getValue(delegateTask).toString())) {
	    	  delegateTask.getExecution().setVariable("applicantsInvited", true);	
	      }
	    System.out.println("applicantsInvited: " + delegateTask.getExecution().getVariable("applicantsInvited"));
	    }
	
}