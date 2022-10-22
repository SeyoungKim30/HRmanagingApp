SELECT * FROM SCHEDULE s ;
SELECT * FROM worktable ;
/* 근무 스케줄 */
DROP TABLE SCHEDULE 
	CASCADE CONSTRAINTS;
/* 근무 스케줄 */
CREATE TABLE SCHEDULE (
	EMPNO CHAR(10)  NOT NULL, /* 사원번호 */
	intime date, /* 출근 */
	outtime date /* 퇴근 */
);
ALTER TABLE SCHEDULE
	ADD CONSTRAINT FK_Employee_TO_SCHEDULE FOREIGN KEY ( EMPNO ) REFERENCES Employee ( EMPNO );
		
/* 출퇴근기록 */
DROP TABLE WORKTABLE CASCADE CONSTRAINTS;
/* 출퇴근기록 */
CREATE TABLE WORKTABLE (
	EMPNO CHAR(10)  NOT NULL, /* 사원번호 */
	timeon DATE, /* 출근시간 */
	timeoff DATE /* 퇴근시간 */
);
ALTER TABLE WORKTABLE
	ADD FOREIGN KEY (EMPNO ) REFERENCES Employee ( EMPNO );

--출근 스케줄 입력 : 모두
INSERT INTO schedule(empno,intime,outtime) (
SELECT e.empno , to_date('2022-10-22 10:00','yyyy-mm-dd HH24:MI'),to_date('2022-10-22 18:00','yyyy-mm-dd HH24:MI')
FROM employee e WHERE e.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사')
);

--출근 스케줄 입력 : 부서
INSERT INTO schedule (
SELECT e.empno , to_date('2022-10-18 11:00','yyyy-mm-dd HH24:MI'),
		to_date('2022-10-18 20:00','yyyy-mm-dd HH24:MI')
FROM employee e 
WHERE e.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사') AND deptno = 20
);

--출근 스케줄 입력 : 개인
INSERT INTO schedule VALUES 
(2022701025,to_date('2022-09-20 09:00','yyyy-mm-dd HH24:MI'),
to_date('2022-09-20 09:00','yyyy-mm-dd HH24:MI')+(8/24));

----------------------------------------------------------조회
--출근 스케줄 보기 : 개인/연월 입력
SELECT to_char(intime,'yyyy-mm-dd') 출근일 ,to_char(intime,'HH24:mi') 출근시간 ,to_char(OUTTIME,'HH24:mi') 퇴근시간 
FROM SCHEDULE s WHERE EMPNO =2022701025 AND trunc(intime,'mm') IN to_date('2022-10','yyyy-mm');
--출근 스케줄 보기 : 부서/연월 입력
--스케줄이 있는 날짜 받아서 아래 문장에 넣어서 날짜별로 출력하기
SELECT DISTINCT to_char(intime,'yyyy-mm-dd') FROM SCHEDULE s WHERE TO_CHAR(intime,'yyyy-mm') LIKE '2022-10' ;
SELECT s.empno , name FROM employee e,SCHEDULE s WHERE e.EMPNO =s.EMPNO and DEPTNO LIKE 20 AND trunc(intime,'dd') IN to_date('2022-10-19','yyyy-mm-dd') ;	

--출근 스케줄 보기 : 전체/연월 입력
SELECT name, to_char(intime,'yyyy-mm-dd') 출근일 ,to_char(intime,'HH24:mi') 출근시간 ,to_char(OUTTIME,'HH24:mi') 퇴근시간 
FROM SCHEDULE s, EMPLOYEE e  WHERE s.EMPNO =e.EMPNO and trunc(intime,'mm') IN to_date('2022-10','yyyy-mm') ORDER BY INTIME;
--출근 스케줄 보기 : 전체/특정날짜입력
SELECT name, to_char(intime,'yyyy-mm-dd') 출근일 ,to_char(intime,'HH24:mi') 출근시간 ,to_char(OUTTIME,'HH24:mi') 퇴근시간 
FROM SCHEDULE s, EMPLOYEE e  WHERE s.EMPNO =e.EMPNO and trunc(intime,'dd') IN to_date('2022-10-22','yyyy-mm-dd');
--출근 스케줄 : 부서별/특정날짜
SELECT name, to_char(intime,'yyyy-mm-dd') 출근일 ,to_char(intime,'HH24:mi') 출근시간 ,to_char(OUTTIME,'HH24:mi') 퇴근시간 
FROM SCHEDULE s, EMPLOYEE e  WHERE s.EMPNO =e.EMPNO AND e.DEPTNO like 10 
and trunc(intime,'dd') IN to_date('2022-10-22','yyyy-mm-dd') ;

