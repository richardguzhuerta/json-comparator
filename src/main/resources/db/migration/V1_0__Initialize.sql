
CREATE TABLE DIFF_RECORD (
  REQUEST_ID VARCHAR NOT NULL PRIMARY KEY,
  LEFT_FILE CLOB,
  RIGHT_FILE CLOB,
  COMPARISON_RESULT CLOB
);