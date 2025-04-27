package com.bytebandit.fileservice.mapper;

import com.bytebandit.fileservice.dto.CreateItemResponse;
import com.bytebandit.fileservice.model.FileSystemItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileSystemItemsMapper {

    @Mapping(source = "id", target = "itemId")
    CreateItemResponse toCreateItemResponse(FileSystemItemEntity fileSystemItemEntity);
}
