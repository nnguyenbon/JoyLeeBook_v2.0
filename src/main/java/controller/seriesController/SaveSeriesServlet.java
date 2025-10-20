package controller.seriesController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.SavedSeries;
import services.series.SavedSeriesService;

import java.io.IOException;

@WebServlet("/save-series")
public class SaveSeriesServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            SavedSeriesService saveSeriesService = new SavedSeriesService();

            int userId = Integer.parseInt(request.getParameter("userId"));
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            String action = request.getParameter("action");

            boolean saved;
            SavedSeries savedSeries = new SavedSeries();
            savedSeries.setUserId(userId);
            savedSeries.setSeriesId(seriesId);

            if ("save".equalsIgnoreCase(action)) {
                saveSeriesService.saveSeries(savedSeries);
                saved = true;
            } else {
                saveSeriesService.unSaveSeries(savedSeries);
                saved = false;
            }

            response.getWriter().write("{\"success\": true, \"saved\": " + saved + "}");
        } catch (Exception ex) {
            ex.printStackTrace();
            response.getWriter().write("{\"success\": false}");
        }
    }
}
