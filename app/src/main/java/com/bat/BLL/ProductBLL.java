package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.ProductDAL;
import com.bat.DTO.ProductDTO;

public class ProductBLL {
    private ProductDAL productDAL;

    ArrayList<ProductDTO> productList;
    public ProductBLL() {
        productDAL = new ProductDAL();
        productList = productDAL.getProducts();
    }

    public ArrayList<ProductDTO> getProductsList() {
        return productList;
    }

    public ProductDTO getProductById(int productId){
        for (ProductDTO product : productList) {
            if (product.getProductId() == productId) {
                return product;
            }
        }
        return null;
    }

    public ProductDTO getProductByLotId(int lotId){
        return productDAL.getProductByLotId(lotId);
    }
}
