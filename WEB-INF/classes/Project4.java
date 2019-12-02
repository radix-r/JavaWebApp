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
                ResultSetMetaData meta = resultsRS.getMetaData();
                int len = meta.getColumnCount();
                result+= "<table BORDER=1 CELLPADDING=0 CELLSPACING=0 WIDTH=100%>";
                result += "<tr>";
                for (int i = 1; i <= len; i++) {
                    result += "<th>"+meta.getColumnName(i)+"</th>";
                }
                result += "</tr>";

                while ( resultsRS.next() )
                {
                    result += "<tr>";
                    for (int i = 1; i <= len; i++) {
                        result += "<td><center>"+resultsRS.getString(i)+"</center></td>";
                    }
                    result+="</tr>"; // end row

                } // end while
                result+="</table>";
            }
            else {
                statement.executeUpdate(sql);
            }
            printPage(out, sql, result);
        }catch (SQLException sqlException){
            // show error on page
            // printStackTrace();
            printPage(out, sql, sqlException.toString());
        }
    } // end method doPost

    private void printPage(PrintWriter out,String sql, String result) {
        // start HTML document
        out.println("<!DOCTYPE html>");
        out.println("<!-- Survey.html -->");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<title>CNT 4714 Remote Database</title>");
        out.println("<style>");
        out.println("<!--");
        out.println("body { background-color: grey; color:white; font-size: 2em; font-family:mono; font-size: 1.2em;text-align: center;}");
        out.println("input[type='submit'] {background-color:black; color:lime; font-size:0.9em; }");
        out.println("h1 { font-weight: bold; font-size 1.0em;}");
        out.println("h2 {}");
        out.println("textarea {background-color: black; color: lime; font-weight: bold}");
        out.println("-->   ");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<br >");
        out.println("<form action = '../project4/project4'  method = 'post'>");
        out.println("<h1> Welcome to the Fall 2019 Project 4 Enterprise System</h1>");
        out.println("<h2>A Remote Database Management System</h2>");
        out.println("<hr color='white'>");
        out.println("<p>");
        out.println("You are connected to the Project 4 Enterprise System database. Please enter any valid SQL query or update statement.<br>");
        out.println("If no query/update command is initially provided the Execute button will display all supplier information in the database.<br>");
        out.println("all execution results will appear below.<br>");
        out.println("<br>");
        out.println("<textarea name='input' rows='20' cols='60' placeholder='select * from suppliers'>");
        out.println(sql);
        out.println("</textarea>");
        out.println("<br>");
        out.println("</p>");
        out.println("<p><input type = 'submit' value = 'Submit' /></p>");
        out.println("<hr color='white'>");
        out.println(" <h2>Database Results</h2>");
        out.println(result);
        out.println("</form>");
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
