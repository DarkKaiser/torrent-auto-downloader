
// NetServerDlg.h : ��� ����
//

#pragma once

class CServiceASyncSocket;

// CNetServerDlg ��ȭ ����
class CNetServerDlg : public CDialog
{
// �����Դϴ�.
public:
	CNetServerDlg(CWnd* pParent = NULL);	// ǥ�� �������Դϴ�.

// ��ȭ ���� �������Դϴ�.
	enum { IDD = IDD_NETSERVER_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV �����Դϴ�.

// �����Դϴ�.
protected:
	HICON m_hIcon;

	CString m_strSendData;
	CString m_strReceiveData;
	CP2PListener m_p2pListener;
	CServiceASyncSocket* m_pClientSocket;

	// ������ �޽��� �� �Լ�
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
