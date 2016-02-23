package de.doubleslash.jedm.model.dataobjects;

public class ItemDO {
   private int itemId;
   private String label;
   private int singlePrice;
   private int quantity;

   public ItemDO() {
      super();
   }

   public ItemDO(final String label, final int singlePrice, final int quantity) {
      super();
      this.label = label;
      this.singlePrice = singlePrice;
      this.quantity = quantity;
   }

   public int getItemId() {
      return itemId;
   }

   public String getLabel() {
      return label;
   }

   public int getSinglePrice() {
      return singlePrice;
   }

   public int getQuantity() {
      return quantity;
   }

   public void setItemId(final int itemId) {
      this.itemId = itemId;
   }

   public void setLabel(final String label) {
      this.label = label;
   }

   public void setSinglePrice(final int singlePrice) {
      this.singlePrice = singlePrice;
   }

   public void setQuantity(final int quantity) {
      this.quantity = quantity;
   }

   @Override
   public String toString() {
      return "\n\t\tItem [Id=" + itemId + ", label=" + label + ", singlePrice=" + singlePrice + "€, quantity="
            + quantity + "]";
   }

}
