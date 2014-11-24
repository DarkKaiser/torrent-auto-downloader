#####################################################################################################
## vocabulary_v3.db
#####################################################################################################

-- 공통코드 테이블
CREATE TABLE TBL_CODE (
    IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,		-- 일련번호
	CODE_GRP_ID				VARCHAR(3),												-- 코드그룹 ID
	CODE_ID					VARCHAR(2),												-- 코드 ID
	CODE_NAME				VARCHAR(50),											-- 코드 이름
	CODE_DESCRIPTION		VARCHAR(50),											-- 코드 설명
	CODE_ORDER				INTEGER NOT NULL,										-- 코드 순서
	USE_YN					VARCHAR(1)												-- 코드 사용 여부
);

CREATE UNIQUE INDEX TBL_CODE_INDEX01
                 ON TBL_CODE(IDX);

CREATE INDEX TBL_CODE_INDEX02
          ON TBL_CODE(CODE_GRP_ID, CODE_ID);

-- 공통코드 테이블 데이터(품사)
INSERT INTO TBL_CODE VALUES(  1, 'W01', '01',     '명사', '', 0, 'Y' );
INSERT INTO TBL_CODE VALUES(  2, 'W01', '02',   '대명사', '', 1, 'Y' );
INSERT INTO TBL_CODE VALUES(  3, 'W01', '03',     '동사', '', 2, 'Y' );
INSERT INTO TBL_CODE VALUES(  4, 'W01', '04',     '조사', '', 3, 'Y' );
INSERT INTO TBL_CODE VALUES(  5, 'W01', '05',   '형용사', '', 4, 'Y' );
INSERT INTO TBL_CODE VALUES(  6, 'W01', '06',     '접사', '', 5, 'Y' );
INSERT INTO TBL_CODE VALUES(  7, 'W01', '07',     '부사', '', 6, 'Y' );
INSERT INTO TBL_CODE VALUES(  8, 'W01', '08',   '감동사', '', 7, 'Y' );
INSERT INTO TBL_CODE VALUES(  9, 'W01', '09', '형용동사', '', 8, 'Y' );
INSERT INTO TBL_CODE VALUES( 10, 'W01', '10',     '기타', '', 9, 'Y' );

-- 공통코드 테이블 데이터(JLPT 등급)
INSERT INTO TBL_CODE VALUES( 11, 'J01', '01',     'N1', '', 0, 'Y' );
INSERT INTO TBL_CODE VALUES( 12, 'J01', '02',     'N2', '', 1, 'Y' );
INSERT INTO TBL_CODE VALUES( 13, 'J01', '03',     'N3', '', 2, 'Y' );
INSERT INTO TBL_CODE VALUES( 14, 'J01', '04',     'N4', '', 3, 'Y' );
INSERT INTO TBL_CODE VALUES( 15, 'J01', '05',     'N5', '', 4, 'Y' );
INSERT INTO TBL_CODE VALUES( 16, 'J01', '99', '미분류', '', 5, 'Y' );


-- 한자 테이블
CREATE TABLE TBL_HANJA (
    IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,		-- 일련번호
    CHARACTER               VARCHAR(10),											-- 한자
    SOUND_READ              VARCHAR(50),											-- 음독
    MEAN_READ               VARCHAR(50),											-- 훈독
    TRANSLATION             TEXT													-- 뜻
);

CREATE UNIQUE INDEX TBL_HANJA_INDEX01 
                 ON TBL_HANJA(IDX);

CREATE INDEX TBL_HANJA_INDEX02
          ON TBL_HANJA(CHARACTER);


-- 단어 테이블
CREATE TABLE TBL_VOCABULARY (
    IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,		-- 일련번호
    VOCABULARY              VARCHAR(50),											-- 단어
    VOCABULARY_GANA         VARCHAR(50),											-- 히라가나/가타가나
    VOCABULARY_TRANSLATION  TEXT,													-- 단어 뜻
    INPUT_DATE       		INTEGER,												-- 등록 일자
	USE_YN					VARCHAR(1)												-- 사용 여부
);

