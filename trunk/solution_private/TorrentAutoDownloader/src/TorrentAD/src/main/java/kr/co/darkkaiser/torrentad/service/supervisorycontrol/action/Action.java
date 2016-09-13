package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public interface Action extends Runnable {

	ActionType getActionType();

}
