package com.silita.biaodaa.common.elastic.indexes;

import com.silita.biaodaa.common.elastic.model.ElasticEntity;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "bdd_company", type = "company", shards = 4, replicas = 1, indexStoreType = "memory", refreshInterval = "1")
public class CompanyIdxElasticsearch extends ElasticEntity {
    @Field(type = FieldType.String)
    private String certificateUrlId;  //公司ID
    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String companyName;   //公司名
    @Field(type = FieldType.Double, store = true)
    private Double registeredCapital;  //注册资金
    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String city;   //市
    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String companyZzType;    //行业类型
    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String setupDate;    //创立时间
    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String certificate;  //资质要求
    @Field(type = FieldType.String)
    private String mianCertificate01;  //主项资质
    @Field(type = FieldType.String)
    private String licenseDate;
    @Field(type = FieldType.String)
    private String legalPerson;    //法人
    @Field(type = FieldType.String)
    private String companyType;   //经济类型
    @Field(type = FieldType.String)
    private String tableName;   //表名
    @Field(type = FieldType.String)
    private String uuid;

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getCertificateUrlId() {return certificateUrlId;}
    public void setCertificateUrlId(String certificateUrlId) {this.certificateUrlId = certificateUrlId;}
    public String getCompanyName() {return companyName;}
    public void setCompanyName(String companyName) {this.companyName = companyName;}
    public String getMianCertificate01() {return mianCertificate01;}
    public void setMianCertificate01(String mianCertificate01) {this.mianCertificate01 = mianCertificate01;}
    public String getLegalPerson() {return legalPerson;}
    public void setLegalPerson(String legalPerson) {this.legalPerson = legalPerson;}
    public Double getRegisteredCapital() {return registeredCapital;}
    public void setRegisteredCapital(Double registeredCapital) {this.registeredCapital = registeredCapital;}
    public String getCompanyType() {return companyType;}
    public void setCompanyType(String companyType) {this.companyType = companyType;}
    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}
    public String getCompanyZzType() {return companyZzType;}
    public void setCompanyZzType(String companyZzType) {this.companyZzType = companyZzType;}
    public String getSetupDate() {return setupDate;}
    public void setSetupDate(String setupDate) {this.setupDate = setupDate;}
    public String getCertificate() {return certificate;}
    public void setCertificate(String certificate) {this.certificate = certificate;}
    public String getTableName() {return tableName;}
    public void setTableName(String tableName) {this.tableName = tableName;}
    public String getLicenseDate() {
        return licenseDate;
    }
    public void setLicenseDate(String licenseDate) {
        this.licenseDate = licenseDate;
    }

    @Override
    public String toString() {
        return "CompanyIdxElasticsearch{" +
                "certificateUrlId='" + certificateUrlId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", registeredCapital=" + registeredCapital +
                ", city='" + city + '\'' +
                ", companyZzType='" + companyZzType + '\'' +
                ", setupDate='" + setupDate + '\'' +
                ", certificate='" + certificate + '\'' +
                ", mianCertificate01='" + mianCertificate01 + '\'' +
                ", licenseDate='" + licenseDate + '\'' +
                ", legalPerson='" + legalPerson + '\'' +
                ", companyType='" + companyType + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}