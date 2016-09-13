package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public interface Action extends Runnable {

	ActionType getActionType();
	
	void init();
	
	void cleanup();

	void execute() throws Exception;

	void validate();
	
	boolean isValid();

}
