/* 퇴사정보 */
DROP TABLE retirement CASCADE CONSTRAINTS;

/* 퇴사정보 */
CREATE TABLE retirement (
	EMPNO CHAR(10)  NOT NULL, /* 사원번호 */
	RETIREDAY DATE, /* 퇴사일 */
	REASON VARCHAR2(50), /* 퇴직사유 */
	STATE VARCHAR2(20) DEFAULT '신청' /* 상태 */
);

ALTER TABLE retirement ADD CHECK (state IN ('신청','승인','퇴사','취소'));
ALTER TABLE retirement ADD CONSTRAINT FK_Employee_TO_retirement FOREIGN KEY ( EMPNO ) REFERENCES Employee ( EMPNO );


SELECT * FROM retirement;
--퇴사 신청하기 (내거, 남의거)
INSERT INTO retirement(empno,RETIREDAY,REASON) VALUES (2022301022,to_date('2022-12-31','yyyy-mm-dd'),'자진퇴사');
INSERT INTO retirement VALUES (2022501028,to_date('2021-06-30','yyyy-mm-dd'),'계약만료','퇴사');
DELETE retirement WHERE empno=2022101001;

--퇴사 신청자 보기 (신청)
SELECT * FROM RETIREMENT r WHERE state = '신청';
--퇴사 신청자 보기 (모두)(연도별)
SELECT * FROM retirement WHERE to_char(retireday,'yyyy') LIKE '%'||'2021'||'%';
--퇴사 신청자 보기(이름검색)
SELECT * FROM retirement r, EMPLOYEE e  WHERE r.EMPNO =e.EMPNO AND to_char(retireday,'yyyy') like '%'||'20'||'%' AND name like '%'||'고비빔'||'%' ;
--내 퇴사신청 상태 보기
SELECT * FROM RETIREMENT r WHERE empno = '내번호';
--퇴사 승인하기(신청-승인-퇴사)
UPDATE RETIREMENT SET STATE = '승인' WHERE EMPNO ='2022501028';
--날짜 읽어와서 '승인'중에 그날 맞는 애들은 저절로 퇴사로 바꾸게
UPDATE RETIREMENT SET STATE = '퇴사' WHERE EMPNO ='2022501028';
