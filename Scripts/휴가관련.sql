/* 휴가 */
DROP TABLE DAYOFF 
	CASCADE CONSTRAINTS;

/* 휴가 */
CREATE TABLE DAYOFF (
	EMPNO CHAR(10)  NOT NULL, /* 사원번호 */
	usable NUMBER, /* 연차남은거 */
	spend NUMBER, /* 연차쓴거 */
	applyday DATE, /* 신청날짜 */
	applyduring NUMBER	
);

ALTER TABLE DAYOFF MODIFY apply varchar2(400);

--매년초 하드리셋??
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
(SELECT empno,trunc((sysdate - min(moveday))/30) "몇개월", trunc(((sysdate - min(moveday))/365)/3) "추가연차"  
	FROM history h GROUP BY empno));
*/
--휴가신청하기
SELECT * FROM schedule ORDER BY intime;
SELECT * FROM dayoff;
UPDATE dayoff --2일 쓸거면 시작일 받고 2일 받아서 자바에서 for문으로 반복실행해주기
	SET applyday = to_date('2022-10-24','yyyy-mm-dd') ,
	applyduring = 3 ,
	usable = usable- 3,
	spend = nvl(spend,0)+3
WHERE empno=2022701025;
--신청하면서 바로 연차가 까이기때문에 인사담당자가 스케줄짤때 여기있는 apply를 조회하고 들어가도록, 만약 거절한다면 다시 돌려주기
-- 승인
INSERT INTO schedule(empno,intime,OUTTIME) 
	(select 2022701025, 
	to_date('2022-10-24 00:00','yyyy-mm-dd HH24:MI')+0,
	to_date('2022-10-24 00:00','yyyy-mm-dd HH24:MI')+0 FROM dual 
WHERE NOT EXISTS (SELECT EMPNO  FROM schedule 
WHERE trunc(intime,'dd') = to_date('2022-10-24','yyyy-mm-dd') AND empno = 2022701025 ));

INSERT INTO schedule(empno,intime,OUTTIME)
(select 2022201023, to_date('2022-12-05 00:00','yyyy-mm-dd HH24:MI') + 4
				  , to_date('2022-12-05 00:00','yyyy-mm-dd HH24:MI')+ 4 FROM dual
					WHERE NOT EXISTS (SELECT EMPNO  FROM schedule
					WHERE trunc(intime,'dd') = to_date('2022-12-05','yyyy-mm-dd')+ 4 AND empno = 2022201023));



--업데이트 할때 담당자한테 신청 들어온거 있는지 보여주기 
SELECT empno, applyday FROM dayoff WHERE applyday IS NOT NULL;
--돌려주면서 신청서 지우기
UPDATE dayoff SET usable = usable + 3, spend = spend - 3 , applyday = NULL, appyduring = null WHERE empno=2022701025;
--승인하고 신청서 지우기
UPDATE dayoff SET applyday = NULL, applyduring = null WHERE empno=2022201027;


--insert schedule 문
/*
INSERT INTO schedule(empno,intime,OUTTIME) 
	(select 2022701025, to_date('2022-10-24 00:00','yyyy-mm-dd HH24:MI'),to_date('2022-10-24 00:00','yyyy-mm-dd HH24:MI') FROM dual 
WHERE NOT EXISTS (SELECT EMPNO  FROM schedule WHERE trunc(intime,'dd') = to_date('2022-10-24','yyyy-mm-dd') AND empno = 2022701025 ));
*/
--연차수당 : 일당, 남은날짜로 extrapay 신청하기, 상태는 승인
SELECT * FROM EXTRAPAY e ;
SELECT dd.empno,origin.nextval||4, usable * salary/200*1.5
FROM dayoff dd,(SELECT h.EMPNO, salary FROM history h WHERE (empno, MOVEDAY) IN (SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno) ) hi
WHERE dd.EMPNO =hi.empno AND usable>0 ;

INSERT INTO extrapay(empno, payno,amount,state) 
(SELECT dd.empno, origin.nextval||4 , ROUND(usable * salary/200*1.5) , '승인'
FROM dayoff dd,(SELECT h.EMPNO, salary FROM history h WHERE (empno, MOVEDAY) IN (SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno) ) hi
WHERE dd.EMPNO =hi.empno AND usable>0);
