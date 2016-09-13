package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public abstract class AbstractAction implements Action, Runnable {

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
	public void run() {
		// @@@@@
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
		return new StringBuilder().append(AbstractAction.class.getSimpleName()).append("{").append("actionType:")
				.append(this.actionType).append("}").toString();
	}

}
