package com.example.TpoProjekt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;


public class HelloServlet extends HttpServlet
{
    private String message;
    Connection connection;
    String URL="jdbc:postgresql://localhost:5432/postgres";
    private static String mode="light";
    private boolean css=false;

    public void init()
    {
        try {
            Class.forName("org.postgresql.Driver");
            connection= DriverManager.getConnection(URL,"postgres","mysecretpassword");
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ServiceRequest(request,response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException
    {
        ServiceRequest(request,response);
    }
    private void ServiceRequest(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException
    {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out=response.getWriter()){
            String pathHTML;
            if(!css) {
                pathHTML = "D:\\JavaP\\TpoProjekt\\src\\main\\webapp/Tabelka.html";

                if (request.getParameterValues("dark") != null && (request.getParameterValues("dark")[0].equals("dark")))
                    mode = "dark";
                else if (request.getParameterValues("dark") != null && (request.getParameterValues("dark")[0].equals("light")))
                    mode = "light";
                switch (mode) {

                    case "dark": {
                        pathHTML = "D:\\JavaP\\TpoProjekt\\src\\main\\webapp/dark.html";
                        break;
                    }
                    case "light": {
                        pathHTML = "D:\\JavaP\\TpoProjekt\\src\\main\\webapp/Tabelka.html";
                        break;
                    }

                }
            }
            else
                pathHTML="D:\\JavaP\\TpoProjekt\\src\\main\\webapp/jscss.html";
            InputStreamReader inputStreamReader=new InputStreamReader(new FileInputStream(pathHTML), StandardCharsets.UTF_8);
            BufferedReader reader=new BufferedReader(inputStreamReader);
            String line;
            while((line=reader.readLine())!=null)
            {
                out.println(line);
            }

            //order by
            Statement statement=connection.createStatement();
            ResultSet set;
            if((request.getParameterValues("order")!=null)&&(request.getParameterValues("incDec")!=null)
                    &&(!request.getParameterValues("order")[0].equals(" "))&&(!request.getParameterValues("incDec")[0].equals(" ")))
            {
                set = statement.executeQuery("select isbn,tytul,autorname,wydawcaname,cena from pozycje \n" +
                        "inner join autor on autor.autid = pozycje.autid \n" +
                        "inner join wydawca on wydawca.wydid=pozycje.wydid "+
                        "order by "+request.getParameterValues("order")[0]+" "+request.getParameterValues("incDec")[0]);
            }
            else {

                set = statement.executeQuery("select isbn,tytul,autorname,wydawcaname,cena from pozycje \n" +
                        "inner join autor on autor.autid = pozycje.autid \n" +
                        "inner join wydawca on wydawca.wydid=pozycje.wydid ");
            }
            while (set.next())
            {
                //sortowanie
                if(request.getParameterValues("Burek")!=null)
                {
                    String string=set.getString("isbn")+" "+set.getString("tytul")+" "+
                            set.getString("autorname")+" "+set.getString("wydawcaname")+" "+
                            set.getString("cena");
                    string=string.toLowerCase();
                    String param=request.getParameterValues("Burek")[0].toLowerCase();
                    if(string.contains(param))
                    {
                        print(set,out);
                    }
                }
                else {
                    print(set,out);
                }
            }
            out .println("<input type=\"text\" size=\"30\" name=\"Burek\"><br>");
            out.println("<input type=\"submit\" value=\"Szukaj\"><br><br>");

            //dark mode
            if(!css) {
                out.println("    <select name=\"dark\" id=\"dark\">\n" +
                        "        <option value=\" \">kolor</option>\n" +
                        "        <option value=\"dark\">ciemny</option>\n" +
                        "        <option value=\"light\">jasny</option>\n" +
                        "    </select>");
                out.println("<input type=\"submit\" value=\"Zatwierdź\"><br><br>");
            }
            out.println("</form>");
            out.println("</table>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
            reader.close();
            statement.close();
            set.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void print(ResultSet set,PrintWriter out) throws SQLException {
        out.println("<tr>");
        out.println("<th>" + set.getString("isbn") + "</th>");
        out.println("<th>" + set.getString("tytul") + "</th>");
        out.println("<th>" + set.getString("autorname") + "</th>");
        out.println("<th>" + set.getString("wydawcaname") + "</th>");
        out.println("<th>" + set.getString("cena") + "</th>");
        out.println("</tr>");
    }

    private ResultSet orderBy(String order) throws SQLException
    {
        return connection.createStatement().executeQuery("select isbn,tytul,autor.\"name\",wydawca.\"name\",cena from pozycje \n" +
                "inner join autor on autor.autid = pozycje.autid \n" +
                "inner join wydawca on wydawca.wydid=pozycje.wydid "+
                "order by "+order);
    }

    public void destroy() {
    }
}