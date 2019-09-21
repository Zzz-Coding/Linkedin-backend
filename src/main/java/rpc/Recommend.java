package rpc;

import org.json.JSONArray;
import recommend.JobRecommend;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/recommend")
public class Recommend extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.addHeader("Access-Control-Allow-Origin", "*");
        String userId = request.getParameter("userId");
        JobRecommend jobRecommend = new JobRecommend(userId);
        JSONArray array = jobRecommend.recommend();
        writeJsonArray(response, array);
    }

    private static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {

        PrintWriter writer = response.getWriter();
        writer.print(array);
        writer.close();
    }
}
