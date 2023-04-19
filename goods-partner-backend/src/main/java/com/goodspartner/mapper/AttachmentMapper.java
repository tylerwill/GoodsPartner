package com.goodspartner.mapper;

import com.goodspartner.dto.AttachmentDto;
import com.goodspartner.entity.Attachment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    AttachmentDto toAttachmetDto(Attachment attachment);

    List<AttachmentDto> toAttachmetDtoList(List<Attachment> attachments);

}
