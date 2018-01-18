package models;
// Generated Dec 24, 2017 5:32:35 PM by Hibernate Tools 4.3.1


import play.data.validation.Constraints;

import java.util.HashSet;
import java.util.Set;

/**
 * Fitting generated by hbm2java
 */
public class Fitting implements java.io.Serializable {


    private Integer id;
    private String code;
    @Constraints.Required(message = "Title is not an optional.")
    @Constraints.MinLength(value = 6, message = "Minimum length should be 6")
    private String title;
    private String description;
    private String imgPath;
    @Constraints.Required(message = "Charge is not an optional.")
    private Double charge;
    private String remark;
    private boolean status;
    private Set<ProductHasFitting> productHasFittings = new HashSet<>();


    public Fitting() {
    }

    public Fitting(boolean status) {
        this.status = status;
    }


    public Fitting(String code, String title, String description, String imgPath, Double charge, String remark, boolean status, Set<ProductHasFitting> productHasFittings) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.charge = charge;
        this.remark = remark;
        this.status = status;
        this.productHasFittings = productHasFittings;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgPath() {
        return this.imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public Double getCharge() {
        return this.charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
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

    public Set<ProductHasFitting> getProductHasFittings() {
        return this.productHasFittings;
    }

    public void setProductHasFittings(Set<ProductHasFitting> productHasFittings) {
        this.productHasFittings = productHasFittings;
    }

}


