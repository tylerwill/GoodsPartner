package com.goodspartner.service.document;

import com.goodspartner.service.dto.DocumentContent;
import com.goodspartner.service.dto.DocumentDto;

public interface ItineraryContentGenerator {
    DocumentContent getItineraryContent(DocumentDto documentDto);
}
