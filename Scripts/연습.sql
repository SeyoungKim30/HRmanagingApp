CREATE TABLE EmpHR (
 empno VARCHAR2(50)
);
INSERT INTO emphr VALUES ('345345');
INSERT INTO emphr VALUES ('asdfw3265');
INSERT INTO emphr VALUES ('655sdaa');
INSERT INTO emphr VALUES ('l;kj76');


CREATE TABLE attendeeList (
 listID CHAR(8) NOT NULL, /* 신청자목록id */
 empno VARCHAR2(50), /* 사번 */
 COL2 DATE /* 신청일 */
);
INSERT INTO ATTENDEELIST VALUES ('23fddsa7','345345',sysdate);
INSERT INTO ATTENDEELIST VALUES ('7295foa','345345',sysdate);
INSERT INTO ATTENDEELIST VALUES ('akdo82sd','655sdaa',sysdate);
INSERT INTO ATTENDEELIST VALUES ('43453533','655sdaa',sysdate);
INSERT INTO ATTENDEELIST VALUES ('43453533','345345',sysdate);
INSERT INTO ATTENDEELIST VALUES ('43453533','asdfw3265',sysdate);
INSERT INTO ATTENDEELIST VALUES ('43453533','l;kj76',sysdate);

CREATE TABLE training (
 Title VARCHAR2(50) NOT NULL, /* 교육명 */
 listID CHAR(8) /* 신청자목록id */
);

INSERT INTO training VALUES ('다시읽는세계사','43453533');
INSERT INTO training VALUES ('영어400','7295foa');
INSERT INTO training VALUES ('소방안전교육','23fddsa7');
INSERT INTO training VALUES ('직장내괴롭힘예방교육','akdo82sd');


--------------------------------
select * from training t , attendeeList a	--트레이닝별 참석자
where t.listID=a.listID AND t.title LIKE '%교%';

select * from attendeeList a , EmpHR e where a.empno=e.empno 
	AND listID LIKE '%fddsa7%';	--트레이닝별 참석자
	
select E.EMPNO, T.TITLE from attendeeList a , EmpHR e, TRAINING T --한사람이 들은 교육명
where a.empno=e.empno AND T.LISTID=A.LISTID
AND A.EMPNO = '655sdaa' ;	
