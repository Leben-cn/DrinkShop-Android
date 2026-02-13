package com.leben.shop.model.bean;


import com.leben.base.widget.linkage.ILinkageItem;

public class LinkageRightEntity implements ILinkageItem {

    private boolean isHeader;
    private String headerName;
    private DrinkEntity drink;

    //标题
    public LinkageRightEntity(String headerName) {
        this.isHeader = true;
        this.headerName = headerName;
    }
    //商品
    public LinkageRightEntity(DrinkEntity drink) {
        this.isHeader = false;
        this.drink = drink;
    }

    public DrinkEntity getDrink() { return drink; }

    @Override
    public boolean isHeader() {
        return isHeader;
    }

    @Override
    public String getHeaderName() {
        return headerName;
    }
}
