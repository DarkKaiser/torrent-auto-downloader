
// NetServer.h : PROJECT_NAME ���� ���α׷��� ���� �� ��� �����Դϴ�.
//

#pragma once

#ifndef __AFXWIN_H__
	#error "PCH�� ���� �� ������ �����ϱ� ���� 'stdafx.h'�� �����մϴ�."
#endif

#include "resource.h"		// �� ��ȣ�Դϴ�.


// CNetServerApp:
// �� Ŭ������ ������ ���ؼ��� NetServer.cpp�� �����Ͻʽÿ�.
//

class CNetServerApp : public CWinAppEx
{
public:
	CNetServerApp();

// �������Դϴ�.
	public:
	virtual BOOL InitInstance();

// �����Դϴ�.

	DECLARE_MESSAGE_MAP()
};

extern CNetServerApp theApp;