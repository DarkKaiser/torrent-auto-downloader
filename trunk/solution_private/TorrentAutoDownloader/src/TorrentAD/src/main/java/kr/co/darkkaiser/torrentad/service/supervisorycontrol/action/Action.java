package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public interface Action extends Runnable {

	ActionType getActionType();
	
	void beforeExecute();
	
	void afterExecute();

	void execute() throws Exception;

}
