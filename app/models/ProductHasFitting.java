package models;
// Generated Dec 24, 2017 5:32:35 PM by Hibernate Tools 4.3.1



/**
 * ProductHasFitting generated by hbm2java
 */
public class ProductHasFitting  implements java.io.Serializable {


     private Integer id;
     private models.Fitting fitting;
     private models.Product product;

    public ProductHasFitting() {
    }

    public ProductHasFitting(models.Fitting fitting, models.Product product) {
       this.fitting = fitting;
       this.product = product;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public models.Fitting getFitting() {
        return this.fitting;
    }
    
    public void setFitting(models.Fitting fitting) {
        this.fitting = fitting;
    }
    public models.Product getProduct() {
        return this.product;
    }
    
    public void setProduct(models.Product product) {
        this.product = product;
    }




}


