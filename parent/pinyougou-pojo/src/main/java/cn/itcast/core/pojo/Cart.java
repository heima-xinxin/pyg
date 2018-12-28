package cn.itcast.core.pojo;

import cn.itcast.core.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable {
    //商品id
    private String sellerId;
    //商品名称
    private String sellerName;
    //商品集合(可能有多个商品)
    private List<OrderItem> orderItemList;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "sellerId='" + sellerId + '\'' +
                ", sellerName='" + sellerName + '\'' +
                ", orderItemList=" + orderItemList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cart cart = (Cart) o;

        return sellerId != null ? sellerId.equals(cart.sellerId) : cart.sellerId == null;
    }

    @Override
    public int hashCode() {
        return sellerId != null ? sellerId.hashCode() : 0;
    }
}
