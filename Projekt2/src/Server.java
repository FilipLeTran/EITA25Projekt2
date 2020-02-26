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
        users.put("Admin", new User("Admin", "Admin Admin", Role.ADMIN));
        users.put("testare", new User("testare", "Jan Jansson", Role.ADMIN));
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
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if(killsession) {
            	out.println("Connection dropped. You have not yet been added to the server whitelist.");
            	out.flush();
            	return;
            } else {
            	out.println("Welcome, " + user.getUsername() + "!");
            }
            String clientInput = null;
            while ((clientInput = in.readLine()) != null) {
					out.println(OperationHandler.handleInput(user, clientInput) + "\n");
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