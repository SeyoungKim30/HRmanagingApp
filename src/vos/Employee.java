package vos;

import java.util.Date;

public class Employee {
	/*
	 * CREATE TABLE Employee ( EMPNO CHAR(10) NOT NULL, 사원번호 name VARCHAR2(50) NOT
	 * NULL, 이름 birth DATE, 생년월일 address VARCHAR2(50), 주소 educational VARCHAR2(50),
	 * 학력 deptno NUMBER, 부서번호 RANK VARCHAR2(50), 직급 PASS VARCHAR2(50) NOT NULL 비밀번호
	 */
private int empno;
private String name;
private String birth;
private String address;
private String educational;
private int deptno;
private String rank;
private String pass;
private boolean access = false;
public Employee() {
	// TODO Auto-generated constructor stub
}
public Employee(int empno, String name, String birth, String address, String educational, int deptno, String rank,
		String pass) {
	this.empno = empno;
	this.name = name;
	this.birth = birth;
	this.address = address;
	this.educational = educational;
	this.deptno = deptno;
	this.rank = rank;
	this.pass = pass;
}
public Employee(int empno, String name) {
	this.empno = empno;
	this.name = name;
}

public void printAll() {
	System.out.println("━━━━━━━━━━━━━━━");
	System.out.println("사번:\t"+empno);
	System.out.println("이름:\t"+name);
	System.out.println("생년월일:\t"+birth);
	System.out.println("학력:\t"+educational);
	System.out.println("부서번호:\t"+deptno);
	System.out.println("직급\t"+rank);
	System.out.println("━━━━━━━━━━━━━━━");
}

public void setAccess() {
	if(rank.equals("사장")) {
		this.access= true;
	}
	if(rank.equals("이사")) {
		this.access= true;
	}
	if(deptno==10) {
		this.access= true;
	}
}
public boolean getAccess() {
	return access;
}

public int getEmpno() {
	return empno;
}
public String getName() {
	return name;
}
public String getBirth() {
	return birth;
}
public String getAddress() {
	return address;
}
public String getEducational() {
	return educational;
}
public int getDeptno() {
	return deptno;
}
public String getRank() {
	return rank;
}
public String getPass() {
	return pass;
}
}
