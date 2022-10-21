/* 휴가 */
DROP TABLE DAYOFF 
	CASCADE CONSTRAINTS;

/* 휴가 */
CREATE TABLE DAYOFF (
	EMPNO CHAR(10)  NOT NULL, /* 사원번호 */
	usable NUMBER, /* 연차남은거 */
	spend NUMBER, /* 연차쓴거 */
	apply varchar2(400) /* 신청서 */
);

ALTER TABLE DAYOFF MODIFY apply varchar2(400);

		--매년초 하드리셋??
--INSERT INTO DAYOFF VALUES(2022101001,)
--연차갯수 구하기 : 히스토리에서 round(min 날짜~ 지금 년도,3) 나온 값을 15에 더해서 일년 연차 갯수 구함, 그걸 서브쿼리로 써서 insert
--가장 작은 moveday


SELECT empno, min(moveday) 입사일 FROM history h GROUP BY empno ;
SELECT empno, trunc((sysdate - min(moveday))/30) 몇개월 FROM history h GROUP BY empno;
SELECT empno, (sysdate - min(moveday))/365 몇년차 FROM history h GROUP BY empno;
SELECT empno, trunc(((sysdate - min(moveday))/365)/3) 추가연차  FROM history h GROUP BY empno;
--조건문 1년 안된사람
/*
SELECT h.empno, CASE WHEN round(((sysdate - min(h.moveday))/365)/3)	>	0 THEN (sysdate - min(h.moveday))/30
			WHEN 추가연차	>	10	THEN 추가연차+15
	ELSE 25 END AS "연차"
FROM history h , (SELECT empno, round(((sysdate - min(moveday))/365)/3) 추가연차  FROM history h1 GROUP BY empno) exv GROUP BY empno;
*/
SELECT empno , 
CASE WHEN 몇개월 < 12 THEN 몇개월
	WHEN 추가연차 <11 THEN 추가연차+15
	ELSE 25 END AS "총연차"
FROM 
(SELECT empno,trunc((sysdate - min(moveday))/30) "몇개월", trunc(((sysdate - min(moveday))/365)/3) "추가연차"  FROM history h GROUP BY empno);


--올해 연차 만들기 (25일 한도)
/*INSERT INTO dayoff(empno,usable) (SELECT empno , 
CASE WHEN 몇개월 < 12 THEN 몇개월
	WHEN 추가연차 <11 THEN 추가연차+15
	ELSE 25 END AS "총연차"
FROM 
(SELECT empno,trunc((sysdate - min(moveday))/30) "몇개월", trunc(((sysdate - min(moveday))/365)/3) "추가연차"  FROM history h GROUP BY empno));
*/
--휴가신청하기
SELECT * FROM schedule;
SELECT * FROM dayoff;
UPDATE dayoff --2일 쓸거면 시작일 받고 2일 받아서 자바에서 for문으로 반복실행해주기
	SET apply = q'[INSERT INTO schedule(empno,intime,OUTTIME) 
	(select 2022701025, to_date('2022-10-24 00:00','yyyy-mm-dd HH24:MI'),to_date('2022-10-24 00:00','yyyy-mm-dd HH24:MI')+2 FROM dual 
WHERE NOT EXISTS (SELECT EMPNO  FROM schedule WHERE trunc(intime,'dd') = to_date('2022-10-24','yyyy-mm-dd') AND empno = 2022701025 ))]' ,
	usable = usable- 1,
	spend = nvl(spend,0)+1 
WHERE empno=2022701025;
--신청하면서 바로 연차가 까이기때문에 인사담당자가 스케줄짤때 여기있는 apply를 조회하고 들어가도록, 만약 거절한다면 다시 돌려주기
UPDATE dayoff SET usable = usable + 1,
	spend = spend - 1 WHERE empno=2022701025;

--insert schedule 문
/*
INSERT INTO schedule(empno,intime,OUTTIME) 
	(select 2022701025, to_date('2022-10-24 00:00','yyyy-mm-dd HH24:MI'),to_date('2022-10-24 00:00','yyyy-mm-dd HH24:MI') FROM dual 
WHERE NOT EXISTS (SELECT EMPNO  FROM schedule WHERE trunc(intime,'dd') = to_date('2022-10-24','yyyy-mm-dd') AND empno = 2022701025 ));
*/
--연차수당 : 일당, 남은날짜로 extrapay 신청하기, 상태는 승인
SELECT * FROM EXTRAPAY e ;
INSERT INTO extrapay(empno, payno,amount,state) (SELECT empno,연차수당번호||시퀀스 ,usable*일당);

