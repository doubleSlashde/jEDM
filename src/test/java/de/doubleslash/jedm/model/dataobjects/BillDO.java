package de.doubleslash.jedm.model.dataobjects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillDO {
   private int billId;
   private Date billDate;
   private int total;
   private List<ItemDO> items = new ArrayList<>();

   public BillDO() {
      super();
   }

   public BillDO(final Date billDate, final int total, final List<ItemDO> items) {
      super();
      this.billDate = billDate;
      this.total = total;
      this.items = items;
   }

   public int getBillId() {
      return billId;
   }

   public Date getBillDate() {
      return billDate;
   }

   public int getTotal() {
      return total;
   }

   public List<ItemDO> getItems() {
      return items;
   }

   public void setBillId(final int billId) {
      this.billId = billId;
   }

   public void setBillDate(final Date billDate) {
      this.billDate = billDate;
   }

   public void setTotal(final int total) {
      this.total = total;
   }

   public void setItems(final List<ItemDO> items) {
      this.items = items;
   }

   @Override
   public String toString() {
      final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

      return "\n\tBillDO [Id=" + billId + ", Date=" + df.format(billDate) + ", total=" + total + "€, items=" + items
            + "]";
   }

}
