package vos;

public class Benefit {

String listnumber;
String title;
String condi;
String reportDay;
String cost;

public Benefit() {}

public Benefit(String listnumber, String condi) {
	this.listnumber = listnumber;
	this.condi = condi;
}

public Benefit(String listnumber, String title, String condi, String reportDay, String cost) {
	this.listnumber = listnumber;
	this.title = title;
	this.condi = condi;
	this.reportDay = reportDay;
	this.cost = cost;
}


public String getListnumber() {
	return listnumber;
}


public String getTitle() {
	return title;
}


public String getCondi() {
	return condi;
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
	System.out.println("지원기간: " + reportDay+" 까지");
} 


}