
// NetServerDlg.h : 헤더 파일
//

#pragma once

class CServiceASyncSocket;

// CNetServerDlg 대화 상자
class CNetServerDlg : public CDialog
{
// 생성입니다.
public:
	CNetServerDlg(CWnd* pParent = NULL);	// 표준 생성자입니다.

// 대화 상자 데이터입니다.
	enum { IDD = IDD_NETSERVER_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV 지원입니다.

// 구현입니다.
protected:
	HICON m_hIcon;

	CString m_strSendData;
	CString m_strReceiveData;
	CP2PListener m_p2pListener;
	CServiceASyncSocket* m_pClientSocket;

	// 생성된 메시지 맵 함수
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	afx_msg void OnDestroy();
	afx_msg HCURSOR OnQueryDragIcon();
	LRESULT OnAccept(WPARAM, LPARAM);
	LRESULT OnClose(WPARAM, LPARAM);
	LRESULT OnReceiveData(WPARAM, LPARAM);
	afx_msg void OnBnClickedBtnSend();
	DECLARE_MESSAGE_MAP()
};
