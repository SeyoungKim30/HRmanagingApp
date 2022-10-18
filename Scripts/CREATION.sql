/* 사원 */
DROP TABLE Employee 
	CASCADE CONSTRAINTS;

/* 사원 */
CREATE TABLE Employee (
	EMPNO CHAR(10)  NOT NULL, /* 사원번호 */
	name VARCHAR2(50) NOT NULL, /* 이름 */
	birth DATE, /* 생년월일 */
	address VARCHAR2(50), /* 주소 */
	educational VARCHAR2(50), /* 학력 */
	deptno NUMBER, /* 부서번호 */
	RANK VARCHAR2(50), /* 직급 */
	PASS VARCHAR2(50) NOT NULL /* 비밀번호 */
);

CREATE UNIQUE INDEX PK_Employee
	ON Employee (
		EMPNO ASC
	);

ALTER TABLE Employee
	ADD
		CONSTRAINT PK_Employee
		PRIMARY KEY (
			EMPNO
		);

ALTER TABLE Employee
	ADD
		CONSTRAINT FK_DEPARTMENT_TO_Employee
		FOREIGN KEY (
			deptno
		)
		REFERENCES DEPARTMENT (
			deptno
		);
		
	
	/* 부서 */
DROP TABLE DEPARTMENT 
	CASCADE CONSTRAINTS;

/* 부서 */
CREATE TABLE DEPARTMENT ( deptno NUMBER NOT NULL, DNAME VARCHAR2(50));

CREATE UNIQUE INDEX PK_DEPARTMENT
	ON DEPARTMENT (
		deptno ASC
	);

ALTER TABLE DEPARTMENT
	ADD
		CONSTRAINT PK_DEPARTMENT
		PRIMARY KEY (
			deptno
		);
		
	/* 이동정보 */
DROP TABLE HISTORY 
	CASCADE CONSTRAINTS;

/* 이동정보 */
CREATE TABLE HISTORY (
	EMPNO CHAR(10)  NOT NULL, /* 사원번호 */
	moveday DATE, /* 변경일 */
	RANK VARCHAR2(50), /* 직급 */
	deptno NUMBER(2) /* 부서번호 */
);
ALTER TABLE SCOTT.HISTORY DROP CONSTRAINT PK_HISTORY;

ALTER TABLE HISTORY
	ADD
		CONSTRAINT FK_Employee_TO_HISTORY
		FOREIGN KEY (
			EMPNO
		)
		REFERENCES Employee (
			EMPNO
		);

ALTER TABLE HISTORY
	ADD
		CONSTRAINT FK_DEPARTMENT_TO_HISTORY
		FOREIGN KEY (
			deptno
		)
		REFERENCES DEPARTMENT (
			deptno
		);
		
/* 인사평가 */
DROP TABLE appraisal 
	CASCADE CONSTRAINTS;

/* 인사평가 */
CREATE TABLE appraisal (
	Queno NUMBER NOT NULL, /* 질문번호 */
	question VARCHAR2(100) NOT NULL, /* 질문 */
	point NUMBER, /* 점수 */
	obj CHAR(10) , /* 피평가자 */
	sub CHAR(10)  /* 평가자 */
);

ALTER TABLE appraisal
	ADD
		CONSTRAINT FK_Employee_TO_appraisal
		FOREIGN KEY (
			obj
		)
		REFERENCES Employee (
			EMPNO
		);

ALTER TABLE appraisal
	ADD
		CONSTRAINT FK_Employee_TO_appraisal2
		FOREIGN KEY (
			sub
		)
		REFERENCES Employee (
			EMPNO
		);