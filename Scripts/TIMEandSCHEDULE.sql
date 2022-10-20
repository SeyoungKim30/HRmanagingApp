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
SELECT e.empno , to_date('2022-10-19 09:00','yyyy-mm-dd HH24:MI'),to_date('2022-10-19 18:00','yyyy-mm-dd HH24:MI')
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

--출근 스케줄 보기 : 개인
SELECT * FROM SCHEDULE s WHERE EMPNO =2022701025;
--출근 스케줄 보기 : 전체/연월 입력
SELECT * FROM SCHEDULE s WHERE trunc(intime,'mm') IN to_date('2022-09','yyyy-mm');
--출근 스케줄 보기 : 전체/특정날짜입력
SELECT * FROM SCHEDULE s WHERE trunc(intime,'dd') IN to_date('2022-10-22','yyyy-mm-dd');

--출퇴근시 입력 : 모두
INSERT INTO WORKTABLE (
SELECT e.empno , to_date('2022-10-19 09:00','yyyy-mm-dd HH24:MI'),to_date('2022-10-19 18:00','yyyy-mm-dd HH24:MI')
FROM employee e WHERE e.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사')
);