CREATE UNIQUE INDEX TBL_VOCABULARY_INDEX01 
                 ON TBL_VOCABULARY(IDX);

				 
-- 예문 테이블
CREATE TABLE TBL_VOCABULARY_EXAMPLE (
    IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,		-- 일련번호
    VOCABULARY              TEXT,													-- 예문
    VOCABULARY_TRANSLATION  TEXT,													-- 예문 뜻
	USE_YN					VARCHAR(1)												-- 사용 여부
);

CREATE UNIQUE INDEX TBL_VOCABULARY_EXAMPLE_INDEX01 
                 ON TBL_VOCABULARY_EXAMPLE(IDX);


-- 단어/예문 매핑 테이블
CREATE TABLE TBL_VOCABULARY_EXAMPLE_MAPP (
    IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,		-- 일련번호
    V_IDX                   INTEGER NOT NULL,										-- TBL_VOCABULARY 일련번호(FK)
    E_IDX                   INTEGER NOT NULL										-- TBL_VOCABULARY_EXAMPLE 일련번호(FK)
);

CREATE UNIQUE INDEX TBL_VOCABULARY_EXAMPLE_MAPP_INDEX01 
                 ON TBL_VOCABULARY_EXAMPLE_MAPP(IDX);

CREATE INDEX TBL_VOCABULARY_EXAMPLE_MAPP_INDEX02
                 ON TBL_VOCABULARY_EXAMPLE_MAPP(V_IDX);


-- 단어/품사 매핑 테이블
CREATE TABLE TBL_VOCABULARY_WORD_CLASS_MAPP (
    IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,		-- 일련번호
    V_IDX                   INTEGER NOT NULL,										-- TBL_VOCABULARY 일련번호(FK)
    CODE_ID                 VARCHAR(2)												-- TBL_CODE 코드ID
);

CREATE UNIQUE INDEX TBL_VOCABULARY_WORD_CLASS_MAPP_INDEX01 
                 ON TBL_VOCABULARY_WORD_CLASS_MAPP(IDX);

CREATE INDEX TBL_VOCABULARY_WORD_CLASS_MAPP_INDEX02
                 ON TBL_VOCABULARY_WORD_CLASS_MAPP(V_IDX);

				 
-- 단어/JLPT등급 매핑 테이블
CREATE TABLE TBL_VOCABULARY_JLPT_CLASS_MAPP (
    IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,		-- 일련번호
    V_IDX                   INTEGER NOT NULL,										-- TBL_VOCABULARY 일련번호(FK)
    CODE_ID                 VARCHAR(2)												-- TBL_CODE 코드ID
);

CREATE UNIQUE INDEX TBL_VOCABULARY_JLPT_CLASS_MAPP_INDEX01 
                 ON TBL_VOCABULARY_JLPT_CLASS_MAPP(IDX);

CREATE INDEX TBL_VOCABULARY_JLPT_CLASS_MAPP_INDEX02
                 ON TBL_VOCABULARY_JLPT_CLASS_MAPP(V_IDX);


#####################################################################################################
## vocabulary_user_v3.db
#####################################################################################################

-- TBL_USER_VOCABULARY 테이블
CREATE TABLE TBL_USER_VOCABULARY (
    V_IDX                       INTEGER PRIMARY KEY NOT NULL UNIQUE,				-- TBL_VOCABULARY 테이블 일련번호(FK)
    MEMORIZE_TARGET             INTEGER DEFAULT (0),								-- 암기대상 여부
    MEMORIZE_COMPLETED          INTEGER DEFAULT (0),								-- 암기완료 여부
    MEMORIZE_COMPLETED_COUNT    INTEGER DEFAULT (0)									-- 암기완료 횟수
);

CREATE UNIQUE INDEX TBL_USER_VOCABULARY_INDEX01
                 ON TBL_USER_VOCABULARY(V_IDX);
