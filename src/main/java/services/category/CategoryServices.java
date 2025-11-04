package services.category;

import dao.CategoryDAO;
import dao.SeriesCategoriesDAO;
import db.DBConnection;
import dto.category.CategoryInfoDTO;
import model.Category;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryServices {
    private final CategoryDAO categoryDAO;
    private final SeriesCategoriesDAO seriesCategoriesDAO;

    public CategoryServices() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getConnection();
        this.categoryDAO = new CategoryDAO(connection);
        this.seriesCategoriesDAO = new SeriesCategoriesDAO(connection);
    }

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

    public List<CategoryInfoDTO> topCategories (int limit) throws SQLException, ClassNotFoundException {
        return buildCategoryInfoDTOList(categoryDAO.getCategoryTop(limit));
    }

    public Category createCategory(String name, String description) throws SQLException, ClassNotFoundException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);


        try {
            boolean success = categoryDAO.insert(category);
            if (!success) {
                throw new SQLException("Failed to add genre into database.");
            }
            return category;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while adding genre", e);
        }
    }

    public Category editCategory(String name, String description) throws SQLException, ClassNotFoundException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);

        try {
            boolean success = categoryDAO.update(category);
            if (!success) {
                throw new SQLException("Failed to update genre into database.");
            }
            return category;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while updating genre", e);
        }
    }

    public void deleteCategory(String categoryIdParam) throws SQLException, ClassNotFoundException {
        int categoryId;
        try {
            categoryId = Integer.parseInt(categoryIdParam);
        }  catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid category ID.");
        }

        boolean deleteMap = seriesCategoriesDAO.deleteByCategoryId(categoryId);
        if (!deleteMap) {
            throw new SQLException("Failed to delete mapping category in database.");
        }

        boolean deleteCategory = categoryDAO.delete(categoryId);
        if (!deleteCategory) {
            throw new SQLException("Failed to delete category in database.");
        }
    }
}
