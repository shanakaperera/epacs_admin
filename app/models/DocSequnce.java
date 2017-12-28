package models;

public class DocSequnce implements java.io.Serializable {


    private Integer id;
    private String seqName;
    private String tableName;
    private String format;
    private String defaultFormat;
    private Integer startFrom;

    public DocSequnce() {
    }

    public DocSequnce(String seqName, String tableName, String format, String defaultFormat, Integer startFrom) {
        this.seqName = seqName;
        this.tableName = tableName;
        this.format = format;
        this.defaultFormat = defaultFormat;
        this.startFrom = startFrom;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSeqName() {
        return this.seqName;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDefaultFormat() {
        return this.defaultFormat;
    }

    public void setDefaultFormat(String defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    public Integer getStartFrom() {
        return this.startFrom;
    }

    public void setStartFrom(Integer startFrom) {
        this.startFrom = startFrom;
    }


}