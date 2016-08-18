// ServiceASyncSocket.cpp : implementation file
//

#include "stdafx.h"
#include "../../NetServer.h"
#include "ServiceASyncSocket.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

#define MAX_SERVICE_ASYNC_SOCKET_IO_BUFFER_SIZE							(512)
/////////////////////////////////////////////////////////////////////////////
// CServiceASyncSocket

CServiceASyncSocket::CServiceASyncSocket()
	: m_hMainViewWnd(NULL)
	, m_sockHandle(INVALID_SOCK_HANDLE)
{
}

CServiceASyncSocket::~CServiceASyncSocket()
{
	::SendMessage(m_hMainViewWnd, WM_USER + 101, NULL, (LPARAM)this);
}


// Do not edit the following lines, which are needed by ClassWizard.
#if 0
BEGIN_MESSAGE_MAP(CServiceASyncSocket, CAsyncSocketEx)
	//{{AFX_MSG_MAP(CServiceASyncSocket)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()
#endif	// 0

/////////////////////////////////////////////////////////////////////////////
// CServiceASyncSocket member functions

void CServiceASyncSocket::OnConnect(int nErrorCode)
{
}

void CServiceASyncSocket::OnReceive(int nErrorCode) 
{
	// 수신된 데이터를 읽어들인다.
	BYTE buffer[MAX_SERVICE_ASYNC_SOCKET_IO_BUFFER_SIZE + 1];
	ZeroMemory(buffer, MAX_SERVICE_ASYNC_SOCKET_IO_BUFFER_SIZE + 1);
	int nReceiveLength = Receive(buffer, MAX_SERVICE_ASYNC_SOCKET_IO_BUFFER_SIZE);

	if (nReceiveLength == SOCKET_ERROR)
	{
		// 에러에 대한 원인을 구한다.
		int nLastError = WSAGetLastError();

		if (nLastError != WSAEWOULDBLOCK)
		{
			if (nLastError == WSAECONNRESET)
				Finish(false/* 소켓 작업 실패 */, 1/* Remote User에 의한 전송 취소 */);
			else
				Finish(false/* 소켓 작업 실패 */, 0/* 다운로드 실패 */);
		}

		return;
	}

	std::copy(buffer, buffer + nReceiveLength, std::back_inserter(m_RemainBuffer));

	// 수신된 데이터를 메인 윈도우로 보낸다.
	CString strReceiveData;
	for (std::vector<BYTE>::iterator it = m_RemainBuffer.begin(); it != m_RemainBuffer.end(); ++it)
		strReceiveData += (TCHAR)(*it);
	::SendMessage(m_hMainViewWnd, WM_USER + 102, NULL, (LPARAM)&strReceiveData);

	AsyncSelect(FD_READ|FD_CLOSE);
}

void CServiceASyncSocket::OnClose(int nErrorCode) 
{
	// 다운로드 종료 처리를 수행합니다.
	// 서버에서 소켓이 먼저 닫힌 경우에는 다운로드 실패한 것으로 본다.
	Finish(false/* 소켓 작업 실패 */, 1/* Remote User에 의한 전송 취소 */);
}

void CServiceASyncSocket::Finish(const bool isSucceeded, const int isWhoCancel)
{
	ASSERT(isWhoCancel >= -1 && isWhoCancel <= 1);

	ShutDown(0x01/* SD_SEND */);
	Close();

	// 소켓을 삭제한다.
	delete this;
}

void CServiceASyncSocket::SetValues(const HWND hMainView)
{
	ASSERT(hMainView != NULL);
	m_hMainViewWnd = hMainView;
}

const SOCK_HANDLE CServiceASyncSocket::GetSocketHandle() const
{
	ASSERT(m_sockHandle != INVALID_SOCK_HANDLE);
	ASSERT(m_sockHandle >= MIN_SOCK_HANDLE_VALUE && m_sockHandle <= MAX_SOCK_HANDLE_VALUE);
	return m_sockHandle;
}

void CServiceASyncSocket::SetSocketHandle(const SOCK_HANDLE sockHandle)
{
	ASSERT(sockHandle != INVALID_SOCK_HANDLE);
	ASSERT(sockHandle >= MIN_SOCK_HANDLE_VALUE && sockHandle <= MAX_SOCK_HANDLE_VALUE);
	m_sockHandle = sockHandle;
}
