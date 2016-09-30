package kr.co.darkkaiser.torrentad.service.bot.telegrambot.torrentbot;

// @@@@@
public enum State {
	
	/* 명령은 따로 관리
	 * 
	 * 
	 * 각 상태를 클래스로 구현
	 * none
input_search_keyword
search_ing
print_board_item
confirm_boarditem_files
print_boarditem_files
boarditem_download
	
select_board
confirm_torrent_status



		// Command(제거), Request, Response
		 // request를 이용해서 command 도움말 제공



	 * 게시물목록에 토렌트임을 나타내는 특정 문자열(링크등)을 심어서 문자만 보냈을때 토렌트 게시물목록임을 알수있도록 한다.
	 *
	 * 검색 - 토렌트 검색
	 * 전체 게시판 검색
	 * 1. 검색어 입력
	 * 2. 검색결과 출력
	 * 3. 게시물 클릭하면 첨부파일 정보 출력
	 * 4. 다운로드(인라인키보드) - 콜백정보 이용
	 * 
	 * BotCommand interface - 명령(검색,조회,도움)
	 * 	  getCommandIdentifier()
	 * 	  getCommandDescription()
	 * 
	 * Base interface
	 * 
	 * BaseExecutor interface
	 *    void execute()
	 * 
	 * Request interface extends Base, BaseExecutor
	 * 
	 * Response interface extends Base, BaseExecutor
	 * 
	 * 
	 *                   Base
	 * Response							Request 
	 * AbstractResponse					AbstractRequest
	 * 
	 * 									검색(SearchRequest extends xxx implements BotCommand)
	 * 검색어를 입력하세요(SearchResponse)
	 * 									검색어입력
	 * 									(기본명령어가 입력되면 검색 취소하고 해당 모드로 변경, 다른 글자는 검색어로 간주한다)
	 * 									(Reply키보드 메시지:검색이 취소되었습니다. 출력하고 해당 게시물의 첨부파일 확인 진행)
	 * 									(인라인키보드가 클릭되면??
	 * 										게시판 선택:게시판선택 설정을 바꾸고 검색이 취소되었습니다. 게시판설정이 ''로 변경되었습니다. 출력
	 * 										첨부파일 선택:검색이 취소되었습니다. 출력하고 해당 첨부파일 다운로드 진행
	 * 									)
	 * 검색중입니다. 잠시만 기다려주세요.(SearchProcessingResponse)
	 * 									(기본명령어가 입력되면 검색이 취소되었습니다 출력하고 해당 모드로 변경, 다른 글자는 모두 "토렌트 검색중입니다. 명령어를 모르시면 '도움'을 입력하세요." 출력
	 * 									(Reply키보드 메시지:검색이 취소되었습니다. 출력하고 해당 게시물의 첨부파일 확인 진행)
	 * 									(인라인키보드가 클릭되면??
	 * 										게시판 선택:게시판선택 설정을 바꾸고 검색이 취소되었습니다. 게시판설징이 ''로 변경되었습니다. 출력
	 * 										첨부파일 선택:검색이 취소되었습니다. 출력하고 해당 첨부파일 다운로드 진행
	 * 									)
	 * 게시물 리스트 출력(Reply키보드)(PrintBoardItemsResponse)
	 * 									게시물 클릭(첨부파일 다운로드) - (ClickBoardItemsRequest)
	 * 									(기본명령어가 입력되면 메시지 출력없이 해당 모드로 변경, 다른 글자는 모두 "명령어를 모르시면 '도움'을 입력하세요." 출력
	 * 									(인라인키보드가 클릭되면??
	 * 										게시판 선택:게시판선택 설정을 바꾸고 검색이 취소되었습니다. 게시판설징이 ''로 변경되었습니다. 출력
	 * 										첨부파일 선택:검색이 취소되었습니다. 출력하고 해당 첨부파일 다운로드 진행
	 * 									)
	 * 첨부파일 확인중입니다. 잠시만 기다려주세요.(DetailBoardItemResponse)
	 * 									(기본명령어가 입력되면 첨부파일 확인이 취소되었습니다 출력하고 해당 모드로 변경, 다른 글자는 모두 "명령어를 모르시면 '도움'을 입력하세요." 출력
	 * 									(Reply키보드 메시지:첨부파일 확인이 취소되었습니다. 출력하고 선택된 게시물의 첨부파일 다운로드 진행)
	 * 									(인라인키보드가 클릭되면??
	 * 										게시판 선택:게시판선택 설정을 바꾸고 첨부파일 확인이 취소되었습니다. 게시판설징이 ''로 변경되었습니다. 출력
	 * 										첨부파일 선택:첨부파일 확인이 취소되었습니다. 출력하고 해당 첨부파일 다운로드 진행
	 * 									)
	 * 첨부파일 목록 출력(인라인키보드) - (PrintBoardItemDetailResponse)
	 * 									첨부파일 클릭 - 첨부파일 다운로드 - (ClickBoardItemsDetailRequest)
	 * 									(기본명령어가 입력되면 해당 모드로 변경, 다른 글자는 모두 "명령어를 모르시면 '도움'을 입력하세요." 출력
	 * 									(Reply키보드 메시지:선택된 게시물의 첨부파일 다운로드 진행)
	 * 									(인라인키보드가 클릭되면??
	 * 										게시판 선택:게시판선택 설정을 바꾸고 게시판설징이 ''로 변경되었습니다. 출력
	 * 										첨부파일 선택:해당 첨부파일 다운로드 진행
	 * 									)
	 * 첨부파일 다운로드
	 * 
	 * 
	 * 조회
	 * 게시판 목록조회
	 * 1. 게시판 선택-조회
	 * 2. 해당 게시판 목록 조회에서 출력
	 * 		명령 제외한 다른 메시지가 들어오면 게시판 목록 클릭하라고 출력
	 * 		기본명령이 들어오면 상태를 되돌린다.
	 * 3. 게시물 클릭하면 첨부파일 정보 출력
	 * 		명령 제외한 다른 메시지가 들어오면 게시물 클릭하라고 출력
	 * 		기본명령이 들어오면 상태를 되돌린다.
	 * 4. 다운로드(인라인키보드)
	 * 
	 *                      			조회
	 * 게시판을 선택하세요(인라인키보드)
	 * 									(기본명령어가 입력되면 조회취소, 다른 글자는 도움말 출력 메시지 출력)
	 * 									(기본명령어가 입력되면 해당 모드로 변경, 다른 글자는 모두 "명령어를 모르시면 '도움'을 입력하세요." 출력
	 * 검색중입니다. 잠시만 기다려주세요.
	 * 									(기본명령어가 입력되면 검색이 취소되었습니다 출력하고 해당 모드로 변경, 다른 글자는 모두 "토렌트 검색중입니다. 명령어를 모르시면 '도움'을 입력하세요." 출력
	 * 									(Reply키보드 메시지:검색이 취소되었습니다. 출력하고 해당 게시물의 첨부파일 다운로드 진행)
	 * 									(인라인키보드가 클릭되면??
	 * 										게시판 선택:게시판선택 설정을 바꾸고 검색이 취소되었습니다. 게시판설징이 ''로 변경되었습니다. 출력
	 * 										첨부파일 선택:검색이 취소되었습니다. 출력하고 해당 첨부파일 다운로드 진행
	 * 									)
	 * 게시물 리스트 출력(Reply키보드)
	 * 									게시물 클릭(첨부파일 다운로드)
	 * 									(기본명령어가 입력되면 검색이 취소되었습니다 출력하고 해당 모드로 변경, 다른 글자는 모두 "명령어를 모르시면 '도움'을 입력하세요." 출력
	 * 									(인라인키보드가 클릭되면??
	 * 										게시판 선택:게시판선택 설정을 바꾸고 검색이 취소되었습니다. 게시판설징이 ''로 변경되었습니다. 출력
	 * 										첨부파일 선택:검색이 취소되었습니다. 출력하고 해당 첨부파일 다운로드 진행
	 * 									)
	 * 첨부파일 확인중입니다. 잠시만 기다려주세요.
	 * 									(기본명령어가 입력되면 검색이 취소되었습니다 출력하고 해당 모드로 변경, 다른 글자는 모두 "명령어를 모르시면 '도움'을 입력하세요." 출력
	 * 									(Reply키보드 메시지:첨부파일 확인이 취소되었습니다. 출력하고 선택된 게시물의 첨부파일 다운로드 진행)
	 * 									(인라인키보드가 클릭되면??
	 * 										게시판 선택:게시판선택 설정을 바꾸고 첨부파일 확인이 취소되었습니다. 게시판설징이 ''로 변경되었습니다. 출력
	 * 										첨부파일 선택:첨부파일 확인이 취소되었습니다. 출력하고 해당 첨부파일 다운로드 진행
	 * 									)
	 * 첨부파일 목록 출력(인라인키보드)
	 * 									첨부파일 클릭 - 첨부파일 다운로드
	 * 									(기본명령어가 입력되면 해당 모드로 변경, 다른 글자는 모두 "명령어를 모르시면 '도움'을 입력하세요." 출력
	 * 									(Reply키보드 메시지:선택된 게시물의 첨부파일 다운로드 진행)
	 * 									(인라인키보드가 클릭되면??
	 * 										게시판 선택:게시판선택 설정을 바꾸고 게시판설징이 ''로 변경되었습니다. 출력
	 * 										첨부파일 선택:해당 첨부파일 다운로드 진행
	 * 									)
	 * 첨부파일 다운로드
	 *
	 * 
	 * 
	 * 
	 * 상태
	 * 									상태
	 * 상태 확인중입니다. 잠시만 기다려주세요.
	 * 									(기본명령어가 입력되면 상태 확인이 취소되었습니다 출력하고 해당 모드로 변경, 다른 글자는 모두 "토렌트 검색중입니다. 명령어를 모르시면 '도움'을 입력하세요." 출력
	 * 									(Reply키보드 메시지:상태확인이 취소되었습니다. 출력하고 해당 게시물의 첨부파일 다운로드 진행)
	 * 									(인라인키보드가 클릭되면??
	 * 										게시판 선택:게시판선택 설정을 바꾸고 상태확인이 취소되었습니다. 게시판설징이 ''로 변경되었습니다. 출력
	 * 										첨부파일 선택:상태확인이 취소되었습니다. 출력하고 해당 첨부파일 다운로드 진행
	 * 									)
	 * 상태출력
	 * 
	 */
	WAITING {
		@Override
		boolean execute() {
			// TODO Auto-generated method stub
			return false;
		}
	},

	WAITING_BOARD_SELECT {
		@Override
		boolean execute() {
			// TODO Auto-generated method stub
			return false;
		}
	},

	WAITING_BOARDITEM_SELECT {
		@Override
		boolean execute() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	
	WAITING_INPUT_SEARCH_KEYWORD {
		@Override
		boolean execute() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	
	WAITING_SEARCH {
		@Override
		boolean execute() {
			// TODO Auto-generated method stub
			return false;
		}
	}, NORMAL {
		@Override
		boolean execute() {
			// TODO Auto-generated method stub
			return false;
		}
	};

	abstract boolean execute();

}
