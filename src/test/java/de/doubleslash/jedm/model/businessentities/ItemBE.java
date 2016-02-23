package de.doubleslash.jedm.model.businessentities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "item")
public class ItemBE {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "itemid")
   private int itemId;

   @Column(name = "description", length = 255)
   private String description;

   @Column(name = "singleprice")
   private int singlePrice;

   @Column(name = "quantity")
   private int quantity;

   public ItemBE() {
      super();
   }

   public ItemBE(final String description, final int price, final int quantity) {
      super();
      this.description = description;
      this.singlePrice = price;
      this.quantity = quantity;
   }

   public int getItemId() {
      return itemId;
   }

   public String getDescription() {
      return description;
   }

   public int getSinglePrice() {
      return singlePrice;
   }

   public int getQuantity() {
      return quantity;
   }

   public void setDescription(final String description) {
      this.description = description;
   }

   public void setSinglePrice(final int singlePrice) {
      this.singlePrice = singlePrice;
   }

   public void setQuantity(final int quantity) {
      this.quantity = quantity;
   }

   @Override
   public String toString() {
      return "\n\t\tItem [Id=" + itemId + ", description=" + description + ", singlePrice=" + singlePrice
            + "€, quantity=" + quantity + "]";
   }

}