package models;
// Generated Dec 24, 2017 5:32:35 PM by Hibernate Tools 4.3.1


/**
 * ProductHasCoating generated by hbm2java
 */
public class ProductHasCoating implements java.io.Serializable {


    private Integer id;
    private Coating coating;
    private Product product;

    public ProductHasCoating() {
    }

    public ProductHasCoating(Coating coating, Product product) {
        this.coating = coating;
        this.product = product;
    }

    public ProductHasCoating(Coating coating) {
        this.coating = coating;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Coating getCoating() {
        return this.coating;
    }

    public void setCoating(Coating coating) {
        this.coating = coating;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}


