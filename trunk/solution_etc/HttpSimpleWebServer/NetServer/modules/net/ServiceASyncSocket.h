#if !defined(AFX_SERVICEASYNCSOCKET_H__900FEB7B_2051_4459_8B60_C29C69885243__INCLUDED_)
#define AFX_SERVICEASYNCSOCKET_H__900FEB7B_2051_4459_8B60_C29C69885243__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// ServiceASyncSocket.h : header file
//

#include "SocketS.h"

/////////////////////////////////////////////////////////////////////////////
// CServiceASyncSocket command target

class CServiceASyncSocket : public CAsyncSocketEx
{
public:
	CServiceASyncSocket();
	virtual ~CServiceASyncSocket();

// Attributes
protected:
	HWND m_hMainViewWnd;								// CMainView ������ �ڵ�
	SOCK_HANDLE m_sockHandle;							// ���� �ڵ�
	std::vector<BYTE> m_RemainBuffer;					// ��Ŷ �����Ͱ� �߷��� ��츦 ����� ������ ó������ ���� ��Ŷ ������

// Operations
public:
	void SetValues(const HWND hMainView);

	// ������ �ڵ��� ��ȯ�մϴ�.
	const SOCK_HANDLE GetSocketHandle() const;

protected:
	// ������ �ڵ��� �����մϴ�.
	void SetSocketHandle(const SOCK_HANDLE sockHandle);

// Overrides
public:
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CServiceASyncSocket)
	public:
	virtual void OnReceive(int nErrorCode);
	virtual void OnClose(int nErrorCode);
	virtual void OnConnect(int nErrorCode);
	//}}AFX_VIRTUAL

	// Generated message map functions
	//{{AFX_MSG(CServiceASyncSocket)
		// NOTE - the ClassWizard will add and remove member functions here.
	//}}AFX_MSG

// Implementation
public:
	// ���� & ���� ���� �Ϸ� �۾��� �����մϴ�.
	//	 isSucceeded	: ���� & ���� ���� �Ϸ� �۾� ���� ����
	//	 isWhoCancel	:   1 : Remote User�� ���� ���� ���
	//						0 : �ٿ�ε� ����
	//					   -1 : Local User�� ���� ���� ���
	virtual void Finish(const bool isSucceeded, const int isWhoCancel);
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_SERVICEASYNCSOCKET_H__900FEB7B_2051_4459_8B60_C29C69885243__INCLUDED_)
