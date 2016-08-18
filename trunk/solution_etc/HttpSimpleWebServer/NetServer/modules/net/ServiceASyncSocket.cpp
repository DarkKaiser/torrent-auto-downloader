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
	// ���ŵ� �����͸� �о���δ�.
	BYTE buffer[MAX_SERVICE_ASYNC_SOCKET_IO_BUFFER_SIZE + 1];
	ZeroMemory(buffer, MAX_SERVICE_ASYNC_SOCKET_IO_BUFFER_SIZE + 1);
	int nReceiveLength = Receive(buffer, MAX_SERVICE_ASYNC_SOCKET_IO_BUFFER_SIZE);

	if (nReceiveLength == SOCKET_ERROR)
	{
		// ������ ���� ������ ���Ѵ�.
		int nLastError = WSAGetLastError();

		if (nLastError != WSAEWOULDBLOCK)
		{
			if (nLastError == WSAECONNRESET)
				Finish(false/* ���� �۾� ���� */, 1/* Remote User�� ���� ���� ��� */);
			else
				Finish(false/* ���� �۾� ���� */, 0/* �ٿ�ε� ���� */);
		}

		return;
	}

	std::copy(buffer, buffer + nReceiveLength, std::back_inserter(m_RemainBuffer));

	// ���ŵ� �����͸� ���� ������� ������.
	CString strReceiveData;
	for (std::vector<BYTE>::iterator it = m_RemainBuffer.begin(); it != m_RemainBuffer.end(); ++it)
		strReceiveData += (TCHAR)(*it);
	::SendMessage(m_hMainViewWnd, WM_USER + 102, NULL, (LPARAM)&strReceiveData);

	AsyncSelect(FD_READ|FD_CLOSE);
}

void CServiceASyncSocket::OnClose(int nErrorCode) 
{
	// �ٿ�ε� ���� ó���� �����մϴ�.
	// �������� ������ ���� ���� ��쿡�� �ٿ�ε� ������ ������ ����.
	Finish(false/* ���� �۾� ���� */, 1/* Remote User�� ���� ���� ��� */);
}

void CServiceASyncSocket::Finish(const bool isSucceeded, const int isWhoCancel)
{
	ASSERT(isWhoCancel >= -1 && isWhoCancel <= 1);

	ShutDown(0x01/* SD_SEND */);
	Close();

	// ������ �����Ѵ�.
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
