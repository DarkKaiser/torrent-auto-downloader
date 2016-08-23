package kr.co.darkkaiser.torrentad.service.task;

public abstract class AbstractTask implements Task {
	
	//@@@@@ 보드명을 여기에 둬야하나??? 보고보고인데 별갱ㄴ데...
	private String boardName;

	// @@@@@
	@Override
	public String getBoardName() {
		return boardName;
	}

	// @@@@@
	@Override
	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}
	
}
