#if !defined(AFX_SOCKETS_H__866E4A7E_2BF6_4A89_A729_1CAC1178A508__INCLUDED_)
#define AFX_SOCKETS_H__866E4A7E_2BF6_4A89_A729_1CAC1178A508__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// SocketS.h : header file
//


/////////////////////////////////////////////////////////////////////////////
// CSocketEx command target

class CSocketEx : public CSocket
{
public:
	BOOL Create(UINT nSocketPort = 0, int nSocketType=SOCK_STREAM, long lEvent = FD_READ | FD_WRITE | FD_OOB | FD_ACCEPT | FD_CONNECT | FD_CLOSE, LPCTSTR lpszSocketAddress = NULL);

	BOOL Socket(int nSocketType=SOCK_STREAM, long lEvent = FD_READ | FD_WRITE | FD_OOB | FD_ACCEPT | FD_CONNECT | FD_CLOSE, int nProtocolType = 0, int nAddressFormat = PF_INET);

	BOOL Bind(UINT nSocketPort, LPCTSTR lpszSocketAddress = NULL);
	BOOL Bind(const SOCKADDR* lpSockAddr, int nSockAddrLen)
		{ return (SOCKET_ERROR != bind(m_hSocket, lpSockAddr, nSockAddrLen)); }

	virtual int Receive(void* lpBuf, int nBufLen, int nFlags = 0);
	virtual int Send(const void* lpBuf, int nBufLen, int nFlags = 0);

	int SendChunk(const void* lpBuf, int nBufLen, int nFlags);
};

class CAsyncSocketEx : public CAsyncSocket
{
public:
	BOOL Create(UINT nSocketPort = 0, int nSocketType=SOCK_STREAM, long lEvent = FD_READ | FD_WRITE | FD_OOB | FD_ACCEPT | FD_CONNECT | FD_CLOSE, LPCTSTR lpszSocketAddress = NULL);

	BOOL Socket(int nSocketType=SOCK_STREAM, long lEvent = FD_READ | FD_WRITE | FD_OOB | FD_ACCEPT | FD_CONNECT | FD_CLOSE, int nProtocolType = 0, int nAddressFormat = PF_INET);

	BOOL Bind(UINT nSocketPort, LPCTSTR lpszSocketAddress = NULL);
	BOOL Bind(const SOCKADDR* lpSockAddr, int nSockAddrLen)
		{ return (SOCKET_ERROR != bind(m_hSocket, lpSockAddr, nSockAddrLen)); }

	virtual int Receive(void* lpBuf, int nBufLen, int nFlags = 0);
	virtual int Send(const void* lpBuf, int nBufLen, int nFlags = 0);
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_SOCKETS_H__866E4A7E_2BF6_4A89_A729_1CAC1178A508__INCLUDED_)
