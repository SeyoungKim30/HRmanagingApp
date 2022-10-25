/* 인사계획 */
DROP TABLE HRplan 
	CASCADE CONSTRAINTS;

/* 인사계획 */
CREATE TABLE HRplan (
	year DATE, /* 연도 */
	deptno NUMBER(2), /* 부서번호 */
	ranker VARCHAR2(50), /* 직급 */
	cnt NUMBER /* 인원 */
);

ALTER TABLE HRplan
	ADD CONSTRAINT FK_DEPARTMENT_TO_HRplan
		FOREIGN KEY ( deptno )
		REFERENCES DEPARTMENT ( deptno );
	/* 예산계획 */
DROP TABLE Budgetplan 
	CASCADE CONSTRAINTS;

/* 예산계획 */
CREATE TABLE Budgetplan (
	year DATE, /* 연도 */
	deptno NUMBER(2), /* 부서번호 */
	type VARCHAR2(50), /* 종류 */
	budget NUMBER /* 비용 */
);

ALTER TABLE Budgetplan
	ADD
		CONSTRAINT FK_DEPARTMENT_TO_Budgetplan
		FOREIGN KEY (
			deptno
		)
		REFERENCES DEPARTMENT (
			deptno
		);

SELECT * FROM hrplan;

--인사계획입력
INSERT INTO hrplan(YEAR,deptno,ranker,cnt) (SELECT to_date('2022','yyyy'),10,'부장',1 
	FROM dual WHERE NOT EXISTS (SELECT 1 FROM hrplan WHERE deptno= 10 AND ranker = '부장'));
INSERT INTO hrplan(YEAR,deptno,ranker,cnt) (SELECT to_date('2022','yyyy'),50,'사원',2 
	FROM dual WHERE NOT EXISTS (SELECT 1 FROM hrplan WHERE deptno= 50 AND ranker = '사원' AND TO_CHAR(YEAR,'yyyy')=2022));

--인사 분석(조회)
--1. 실제 부서별 직급별 사람수
SELECT d.deptno "부서번호별", COUNT(*) "사람수"  FROM EMPLOYEE e , DEPARTMENT d 
	WHERE e.DEPTNO =d.deptno AND "RANK" ='대리' AND e.deptno LIKE '%'||''||'%' GROUP BY d.DEPTNO ;
--1-2 조건별 계획 
SELECT deptno,ranker, cnt  FROM hrplan h
				WHERE ranker LIKE '%'||'대리'||'%' AND deptno LIKE '%'||''||'%' AND TO_CHAR(YEAR,'yyyy') = 2022
				;
--1-3. 비교, 부서내에서 직급별(올해만)
SELECT d.부서번호별, h.ranker , h.cnt, d.사람수 , ROUND(nvl(사람수/cnt*100,0))||'%' 달성률 , 
	CASE WHEN nvl(사람수/cnt*100,0)<90 THEN '미달'
		WHEN nvl(사람수/cnt*100,0) <110 THEN '적정'
		ELSE '초과' END AS 상태
from (SELECT deptno,ranker, cnt  FROM hrplan
				WHERE ranker LIKE '%'||'사원'||'%' AND deptno LIKE '%'||''||'%' AND TO_CHAR(YEAR,'yyyy') = 2022) h , 
	(SELECT d.deptno "부서번호별", COUNT(*) "사람수"  FROM EMPLOYEE e , DEPARTMENT d 
				WHERE e.DEPTNO =d.deptno AND "RANK" LIKE '%'||'사원'||'%' AND d.deptno LIKE '%'||''||'%'
				GROUP BY d.DEPTNO) d
WHERE d.부서번호별= h.deptno
;

--2-1.전체직급 부서별 사람수
SELECT d.deptno 부서번호별, COUNT(*) "실제인원" FROM EMPLOYEE e , DEPARTMENT d 
	WHERE e.DEPTNO =d.deptno GROUP BY d.DEPTNO;
--2-2. 부서별 계획 수
SELECT DEPTNO, SUM(CNT) 계획합
FROM HRPLAN H GROUP BY DEPTNO;
--2-3. 비교하기
SELECT DNAME, 계획합 , 실제인원 , NVL(ROUND(실제인원/계획합*100),0) ||'%' 달성률,
	CASE WHEN NVL(ROUND(실제인원/계획합*100),0)<90 THEN '미달'
		WHEN NVL(ROUND(실제인원/계획합*100),0) <110 THEN '적정'
		ELSE '초과' END AS 상태
