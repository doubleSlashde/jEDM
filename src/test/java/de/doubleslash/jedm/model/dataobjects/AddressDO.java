package de.doubleslash.jedm.model.dataobjects;

public class AddressDO {
   private int addressId;
   private String street;
   private int houseNumber;
   private String town;
   private int zipCode;

   public AddressDO() {
      super();
   }

   public AddressDO(final String street, final int houseNumber, final String town, final int zipCode) {
      super();
      this.street = street;
      this.houseNumber = houseNumber;
      this.town = town;
      this.zipCode = zipCode;
   }

   public int getAddressId() {
      return addressId;
   }

   public String getStreet() {
      return street;
   }

   public int getHouseNumber() {
      return houseNumber;
   }

   public String getTown() {
      return town;
   }

   public int getZipCode() {
      return zipCode;
   }

   public void setAddressId(final int addressId) {
      this.addressId = addressId;
   }

   public void setStreet(final String street) {
      this.street = street;
   }

   public void setHouseNumber(final int houseNumber) {
      this.houseNumber = houseNumber;
   }

   public void setTown(final String town) {
      this.town = town;
   }

   public void setZipCode(final int zipCode) {
      this.zipCode = zipCode;
   }

   @Override
   public String toString() {
      return "\n\tAddressDO [Id=" + addressId + ", " + street + " " + houseNumber + ", " + zipCode + " " + town + "]";
   }

}
