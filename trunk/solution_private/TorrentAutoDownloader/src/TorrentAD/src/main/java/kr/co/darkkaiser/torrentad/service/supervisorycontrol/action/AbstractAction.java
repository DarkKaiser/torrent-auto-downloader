package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

import java.util.concurrent.Callable;

public abstract class AbstractAction implements Action, Callable<Integer> {

	protected final ActionType actionType;
	
	protected AbstractAction(ActionType actionType) {
		if (actionType == null)
			throw new NullPointerException("actionType");

		this.actionType = actionType;
	}

	@Override
	public ActionType getActionType() {
		return this.actionType;
	}

	@Override
	public Integer call() throws Exception {
		// @@@@@
		return null;
	}

	@Override
	public void validate() {
		if (this.actionType == null)
			throw new NullPointerException("actionType");
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractAction.class.getSimpleName())
				.append("{")
				.append("actionType:").append(this.actionType)
				.append("}")
				.toString();
	}

}
