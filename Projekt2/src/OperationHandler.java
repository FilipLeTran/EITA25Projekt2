
public class OperationHandler {
	
	//Bestämmer vilken sorts operation som användaren skickat in via klienten.
	//Returnerar "Unspecified operation" om operationen inte finns.
	public static String handleInput(User user, String input) {
    	try {
    		String[] operations = input.split(" ");
	    	switch(operations[0]) {
		    	case "open":
		    		return openRecord(user, operations[1]);
		    	case "delete":
		    		return deleteRecord(user, operations[1]);
		    	case "write":
		    		return writeToRecord(user, operations[1], input.substring(input.indexOf(operations[2])));
		    	case "add":
		    		switch(operations[1]) {
		    			case "user":
		    				return addUser(user, input.substring(input.indexOf(operations[2])));
		    			case "record":
		    				return addRecord(user, input.substring(input.indexOf(operations[2])));
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
    
    private static String addRecord(User user, String data) {
    	try {
    		if(!user.getPermittedOperations().contains("a")) {
    	    	Server.serverLog.newEntry(user.getUsername() + " tried to add a record. Permission denied");
    			return "Permission denied";
    		}
	    	String[] inputs = data.split(" ");
		   	User patient = Server.getUser(inputs[0]);
		   	User nurse = Server.getUser(inputs[1]);
		   	if(patient == null || nurse == null) {
		   		return "User not found.";
		   	}
		   	Record r = new Record(user, patient, nurse);
	    	Server.records.put(r.getRecordID(), r);
	    	Server.serverLog.newEntry(user.getUsername() + " added record " + r.getRecordID());
		    return "Record added successfully.";
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
			recordString+=record.toString() + "\n-----------------------------------";
			logEntry = user.getUsername() + " opened record " + recordname;
		} else {
			recordString = "Permission denied.";
			logEntry = user.getUsername() + " tried to open " + recordname + ". Permission denied.";
		}
    	Server.serverLog.newEntry(logEntry);
    	return recordString;
	}
    
    private static String writeToRecord(User user, String recordName, String data) {
    	Record record = Server.records.get(recordName);
    	if(!record.getPermissions(user).contains("w")) {
    		Server.serverLog.newEntry(user.getUsername() + " tried to write to record " + recordName + ". Permission denied.");
    		return "Permission denied.";
    	} else {
    		record.appendData(data);
			Server.serverLog.newEntry(user.getUsername() + " wrote " + '"' + data + '"' + " to record " + recordName);
			return "Success.";
    	}
    }
	
	private static String deleteRecord(User user, String recordName){
		Record record = Server.records.get(recordName);
		
		if(!user.getPermittedOperations().contains("d")) {
			Server.serverLog.newEntry(user.getUsername() + " tried deleting record " + recordName + ". Permission denied.");
			return "Permission denied.";
		} else if(record == null) {
			return "No such record.";
		} else {
			Server.records.remove(recordName);
			Server.serverLog.newEntry(user.getUsername() + " deleted record " + recordName);
			return "Record deleted successfully.";
		}
	}
    
    private static String listRecords(User user) {
    	String recordString = "Available records:\n--------------------------------------------\n";
    	for(Record record : Server.records.values()) {
    		if(record.getPermissions(user).contains("r")) {
    			recordString+= "Record ID: " + record.getRecordID() + "   Patient: " + record.getPatientName() + "   Permissions: " + record.getPermissions(user) +"\n";
    		}
    	}
    	recordString+="--------------------------------------------";
    	Server.serverLog.newEntry(user.getUsername() + " used the list records command.");
    	return recordString.substring(0, recordString.length()-2);
    }
    
    private static String addUser(User user, String data) {
    	// username Lastname,Firstname role
    	try {
    		if(!user.getPermittedOperations().contains("a")) {
    			return "Permission denied";
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
