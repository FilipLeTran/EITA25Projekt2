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

    public Server(ServerSocket ss) throws IOException {
        serverSocket = ss;
        users = new HashMap<>();
        records = new HashMap<>();
        newListener();
    }

    public void run() {
        try {
            SSLSocket socket=(SSLSocket)serverSocket.accept();
            newListener();
            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName();
            User user = initiateUser(subject);
            if(user instanceof User) {
            	if(!users.containsKey(user.getUsername())) {
            		users.put(user.getUsername(), user);
            	}
            	numConnectedClients++;
            } else {
                System.out.println(numConnectedClients + " concurrent connection(s)\n");
            	return;
            }
     
            System.out.println(user + " connected");
            
            System.out.println(numConnectedClients + " concurrent connection(s)\n");

            PrintWriter out = null;
            BufferedReader in = null;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String clientMsg = null;
            while ((clientMsg = in.readLine()) != null) {
			    String rev = new StringBuilder(clientMsg).reverse().toString();
                System.out.println("received '" + clientMsg + "' from client");
                System.out.print("sending '" + rev + "' to client...");
				out.println(rev);
				out.flush();
                System.out.println("done\n");
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
    
    private User initiateUser(String subject) {
    	// username,Full Name,role
    	try {
	        int firstMarker = subject.indexOf(',');
	        int secMarker = subject.indexOf(',', firstMarker+1);
	        String username = subject.substring(subject.indexOf('"')+1, firstMarker);
	        String fullname = subject.substring(firstMarker+1, secMarker);
	        String rolestr = subject.substring(secMarker+1, subject.indexOf('"', secMarker+1)); //fixa rätt index
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
				default:
					role = null;
					
	        }
	        return new User(username, fullname, role);
    	} catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
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