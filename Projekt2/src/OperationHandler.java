
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
		    				if(user.getRole() == Role.ADMIN) {
		    					return addUser(input.substring(input.indexOf(operations[2])));
		    				}
		    				return "Access denied";
		    			case "record":
		    				if(user.getRole() == Role.DOCTOR || user.getRole() == Role.ADMIN) {
		    					return addRecord(user, operations[2], input.substring(input.indexOf(operations[3])));
		    				}
		    				return "Access denied";
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
    		String[] inputs = data.split(" ");
	    	User patient = Server.getUser(inputs[0]);
	    	User nurse = Server.getUser(inputs[1]);
	    	if(patient == null || nurse == null) {
	    		return "User not found.";
	    	}
	    	String text = inputs[2];
	    	Server.records.put(recordname, new Record(user, patient, nurse, text)); 
	    	return "Record added successfully.";
    	} catch(Exception e) {
    		e.printStackTrace();
    		return "Input error.";
    	}
    }
    
    private static String openRecord(User user, String recordname) {
    	Record record = Server.records.get(recordname);
    	String recordString = "No record found.";
    	if(record.getPermissions(user).contains("r")) {
    		recordString = "-----------------------------------\n";
			recordString+=record.toString() + "-----------------------------------\n";
		}
    	return recordString;
    }
    
    private static String listRecords(User user) {
    	String recordString = "-----------------------------------\n";
    	for(Record record : Server.records.values()) {
    		if(record.getPermissions(user).contains("r")) {
    			recordString+=record.toString() + "-----------------------------------\n";
    		}
    	}
    	return recordString;
    }
    
    private static String addUser(String data) {
    	// username Lastname,Firstname role
    	try {
    		String[] inputs = data.split(" ");
    		for(String s : inputs) {
    			System.out.println(s);
    		}
	        String username = inputs[0];
	        String fullname = inputs[1];
	        String rolestr = inputs[2];
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
	        Server.users.put(username, new User(username, fullname, role));
	        return "User added successfully.";
    	} catch(Exception e){
        	e.printStackTrace();
        	return "Input error.";
        }
    }
}
