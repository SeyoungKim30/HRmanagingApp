package vos;

public class Appraisal {
int queno;
String question;
int sub;
int obj;

public Appraisal() {}
public Appraisal(int queno, String question) {
	this.queno = queno;
	this.question = question;
}
public Appraisal(int queno, int obj) {
	this.queno = queno;
	this.obj = obj;
}
public Appraisal(int queno, String question, int sub, int obj) {
	this.queno = queno;
	this.question = question;
	this.sub = sub;
	this.obj = obj;
}
public int getSub() {
	return sub;
}
public int getObj() {
	return obj;
}
public int getQueno() {
	return queno;
}
public String getQuestion() {
	return question;
}
}
