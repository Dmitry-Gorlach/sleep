package com.noom.interview.fullstack.sleep.mapper;

import com.noom.interview.fullstack.sleep.domain.dto.*;
import com.noom.interview.fullstack.sleep.domain.entity.SleepLog;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting between SleepLog entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface SleepLogMapper {

    /**
     * Converts a SleepLogRequest to a SleepLog entity.
     * 
     * @param request the SleepLogRequest DTO
     * @return the SleepLog entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "totalTimeInBedMinutes", ignore = true)
    SleepLog toEntity(SleepLogRequest request);

    /**
     * Converts a SleepLog entity to a SleepLogResponse DTO.
     * 
     * @param entity the SleepLog entity
     * @return the SleepLogResponse DTO
     */
    SleepLogResponse toResponse(SleepLog entity);
}
