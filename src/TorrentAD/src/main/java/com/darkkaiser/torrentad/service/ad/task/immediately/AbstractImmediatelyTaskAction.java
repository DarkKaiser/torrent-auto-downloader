package com.darkkaiser.torrentad.service.ad.task.immediately;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractImmediatelyTaskAction implements ImmediatelyTaskAction {

	@Override
	public void validate() {
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (final Exception e) {
			log.debug(null, e);
			return false;
		}

		return true;
	}

}
