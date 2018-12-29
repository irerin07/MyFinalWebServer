package my.examples;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private final int port;

    public WebServer(int port) {
        this.port = port;
    }
    public void run(){
        ServerSocket serverSocket = null;
        //This class implements server sockets. A server socket waits for requests to come in over the network.
        // It performs some operation based on that request, and then possibly returns a result to the requester.

        try{
            //Creates a server socket, bound to the specified port.
            serverSocket = new ServerSocket(port);

            while(true) {
                System.out.println("Waiting for the Connection to be made.");

                Socket socket = serverSocket.accept(); //Listens for a connection to be made to this socket and accepts it.
                //Socket -> This class implements client sockets (also called just "sockets"). A socket is an endpoint for communication between two machines.
                //A socket is bound to a port number so that the TCP layer can identify the application that data is destined to be sent to.

                HttpHandler httpHandler = new HttpHandler(socket); //Create new HttpHandler instance which takes the socket information with port number.

                httpHandler.start(); //Start the httpHandler which extends the Thread.
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
