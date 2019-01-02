package pojoGroup;

import java.io.Serializable;
import java.util.Arrays;

public class OrderVo implements Serializable {

    //商品名称
    private String goodName;

    //商品销量
    private int num;

    //销售总金额
    private double totalFee;

    //时间区间
    private String[] time;

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }

    public String[] getTime() {
        return time;
    }

    public void setTime(String[] time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "OrderVo{" +
                "goodName='" + goodName + '\'' +
                ", num=" + num +
                ", totalFee=" + totalFee +
                ", time=" + Arrays.toString(time) +
                '}';
    }
}
