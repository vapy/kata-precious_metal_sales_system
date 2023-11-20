package com.coding.sales.jx;

import javax.smartcardio.Card;

/**
 * @author jixiang
 * @date 3:06 下午 2023/11/20
 * 客户等级
 **/
public class CustomerLevel {
    public static Level getCustomerLevel(int points) {


        return Level.CLASSIC_CARD;
    }
}

enum Level {
    // 普卡，[0, 10000)
    CLASSIC_CARD("普卡", 1),
    // 金卡 [10000, 50000)
    GOLD_CARD("金卡", 1.5),
    // 白金卡 [50000, 100000)
    PLATINUM_CARD("白金卡", 1.8),
    // 钻石卡 [100000, ∞)
    DIAMOND_CARD("钻石卡", 2);

    String cardLevel;
    double pointsMultiple;

    Level(String cardLevel, double pointsMultiple) {
        this.cardLevel = cardLevel;
        this.pointsMultiple = pointsMultiple;
    }
}
