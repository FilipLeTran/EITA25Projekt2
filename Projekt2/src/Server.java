import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

public class Server implements Runnable {
    private ServerSocket serverSocket = null;
    public static HashMap<String, User> users;
    public static HashMap<String, Record> records;
    private static int numConnectedClients = 0;
    public static Log serverLog;

    public Server(ServerSocket ss) throws IOException {
        serverSocket = ss;
        users = new HashMap<>();
        records = new HashMap<>();
        users.put("nurse", new User("nurse", "Eva Sadu", Role.NURSE, Division.MEDICINE));
        users.put("nurse2", new User("nurse2", "Martin Mattson", Role.NURSE, Division.SURGERY));
        users.put("patient", new User("patient", "Rolf Roffino", Role.PATIENT, Division.SURGERY));
        users.put("patient2", new User("patient2", "Adam Dam", Role.PATIENT, Division.MEDICINE));
        users.put("patient3", new User("patient3", "Rebecca Snäcka", Role.PATIENT, Division.RADIOLOGY));
        users.put("doctor", new User("doctor", "Dr. Jan Jansson", Role.DOCTOR, Division.MEDICINE));
        users.put("doctor2", new User("doctor2", "Dr. Heja Hooa", Role.DOCTOR, Division.SURGERY));
        users.put("agent", new User("agent", "John Smith", Role.GOV, Division.MISC));

        users.put("testare", new User("testare", "Testare", Role.ADMIN, Division.MISC));
        Record r1 = new Record(users.get("doctor"), users.get("patient"), users.get("nurse"));
        Record r2 = new Record(users.get("doctor2"), users.get("patient2"), users.get("nurse2"));
        records.put(r1.getRecordID(), r1);
        records.put(r2.getRecordID(), r2);
        serverLog = new Log("log.txt");
        newListener();
    }

    public void run() {
        try {
            SSLSocket socket=(SSLSocket)serverSocket.accept();
            newListener();
            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName();
            User user = getUser(subject.substring(subject.indexOf('"')+1, subject.indexOf(",")));
            Boolean killsession = false;
            if(user != null) {
            	numConnectedClients++;
            	System.out.println(user + " connected");
                System.out.println(numConnectedClients + " concurrent connection(s)\n");
            } else {
            	System.out.println("Unauthorized user connected to server but was dropped.");
                System.out.println(numConnectedClients + " concurrent connection(s)\n");
                killsession = true;
            }
            
            PrintWriter out = null;
            BufferedReader in = null;
            out = new PrintWriter(socket.getOutputStream(), true);
            if(killsession) {
            	out.println("Connection dropped. You have not yet been added to the server whitelist.");
            	out.flush();
            	return;
            }
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("Welcome, " + user.getUsername() + "!");
            
            String clientInput = null;
            while ((clientInput = in.readLine()) != null) {
					out.println(OperationHandler.handleInput(user, clientInput));
					out.flush();
			}
			in.close();
			out.close();
			socket.close();
    	    numConnectedClients--;
            System.out.println(user.getUsername() + " disconnected");
            System.out.println(numConnectedClients + " concurrent connection(s)\n");
		} catch (IOException e) {
            System.out.println("Client died: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    private void newListener() { (new Thread(this)).start(); } // calls run()

    public static void main(String args[]) {
        System.out.println("\nServer Started\n");
        int port = -1;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        String type = "TLS";
        try {
            ServerSocketFactory ssf = getServerSocketFactory(type);
            ServerSocket ss = ssf.createServerSocket(port);
            ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
            new Server(ss);
        } catch (IOException e) {
            System.out.println("Unable to start Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    
    public static User getUser(String username) {
    	if(users.containsKey(username)) {
    		return users.get(username);
    	}
    	return null;
    }

    private static ServerSocketFactory getServerSocketFactory(String type) {
        if (type.equals("TLS")) {
            SSLServerSocketFactory ssf = null;
            try { // set up key manager to perform server authentication
                SSLContext ctx = SSLContext.getInstance("TLS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                KeyStore ks = KeyStore.getInstance("JKS");
				KeyStore ts = KeyStore.getInstance("JKS");
                char[] password = "password".toCharArray();

                ks.load(new FileInputStream("serverkeystore"), password);  // keystore password (storepass)
                ts.load(new FileInputStream("servertruststore"), password); // truststore password (storepass)
                kmf.init(ks, password); // certificate password (keypass)
                tmf.init(ts);  // possible to use keystore as truststore here
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                ssf = ctx.getServerSocketFactory();
                return ssf;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return ServerSocketFactory.getDefault();
        }
        return null;
    }
}