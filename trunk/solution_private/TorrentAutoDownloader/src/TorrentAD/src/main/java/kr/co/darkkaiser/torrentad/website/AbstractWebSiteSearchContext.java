package kr.co.darkkaiser.torrentad.website;

public abstract class AbstractWebSiteSearchContext implements WebSiteSearchContext {

	@Override
	public void validate() {
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractWebSiteSearchContext.class.getSimpleName())
				.append("{")
				.append("}")
				.toString();
	}
	
}
