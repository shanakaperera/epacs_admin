package models;
// Generated Dec 24, 2017 5:32:35 PM by Hibernate Tools 4.3.1


import play.data.validation.Constraints;

import java.util.HashSet;
import java.util.Set;

/**
 * Size generated by hbm2java
 */
public class Size  implements java.io.Serializable {


     private int id;
     @Constraints.Required(message = "Size name is not optional")
     private String name;
    @Constraints.Required(message = "Width is not optional")
     private Integer width;
    @Constraints.Required(message = "Height is not optional")
     private Integer height;
    @Constraints.Required(message = "Measure unit is not optional")
     private String measureUnit;
     private String remark;
     private boolean status;
     private Set<ProductHasSize> productHasSizes = new HashSet<>();

    public Size() {
    }

	
    public Size(int id, boolean status) {
        this.id = id;
        this.status = status;
    }

    //////////////////////////
    public Size(String code,String action) {

    }
    //////////////////////////

    public Size(int id, String name, Integer width, Integer height, String measureUnit, String remark, boolean status, Set<ProductHasSize> productHasSizes) {
       this.id = id;
       this.name = name;
       this.width = width;
       this.height = height;
       this.measureUnit = measureUnit;
       this.remark = remark;
       this.status = status;
       this.productHasSizes = productHasSizes;
    }
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public Integer getWidth() {
        return this.width;
    }
    
    public void setWidth(Integer width) {
        this.width = width;
    }
    public Integer getHeight() {
        return this.height;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    public String getMeasureUnit() {
        return this.measureUnit;
    }
    
    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }
    public String getRemark() {
        return this.remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public boolean isStatus() {
        return this.status;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }
    public Set<ProductHasSize> getProductHasSizes() {
        return this.productHasSizes;
    }
    
    public void setProductHasSizes(Set<ProductHasSize> productHasSizes) {
        this.productHasSizes = productHasSizes;
    }




}


