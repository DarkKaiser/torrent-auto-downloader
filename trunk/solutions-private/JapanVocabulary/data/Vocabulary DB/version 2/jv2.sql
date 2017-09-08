﻿#####################################################################################################
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
    REGISTRATION_DATE       INTEGER
);

CREATE UNIQUE INDEX TBL_VOCABULARY_INDEX01 
                 ON TBL_VOCABULARY(IDX);

-- TBL_VOCABULARY_EXAMPLE 테이블
CREATE TABLE TBL_VOCABULARY_EXAMPLE (
    IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,
    V_IDX                   INTEGER NOT NULL,
    VOCABULARY              TEXT,
    VOCABULARY_TRANSLATION  TEXT
);

CREATE UNIQUE INDEX TBL_VOCABULARY_EXAMPLE_INDEX01 
                 ON TBL_VOCABULARY_EXAMPLE(IDX);

CREATE INDEX TBL_VOCABULARY_EXAMPLE_INDEX02
                 ON TBL_VOCABULARY_EXAMPLE(V_IDX);

#####################################################################################################
## jv2_user.db
#####################################################################################################

-- TBL_USER_VOCABULARY 테이블
CREATE TABLE TBL_USER_VOCABULARY (
    V_IDX                       INTEGER PRIMARY KEY NOT NULL UNIQUE,
    MEMORIZE_TARGET             INTEGER DEFAULT (0),
    MEMORIZE_COMPLETED          INTEGER DEFAULT (0),
    MEMORIZE_COMPLETED_COUNT    INTEGER DEFAULT (0)
);

CREATE UNIQUE INDEX TBL_USER_VOCABULARY_INDEX01 
                 ON TBL_USER_VOCABULARY(V_IDX);