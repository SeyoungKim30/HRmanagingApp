/* 급여 */
DROP TABLE pay 
	CASCADE CONSTRAINTS;

/* 급여 */
CREATE TABLE pay (
	EMPNO CHAR(10)  NOT NULL, /* 사원번호 */
	payday DATE, /* 지급일 */
	salary NUMBER, /* 월급여 */
	bankAC VARCHAR2(30) /* 급여계좌 */
);
ALTER TABLE SCOTT.PAY MODIFY BANKAC VARCHAR2(30);


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
/*
INSERT INTO PAY (empno,payday,salary) (
SELECT h.EMPNO,to_date('2022-10-20','yyyy-mm-dd'),salary 
FROM history h WHERE h.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사') 
		AND (empno, MOVEDAY) 
	IN (SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno));
	*/
--확인
SELECT * FROM pay WHERE PAYDAY = to_date('2022-10-20','yyyy-mm-dd') ;
DELETE pay WHERE PAYDAY = to_date('2022-10-20','yyyy-mm-dd') ;
	
--부서별로 월급 주기
/*
INSERT INTO PAY (empno,payday,salary) (
SELECT h.EMPNO,to_date('2022-10-20','yyyy-mm-dd'),salary 
FROM history h WHERE h.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사') 
		AND (empno, MOVEDAY) 
	IN (SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno)AND deptno=20);
	*/
--급여 지급방식 수정
UPDATE pay SET bankAC = '신한85252-112-1123' WHERE EMPNO =2022101001;
UPDATE pay SET bankAC = '국민81455-78879541' WHERE EMPNO =2022201023;

/* 수당 */
DROP TABLE EXTRAPAY CASCADE CONSTRAINTS;

/* 수당 */
CREATE TABLE EXTRAPAY (
	EMPNO CHAR(10)  NOT NULL, /* 사원번호 */
	payno NUMBER, /* 수당번호 */
	amount NUMBER, /* 금액 */
	state VARCHAR2(50), /* 상태 */
	payday DATE /* 지급일 */
);
ALTER TABLE EXTRAPAY ADD CONSTRAINT FK_Employee_TO_EXTRAPAY
		FOREIGN KEY ( EMPNO ) REFERENCES Employee ( EMPNO );
ALTER TABLE EXTRAPAY ADD CHECK (state IN ('신청','승인','지급'));

SELECT * FROM extrapay;
	
INSERT INTO extrapay (empno,payno,amount,state) VALUES (2022201023,origin.nextval||10,140000,'신청'); 
UPDATE extrapay SET STATE = '승인' WHERE EMPNO ='2022101001' AND state ='신청' AND amount = 140000;
UPDATE extrapay SET STATE = '지급', payday=sysdate WHERE EMPNO ='2022101001' AND state ='승인' AND amount = 150000;

SELECT empno,to_char(payday,'YYYY'),to_char(payday,'mm'),salary FROM pay WHERE empno =2022101001 ;
SELECT empno,amount,to_char(payday,'YYYY'),to_char(payday,'mm') FROM extrapay WHERE empno =2022101001 AND payday IS NOT NULL;
