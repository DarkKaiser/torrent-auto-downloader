package com.darkkaiser.torrentad.service.au.action;

import com.darkkaiser.torrentad.config.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public abstract class AbstractAction implements Action {

	@Getter
	private final ActionType actionType;
	
	protected final Configuration configuration;

	protected AbstractAction(final ActionType actionType, final Configuration configuration) {
		Objects.requireNonNull(actionType, "actionType");
		Objects.requireNonNull(configuration, "configuration");

		this.actionType = actionType;
		this.configuration = configuration;
	}

	@Override
	public final void run() {
		if (beforeExecute() == true) {
			try {
				execute();
			} catch (final Exception e) {
				log.error("Action 실행 중 예외가 발생하였습니다.({})", this, e);
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
