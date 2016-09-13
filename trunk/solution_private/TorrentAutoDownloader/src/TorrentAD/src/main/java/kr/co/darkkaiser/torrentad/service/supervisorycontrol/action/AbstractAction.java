package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAction implements Action {

	private static final Logger logger = LoggerFactory.getLogger(AbstractAction.class);
	
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
	public final void run() {
		init();

		try {
			execute();
		} catch (Exception e) {
			logger.error("Action 실행 중 예외가 발생하였습니다.({})", this, e);
		}

		cleanup();
	}

	@Override
	public void init() {
	}

	@Override
	public void cleanup() {
	}

	@Override
	public void execute() throws Exception {
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
