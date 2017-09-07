#if !defined(AFX_P2PLISTENER_H__755F1A97_2504_402C_9CA8_5D1A6A753E88__INCLUDED_)
#define AFX_P2PLISTENER_H__755F1A97_2504_402C_9CA8_5D1A6A753E88__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// P2PListener.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CP2PListener command target

class CP2PListener : public CAsyncSocketEx
{
public:
	CP2PListener();

// Attributes
protected:
	HWND m_hMainView;						// 메인뷰 윈도우 핸들

// Operations
public:
	void SetValues(const HWND hMainView);

// Overrides
public:
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CP2PListener)
	public:
	virtual void OnAccept(int nErrorCode);
	//}}AFX_VIRTUAL

	// Generated message map functions
	//{{AFX_MSG(CP2PListener)
		// NOTE - the ClassWizard will add and remove member functions here.
	//}}AFX_MSG

// Implementation
protected:

};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_P2PLISTENER_H__755F1A97_2504_402C_9CA8_5D1A6A753E88__INCLUDED_)
