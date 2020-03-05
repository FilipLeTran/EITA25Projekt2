import java.util.UUID;

public class Record {
	private Division division;
	private User doctor;
	private User patient;
	private User nurse;
	private String recordID;
	private String data;

	public Record(User doctor, User patient, User nurse){
		this.doctor = doctor;
		this.patient = patient;
		this.nurse = nurse;
		this.division = doctor.getDivision();
		this.recordID = UUID.randomUUID().toString().substring(0,8);
		data = "";
	}

	public String getRecordID() {
		return recordID;
	}
	
	public String getData(){
		return data;		
	}
	
	public Division getDivision(){
		return division;
	}
	
	public String getPatientName() {
		return patient.getFullname();
	}
	
	public void appendData(String newData){
		data+= "\n" + newData;
	}

	public String getPermissions(User user){
		switch(user.getRole()){
			case ADMIN:
				return "rwd";
			case GOV:
				return "rd";
			case DOCTOR:
				if(user.getDivision().equals(division)){
					return "rw";
				}
				return "";
			case NURSE:
				if(user.getUsername().equals(nurse.getUsername())){
					return "rw";
				} else if(user.getDivision().equals(division)) {
					return "r";
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
	
	public String toString() {
		return "Record ID: " + recordID + "\nPatient: " + patient.getFullname() +"\nDoctor: " + doctor.getFullname() + "\nNurse: " + nurse.getFullname() + "\nDivision: " + division + "\n" + data;
	}

}
