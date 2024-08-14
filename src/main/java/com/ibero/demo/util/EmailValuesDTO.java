package com.ibero.demo.util;

public class EmailValuesDTO {
	
	private String mailFrom;//De
    private String mailTo;//Para
    private String subject;//Asunto
    private String userName;//username del correo
    private String newuserpass;//nuevo password restablecido del sistema
    
	public String getMailFrom() {
		return mailFrom;
	}
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}
	public String getMailTo() {
		return mailTo;
	}
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNewuserpass() {
		return newuserpass;
	}
	public void setNewuserpass(String newuserpass) {
		this.newuserpass = newuserpass;
	}
    
}
