package com.lit.litmoments;

import java.util.Date;

public class SubscribersModelActivity {

    private String isActiveMember;
    private String  purchaseDate;
    private String  expiryDate;
    private String productId;

    public SubscribersModelActivity(String isActiveMember, String purchaseDate, String expiryDate, String productId){

       this.isActiveMember = isActiveMember;
       this.purchaseDate = purchaseDate;
       this.expiryDate = expiryDate;
       this.productId = productId;
    }

    public SubscribersModelActivity(){

    }
    public String isActiveMember() {
        return isActiveMember;
    }

    public void setActiveMember(String activeMember) {
        isActiveMember = activeMember;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
