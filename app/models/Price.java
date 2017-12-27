package models;

import java.io.Serializable;

public class Price implements Serializable {

    private Integer id;
    private ProductHasSize productHasSize;
    private String qtyRange;
    private Double price;

    public Price() {
    }


    public Price(ProductHasSize productHasSize) {
        this.productHasSize = productHasSize;
    }
    public Price(ProductHasSize productHasSize, String qtyRange, Double price) {
        this.productHasSize = productHasSize;
        this.qtyRange = qtyRange;
        this.price = price;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public ProductHasSize getProductHasSize() {
        return this.productHasSize;
    }

    public void setProductHasSize(ProductHasSize productHasSize) {
        this.productHasSize = productHasSize;
    }
    public String getQtyRange() {
        return this.qtyRange;
    }

    public void setQtyRange(String qtyRange) {
        this.qtyRange = qtyRange;
    }
    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
