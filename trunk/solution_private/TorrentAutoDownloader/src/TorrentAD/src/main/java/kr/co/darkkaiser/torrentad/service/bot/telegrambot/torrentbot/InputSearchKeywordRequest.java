package kr.co.darkkaiser.torrentad.service.bot.telegrambot.torrentbot;

public class InputSearchKeywordRequest {
	
	public void execute() {
		
	}
	
	public void cancel() {
		
	}
	
	public String getDefaultBotMessage() {
		return "검색중입니다. 잠시만 기다려주세요.";
	}
	
	public String getCancelMessage() {
		return "검색이 취소되었습니다.";
	}
	
	// 기본명령
	// 확장명령(기본명령 이후 진행되는 명령)

	// @@@@@
	public State execute(String message/*, NewRequest */) {
		// 기본 명령어인가?
		if (hasDefaultCommand() == true) {
			// requestId 변경
//			parent.addRequestId();

//			return NewRequest.execute();
		}

		// Reply키보드 메시지인가?
		if (isReplyKeyboardMessage() == true) {
			// requestId 변경
			// 검색이 취소되었습니다 출력
			// 해당 게시물의 첨부파일 확인 진행
//			Action action = new ConfirmBoardItemFileAction();
//			return action.execute(message)
		}
		
		// 인라인 키보드 메시지인가?
		if (isInineKeyboardMessage() == true) {
			// 게시판선택
				// requestId 변경
				// 검색이 취소되었습니다 출력
//				Action action = new SelectBoardAction(getCancelMessage());
//				return action.execute(message)
			// 첨부파일 다운로드
				// requestId 변경
				// 검색이 취소되었습니다 출력
				// 해당 게시물의 첨부파일 다운로드 진행
//				Action action = new DownloadBoardItemFileAction(getCancelMessage());
//				return action.execute(message)
		}

		// 검색어로 간주
		// 검색작업 진행
		// 기본 메시지 출력
		
		// 다음상태 반환
		return State.WAITING;
	}

	private boolean isInineKeyboardMessage() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isReplyKeyboardMessage() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean hasDefaultCommand() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
