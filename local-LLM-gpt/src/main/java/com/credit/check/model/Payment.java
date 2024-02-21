package com.credit.check.model;

public class Payment 
{
	String source;
	String firstName;
	String lastName;
	String maritalStatus;		
	String citizenship;
	String currentResidenceCountry;		
	String creditcardnumber;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getCitizenship() {
		return citizenship;
	}
	public void setCitizenship(String citizenship) {
		this.citizenship = citizenship;
	}
	public String getCurrentResidenceCountry() {
		return currentResidenceCountry;
	}
	public void setCurrentResidenceCountry(String currentResidenceCountry) {
		this.currentResidenceCountry = currentResidenceCountry;
	}
	public String getCreditcardnumber() {
		return creditcardnumber;
	}
	public void setCreditcardnumber(String creditcardnumber) {
		this.creditcardnumber = creditcardnumber;
	}
}
