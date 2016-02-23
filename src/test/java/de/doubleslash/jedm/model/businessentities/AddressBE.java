package de.doubleslash.jedm.model.businessentities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "address")
public class AddressBE {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "addressid")
   private int addressId;

   @Column(name = "street", length = 255)
   private String street;

   @Column(name = "number")
   private int number;

   @Column(name = "town")
   private String town;

   @Column(name = "zip")
   private int zip;

   public AddressBE() {
      super();
   }

   public AddressBE(final String street, final int number, final String town, final int zip) {
      super();
      this.street = street;
      this.number = number;
      this.town = town;
      this.zip = zip;
   }

   public int getAddressId() {
      return addressId;
   }

   public String getStreet() {
      return street;
   }

   public int getNumber() {
      return number;
   }

   public String getTown() {
      return town;
   }

   public int getZip() {
      return zip;
   }

   public void setStreet(final String street) {
      this.street = street;
   }

   public void setNumber(final int number) {
      this.number = number;
   }

   public void setTown(final String town) {
      this.town = town;
   }

   public void setZip(final int zip) {
      this.zip = zip;
   }

   @Override
   public String toString() {
      // Style: AddressBE [Id=12345, Musterstraße 19, 88046 Friedrichshafen]
      return "AddressBE [Id=" + addressId + ", " + street + " " + number + ", " + zip + " " + town + "]";
   }

}
