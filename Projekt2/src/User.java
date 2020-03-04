
public class User {
	private String username;
	private String fullname;
	private Role role;
	private Division division;
	
	public User(String username, String fullname, Role role, Division division) {
		this.username = username;
		this.fullname = fullname;
		this.role = role;
		this.division = division;
	}
	
	public String getUsername() {
		return username;
	}

	public String getFullname() {
		return fullname;
	}

	public Role getRole() {
		return role;
	}
	
	public Division getDivision() {
		return division;
	}

	public String toString() {
		return username + ": " + fullname + ", " + role; 
	}
}
