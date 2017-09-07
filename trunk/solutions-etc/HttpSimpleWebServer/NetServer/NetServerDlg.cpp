
// NetServerDlg.cpp : ���� ����
//

#include "stdafx.h"
#include "NetServer.h"
#include "NetServerDlg.h"

#include "./modules/net/ServiceASyncSocket.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CNetServerDlg ��ȭ ����




CNetServerDlg::CNetServerDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CNetServerDlg::IDD, pParent)
	, m_pClientSocket(NULL)
	, m_strReceiveData(_T(""))
	, m_strSendData(_T("HTTP/1.1 200 OK\r\n\r\n���ŵ� �޽����Դϴ�."))
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


// CNetServerDlg �޽��� ó����

BOOL CNetServerDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// �� ��ȭ ������ �������� �����մϴ�. ���� ���α׷��� �� â�� ��ȭ ���ڰ� �ƴ� ��쿡��
	//  �����ӿ�ũ�� �� �۾��� �ڵ����� �����մϴ�.
	SetIcon(m_hIcon, TRUE);			// ū �������� �����մϴ�.
	SetIcon(m_hIcon, FALSE);		// ���� �������� �����մϴ�.

	// ������ �����Ѵ�.
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

	return TRUE;  // ��Ŀ���� ��Ʈ�ѿ� �������� ������ TRUE�� ��ȯ�մϴ�.
}

void CNetServerDlg::OnDestroy()
{
	CDialog::OnDestroy();

	m_p2pListener.Close();
}

// ��ȭ ���ڿ� �ּ�ȭ ���߸� �߰��� ��� �������� �׸�����
//  �Ʒ� �ڵ尡 �ʿ��մϴ�. ����/�� ���� ����ϴ� MFC ���� ���α׷��� ��쿡��
//  �����ӿ�ũ���� �� �۾��� �ڵ����� �����մϴ�.

void CNetServerDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // �׸��⸦ ���� ����̽� ���ؽ�Ʈ

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Ŭ���̾�Ʈ �簢������ �������� ����� ����ϴ�.
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// �������� �׸��ϴ�.
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

// ����ڰ� �ּ�ȭ�� â�� ���� ���ȿ� Ŀ���� ǥ�õǵ��� �ý��ۿ���
//  �� �Լ��� ȣ���մϴ�.
HCURSOR CNetServerDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}

LRESULT CNetServerDlg::OnAccept(WPARAM, LPARAM lParam)
{
	if (m_pClientSocket == NULL)
	{
		SetWindowText(_T("NetServer - Ŭ���̾�Ʈ �����"));
		m_pClientSocket = (CServiceASyncSocket*)lParam;
	} else
		AfxMessageBox(_T("�̹� ����� ������ ����"));

	return NULL;
}

LRESULT CNetServerDlg::OnClose(WPARAM, LPARAM lParam)
{
	if (m_pClientSocket == (CServiceASyncSocket*)lParam)
	{
		SetWindowText(_T("NetServer - Ŭ���̾�Ʈ ���� �����"));
		m_pClientSocket = NULL;
	} else if (m_pClientSocket == NULL)
		AfxMessageBox(_T("���� �����쿡 �Ҵ�� ������ ����"));
	else
		AfxMessageBox(_T("���� ������� �ٸ� ������ ����Ǿ���"));

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
		AfxMessageBox(_T("����� ������ ���� ���� �� ����"));
	}
}
