package com.ibero.demo.entity;

public class User {
	
	private People idPeople; // Obtendría el id de la persona
	private int idRolUser; 
    private String UserName;
    private String Userpassword;
    private String UserState;
    
	public User() {

	}

	public User(People idPeople, int idRolUser, String userName, String userpassword, String userState) {
		super();
		this.idPeople = idPeople;
		this.idRolUser = idRolUser;
		UserName = userName;
		Userpassword = userpassword;
		UserState = userState;
	}

	public People getIdPeople() {
		return idPeople;
	}

	public void setIdPeople(People idPeople) {
		this.idPeople = idPeople;
	}

	public int getIdRolUser() {
		return idRolUser;
	}

	public void setIdRolUser(int idRolUser) {
		this.idRolUser = idRolUser;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUserpassword() {
		return Userpassword;
	}

	public void setUserpassword(String userpassword) {
		Userpassword = userpassword;
	}

	public String getUserState() {
		return UserState;
	}

	public void setUserState(String userState) {
		UserState = userState;
	}  
	
}
