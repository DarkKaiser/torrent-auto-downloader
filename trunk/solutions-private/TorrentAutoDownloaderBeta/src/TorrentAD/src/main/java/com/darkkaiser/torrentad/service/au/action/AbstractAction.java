package com.darkkaiser.torrentad.service.au.action;

import com.darkkaiser.torrentad.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAction implements Action {

	private static final Logger logger = LoggerFactory.getLogger(AbstractAction.class);

	private final ActionType actionType;
	
	protected final Configuration configuration;

	protected AbstractAction(final ActionType actionType, final Configuration configuration) {
		if (actionType == null)
			throw new NullPointerException("actionType");
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.actionType = actionType;
		this.configuration = configuration;
	}

	@Override
	public ActionType getActionType() {
		return this.actionType;
	}

	@Override
	public final void run() {
		if (beforeExecute() == true) {
			try {
				execute();
			} catch (final Exception e) {
				logger.error("Action 실행 중 예외가 발생하였습니다.({})", this, e);
			}
		}

		afterExecute();
	}

	protected abstract boolean beforeExecute();

	protected abstract void afterExecute();

	protected abstract void execute() throws Exception;

	@Override
	public String toString() {
		return AbstractAction.class.getSimpleName() +
				"{" +
				"actionType:" + this.actionType +
				"}";
	}

}
