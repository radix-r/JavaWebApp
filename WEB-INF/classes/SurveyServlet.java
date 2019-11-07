
// SurveyServlet.java
// A Web-based survey that uses JDBC from a servlet.

import java.sql.*;
import java.lang.*;
import java.io.PrintWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SurveyServlet extends HttpServlet 
{
   private Connection connection;
   private Statement statement;

   // set up database connection and create SQL statement
   public void init( ServletConfig config ) throws ServletException
   {
      // attempt database connection and create Statement
      try 
      {
  /*       Class.forName( config.getInitParameter( "databaseDriver" ) );
           connection = DriverManager.getConnection( 
           config.getInitParameter( "databaseName" ),
           config.getInitParameter( "username" ),
           config.getInitParameter( "password" ) );
    */   

          Class.forName("com.mysql.jdbc.Driver");
			 connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/colorsurvey", "root", "root" );
         // create Statement to query database
         statement = connection.createStatement();
      } // end try
      // for any exception throw an UnavailableException to 
      // indicate that the servlet is not currently available
      catch ( Exception exception ) 
      {
         exception.printStackTrace();
         throw new UnavailableException( exception.getMessage() );
      } // end catch
   }  // end method init 

   // process survey response
   protected void doPost( HttpServletRequest request,
      HttpServletResponse response )
         throws ServletException, IOException
   {
      // set up response to client
      response.setContentType( "text/html" ); 
      PrintWriter out = response.getWriter();

      // start HTML document
      out.println( 
         "<html>" );
      // head section of document
      out.println( "<head>" );  
      // read current survey response
      int value = 
         Integer.parseInt( request.getParameter( "color" ) );
      String sql;
      // attempt to process a vote and display current results
      try 
      {
         // update total for current survey response
         sql = "UPDATE surveyresults SET votes = votes + 1 " +
               "WHERE id = " + value;
         statement.executeUpdate( sql );

         // get total of all survey responses
         sql = "SELECT sum( votes ) FROM surveyresults";
         ResultSet totalRS = statement.executeQuery( sql );
         totalRS.next(); // position to first record
         int total = totalRS.getInt( 1 );

         // get results
         sql = "SELECT surveyoption, votes, id FROM surveyresults " + 
            "ORDER BY id";
         ResultSet resultsRS = statement.executeQuery( sql );
         out.println( "<pre><title>Thank you!</title>" );
         out.println( "</head>" );  
         
         out.println( "<body>" ); 
		   out.println ("<body bgcolor=white background=images/background.jpg lang=EN-US link=blue vlink=blue >");
         out.println ("<body style='tab-interval:.5in'>");
         out.println ("<font size = 4> <b>");
         out.println ("<p>Thank you for participating in the CNT 4714 <span style='color:blue'>C</span><span style='color:red'>O</span><span style='color:green'>L</span><span style='color:yellow'>O</span><span style='color:orange'>R</span> Preference Survey." );
			out.println ("</b><br>");
			out.println ("</font>");
         out.println ("<br />\t Current Results:</p><pre>" );
         
         // process results
         int votes;
         
         while ( resultsRS.next() ) 
         {
            out.print( resultsRS.getString( 1 ) );
            out.print( ": " );
            votes = resultsRS.getInt( 2 );
            out.printf( "%.2f", ( double ) votes / total * 100 );
            out.print( "%\t  responses: " );
            out.println( votes );
         } // end while

         resultsRS.close();
         
			out.println();
         out.print( "Total number of responses: " );
         out.print( total );
         
         // end HTML document
         out.println( "</pre></body></html>" );         
         out.close();
      } // end try
      // if database exception occurs, return error page
      catch ( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
         out.println( "<title>Error</title>" );
         out.println( "</head>" );  
         out.println( "<body><p>Database error occurred. " );
         out.println( "Try again later.</p></body></html>" );
         out.close();
      } // end catch
   } // end method doPost

   // close SQL statements and database when servlet terminates
   public void destroy()
   {
      // attempt to close statements and database connection
      try 
      {
         statement.close();
         connection.close();
      } // end try
      // handle database exceptions by returning error to client
      catch( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
      } // end catch
   } // end method destroy
} // end class SurveyServlet

