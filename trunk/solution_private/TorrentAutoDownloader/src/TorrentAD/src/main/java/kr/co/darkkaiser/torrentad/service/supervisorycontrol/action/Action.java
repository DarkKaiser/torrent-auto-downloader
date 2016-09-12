package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public interface Action {

	ActionType getActionType();

	void validate();
	
	boolean isValid();

}
