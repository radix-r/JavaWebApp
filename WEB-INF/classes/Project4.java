/*
Name: Ross Wagner
Course: CNT 4714 – Fall 2019 – Project Four
Assignment title: A Three-Tier Distributed Web-Based Application
Date: November 28, 2019
*/

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

public class Project4 extends HttpServlet {
    private Connection connection;
    private Statement statement;

    // set up database connection and create SQL statement
    public void init( ServletConfig config ) throws ServletException
    {
        // attempt database connection and create Statement
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/project4", "root", "root" );
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
        String defaultCommand = "select * from suppliers";
        String result = "";

        String sql = request.getParameter("input");
        if (sql.compareTo("")==0){
            sql = defaultCommand;
        }

        try {
            // determine if command update or query
            String[] tokens = sql.split(" ");
            if (tokens[0].toLowerCase().compareTo("select")== 0){
                ResultSet resultsRS = statement.executeQuery(sql);
                resultsRS.getMetaData();
                while ( resultsRS.next() )
                {
                    int len = resultsRS.getFetchSize();
                    for (int i = 1; i <= len; i++) {
                        result += resultsRS.getString(i);
                        result+=": ";
                    }
                    result+="\n";

                } // end while
            }
            else {
                statement.executeUpdate(sql);
            }

            printPage(out, sql, result);
//        try
//        {
//            // update total for current survey response
//            sql = "UPDATE surveyresults SET votes = votes + 1 " +
//                    "WHERE id = " + value;
//
//
//            // get total of all survey responses
//            sql = "SELECT sum( votes ) FROM surveyresults";
//            ResultSet totalRS = statement.executeQuery( sql );
//            totalRS.next(); // position to first record
//            int total = totalRS.getInt( 1 );
//
//            // get results
//            sql = "SELECT surveyoption, votes, id FROM surveyresults " +
//                    "ORDER BY id";

//            int votes;
//
//            while ( resultsRS.next() )
//            {
//                out.print( resultsRS.getString( 1 ) );
//                out.print( ": " );
//                votes = resultsRS.getInt( 2 );
//                out.printf( "%.2f", ( double ) votes / total * 100 );
//                out.print( "%\t  responses: " );
//                out.println( votes );
//            } // end while
//
//            resultsRS.close();
//
//            out.println();
//            out.print( "Total number of responses: " );
//            out.print( total );
//
//            // end HTML document
//            out.println( "</pre></body></html>" );
//            out.close();
//        } // end try
//        // if database exception occurs, return error page
//        catch ( SQLException sqlException )
//        {
//
//            sqlException.printStackTrace();
//            out.println( "<title>Error</title>" );
//            out.println( "</head>" );
//            out.println( "<body><p>Database error occurred. " );
//            out.println( "Try again later.</p></body></html>" );
//            out.close();
//        } // end catch
        }catch (SQLException sqlException){
            // show error on page
            printPage(out, sql, "error");
        }
    } // end method doPost

    private void printPage(PrintWriter out,String sql, String result) {
        // start HTML document
        out.println(
                "<html>" );
        // head section of document
        out.println( "<head></head>" );
        out.println( "<body>");
        out.println(sql);
        out.println( "<br>");
        out.println(result);
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

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
}