FROM (SELECT d.deptno 부서번호별, COUNT(*) "실제인원" FROM EMPLOYEE e , DEPARTMENT d 
	WHERE e.DEPTNO =d.deptno GROUP BY d.DEPTNO) 실제 , 
	(SELECT DEPTNO, SUM(CNT) 계획합 FROM HRPLAN H GROUP BY DEPTNO) 계획,
	DEPARTMENT DP
WHERE 실제.부서번호별=계획.DEPTNO AND DP.DEPTNO=계획.DEPTNO;
	
-----------------------------------
SELECT * FROM DEPARTMENT d ;
SELECT * FROM BUDGETPLAN ;
--예산계획 입력(1월1일)
INSERT INTO BUDGETPLAN VALUES (to_date('2022-01-01','yyyy-MM-DD'),30,'복리후생비',5000);
--초과해서 추가예산 편성할때 
INSERT INTO BUDGETPLAN VALUES(SYSDATE,30, '운반비', 5000);
--예산계획 분석(조회)
--1-1.초기예산(특정부서, 항목별)
SELECT "TYPE", BUDGET FROM BUDGETPLAN WHERE YEAR=to_date('2021-01-01','yyyy-MM-DD') AND DEPTNO=30;
--1-2. 추가된예산포함(특정부서, 항목별)
SELECT "TYPE", SUM(BUDGET) 예산결과 FROM BUDGETPLAN WHERE to_CHAR(YEAR,'yyyy') LIKE 2021 AND DEPTNO=30 GROUP BY TYPE;
--1-3. 비교(특정부서, 항목별)
SELECT B1.TYPE , 초기예산 , 예산결과 , 예산결과-초기예산 초과비용, NVL(ROUND(예산결과/초기예산*100),0)||'%' "사용률"
FROM
(SELECT "TYPE", SUM(BUDGET) 초기예산 FROM BUDGETPLAN WHERE YEAR=to_date('2021-01-01','yyyy-MM-DD') AND DEPTNO LIKE '%'||'30'||'%' GROUP BY "TYPE") B1,
(SELECT "TYPE", SUM(BUDGET) 예산결과 FROM BUDGETPLAN WHERE to_CHAR(YEAR,'yyyy') LIKE 2021 AND DEPTNO LIKE '%'||'30'||'%' GROUP BY "TYPE") B2
WHERE B1.TYPE=B2.TYPE
;

--2-1. 초기예산(전체항목, 각 부서별)
SELECT DEPTNO, SUM(BUDGET) FROM BUDGETPLAN WHERE YEAR=to_date('2021-01-01','yyyy-MM-DD') GROUP BY DEPTNO;
--2-2. 전체예산
SELECT DEPTNO, SUM(BUDGET) FROM BUDGETPLAN WHERE to_CHAR(YEAR,'yyyy') LIKE 2021 GROUP BY DEPTNO;
--2-3. 비교(전체항목, 각 부서)
SELECT DNAME , 초기예산, 예산결과 , 예산결과-초기예산 "초과비용" , ROUND(NVL(예산결과/초기예산*100,0))||'%' "사용률"
FROM 
(SELECT DEPTNO, SUM(BUDGET) 초기예산 FROM BUDGETPLAN WHERE YEAR=to_date('2021-01-01','yyyy-MM-DD') GROUP BY DEPTNO) B1,
(SELECT DEPTNO, SUM(BUDGET) 예산결과 FROM BUDGETPLAN WHERE to_CHAR(YEAR,'yyyy') LIKE 2021 GROUP BY DEPTNO) B2,
DEPARTMENT D
WHERE B1.DEPTNO = B2.DEPTNO AND B1.DEPTNO=D.DEPTNO;

-------------------------
--인사평가 분석: 
--1. 검색조건 : 히스토리에서 사람별로 가장 최근거, WHERE BETWEEN 급여, 부서, 직급
SELECT EMPNO, "RANK",DEPTNO,SALARY  FROM HISTORY H 
WHERE (EMPNO ,MOVEDAY ) IN
		(SELECT EMPNO, MAX(MOVEDAY) FROM HISTORY h1 GROUP BY EMPNO)  
