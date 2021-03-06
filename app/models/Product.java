package models;
// Generated Dec 24, 2017 5:32:35 PM by Hibernate Tools 4.3.1


import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Product generated by hbm2java
 */
@Constraints.Validate
public class Product implements Constraints.Validatable<List<ValidationError>>, java.io.Serializable {


    private Integer id;
    private String code;
    @Constraints.Required(message = "Title is not an optional.")
    @Constraints.MinLength(value = 4, message = "Minimum length should be 4")
    private String title;
    private String boldText;
    private String homeDescription;
    private String shortDescription;
    private String longDescription;
    private String homeImgPath;
    private String detailImgPath;
    private String imgTitle1;
    private String imgTitle2;
    private String sizeImgPath;
    private String borderImgPath;
    private String sideViewsImg;
    private Set<ProductHasFitting> productHasFittings = new HashSet<>();
    private Set<ProductHasCoating> productHasCoatings = new HashSet<>();
    private Set<ProductHasSize> productHasSizes = new HashSet<>();
    private TreeContent treeContent;

    public Product() {
    }

    public Product(Set<ProductHasFitting> productHasFittings, Set<ProductHasCoating> productHasCoatings, String code) {
        this.productHasFittings = productHasFittings;
        this.productHasCoatings = productHasCoatings;
        this.code = code;
    }

    public Product(String code, String title, String boldText, String homeDescription, String shortDescription, String longDescription, String homeImgPath, String detailImgPath, String imgTitle1, String imgTitle2, String sizeImgPath, String borderImgPath, String sideViewsImg, Set<ProductHasFitting> productHasFittings, Set<ProductHasCoating> productHasCoatings, Set<ProductHasSize> productHasSizes) {
        this.code = code;
        this.title = title;
        this.boldText = boldText;
        this.homeDescription = homeDescription;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.homeImgPath = homeImgPath;
        this.detailImgPath = detailImgPath;
        this.imgTitle1 = imgTitle1;
        this.imgTitle2 = imgTitle2;
        this.sizeImgPath = sizeImgPath;
        this.borderImgPath = borderImgPath;
        this.sideViewsImg = sideViewsImg;
        this.productHasFittings = productHasFittings;
        this.productHasCoatings = productHasCoatings;
        this.productHasSizes = productHasSizes;
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

    public String getBoldText() {
        return this.boldText;
    }

    public void setBoldText(String boldText) {
        this.boldText = boldText;
    }

    public String getHomeDescription() {
        return this.homeDescription;
    }

    public void setHomeDescription(String homeDescription) {
        this.homeDescription = homeDescription;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return this.longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getHomeImgPath() {
        return this.homeImgPath;
    }

    public void setHomeImgPath(String homeImgPath) {
        this.homeImgPath = homeImgPath;
    }

    public String getDetailImgPath() {
        return this.detailImgPath;
    }

    public void setDetailImgPath(String detailImgPath) {
        this.detailImgPath = detailImgPath;
    }

    public String getImgTitle1() {
        return this.imgTitle1;
    }

    public void setImgTitle1(String imgTitle1) {
        this.imgTitle1 = imgTitle1;
    }

    public String getImgTitle2() {
        return this.imgTitle2;
    }

    public void setImgTitle2(String imgTitle2) {
        this.imgTitle2 = imgTitle2;
    }

    public String getSizeImgPath() {
        return this.sizeImgPath;
    }

    public void setSizeImgPath(String sizeImgPath) {
        this.sizeImgPath = sizeImgPath;
    }

    public String getBorderImgPath() {
        return this.borderImgPath;
    }

    public void setBorderImgPath(String borderImgPath) {
        this.borderImgPath = borderImgPath;
    }

    public String getSideViewsImg() {
        return this.sideViewsImg;
    }

    public void setSideViewsImg(String sideViewsImg) {
        this.sideViewsImg = sideViewsImg;
    }

    public Set<ProductHasFitting> getProductHasFittings() {
        return this.productHasFittings;
    }

    public void setProductHasFittings(Set<ProductHasFitting> productHasFittings) {
        this.productHasFittings = productHasFittings;
    }

    public Set<ProductHasCoating> getProductHasCoatings() {
        return this.productHasCoatings;
    }

    public void setProductHasCoatings(Set<ProductHasCoating> productHasCoatings) {
        this.productHasCoatings = productHasCoatings;
    }

    public Set<ProductHasSize> getProductHasSizes() {
        return this.productHasSizes;
    }

    public void setProductHasSizes(Set<ProductHasSize> productHasSizes) {
        this.productHasSizes = productHasSizes;
    }

    public TreeContent getTreeContent() {
        return treeContent;
    }

    public void setTreeContent(TreeContent treeContent) {
        this.treeContent = treeContent;
    }

    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<>();
        return errors.isEmpty() ? null : errors;
    }
}


