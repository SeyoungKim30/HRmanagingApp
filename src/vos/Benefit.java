package vos;

public class Benefit {

String listnumber;
String title;
String condi1;
String condi2;
String reportDay;
String cost;

public Benefit() {}

public Benefit(String listnumber,String title, String condi1, String condi2, String reportDay, String cost) {
	this.listnumber=listnumber;
	this.title = title;
	this.condi1 = condi1;
	this.condi2 = condi2;
	this.reportDay = reportDay;
	this.cost = cost;
}

public Benefit(String title, String condi1, String condi2, String reportDay, String cost) {
	this.title = title;
	this.condi1 = condi1;
	this.condi2 = condi2;
	this.reportDay = reportDay;
	this.cost = cost;
}
public String getTitle() {
	return title;
}
public String getCondi1() {
	String getCon1=condi1;
	if(condi1.equals("X")) getCon1="'||NULL||'";
	return getCon1;
}
public String getCondi2() {
	String getCon2=condi2;
	if(condi2.equals("X")) getCon2="'||NULL||'";
	return getCon2;
}
public int getReportDay() {
	return Integer.parseInt(reportDay);
}
public int getCost() {
	return Integer.parseInt(cost);
}

public void printAll() {
	System.out.println("━━━━━━━━"+title+"━━━━━━━━");
	System.out.println("복지번호: "+listnumber);
	System.err.println("직급조건: "+ condi1);
	System.out.println("부서조건: "+ condi2);
	System.out.println("지원기간: " + reportDay+" 까지");
} 


}