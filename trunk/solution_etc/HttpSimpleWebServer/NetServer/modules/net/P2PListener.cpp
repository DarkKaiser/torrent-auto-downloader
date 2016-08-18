// P2PListener.cpp : implementation file
//

#include "stdafx.h"
#include "../../NetServer.h"
#include "P2PListener.h"

#include "ServiceASyncSocket.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CP2PListener

CP2PListener::CP2PListener() : m_hMainView(NULL)
{
}

// Do not edit the following lines, which are needed by ClassWizard.
#if 0
BEGIN_MESSAGE_MAP(CP2PListener, CAsyncSocketEx)
	//{{AFX_MSG_MAP(CP2PListener)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()
#endif	// 0

/////////////////////////////////////////////////////////////////////////////
// CP2PListener member functions

void CP2PListener::OnAccept(int nErrorCode) 
{
	ASSERT(m_hMainView != NULL);
	CServiceASyncSocket* pASyncSocket = new CServiceASyncSocket();
	pASyncSocket->SetValues(m_hMainView);

	if (Accept(*pASyncSocket) == TRUE)
	{
		// 소켓의 이벤트를 설정합니다.
		pASyncSocket->AsyncSelect(FD_READ|FD_CLOSE);

		::SendMessage(m_hMainView, WM_USER + 100, NULL, (LPARAM)pASyncSocket);
	} else {
		delete pASyncSocket;
	}
}

void CP2PListener::SetValues(const HWND hMainView)
{
	ASSERT(hMainView != NULL);
	m_hMainView = const_cast<HWND>(hMainView);
}
