package com.coding.sales.jx

import spock.lang.Shared
import spock.lang.Unroll

import javax.xml.transform.Result

/**
 *
 * @author jixiang* @date 3:18 下午 2023/11/20
 * CustomerLevelTest 单元测试
 *
 * */
class CustomerLevelTest extends spock.lang.Specification {

    @Unroll
    def "getCustomerLevel"() {
        expect:
        CustomerLevel.getCustomerLevel(points as int) == result

        where:
        points      |   result
        0           |   Level.CLASSIC_CARD
        100         |   Level.CLASSIC_CARD
        10000       |   Level.GOLD_CARD
        15000       |   Level.GOLD_CARD
        50000       |   Level.PLATINUM_CARD
        75000       |   Level.PLATINUM_CARD
        100000      |   Level.DIAMOND_CARD
        110000      |   Level.DIAMOND_CARD
    }
}
