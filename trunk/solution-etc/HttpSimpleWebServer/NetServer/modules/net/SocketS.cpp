// SocketS.cpp : implementation file
//

#include "stdafx.h"
#include "SocketS.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CSocketEx

// Do not edit the following lines, which are needed by ClassWizard.
#if 0
BEGIN_MESSAGE_MAP(CSocketEx, CSocket)
	//{{AFX_MSG_MAP(CIOTimeOutSocket)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()
#endif	// 0

BOOL CSocketEx::Create(UINT nSocketPort/* = 0*/, int nSocketType/*=SOCK_STREAM*/, long lEvent/* = FD_READ | FD_WRITE | FD_OOB | FD_ACCEPT | FD_CONNECT | FD_CLOSE*/, LPCTSTR lpszSocketAddress/* = NULL*/)
{
	if (CSocketEx::Socket(nSocketType, lEvent))
	{
		if (CSocketEx::Bind(nSocketPort,lpszSocketAddress))
			return TRUE;
		int nResult = GetLastError();
		Close();
		WSASetLastError(nResult);
	}
	return FALSE;
}

BOOL CSocketEx::Socket(int nSocketType/*=SOCK_STREAM*/, long lEvent/* = FD_READ | FD_WRITE | FD_OOB | FD_ACCEPT | FD_CONNECT | FD_CLOSE*/, int nProtocolType/* = 0*/, int nAddressFormat/* = PF_INET*/)
{
	ASSERT(m_hSocket == INVALID_SOCKET);

	m_hSocket = socket(nAddressFormat,nSocketType,nProtocolType);
	if (m_hSocket != INVALID_SOCKET)
	{
		CAsyncSocket::AttachHandle(m_hSocket, this, FALSE);
		return AsyncSelect(lEvent);
	}
	return FALSE;
}

BOOL CSocketEx::Bind(UINT nSocketPort, LPCTSTR lpszSocketAddress/* = NULL*/)
{
	USES_CONVERSION_EX;

	SOCKADDR_IN sockAddr;
	memset(&sockAddr,0,sizeof(sockAddr));

	LPSTR lpszAscii;
	if (lpszSocketAddress != NULL)
	{
		lpszAscii = T2A_EX((LPTSTR)lpszSocketAddress, _ATL_SAFE_ALLOCA_DEF_THRESHOLD);
		if (lpszAscii == NULL)
		{
			// OUT OF MEMORY
			WSASetLastError(ERROR_NOT_ENOUGH_MEMORY);
			return FALSE;
		}
	}
	else
	{
		lpszAscii = NULL;
	}

	sockAddr.sin_family = AF_INET;

	if (lpszAscii == NULL)
		sockAddr.sin_addr.s_addr = htonl(INADDR_ANY);
	else
	{
		DWORD lResult = inet_addr(lpszAscii);
		if (lResult == INADDR_NONE)
		{
			WSASetLastError(WSAEINVAL);
			return FALSE;
		}
		sockAddr.sin_addr.s_addr = lResult;
	}

	sockAddr.sin_port = htons((u_short)nSocketPort);

	return CSocketEx::Bind((SOCKADDR*)&sockAddr, sizeof(sockAddr));
}

int CSocketEx::Receive(void* lpBuf, int nBufLen, int nFlags/* = 0*/)
{
	if (m_pbBlocking != NULL)
	{
		WSASetLastError(WSAEINPROGRESS);
		return  FALSE;
	}
	int nResult;
	while ((nResult = recv(m_hSocket, (LPSTR)lpBuf, nBufLen, nFlags)) == SOCKET_ERROR)
	{
		if (GetLastError() == WSAEWOULDBLOCK)
		{
			if (!PumpMessages(FD_READ))
				return SOCKET_ERROR;
		}
		else
			return SOCKET_ERROR;
	}
	return nResult;
}

int CSocketEx::Send(const void* lpBuf, int nBufLen, int nFlags/* = 0*/)
{
	if (m_pbBlocking != NULL)
	{
		WSASetLastError(WSAEINPROGRESS);
		return  FALSE;
	}

	int nLeft, nWritten;
	PBYTE pBuf = (PBYTE)lpBuf;
	nLeft = nBufLen;

	while (nLeft > 0)
	{
		nWritten = CSocketEx::SendChunk(pBuf, nLeft, nFlags);
		if (nWritten == SOCKET_ERROR)
			return nWritten;

		nLeft -= nWritten;
		pBuf += nWritten;
	}
	return nBufLen - nLeft;
}

