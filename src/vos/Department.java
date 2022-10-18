package vos;

public class Department {
	
private int deptno;
private String dname;
private int cnt;


public Department(int deptno, String dname, int cnt) {
	this.deptno = deptno;
	this.dname = dname;
	this.cnt = cnt;
}
public Department(int deptno, String dname) {
	this.deptno = deptno;
	this.dname = dname;
}
public Department() {}

public void printAll() {
	System.out.println("┃부서번호 : "+deptno+"\t┃부서명 : "+dname+"\t┃인원 : "+cnt);
}

public int getDeptno() {
	return deptno;
}
public void setDeptno(int deptno) {
	this.deptno = deptno;
}
public String getDname() {
	return dname;
}
public void setDname(String dname) {
	this.dname = dname;
}


}
