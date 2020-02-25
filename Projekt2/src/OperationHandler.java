
public class OperationHandler {
	
	
	public static String handleInput(User user, String input) {
    	try {
    		String[] operations = input.split(" ");
    		for(String s : operations) {
    			System.out.println(s);
    		}
	    	switch(operations[0]) {
		    	case "open":
		    		return "Failed open operation";
		    	case "remove":
		    		return "Failed remove operation";
		    	case "add":
		    		switch(operations[1]) {
		    			case "user":
		    				if(user.getRole() == Role.ADMIN) {
		    					if(addUser(input.substring(input.indexOf(operations[1])+1))) {
		    						return "User added succesfully";
		    					}
		    				}
		    			case "record":
		    				if(user.getRole() == Role.DOCTOR || user.getRole() == Role.ADMIN) {
		    					if(addRecord(user, operations[2], input.substring(input.indexOf(operations[2])+1))) {
		    						return "Record added successfully.";
		    					}
		    				}
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
    		return "Error: " + e.getCause();
    	}
    	
    }
    
	// TODO fixa så att denna metod kan hantera data utan ett textfält.
    private static boolean addRecord(User user, String recordname, String data) {
    	try {
    		String[] inputs = data.split(" ");
	    	User patient = Server.getUser(inputs[0]);
	    	User nurse = Server.getUser(inputs[1]);
	    	String text = inputs[2];
	    	Server.records.put(recordname, new Record(user, patient, nurse, text)); 
	    	return true;
    	} catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
    private static boolean addUser(String data) {
    	// username Lastname,Firstname role
    	try {
    		String[] inputs = data.split(" ");
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
					return false;
					
	        }
	        Server.users.put(username, new User(username, fullname, role));
	        return true;
    	} catch(Exception e){
        	e.printStackTrace();
        	return false;
        }
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
}
