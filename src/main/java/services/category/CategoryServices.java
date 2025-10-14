package services.category;

import dao.SeriesCategoriesDAO;
import db.DBConnection;
import dto.category.CategoryInfoDTO;
import model.Category;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryServices {
    public CategoryInfoDTO buildCategoryInfoDTO(Category category) throws SQLException, ClassNotFoundException {
        SeriesCategoriesDAO seriesCategoriesDAO = new SeriesCategoriesDAO(DBConnection.getConnection());
        CategoryInfoDTO categoryInfoDTO = new CategoryInfoDTO();
        categoryInfoDTO.setCategoryId(category.getCategoryId());
        categoryInfoDTO.setName(category.getName());
        int totalSeries = seriesCategoriesDAO.countSeriesByCategoryId(category.getCategoryId());
        categoryInfoDTO.setTotalSeries(totalSeries);
        return categoryInfoDTO;
    }

    public List<CategoryInfoDTO> buildCategoryInfoDTOList(List<Category> categoryList) throws SQLException, ClassNotFoundException {
        List<CategoryInfoDTO> categoryInfoDTOList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryInfoDTOList.add(buildCategoryInfoDTO(category));
        }
        return categoryInfoDTOList;
    }
}
