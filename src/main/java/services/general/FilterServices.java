package services.general;

import dao.CategoryDAO;
import dao.SeriesDAO;
import dto.series.SeriesInfoDTO;
import model.Series;
import services.series.SeriesServices;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilterServices {
    public List<SeriesInfoDTO> filterSeries(List<String> statuses, List<String> genres, Connection connection) throws SQLException, ClassNotFoundException {
        SeriesDAO seriesDAO = new SeriesDAO(connection);
        CategoryDAO categoryDAO = new CategoryDAO(connection);
        List<Series> allSeries = seriesDAO.getAll();
        List<SeriesInfoDTO> result = new ArrayList<>();

        SeriesServices seriesServices = new SeriesServices(connection);
        for (Series s : allSeries) {
            boolean matchStatus = statuses.isEmpty() || statuses.contains(s.getStatus());
            boolean matchGenre = genres.isEmpty() || categoryDAO.matchGenres(s.getSeriesId(), genres);

            if (matchStatus && matchGenre) {
                result.add(seriesServices.buildSeriesInfoDTO(s));
            }
        }
        return result;
    }

}
