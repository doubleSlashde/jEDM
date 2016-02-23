package de.doubleslash.jedm.model.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class CustomerDO {
   private int customerId;
   private String givenName;
   private String familyName;
   private List<AddressDO> addresses = new ArrayList<>();
   private int age;
   private List<BillDO> bills = new ArrayList<>();

   public CustomerDO() {
      super();
   }

   public CustomerDO(final String givenName, final String familyName, final List<AddressDO> addresses, final int age,
         final List<BillDO> bills) {
      super();
      this.givenName = givenName;
      this.familyName = familyName;
      this.addresses = addresses;
      this.age = age;
      this.bills = bills;
   }

   public int getCustomerId() {
      return customerId;
   }

   public String getGivenName() {
      return givenName;
   }

   public String getFamilyName() {
      return familyName;
   }

   public List<AddressDO> getAddresses() {
      return addresses;
   }

   public int getAge() {
      return age;
   }

   public List<BillDO> getBills() {
      return bills;
   }

   public void setCustomerId(final int customerId) {
      this.customerId = customerId;
   }

   public void setGivenName(final String givenName) {
      this.givenName = givenName;
   }

   public void setFamilyName(final String familyName) {
      this.familyName = familyName;
   }

   public void setAddresses(final List<AddressDO> addresses) {
      this.addresses = addresses;
   }

   public void setAge(final int age) {
      this.age = age;
   }

   public void setBills(final List<BillDO> bills) {
      this.bills = bills;
   }

   @Override
   public String toString() {
      return "CustomerDO [Id=" + customerId + ", " + givenName + " " + familyName + ", Alter: " + age + "\naddresses="
            + addresses + " \nbills=" + bills + "]\n";
   }
}
