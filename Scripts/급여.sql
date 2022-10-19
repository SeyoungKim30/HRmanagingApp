/* 급여 */
DROP TABLE pay 
	CASCADE CONSTRAINTS;

/* 급여 */
CREATE TABLE pay (
	EMPNO CHAR(10)  NOT NULL, /* 사원번호 */
	payday DATE, /* 지급일 */
	salary NUMBER, /* 월급여 */
	bankAC NUMBER /* 급여계좌 */
);

ALTER TABLE pay ADD CONSTRAINT FK_Employee_TO_pay FOREIGN KEY ( EMPNO ) REFERENCES Employee ( EMPNO );
--ALTER TABLE history ADD salary NUMBER ;
SELECT * FROM HISTORY h , EMPLOYEE e WHERE h.EMPNO (+)=e.EMPNO ;
SELECT * FROM HISTORY h ;

SELECT * FROM pay ;
SELECT * FROM employee;
SELECT * FROM PAY p , employee e  WHERE p.empno=e.EMPNO ORDER BY rank;
INSERT INTO pay(EMPNO) (SELECT EMPNO FROM employee);
INSERT INTO pay VALUES (2022101001,sysdate-122,190,11541148552);	--개인에게 주기

--부서에 주기
INSERT INTO pay(EMPNO) (SELECT empno FROM employee WHERE deptno = 10); 
--가장 최근 history만
SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno;
--가장 최근 월급만
SELECT EMPNO, salary FROM history WHERE (empno, MOVEDAY) IN (SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno)  ;
--퇴사자 빼고 
SELECT * FROM RETIREMENT r2 ;--퇴사정보 2022501028
SELECT h.EMPNO, salary,state FROM history h, RETIREMENT r WHERE h.EMPNO(+)=r.EMPNO AND r.STATE (+)<> '퇴사' ; --퇴사 빼고
SELECT * FROM history h, RETIREMENT r WHERE r.EMPNO(+)=h.EMPNO ;
SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사';--퇴사자 번호만
SELECT e.EMPNO,state FROM RETIREMENT r , employee e WHERE r.EMPNO (+)=e.EMPNO AND r.state(+)!='퇴사';		
SELECT * FROM history h WHERE h.EMPNO IN (SELECT r.EMPNO FROM RETIREMENT r , employee e WHERE r.EMPNO (+)=e.EMPNO AND r.state(+)!='퇴사');
--퇴사자빼고 최근월급
SELECT h.EMPNO, salary FROM history h WHERE h.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사') 
		AND (empno, MOVEDAY) IN (SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno)  ;

--모두에게 최근 월급 주기
INSERT INTO PAY (empno,payday,salary) (
SELECT h.EMPNO,to_date('2022-10-21','yyyy-mm-dd'),salary 
FROM history h WHERE h.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사') 
		AND (empno, MOVEDAY) 
	IN (SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno));
--확인
SELECT * FROM pay WHERE PAYDAY = to_date('2022-10-21','yyyy-mm-dd') ;
	
--부서별로 월급 주기
INSERT INTO PAY (empno,payday,salary) (
SELECT h.EMPNO,to_date('1999-10-21','yyyy-mm-dd'),salary 
FROM history h WHERE h.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사') 
		AND (empno, MOVEDAY) 
	IN (SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno)AND deptno=20);


UPDATE pay SET salary = 300 WHERE empno IN (SELECT e.empno FROM PAY p , employee e  WHERE p.empno=e.EMPNO AND RANK = '사원');
UPDATE pay SET payday = SYSDATE-91 WHERE payday IS NULL ;
