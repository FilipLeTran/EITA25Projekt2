
public class OperationHandler {
	
	//Bestämmer vilken sorts operation som användaren skickat in via klienten.
	//Returnerar "Unspecified operation" om operationen inte finns.
	public static String handleInput(User user, String input) {
    	try {
    		String[] operations = input.split(" ");
	    	switch(operations[0]) {
		    	case "open":
		    		return openRecord(user, operations[1]);
		    	case "remove":
		    		return "Failed remove operation";
		    	case "add":
		    		switch(operations[1]) {
		    			case "user":
		    				return addUser(user, input.substring(input.indexOf(operations[2])));
		    			case "record":
		    				return addRecord(user, operations[2], input.substring(input.indexOf(operations[3])));
		    		}
		    	case "list":
		    		switch(operations[1]) {
		    			case "records":
		    				return listRecords(user);
		    		}
		    	default:
		    		return "Unspecified operation.";
	    	}
    	} catch(Exception e) {
    		e.printStackTrace();
    		return "Input error.";
    	}
    	
    }
    
	// TODO fixa så att denna metod kan hantera data utan ett textfält.
	// Skriver över andra records??
    private static String addRecord(User user, String recordname, String data) {
    	try {
    		if(user.getRole() == Role.DOCTOR || user.getRole() == Role.ADMIN) {
	    		String[] inputs = data.split(" ");
		    	User patient = Server.getUser(inputs[0]);
		    	User nurse = Server.getUser(inputs[1]);
		    	if(patient == null || nurse == null) {
		    		return "User not found.";
		    	}
		    	String text = inputs[2];
		    	Server.records.put(recordname, new Record(user, patient, nurse, recordname, text));
		    	Server.serverLog.newEntry(user.getUsername() + " added record " + recordname);
		    	return "Record added successfully.";
    		}
    		return "Permission denied";
    	} catch(Exception e) {
    		e.printStackTrace();
    		return "Input error.";
    	}
    }
    
    private static String openRecord(User user, String recordname) {
    	Record record = Server.records.get(recordname);
    	String recordString;
    	String logEntry;
    	if(record.getPermissions(user).contains("r")) {
    		recordString = "-----------------------------------\n";
			recordString+=record.toString() + "-----------------------------------";
			logEntry = user.getUsername() + " opened record " + recordname;
		} else {
			recordString = "Permission denied.";
			logEntry = user.getUsername() + " tried to open " + recordname + ". Permission denied.";
		}
    	Server.serverLog.newEntry(logEntry);
    	return recordString;
    }
    
    private static String listRecords(User user) {
    	String recordString = "-----------------------------------\n";
    	for(Record record : Server.records.values()) {
    		if(record.getPermissions(user).contains("r")) {
    			recordString+=record.toString() + "-----------------------------------\n";
    		}
    	}
    	Server.serverLog.newEntry(user.getUsername() + " used the list records command.");
    	return recordString.substring(0, recordString.length()-2);
    }
    
    private static String addUser(User user, String data) {
    	// username Lastname,Firstname role
    	try {
    		if(user.getRole() != Role.ADMIN) {
    			return "Permission denied.";
    		}
    		String[] inputs = data.split(" ");
    		for(String s : inputs) {
    			System.out.println(s);
    		}
	        String username = inputs[0];
	        String fullname = inputs[1];
	        String rolestr = inputs[2];
	        String divisionstr = inputs[3];
	        Role role;
	        switch(rolestr) {
				case "patient":
					role = Role.PATIENT;
					break;
				case "nurse":
					role = Role.NURSE;
					break;
				case "doctor":
					role = Role.DOCTOR;
					break;
				case "gov":
					role = Role.GOV;
					break;
				case "admin":
					role = Role.ADMIN;
					break;
				default:
					return "Please enter a valid role.";
					
	        }
	        Division division;
	        switch(divisionstr) {
			case "medicine":
				division = Division.MEDICINE;
				break;
			case "nurse":
				division = Division.NEUROLOGY;
				break;
			case "doctor":
				division = Division.SURGERY;
				break;
			case "gov":
				division = Division.RADIOLOGY;
				break;
			default:
				return "Please enter a valid division.";
				
        }
	        Server.users.put(username, new User(username, fullname, role, division));
	    	Server.serverLog.newEntry(user.getUsername() + " added user " + username + " (" + role + ").");
	        return "User added successfully.";
    	} catch(Exception e){
        	e.printStackTrace();
        	return "Input error.";
        }
    }
}
