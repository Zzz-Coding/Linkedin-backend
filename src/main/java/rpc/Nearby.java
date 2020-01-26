package rpc;

import external.JobAPI;
import org.json.JSONArray;
import recommend.JobRecommend;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/nearby")
public class Nearby extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.addHeader("Access-Control-Allow-Origin", "*");
        String keyword = request.getParameter("description");
        if (keyword == null) {
            keyword = "";
        }
        String loc = request.getParameter("location");
        JSONArray array = null;
        if (loc != null) {
            array = JobAPI.searchByLocation(keyword, loc);
        } else {
            String lat = request.getParameter("lat");
            String lon = request.getParameter("long");

            array = JobAPI.searchByLatLong(keyword, lat, lon);
        }

        writeJsonArray(response, array);
    }

    private static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {

        PrintWriter writer = response.getWriter();
        writer.print(array);
        writer.close();
    }
}
