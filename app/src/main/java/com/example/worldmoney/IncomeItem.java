package com.example.worldmoney;

public class IncomeItem {
    String amount,desc;
    int ImageRes;
    Boolean isPositive,isCash;

    public Boolean getCash() {
        return isCash;
    }

    public void setCash(Boolean cash) {
        isCash = cash;
    }

    public Boolean getPositive() {
        return isPositive;
    }

    public void setPositive(Boolean positive) {
        isPositive = positive;
    }

    public int getImageRes() {
        return ImageRes;
    }

    public void setImageRes(int imageRes) {
        ImageRes = imageRes;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
