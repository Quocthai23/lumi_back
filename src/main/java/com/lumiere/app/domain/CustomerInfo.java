package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@Entity
@Table(name = "customer_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CustomerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "phone", nullable = false, length = 50)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "province_code", nullable = false, length = 50)
    private String provinceCode;

    @Column(name = "province_name", nullable = false, length = 255)
    private String provinceName;

    @Column(name = "district_code", nullable = false, length = 50)
    private String districtCode;

    @Column(name = "district_name", nullable = false, length = 255)
    private String districtName;

    @Column(name = "ward_code", length = 50)
    private String wardCode;

    @Column(name = "ward_name", length = 255)
    private String wardName;

    @Column(name = "address_line", nullable = false, length = 255)
    private String addressLine;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "tax_code", length = 50)
    private String taxCode;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "customerInfos" }, allowSetters = true)
    private Customer customer;

    // ===== GETTER / SETTER + builder style =====

    public Long getId() {
        return id;
    }

    public CustomerInfo id(Long id) {
        this.id = id;
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public CustomerInfo fullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public CustomerInfo phone(String phone) {
        this.phone = phone;
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public CustomerInfo email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public CustomerInfo provinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
        return this;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public CustomerInfo provinceName(String provinceName) {
        this.provinceName = provinceName;
        return this;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public CustomerInfo districtCode(String districtCode) {
        this.districtCode = districtCode;
        return this;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public CustomerInfo districtName(String districtName) {
        this.districtName = districtName;
        return this;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getWardCode() {
        return wardCode;
    }

    public CustomerInfo wardCode(String wardCode) {
        this.wardCode = wardCode;
        return this;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    public String getWardName() {
        return wardName;
    }

    public CustomerInfo wardName(String wardName) {
        this.wardName = wardName;
        return this;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public CustomerInfo addressLine(String addressLine) {
        this.addressLine = addressLine;
        return this;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCompanyName() {
        return companyName;
    }

    public CustomerInfo companyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public CustomerInfo taxCode(String taxCode) {
        this.taxCode = taxCode;
        return this;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getNote() {
        return note;
    }

    public CustomerInfo note(String note) {
        this.note = note;
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public CustomerInfo isDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Customer getCustomer() {
        return customer;
    }

    public CustomerInfo customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // ===== equals / hashCode / toString =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerInfo)) return false;
        return id != null && id.equals(((CustomerInfo) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CustomerInfo{" +
            "id=" + id +
            ", fullName='" + fullName + '\'' +
            ", phone='" + phone + '\'' +
            ", email='" + email + '\'' +
            ", provinceCode='" + provinceCode + '\'' +
            ", provinceName='" + provinceName + '\'' +
            ", districtCode='" + districtCode + '\'' +
            ", districtName='" + districtName + '\'' +
            ", wardCode='" + wardCode + '\'' +
            ", wardName='" + wardName + '\'' +
            ", addressLine='" + addressLine + '\'' +
            ", companyName='" + companyName + '\'' +
            ", taxCode='" + taxCode + '\'' +
            ", isDefault=" + isDefault +
            '}';
    }
}