--출퇴근시 입력 
INSERT INTO --WORKTABLE (
SELECT e.empno , to_date('2022-10-19 09:00','yyyy-mm-dd HH24:MI'),to_date('2022-10-19 18:00','yyyy-mm-dd HH24:MI')
FROM employee e WHERE e.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사')
);

--출근
INSERT INTO WORKTABLE (empno, TIMEON)VALUES (2022101026, to_date('2022-10-20 09:00','yyyy-mm-dd HH24:MI'));
INSERT INTO WORKTABLE (empno, TIMEON)VALUES (2022101026, to_date('2022-10-19 09:00','yyyy-mm-dd HH24:MI'));
INSERT INTO WORKTABLE (empno, TIMEON)VALUES (2022101026, to_date('2022-10-18 09:00','yyyy-mm-dd HH24:MI'));
INSERT INTO WORKTABLE (empno, TIMEON)VALUES (2022101026, to_date('2022-10-17 09:00','yyyy-mm-dd HH24:MI'));
INSERT INTO WORKTABLE (empno, TIMEON)VALUES (2022101026, to_date('2022-10-16 09:00','yyyy-mm-dd HH24:MI'));
UPDATE worktable SET timeoff =to_date('2022-10-20 18:00','yyyy-mm-dd HH24:MI') WHERE EMPNO LIKE 2022101026 AND TIMEON =to_date('2022-10-20 09:00','yyyy-mm-dd HH24:MI');
UPDATE worktable SET timeoff =to_date('2022-10-19 18:00','yyyy-mm-dd HH24:MI') WHERE EMPNO LIKE 2022101026 AND TIMEON =to_date('2022-10-19 09:00','yyyy-mm-dd HH24:MI');
UPDATE worktable SET timeoff =to_date('2022-10-18 18:00','yyyy-mm-dd HH24:MI') WHERE EMPNO LIKE 2022101026 AND TIMEON =to_date('2022-10-18 09:00','yyyy-mm-dd HH24:MI');
UPDATE worktable SET timeoff =to_date('2022-10-17 18:00','yyyy-mm-dd HH24:MI') WHERE EMPNO LIKE 2022101026 AND TIMEON =to_date('2022-10-17 09:00','yyyy-mm-dd HH24:MI');
UPDATE worktable SET timeoff =to_date('2022-10-16 18:00','yyyy-mm-dd HH24:MI') WHERE EMPNO LIKE 2022101026 AND TIMEON =to_date('2022-10-16 09:00','yyyy-mm-dd HH24:MI');

--퇴근 : 출근일보단 뒤지만 24시간이 지나지 않았고 타임스탬프가 비어있는 칸에 찍기
UPDATE worktable SET TIMEOFF =to_date('2022-10-23 03:40','yyyy-mm-dd HH24:MI')
WHERE EMPNO LIKE 2022501030 AND TIMEON < to_date('2022-10-23 03:40','yyyy-mm-dd HH24:MI')
AND TIMEON+1 > to_date('2022-10-23 03:40','yyyy-mm-dd HH24:MI')
AND TIMEOFF is NULL;
--DELETE worktable WHERE empno = 2022101001 AND TIMEOFF is NULL ;
--INSERT INTO worktable VALUES (2022101026,to_date('2022-10-19 09:08','yyyy-mm-dd HH24:MI'), to_date('2022-10-19 19:11','yyyy-mm-dd HH24:MI') );
--INSERT INTO worktable VALUES (2022101026,to_date('2022-10-21 10:05','yyyy-mm-dd HH24:MI'), to_date('2022-10-21 17:11','yyyy-mm-dd HH24:MI') );
--INSERT INTO worktable VALUES (2022101026,to_date('2022-10-22 08:00','yyyy-mm-dd HH24:MI'), to_date('2022-10-22 20:11','yyyy-mm-dd HH24:MI') );