AND SALARY BETWEEN 200 AND 500
AND DEPTNO  LIKE '%'||''||'%'
AND "RANK"  LIKE '%'||''||'%';
--2. 조건에 맞는 사원들의 평가 정보 WHERE BETWEEN 급여, 부서, 직급
SELECT  EMPNO, "RANK",DEPTNO, SALARY, 점수
FROM 
	(SELECT EMPNO, "RANK",DEPTNO,SALARY  FROM HISTORY H 
		WHERE (EMPNO ,MOVEDAY ) IN
				(SELECT EMPNO, MAX(MOVEDAY) FROM HISTORY h1 GROUP BY EMPNO)  
		AND SALARY BETWEEN 100 AND 500
		AND DEPTNO  LIKE '%'||''||'%'
		AND "RANK"  LIKE '%'||''||'%') FILTER1,
	(SELECT OBJ, ROUND(AVG(POINT),2) 점수 FROM appraisalAnswer GROUP BY OBJ) APPRE1
WHERE APPRE1.OBJ(+)=FILTER1.EMPNO
ORDER BY 점수
;
--2.2 +조건에 따라 나온것중에 최소최대평균 점수 보여주기
SELECT  AVG(점수) 평균 ,MAX(점수) 최대 ,MIN(점수) 최소
FROM 
	(SELECT EMPNO, "RANK",DEPTNO,SALARY  FROM HISTORY H 
		WHERE (EMPNO ,MOVEDAY ) IN
				(SELECT EMPNO, MAX(MOVEDAY) FROM HISTORY h1 GROUP BY EMPNO)  
		AND SALARY BETWEEN 100 AND 500
		AND DEPTNO  LIKE '%'||''||'%'
		AND "RANK"  LIKE '%'||''||'%') FILTER1,
	(SELECT OBJ, ROUND(AVG(POINT),2) 점수 FROM appraisalAnswer GROUP BY OBJ) APPRE1
WHERE APPRE1.OBJ(+)=FILTER1.EMPNO
ORDER BY 점수

----------------------------------------
--퇴사자 분석 : 퇴사자의 마지막 history, 가장많은 부서, 가장 많은 직급, 가장 많은 급여
SELECT deptno, COUNT(deptno) 
FROM history WHERE (empno, moveday) in(
SELECT h.empno, MIN(moveday) FROM history h
	WHERE h.empno in (SELECT EMPNO FROM RETIREMENT WHERE state LIKE '퇴사') GROUP BY h.empno)
GROUP BY DEPTNO ;

SELECT "RANK" , COUNT(*) 
FROM history WHERE (empno, moveday) in(
SELECT h.empno, MIN(moveday) FROM history h
	WHERE h.empno in (SELECT EMPNO FROM RETIREMENT WHERE state LIKE '퇴사') GROUP BY h.empno)
GROUP BY "RANK" ;

SELECT TRUNC(SALARY/100) "100단위 급여",COUNT(*)  
FROM history WHERE (empno, moveday) in(
SELECT h.empno, MIN(moveday) FROM history h
	WHERE h.empno in (SELECT EMPNO FROM RETIREMENT WHERE state LIKE '퇴사') GROUP BY h.empno)
GROUP BY TRUNC(SALARY/100) ;

--퇴사자 min() history랑 퇴사일 비교해서 근무기간 알아내기
SELECT r.EMPNO, ROUND((RETIREDAY -moveday)) 근무기간
FROM RETIREMENT r , HISTORY h2 
WHERE r.EMPNO =h2.EMPNO 
AND (r.EMPNO ,moveday) IN 
	(SELECT h.empno, MIN(moveday) FROM history h
	WHERE h.empno in (SELECT EMPNO FROM RETIREMENT WHERE state LIKE '퇴사') GROUP BY h.empno);
--개개인 말고 통계 근무기간
SELECT round(avg(RETIREDAY -moveday)) 평균, round(min(RETIREDAY -moveday)) 최소 , round(max(RETIREDAY -moveday)) 최대
FROM RETIREMENT r , HISTORY h2 
WHERE r.EMPNO =h2.EMPNO 
AND (r.EMPNO ,moveday) IN 
	(SELECT h.empno, MIN(moveday) FROM history h
	WHERE h.empno in (SELECT EMPNO FROM RETIREMENT WHERE state LIKE '퇴사') GROUP BY h.empno);


--퇴사자들의 사원평가 정보


SELECT * FROM EMPLOYEE e WHERE empno in (SELECT EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사');
SELECT * FROM history h , EMPloyee e WHERE e.EMPNO =h.EMPNO AND  e.empno =2022601663;
SELECT * FROM RETIREMENT r WHERE state LIKE '퇴사' ;