int CSocketEx::SendChunk(const void* lpBuf, int nBufLen, int nFlags)
{
	int nResult;
	while ((nResult = send(m_hSocket, (LPSTR)lpBuf, nBufLen, nFlags)) == SOCKET_ERROR)
	{
		if (GetLastError() == WSAEWOULDBLOCK)
		{
			if (!PumpMessages(FD_WRITE))
				return SOCKET_ERROR;
		}
		else
			return SOCKET_ERROR;
	}
	return nResult;
}


/////////////////////////////////////////////////////////////////////////////
// CAsyncSocketEx

// Do not edit the following lines, which are needed by ClassWizard.
#if 0
BEGIN_MESSAGE_MAP(CAsyncSocketEx, CAsyncSocket)
	//{{AFX_MSG_MAP(CIOTimeOutSocket)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()
#endif	// 0


BOOL CAsyncSocketEx::Create(UINT nSocketPort/* = 0*/, int nSocketType/*=SOCK_STREAM*/, long lEvent/* = FD_READ | FD_WRITE | FD_OOB | FD_ACCEPT | FD_CONNECT | FD_CLOSE*/, LPCTSTR lpszSocketAddress/* = NULL*/)
{
	if (CAsyncSocketEx::Socket(nSocketType, lEvent))
	{
		if (CAsyncSocketEx::Bind(nSocketPort,lpszSocketAddress))
			return TRUE;
		int nResult = GetLastError();
		Close();
		WSASetLastError(nResult);
	}
	return FALSE;
}

BOOL CAsyncSocketEx::Socket(int nSocketType/*=SOCK_STREAM*/, long lEvent/* = FD_READ | FD_WRITE | FD_OOB | FD_ACCEPT | FD_CONNECT | FD_CLOSE*/, int nProtocolType/* = 0*/, int nAddressFormat/* = PF_INET*/)
{
	ASSERT(m_hSocket == INVALID_SOCKET);

	m_hSocket = socket(nAddressFormat,nSocketType,nProtocolType);
	if (m_hSocket != INVALID_SOCKET)
	{
		CAsyncSocket::AttachHandle(m_hSocket, this, FALSE);
		return AsyncSelect(lEvent);
	}
	return FALSE;
}

BOOL CAsyncSocketEx::Bind(UINT nSocketPort, LPCTSTR lpszSocketAddress/* = NULL*/)
{
	USES_CONVERSION_EX;

	SOCKADDR_IN sockAddr;
	memset(&sockAddr,0,sizeof(sockAddr));

	LPSTR lpszAscii;
	if (lpszSocketAddress != NULL)
	{
		lpszAscii = T2A_EX((LPTSTR)lpszSocketAddress, _ATL_SAFE_ALLOCA_DEF_THRESHOLD);
		if (lpszAscii == NULL)
		{
			// OUT OF MEMORY
			WSASetLastError(ERROR_NOT_ENOUGH_MEMORY);
			return FALSE;
		}
	}
	else
	{
		lpszAscii = NULL;
	}

	sockAddr.sin_family = AF_INET;

	if (lpszAscii == NULL)
		sockAddr.sin_addr.s_addr = htonl(INADDR_ANY);
	else
	{
		DWORD lResult = inet_addr(lpszAscii);
		if (lResult == INADDR_NONE)
		{
			WSASetLastError(WSAEINVAL);
			return FALSE;
		}
		sockAddr.sin_addr.s_addr = lResult;
	}

	sockAddr.sin_port = htons((u_short)nSocketPort);

	return CAsyncSocketEx::Bind((SOCKADDR*)&sockAddr, sizeof(sockAddr));
}

int CAsyncSocketEx::Receive(void* lpBuf, int nBufLen, int nFlags/* = 0*/)
{
	return recv(m_hSocket, (LPSTR)lpBuf, nBufLen, nFlags);
}

int CAsyncSocketEx::Send(const void* lpBuf, int nBufLen, int nFlags/* = 0*/)
{
	return send(m_hSocket, (LPSTR)lpBuf, nBufLen, nFlags);
}
