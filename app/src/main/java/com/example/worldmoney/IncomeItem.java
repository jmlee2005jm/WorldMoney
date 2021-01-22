package com.example.worldmoney;

public class IncomeItem {
    String amount,title,desc;
    long leftMoney;
    int ImageRes,moneySignColor;

    public int getMoneySignColor() {
        return moneySignColor;
    }

    public void setMoneySignColor(int moneySignColor) {
        this.moneySignColor = moneySignColor;
    }

    public int getImageRes() {
        return ImageRes;
    }

    public void setImageRes(int imageRes) {
        ImageRes = imageRes;
    }

    public long getLeftMoney() {
        return leftMoney;
    }

    public void setLeftMoney(long leftMoney) {
        this.leftMoney = leftMoney;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
