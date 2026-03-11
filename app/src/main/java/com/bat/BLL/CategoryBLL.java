package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.CategoryDAL;
import com.bat.DTO.CategoryDTO;

public class CategoryBLL {
    private CategoryDAL categoryDAL;
    private ArrayList<CategoryDTO> categories;

    public CategoryBLL() {
        categoryDAL = new CategoryDAL();
        categories = categoryDAL.getCategories();
    }

    public ArrayList<CategoryDTO> getCategoryList() {
        categories = categoryDAL.getCategories();
        return categories;
    }

    public String getCategoryNameById(int id) {
        if (categories == null) categories = categoryDAL.getCategories();
        for (CategoryDTO c : categories) {
            if (c.getCategoryId() == id) 
                return c.getCategoryName();
        }
        return null;
    }

    public CategoryDTO getCategoryById(int id) {
        if (categories == null) categories = categoryDAL.getCategories();
        for (CategoryDTO c : categories) {
            if (c.getCategoryId() == id) return c;
        }
        return null;
    }

    public String add(CategoryDTO c) {
        c.setCategoryId(categoryDAL.getAutoIncrement());

        if (categoryDAL.add(c)) {
            return "Thêm danh mục thành công!";
        }
        return "Thêm thất bại!";
    }

    public String update(CategoryDTO c) {

        if (categoryDAL.update(c)) {
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String delete(int id) {
        if (categoryDAL.delete(id)) {
            return "Xóa thành công!";
        }
        return "Xóa thất bại!";
    }
}