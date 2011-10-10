package org.motechproject.ghana.mtn.billing.dto;

public class CustomerBill {

   private Double amountCharged;
   private String message;

   public CustomerBill(String value, Double amountCharged) {
       this.message = value;
       this.amountCharged = amountCharged;
   }

   public CustomerBill() {
   }

    public Double getAmountCharged() {
        return amountCharged;
    }

    public String getMessage() {
        return message;
    }
}