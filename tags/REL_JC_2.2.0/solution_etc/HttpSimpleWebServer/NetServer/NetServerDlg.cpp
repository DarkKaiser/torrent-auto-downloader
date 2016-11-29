
// NetServerDlg.cpp : 구현 파일
//

#include "stdafx.h"
#include "NetServer.h"
#include "NetServerDlg.h"

#include "./modules/net/ServiceASyncSocket.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CNetServerDlg 대화 상자




CNetServerDlg::CNetServerDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CNetServerDlg::IDD, pParent)
	, m_pClientSocket(NULL)
	, m_strReceiveData(_T(""))
	, m_strSendData(_T("HTTP/1.1 200 OK\r\n\r\n수신된 메시지입니다."))
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CNetServerDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Text(pDX, IDC_EDT_RECEIVE_DATA, m_strReceiveData);
	DDX_Text(pDX, IDC_EDT_SEND_DATA, m_strSendData);
}

BEGIN_MESSAGE_MAP(CNetServerDlg, CDialog)
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	//}}AFX_MSG_MAP
	ON_WM_DESTROY()
	ON_MESSAGE(WM_USER + 100, &CNetServerDlg::OnAccept)
	ON_MESSAGE(WM_USER + 101, &CNetServerDlg::OnClose)
	ON_MESSAGE(WM_USER + 102, &CNetServerDlg::OnReceiveData)
	ON_BN_CLICKED(IDC_BTN_SEND, &CNetServerDlg::OnBnClickedBtnSend)
END_MESSAGE_MAP()


// CNetServerDlg 메시지 처리기

BOOL CNetServerDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// 이 대화 상자의 아이콘을 설정합니다. 응용 프로그램의 주 창이 대화 상자가 아닐 경우에는
	//  프레임워크가 이 작업을 자동으로 수행합니다.
	SetIcon(m_hIcon, TRUE);			// 큰 아이콘을 설정합니다.
	SetIcon(m_hIcon, FALSE);		// 작은 아이콘을 설정합니다.

	// 소켓을 생성한다.
	if (m_p2pListener.Create(8080) == TRUE)
	{
		m_p2pListener.SetValues(GetSafeHwnd());

		if (m_p2pListener.Listen() == FALSE)
		{
			CString str;
			str.Format(_T("Server Listen socket listen failed. ErrorCode : %d"), WSAGetLastError());
			AfxMessageBox(str);
		}
	} else {
		CString str;
		str.Format(_T("Server Listen socket create failed. ErrorCode : %d"), WSAGetLastError());
		AfxMessageBox(str);
	}

	return TRUE;  // 포커스를 컨트롤에 설정하지 않으면 TRUE를 반환합니다.
}

void CNetServerDlg::OnDestroy()
{
	CDialog::OnDestroy();

	m_p2pListener.Close();
}

// 대화 상자에 최소화 단추를 추가할 경우 아이콘을 그리려면
//  아래 코드가 필요합니다. 문서/뷰 모델을 사용하는 MFC 응용 프로그램의 경우에는
//  프레임워크에서 이 작업을 자동으로 수행합니다.

void CNetServerDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // 그리기를 위한 디바이스 컨텍스트

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// 클라이언트 사각형에서 아이콘을 가운데에 맞춥니다.
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// 아이콘을 그립니다.
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

// 사용자가 최소화된 창을 끄는 동안에 커서가 표시되도록 시스템에서
//  이 함수를 호출합니다.
HCURSOR CNetServerDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}

LRESULT CNetServerDlg::OnAccept(WPARAM, LPARAM lParam)
{
	if (m_pClientSocket == NULL)
	{
		SetWindowText(_T("NetServer - 클라이언트 연결됨"));
		m_pClientSocket = (CServiceASyncSocket*)lParam;
	} else
		AfxMessageBox(_T("이미 연결된 소켓이 있음"));

	return NULL;
}

LRESULT CNetServerDlg::OnClose(WPARAM, LPARAM lParam)
{
	if (m_pClientSocket == (CServiceASyncSocket*)lParam)
	{
		SetWindowText(_T("NetServer - 클라이언트 연결 종료됨"));
		m_pClientSocket = NULL;
	} else if (m_pClientSocket == NULL)
		AfxMessageBox(_T("메인 윈도우에 할당된 소켓이 없음"));
	else
		AfxMessageBox(_T("메인 윈도우와 다른 소켓이 종료되었음"));

	return NULL;
}

LRESULT CNetServerDlg::OnReceiveData(WPARAM, LPARAM lParam)
{
	UpdateData(TRUE);
	CString s = *((CString*)lParam);
	m_strReceiveData = s;
	UpdateData(FALSE);
	return NULL;
}

void CNetServerDlg::OnBnClickedBtnSend()
{
	if (m_pClientSocket != NULL)
	{
		UpdateData(TRUE);
		m_pClientSocket->Send(m_strSendData, m_strSendData.GetLength() * sizeof(TCHAR));
		m_pClientSocket->Finish(true, 0);
	} else {
		AfxMessageBox(_T("연결된 소켓이 없어 보낼 수 없음"));
	}
}
