
public class OperationHandler {
	public static boolean handleInput(User user, String input) {
    	try {
	    	int sep1 = input.indexOf(' ');
	        int sep2 = input.indexOf(' ', sep1+1);
	    	String operation = input.substring(0, sep1);
	    	String recordname = input.substring(sep1+1, sep2);
	    	String data = input.substring(sep2+1, input.length());
	    	switch(operation) {
	    	case "open":
	    		return false;
	    	case "remove":
	    		return false;
	    	case "add":
	    		return false;
	    	default:
	    		System.out.println("Unspecified action");
	    		return false;
    	}
    	} catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	
    }
    
    private boolean addRecord(User user, String recordname, String data) {
    	try {
    		int sep1 = data.indexOf(' ');
	        int sep2 = data.indexOf(' ', sep1+1);
	    	String p_username = data.substring(0, sep1);
	    	User patient = 
	    	String n_username = data.substring(sep1+1, sep2);
	    	String text = data.substring(sep2+1, data.length());
	    	Server.records.put(recordname, new Record(user,))
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
