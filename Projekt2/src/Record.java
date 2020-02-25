
public class Record {
	private Division division;
	private User doctor;
	private User patient;
	private User nurse;
	private static String data;
	//private static int lastEdited;

	public Record(User doctor, User patient, User nurse, Division division){
		this.doctor = doctor;
		this.patient = patient;
		this.nurse = nurse;
		this.division = division;
	}

	public String getData(){
		return data;		
	}
	
	public Division getDivision(){
		return division;
	}
	
	/* public int getLastEdited(){
		return lastEdited;
	} */
	
	public void setData(User user, String newData){
		data = data + "\n" + newData;
	}

	public String getPermissions(User user){
		switch(user.getRole()){
			case GOV:
				return "rw";
			case DOCTOR:
				if(user.getDivision() == division){
					return "rw";
				}
				return "";
			case NURSE:
				if(user.getUsername().equals(nurse.getUsername())){
					return "rw";
				}
				return "";
			case PATIENT:
				if(user.getUsername().equals(patient.getUsername())){
					return "r";
				}
				return "";
			default:
				return "";
		}
	}

}
