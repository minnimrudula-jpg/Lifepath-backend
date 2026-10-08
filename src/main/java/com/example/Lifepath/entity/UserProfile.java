package com.example.Lifepath.entity;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDate;
@Entity @Table(name="user_profiles")
public class UserProfile {
 @Id @Column(name="user_id") private Long userId;
 @OneToOne(fetch=FetchType.LAZY) @MapsId @JoinColumn(name="user_id") private User user;
 private LocalDate dateOfBirth; private String gender; private String category; private BigDecimal annualIncome; private String education; private String branch; private Integer experienceYears; private String phone; @Column(length=1000) private String address; private String city; private String state;
 protected UserProfile(){} public UserProfile(User u){user=u;}
 public Long getUserId(){return userId;} public LocalDate getDateOfBirth(){return dateOfBirth;} public String getGender(){return gender;} public String getCategory(){return category;} public BigDecimal getAnnualIncome(){return annualIncome;} public String getEducation(){return education;} public String getBranch(){return branch;} public Integer getExperienceYears(){return experienceYears;} public String getPhone(){return phone;} public String getAddress(){return address;} public String getCity(){return city;} public String getState(){return state;}
 public void setDateOfBirth(LocalDate v){dateOfBirth=v;} public void setGender(String v){gender=v;} public void setCategory(String v){category=v;} public void setAnnualIncome(BigDecimal v){annualIncome=v;} public void setEducation(String v){education=v;} public void setBranch(String v){branch=v;} public void setExperienceYears(Integer v){experienceYears=v;} public void setPhone(String v){phone=v;} public void setAddress(String v){address=v;} public void setCity(String v){city=v;} public void setState(String v){state=v;}
}
