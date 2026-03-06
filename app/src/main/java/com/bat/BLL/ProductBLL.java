package com.bat.BLL;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.bat.DAL.ProductDAL;
import com.bat.DTO.ProductDTO;

public class ProductBLL {
    private ProductDAL productDAL;
    private ArrayList<ProductDTO> products;

    public ProductBLL() {
        productDAL = new ProductDAL();
        products = productDAL.getProducts();
    }

    // Hàm mới chuẩn theo Customer
    public ArrayList<ProductDTO> getProductList() {
        products = productDAL.getProducts();
        return products;
    }

    // Hàm cũ giữ lại để không bị lỗi AddCheckDialog
    public ArrayList<ProductDTO> getProductsList() {
        return getProductList();
    }

    public ProductDTO getProductById(int id) {
        if (products == null) products = productDAL.getProducts();
        for (ProductDTO p : products) {
            if (p.getProductId() == id) return p;
        }
        return null;
    }

    // --- GIỮ LẠI CHO CÁC MODULE KHÁC ---
    public ProductDTO getProductByLotId(int lotId){
        return productDAL.getProductByLotId(lotId);
    }

    public int getProductIdByIdx(int idx) {
        if (idx >= 0 && idx < products.size()) {
            return products.get(idx).getProductId();
        }
        return -1;
    }

    public ArrayList<ProductDTO> getPrdInImport(int importId) {
        return productDAL.getPrInImport(importId);
    }
    
    // --- LOGIC CHO GIAO DIỆN QUẢN LÝ ---
    public String add(ProductDTO p) {
        if (p.getProductName().trim().isEmpty()) return "Tên sản phẩm không được để trống!";
        if (p.getPublisher().trim().isEmpty()) return "Thương hiệu không được để trống!";
        if (p.getPrice().compareTo(BigDecimal.ZERO) < 0) return "Giá sản phẩm không được âm!";
        if (p.getQuantity() < 0) return "Số lượng không được âm!";

        p.setProductId(productDAL.getAutoIncrement());

        if (productDAL.add(p)) {
            return "Thêm sản phẩm thành công!";
        }
        return "Thêm thất bại!";
    }

    public String update(ProductDTO p) {
        if (p.getProductName().trim().isEmpty()) return "Tên sản phẩm không được để trống!";
        if (p.getPrice().compareTo(BigDecimal.ZERO) < 0) return "Giá sản phẩm không được âm!";

        if (productDAL.update(p)) {
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String delete(int id) {
        if (productDAL.delete(id)) {
            return "Xóa thành công!";
        }
        return "Xóa thất bại!";
    }
}
