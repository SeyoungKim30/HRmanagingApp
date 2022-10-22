SELECT * FROM worktable WHERE empno=2022201020	; 
SELECT * FROM SCHEDULE s WHERE empno=2022201020	 ; 
--INSERT INTO WORKTABLE VALUES (2022201020,sysdate-2,sysdate-2+(8/24));
SELECT distinct Trunc(TIMEON,'dd') FROM worktable;  --19,20,21
SELECT DISTINCT TRUNC(INTIME,'dd'), s. * FROM SCHEDULE s;	--20,21,22

SELECT timeon, intime FROM WORKTABLE w FULL OUTER JOIN SCHEDULE s ON TRUNC(s.INTIME,'dd') =Trunc(w.TIMEON,'dd') ;
SELECT DISTINCT TRUNC(s.INTIME,'dd'), Trunc(w.TIMEON,'dd') FROM WORKTABLE w FULL OUTER JOIN SCHEDULE s ON TRUNC(s.INTIME,'dd') =Trunc(w.TIMEON,'dd') ; --출근날짜만

SELECT DISTINCT w.empno, timeon 출근시간, intime 스케줄 , timeoff 퇴근시간, outtime 최소퇴근, Nvl((intime - timeon)*24*60,0) ,Nvl((timeoff-outtime)*24*60,0) 분  FROM WORKTABLE w FULL OUTER JOIN SCHEDULE s ON TRUNC(s.INTIME,'dd') =Trunc(w.TIMEON,'dd') ;
SELECT  Nvl((timeon - intime)*24*60,0) + Nvl((timeoff - outtime)*24*60,0) 초과근무몇분   FROM WORKTABLE w FULL OUTER JOIN SCHEDULE s ON TRUNC(s.INTIME,'dd') =Trunc(w.TIMEON,'dd') ;


SELECT (sysdate - to_date('2022-10-20 14:15:00','yyyy-mm-dd HH24:mi:ss'))*24*60 FROM dual;

----- 합치기 완성 : 직원별
SELECT DISTINCT w.empno, timeon 실제출근, intime 배정출근 , timeoff 실제퇴근, outtime 배정된퇴근, 
	(intime - timeon)*24 일찍온시간 ,(timeoff-outtime)*24 초과시간, round(nvl(nvl((intime - timeon)*24  + (timeoff-outtime)*24 , (outtime-intime)*24),(timeoff-timeon)*24)) 총초과
FROM WORKTABLE w FULL OUTER JOIN SCHEDULE s ON Trunc(w.TIMEON,'dd') =TRUNC(s.INTIME,'dd')
WHERE w.empno like '%'||2022201020||'%' 
AND TRUNC(s.INTIME,'mm') like to_date('2022-10','yyyy-mm') ORDER BY timeon;

--합치기 완성 : 이 직원이 한달동안 초과근무한 시간은
SELECT sum(총초과) FROM (
SELECT DISTINCT timeon,intime, 
nvl(nvl((intime - timeon)*24  + (timeoff-outtime)*24 , (outtime-intime)*24),(timeoff-timeon)*24) 총초과
FROM WORKTABLE w FULL OUTER JOIN SCHEDULE s ON TRUNC(s.INTIME,'dd') =Trunc(w.TIMEON,'dd') 
WHERE w.empno like '%'||2022201020||'%' AND TRUNC(s.INTIME,'mm') like to_date('2022-10','yyyy-mm'));


------날짜별
SELECT DISTINCT w.empno, timeon 출근시간, intime 스케줄 , timeoff 퇴근시간, outtime 최소퇴근, 
	(intime - timeon)*24 일찍온시간 ,(timeoff-outtime)*24 초과시간, nvl(nvl((intime - timeon)*24  + (timeoff-outtime)*24 , (outtime-intime)*24),(timeoff-timeon)*24)
FROM WORKTABLE w FULL OUTER JOIN SCHEDULE s ON TRUNC(s.INTIME,'dd') =Trunc(w.TIMEON,'dd') ORDER BY timeon;
--WHERE TRUNC(INTIME,'dd') = TRUNC(SYSDATE ,'dd') OR  TRUNC(timeon,'dd') = TRUNC(SYSDATE ,'dd')  ORDER BY timeon;

