package de.doubleslash.jedm.model.businessentities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "bill")
public class BillBE {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "billid")
   private int billId;

   @Column(name = "billdate")
   private Date billDate;

   @Column(name = "sum")
   private int sum;

   @ManyToMany
   private Set<ItemBE> items = new HashSet<>();

   public BillBE() {
      super();
   }

   public BillBE(final Date billDate) {
      super();
      this.billDate = billDate;
      this.sum = 0;
   }

   public int getBillId() {
      return billId;
   }

   public Date getBillDate() {
      return billDate;
   }

   public int getSum() {
      return sum;
   }

   public Set<ItemBE> getItems() {
      return items;
   }

   public void setBillDate(final Date billDate) {
      this.billDate = billDate;
   }

   public void setSum(final int sum) {
      this.sum = sum;
   }

   public void setItems(final Set<ItemBE> items) {
      this.items = items;
   }

   @Override
   public String toString() {
      final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

      return "\n\tBillBE [Id=" + billId + ", Date=" + df.format(billDate) + ", sum=" + sum + "€, items=" + items + "]";
   }

}
