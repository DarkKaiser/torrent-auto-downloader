#####################################################################################################
## jv2.db
#####################################################################################################

-- TBL_HANJA 테이블
CREATE TABLE TBL_HANJA (
    IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,
    CHARACTER               VARCHAR(10),
    SOUND_READ              VARCHAR(50),
    MEAN_READ               VARCHAR(50),
    JLPT_CLASS              INTEGER DEFAULT (99),
    TRANSLATION             TEXT
);

CREATE UNIQUE INDEX TBL_HANJA_INDEX01 
                 ON TBL_HANJA(IDX);

CREATE INDEX TBL_HANJA_INDEX02
          ON TBL_HANJA(CHARACTER);

CREATE INDEX TBL_HANJA_INDEX03
          ON TBL_HANJA(JLPT_CLASS);

-- TBL_VOCABULARY 테이블
CREATE TABLE TBL_VOCABULARY (
    IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,
    VOCABULARY              VARCHAR(50),
    VOCABULARY_GANA         VARCHAR(50),
    VOCABULARY_TRANSLATION  TEXT,
    REGISTRATION_DATE       INTEGER,
    PARTS_OF_SPEECH         INTEGER DEFAULT (99)
);

CREATE UNIQUE INDEX TBL_VOCABULARY_INDEX01 
                 ON TBL_VOCABULARY(IDX);

-- TBL_PARTS_OF_SPEECH 테이블
CREATE TABLE TBL_PARTS_OF_SPEECH (
    IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,
    NAME                    VARCHAR(50)
);

CREATE UNIQUE INDEX TBL_PARTS_OF_SPEECH_INDEX01 
                 ON TBL_PARTS_OF_SPEECH(IDX)

-- 동사, 형용사, 형용동사, 명사, 부사, 접속사, 조사, 조동사, 연체사, 감동사

#####################################################################################################
## jv2_user.db
#####################################################################################################

-- TBL_USER_VOCABULARY 테이블
CREATE TABLE TBL_USER_VOCABULARY (
    IDX                         INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,
    VOCABULARY_IDX              INTEGER,
    MEMORIZE_COMPLETED          INTEGER DEFAULT (0),
    MEMORIZE_TARGET             INTEGER DEFAULT (0),
    MEMORIZE_COMPLETED_COUNT    INTEGER DEFAULT (0)
};

CREATE UNIQUE INDEX TBL_USER_VOCABULARY_INDEX01 
                 ON TBL_USER_VOCABULARY(IDX);

CREATE UNIQUE INDEX TBL_USER_VOCABULARY_INDEX02 
                 ON TBL_USER_VOCABULARY(VOCABULARY_IDX);
