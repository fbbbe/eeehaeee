-- 동그리 오답노트 v1 스키마
-- 파일형 DB(SQLite)용. 재실행 안전하게 DROP부터.
PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS QUIZ_ITEM;
DROP TABLE IF EXISTS QUIZ_SESSION;
DROP TABLE IF EXISTS STATS_PAIR;
DROP TABLE IF EXISTS CONCEPT_PAIR;
DROP TABLE IF EXISTS NOTE;
DROP TABLE IF EXISTS FOLDER;

-- 1) 폴더: 노트 분류
CREATE TABLE FOLDER (
  folder_id   INTEGER PRIMARY KEY AUTOINCREMENT, -- 폴더 고유번호
  name        TEXT NOT NULL,                     -- 폴더명
  created_at  TEXT DEFAULT (datetime('now'))     -- 생성시각(ISO8601)
);

-- 2) 노트(일반/개념)
CREATE TABLE NOTE (
  note_id     INTEGER PRIMARY KEY AUTOINCREMENT, -- 노트 고유번호
  folder_id   INTEGER NOT NULL,                  -- 소속 폴더
  title       TEXT NOT NULL,                     -- 제목
  type        TEXT NOT NULL CHECK (type IN ('GENERAL','CONCEPT')), -- 노트 종류
  created_at  TEXT DEFAULT (datetime('now')),    -- 생성시각
  updated_at  TEXT,                              -- 수정시각
  FOREIGN KEY (folder_id) REFERENCES FOLDER(folder_id) ON DELETE CASCADE
);

CREATE INDEX idx_note_folder ON NOTE(folder_id);
CREATE INDEX idx_note_title  ON NOTE(title);

-- 3) 개념-설명 쌍(개념 노트 전용)
CREATE TABLE CONCEPT_PAIR (
  pair_id     INTEGER PRIMARY KEY AUTOINCREMENT, -- 개념쌍 고유번호
  note_id     INTEGER NOT NULL,                  -- 소속 노트
  term        TEXT NOT NULL,                     -- 개념(용어)
  definition  TEXT NOT NULL,                     -- 설명
  hint        TEXT,                              -- 힌트(선택)
  difficulty  INTEGER DEFAULT 1,                 -- 난이도(1~5 등)
  created_at  TEXT DEFAULT (datetime('now')),    -- 생성시각
  FOREIGN KEY (note_id) REFERENCES NOTE(note_id) ON DELETE CASCADE
);

CREATE INDEX idx_pair_note ON CONCEPT_PAIR(note_id);
CREATE INDEX idx_pair_term ON CONCEPT_PAIR(term);

-- 4) 시험 세션(1회 시험 기록)
CREATE TABLE QUIZ_SESSION (
  session_id    INTEGER PRIMARY KEY AUTOINCREMENT, -- 세션 고유번호
  started_at    TEXT,                              -- 시작시각
  ended_at      TEXT,                              -- 종료시각
  mode          TEXT,                              -- 모드: ALL/TOPN/FOLDER/NOTE 등
  timer_seconds INTEGER                            -- 제한시간(초)
);

-- 5) 시험 문항(세션 내 개별 문항 결과)
CREATE TABLE QUIZ_ITEM (
  item_id     INTEGER PRIMARY KEY AUTOINCREMENT, -- 문항 고유번호
  session_id  INTEGER NOT NULL,                  -- 소속 세션
  pair_id     INTEGER NOT NULL,                  -- 출제된 개념쌍
  user_answer TEXT,                              -- 내 답안
  is_correct  INTEGER NOT NULL,                  -- 정답여부(1/0)
  elapsed_ms  INTEGER,                           -- 소요시간(ms)
  FOREIGN KEY (session_id) REFERENCES QUIZ_SESSION(session_id) ON DELETE CASCADE,
  FOREIGN KEY (pair_id)   REFERENCES CONCEPT_PAIR(pair_id)
);

-- 6) 개념별 누적 통계(오답률 계산용)
CREATE TABLE STATS_PAIR (
  pair_id       INTEGER PRIMARY KEY,             -- 개념쌍 고유번호(1:1)
  correct_count INTEGER NOT NULL DEFAULT 0,      -- 정답수
  wrong_count   INTEGER NOT NULL DEFAULT 0,      -- 오답수
  FOREIGN KEY (pair_id) REFERENCES CONCEPT_PAIR(pair_id) ON DELETE CASCADE
);

-- 간단 시드(발표 데모용): 폴더/노트/개념 2개
INSERT INTO FOLDER(name) VALUES ('자료구조'), ('운영체제');

INSERT INTO NOTE(folder_id, title, type) VALUES
  (1, '자료구조-핵심개념', 'CONCEPT'),
  (2, '운영체제-핵심개념', 'CONCEPT');

INSERT INTO CONCEPT_PAIR(note_id, term, definition, hint, difficulty) VALUES
  (1, '스택(Stack)', 'LIFO(후입선출) 구조의 선형 자료구조', 'push/pop/peek', 1),
  (1, '큐(Queue)', 'FIFO(선입선출) 구조의 선형 자료구조', 'enqueue/dequeue', 1);

-- STATS_PAIR은 앱에서 시험 후 갱신(초깃값 0)