UPDATE worktable SET TIMEOFF = sysdate
WHERE EMPNO LIKE 2022201020 AND TIMEON < sysdate
AND TIMEON+1 > sysdate
AND TIMEOFF is NULL;

--출근 조회 비교
/*
SELECT DISTINCT to_char(intime,'yyyy-mm-dd') 근무일 ,to_char(intime,'HH24:mi') 출근스케줄 ,to_char(OUTTIME,'HH24:mi') 퇴근스케줄, 실제출근,실제퇴근
FROM SCHEDULE s ,(SELECT to_char(TIMEON ,'yyyy-mm-dd') 실근무, to_char(timeon,'HH24:mi') 실제출근 ,to_char(timeoff,'HH24:mi') 실제퇴근 FROM WORKTABLE) w
WHERE w.실근무=to_char(intime,'yyyy-mm-dd') AND empno = 2022101026;

SELECT * FROM SCHEDULE s,worktable w WHERE s.EMPNO = w.EMPNO AND s.empno = 2022101001;
SELECT * FROM SCHEDULE s,worktable w WHERE to_char(TIMEON ,'yyyy-mm-dd') =(+) to_char(intime,'yyyy-mm-dd') AND s.empno = 2022101001;
*/
--스케줄이랑 근무일지 합친거
SELECT * FROM SCHEDULE s FULL JOIN worktable b 
	on(b.EMPNO = s.EMPNO AND trunc(TIMEon,'dd')=trunc(intime,'dd') )
WHERE s.empno = 2022101026 AND intime BETWEEN (intime-30) AND (intime+30);


--AND intime BETWEEN (intime-30) AND (intime+30);
SELECT trunc(intime,'dd') FROM SCHEDULE WHERE EMPNO =2022101026;--근무 모든 날짜
SELECT trunc(TIMEon,'dd') FROM worktable w WHERE EMPNO =2022101026;  --출근 모든 날짜


SELECT * FROM SCHEDULE s  WHERE empno = 2022101026; --18,19,22
SELECT * FROM worktable WHERE empno = 2022101026;	--

CREATE TABLE faketb (fkdate DATE );
DROP TABLE faketb;
INSERT INTO FAKETB (SELECT trunc(intime,'dd') FROM SCHEDULE WHERE EMPNO =2022101026);
INSERT INTO faketb (SELECT trunc(TIMEon,'dd') FROM worktable w WHERE EMPNO =2022101026);
SELECT DISTINCT fkdate FROM faketb;

SELECT DISTINCT intime, outtime FROM faketb f,SCHEDULE s WHERE fkdate=trunc(intime,'dd');

SELECT trunc(TIMEon,'dd'), trunc(intime,'dd')  FROM Schedule s FULL OUTER JOIN WORKTABLE w 
ON  trunc(TIMEon,'dd') IN trunc(intime,'dd') 
WHERE s.EMPNO =2022101026;

--ALTER TABLE WORKTABLE add mmdd DATE ;
--INSERT INTO WORKTABLE(mmdd,empno)(SELECT DISTINCT fkdate,2022101026 FROM faketb); 
SELECT mmdd, intime, timeon FROM SCHEDULE s,worktable w WHERE  mmdd=trunc(intime,'dd');


/*CREATE TABLE fooda(
id NUMBER , food varchar2(30)); 
CREATE TABLE foodb(
id NUMBER , food varchar2(30)); 
INSERT INTO fooda VALUES (1,'돈까스');
INSERT INTO foodb VALUES (1,'돈까스');
INSERT INTO fooda VALUES (2,'초밥');
INSERT INTO foodb VALUES (1,'냉면');
INSERT INTO fooda VALUES (3,'햄버거');
INSERT INTO foodb VALUES (3,'햄버거');
INSERT INTO fooda VALUES (5,'잔치국수');
INSERT INTO foodb VALUES (4,'막국수');
SELECT * FROM fooda a FULL OUTER JOIN FOODB b ON a.food = b.food;
DROP TABLE foodb;*/