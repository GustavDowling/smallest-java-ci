package smallest.java.ci;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
 
import java.io.IOException;
import java.io.File; 
import java.io.*;

import java.util.concurrent.TimeUnit;
 
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/** 
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/
public class ContinuousIntegrationServer extends AbstractHandler
{
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException
    {
        String requestMethod = request.getMethod();
        switch (requestMethod){
            case "GET":
                System.out.printf("handling %s request to URI: %s\n", requestMethod, target);
               
                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
                response.getWriter().println("CI job done");
                
            case "POST":
				System.out.printf("handling %s request to URI: %s\n", requestMethod, target);
				
				
				File output = new File("/home/gustav/Desktop/prog/output");
                Runtime.getRuntime().exec("git clone https://github.com/GustavDowling/DD2480-Software-Engineering-Fundamentals_Group-13.git", null, output);
                
				try {
					TimeUnit.SECONDS.sleep(3); //sleep for 3 seconds while it downloads the files
				}
				catch(InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
				
				File gitfolder = new File("/home/gustav/Desktop/prog/output/DD2480-Software-Engineering-Fundamentals_Group-13");
				
				Runtime.getRuntime().exec("git pull", null, gitfolder); //if git folder was already there, get a new version
				
				try {
					TimeUnit.SECONDS.sleep(3); //sleep for 3 seconds while it downloads the files
				}
				catch(InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
				
				//solution for getting the output from "exec" https://stackoverflow.com/questions/4741878/redirect-runtime-getruntime-exec-output-with-system-setout
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec("./gradlew clean build", null, gitfolder);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                
                String s = null;
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}
         
                
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
        }
    }
 
    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer()); 
        server.start();
        server.join();
    }
}
