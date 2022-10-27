/* 복지 */
DROP TABLE benefit 
	CASCADE CONSTRAINTS;

/* 복지 */
CREATE TABLE benefit (
	listnumber NUMBER NOT NULL, /* 목록번호 */
	title VARCHAR2(50) NOT NULL, /* 복지명 */
	condi VARCHAR2(300), 
	reportDay DATE, /* 마감일 */
	cost NUMBER /* 비용 */
);
		
	/* 교육 */
DROP TABLE training 
	CASCADE CONSTRAINTS;

/* 교육 */
CREATE TABLE training (
	listnumber NUMBER NOT NULL, /* 목록번호 */
	Title VARCHAR2(50) NOT NULL, /* 교육명 */
	trainday DATE, /* 실행일 */
	type VARCHAR2(50), /* 분류 */
	maxAttendee NUMBER, /* 최대인원 */
	crtAttendee NUMBER, /* 신청인원 */
	lecturer VARCHAR2(50) /* 강사 */
);

ALTER TABLE training
	ADD
		CONSTRAINT PK_training
		PRIMARY KEY (
			listnumber
		);
		
	/* 신청자목록 */
DROP TABLE attendeeList 
	CASCADE CONSTRAINTS;

/* 신청자목록 */
CREATE TABLE attendeeList (
	listnumber NUMBER, /* 목록번호 */
	EMPNO CHAR(10)  /* 사원번호 */
);
ALTER TABLE attendeeList ADD CONSTRAINT FK_Employee_TO_attendeeList FOREIGN KEY ( EMPNO ) REFERENCES Employee ( EMPNO );

SELECT * FROM TRAINING t ;
SELECT * FROM benefit ;		--170620221251
SELECT* FROM ATTENDEELIST;

SELECT t.LISTNUMBER ,t.title,t.title FROM ATTENDEELIST a , TRAINING t ,BENEFIT b WHERE a.LISTNUMBER =t.LISTNUMBER ;
SELECT b.LISTNUMBER ,b.title , a.EMPNO  FROM ATTENDEELIST a ,BENEFIT b WHERE a.LISTNUMBER =b.LISTNUMBER ;
--교육 입력
INSERT INTO training VALUES (origin.nextval||220824+10,'2022하반기 소방안전교육',to_date('2022-08-24 13:00','yyyy-mm-dd HH24:MI'),'안전',10,0,'미정');
--복지 입력
INSERT INTO benefit VALUES (origin.nextval||20211231+20,'교통비지원',''||NULL||'',to_date('20221231','yyyymmdd'),100);

INSERT INTO benefit VALUES (origin.nextval||20211231+20,'자녀 대학장학금',
q'[ INSERT INTO ATTENDEELIST 
	(SELECT ? ,empno FROM employee WHERE empno=? and (deptno = 20 or deptno = 10 ) )]',
	to_date('20231231','yyyymmdd'),500);

--복지에 조건 설정하기
--조건: 나이가 몇살 이상
SELECT * FROM employee WHERE (sysdate - birth)/365 >= 40 AND (sysdate - birth)/365 <=60 ;
--조건 : 직급
SELECT * FROM employee WHERE (RANK LIKE '%'||'부장'||'%') ;
--조건 : 부서가 어디
SELECT * FROM employee WHERE deptno = 10 OR deptno =20; 
--조건 설정해서 신청할때
SELECT listnumber , CONDI FROM benefit WHERE LISTNUMBER =170620221251;
INSERT INTO ATTENDEELIST 
(SELECT 170220211251 ,empno FROM employee WHERE empno=2022101001 AND (RANK LIKE '%'||'부장'||'%' OR RANK LIKE '%'||'이사'||'%') );

--복지 신청
INSERT INTO ATTENDEELIST 
	(SELECT 1648220844,2022101001 FROM employee 
	WHERE EMPNO =2022101001 AND "RANK" LIKE '%'||''||'%' AND deptno LIKE '%'||''||'%');
--내가 신청한 복지 조회
SELECT title FROM benefit b , ATTENDEELIST a WHERE a.LISTNUMBER =b.listnumber AND a.empno =2022701025 ;
--복지 신청한 사람들 
SELECT name FROM benefit b , ATTENDEELIST a,employee e 
WHERE a.LISTNUMBER =b.listnumber AND e.EMPNO =a.EMPNO  AND a.listnumber= 165320241251;
--교육신청할때 신청자 수 늘어나게
UPDATE TRAINING SET CRTATTENDEE = CRTATTENDEE + 1 WHERE LISTNUMBER = 1646220824 AND CRTATTENDEE < MAXATTENDEE ;
INSERT INTO ATTENDEELIST VALUES (1646220824, 2022201027) ;



--교육 조회
SELECT * FROM TRAINING WHERE trainday >= SYSDATE  ;
--교육 신청자
SELECT a.empno,e.name, e.deptno, e.rank FROM training t,ATTENDEELIST a,employee e WHERE t.LISTNUMBER =a.LISTNUMBER AND e.EMPNO =a.EMPNO AND a.LISTNUMBER = 168021;
--내가 신청한 교육
SELECT * FROM training t, ATTENDEELIST a WHERE t.LISTNUMBER =a.LISTNUMBER  AND empno= 2022101026;