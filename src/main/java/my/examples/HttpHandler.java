package my.examples;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;

public class HttpHandler extends Thread {
    private Socket socket;

    public HttpHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        final String baseDir = "C:\\Users\\count\\WebstormProjects\\simpleHTMLpage"; //Base Directory where my html ,css and image files are located.

        BufferedReader in = null;
        PrintStream out = null;
        FileInputStream fis = null;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //BufferedReader requires Reader as Parameter.
            //Using InputStreamReader we can read bytes and decodes them into characters.
            //socket.getInputStream(); -> Returns an input stream(bytes) for this socket.

            out = new PrintStream(socket.getOutputStream());
            //The Reason We used PrintStream is that we needed to use few different ways to write.
            //As PrintStream provides with many different write, print methods, it seemed suitable.

            String requestLine = in.readLine(); //read the request from the Client.
            System.out.println("Request Line" + requestLine);

            //Using Split, we separated request's method (Get, Post...)
            //and the requested item's location so that we can response with it later.
            String[] s = requestLine.split(" ");
            //The Request Line comes in a form of 'Get /...' so we used split to separate them.
            String httpMethod = s[0];
            String httpPath = s[1];
            System.out.println("Http Method: " + httpMethod);
            System.out.println("Http Path: " + httpPath);
            String mimeType = null;
            if (httpPath.equals("/")) { //When the web page is called for the first time, unless it is modified to something else, it will send requestLine which looks like 'Get /'
                httpPath = "/index.html";//In this case, we want to redirect the client to the index page.
            }else if(!httpPath.equals("/")) {
                String mainType = "";
                String subType = null;
                subType = httpPath.substring(httpPath.lastIndexOf(".")+1); //Get a file extension to generate the content Type of a file.
                System.out.println("Sub Type: " + subType);
                if(subType.equals("css")||subType.equals("html")){ //if the extension ends with httml or css,
                    mimeType = "text/"+subType; //return text/ + what ever the extension is
                    System.out.println("Mime Type: " + mimeType); //same goes for this line but it returns content-type for the image files.
                }else if(subType.equals("jpg")||subType.equals("gif")||subType.equals("png")||subType.equals("jpeg")||subType.equals("bmp")){
                    mimeType = "image/" + subType;
                    System.out.println("Mime Type: " + mimeType);
                }
            }


            //Other that that, we will combine both baseDir and httpPath to get the location of the requested file.
            String filePath = baseDir + httpPath;
            System.out.println("File path: " + filePath);

            File file = new File(filePath);
//            String mimeType = URLConnection.guessContentTypeFromName(file.getName()); <-- I found this line is not working as intended so I changed the code a bit int a way I inteded to .
//            //this line tells us what contene-type is the file with the name given
//            System.out.println(mimeType);
//            if (mimeType == null)
//                mimeType = "text/html";

            String line = null;
            while ((line = in.readLine()) != null) { //reads untill the end of the stream. (which returns null)
                if ("".equals(line)) { //if it reaches the end of the line, break.
                    break;
                }
                System.out.println("Header informaion : " + line);
            }

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + mimeType);
            out.println("Content-Length: " + file.length());
            out.println(); //prints the empty line

            byte[] buffer = new byte[1024];
            int readCount = 0;
            fis = new FileInputStream(file);
            while ((readCount = fis.read(buffer)) != -1) {
                out.write(buffer, 0, readCount);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
