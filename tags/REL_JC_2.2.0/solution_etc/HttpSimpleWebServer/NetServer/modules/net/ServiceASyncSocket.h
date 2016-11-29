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
	HWND m_hMainViewWnd;								// CMainView 윈도우 핸들
	SOCK_HANDLE m_sockHandle;							// 소켓 핸들
	std::vector<BYTE> m_RemainBuffer;					// 패킷 데이터가 잘려올 경우를 대비한 이전에 처리하지 못한 패킷 데이터

// Operations
public:
	void SetValues(const HWND hMainView);

	// 소켓의 핸들을 반환합니다.
	const SOCK_HANDLE GetSocketHandle() const;

protected:
	// 소켓의 핸들을 설정합니다.
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
	// 쪽지 & 파일 전송 완료 작업을 수행합니다.
	//	 isSucceeded	: 쪽지 & 파일 전송 완료 작업 성공 여부
	//	 isWhoCancel	:   1 : Remote User에 의한 전송 취소
	//						0 : 다운로드 실패
	//					   -1 : Local User에 의한 전송 취소
	virtual void Finish(const bool isSucceeded, const int isWhoCancel);
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_SERVICEASYNCSOCKET_H__900FEB7B_2051_4459_8B60_C29C69885243__INCLUDED_)
