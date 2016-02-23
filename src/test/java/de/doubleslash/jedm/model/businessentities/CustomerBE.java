package de.doubleslash.jedm.model.businessentities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
public class CustomerBE {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "cid")
   private int customerId;

   @Column(name = "fName", length = 255)
   private String firstName;

   @Column(name = "lName", length = 255)
   private String lastName;

   @ManyToMany(fetch = FetchType.LAZY)
   private Set<AddressBE> addresses = new HashSet<>();

   @Column(name = "age")
   private int age;

   @OneToMany
   private Set<BillBE> bills = new HashSet<>();

   public CustomerBE() {
      super();
   }

   public CustomerBE(final String firstName, final String lastName, final int age) {
      super();
      this.firstName = firstName;
      this.lastName = lastName;
      this.age = age;
   }

   public int getCustomerId() {
      return customerId;
   }

   public String getFirstName() {
      return firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public Set<AddressBE> getAddresses() {
      return addresses;
   }

   public int getAge() {
      return age;
   }

   public Set<BillBE> getBills() {
      return bills;
   }

   public void setFirstName(final String firstName) {
      this.firstName = firstName;
   }

   public void setLastName(final String lastName) {
      this.lastName = lastName;
   }

   public void setAge(final int age) {
      this.age = age;
   }

   public void setBills(final Set<BillBE> bills) {
      this.bills = bills;
   }

   public void setAddresses(final Set<AddressBE> addresses) {
      this.addresses = addresses;
   }

   @Override
   public String toString() {
      return "CustomerBE [Id=" + customerId + ", " + firstName + " " + lastName + ", Alter: " + age + " (" + addresses
            + ") \nbills=" + bills + "]\n";
   }
}
