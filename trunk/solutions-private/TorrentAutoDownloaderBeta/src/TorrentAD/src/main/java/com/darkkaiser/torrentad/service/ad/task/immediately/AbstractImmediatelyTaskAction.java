package com.darkkaiser.torrentad.service.ad.task.immediately;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractImmediatelyTaskAction implements ImmediatelyTaskAction {

	private static final Logger logger = LoggerFactory.getLogger(AbstractImmediatelyTaskAction.class);

	@Override
	public void validate() {
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (Exception e) {
			logger.debug(null, e);
			return false;
		}

		return true;
	}

}
