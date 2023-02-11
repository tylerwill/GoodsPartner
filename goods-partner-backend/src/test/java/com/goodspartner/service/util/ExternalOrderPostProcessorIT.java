package com.goodspartner.service.util;

import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.DeliveryType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

class ExternalOrderPostProcessorIT extends AbstractBaseITest {

    @Autowired
    private ExternalOrderPostProcessor orderPostProcessor;

    @Test
    void checkCommentIfShippedByPostalTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        OrderDto orderDto = new OrderDto(151, "00000003433", "00000152",
                "бн, НП: м. Миколаїв, Херсонське шосе, 1, ДЛЯ КАФЕ, отримувач Марченко Марьяна Миколаївна, тел. 095-150-17-45, доставку сплачує ГД     "
                , "Марія Стасів", null, null, null
                , false, false, false, null, null
                , null, null, "Ашан", "Львів", null, null, 5);

        orderPostProcessor.checkIfPostal(Collections.singletonList(orderDto));
        Assertions.assertEquals(DeliveryType.POSTAL, orderDto.getDeliveryType());
    }
}