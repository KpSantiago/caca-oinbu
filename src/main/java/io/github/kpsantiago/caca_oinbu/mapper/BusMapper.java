package io.github.kpsantiago.caca_oinbu.mapper;

import io.github.kpsantiago.caca_oinbu.dto.request.BusRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.BusResponseDto;
import io.github.kpsantiago.caca_oinbu.model.Bus;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BusMapper {
    private final ModelMapper modelMapper;

    public BusResponseDto toDto(Bus entity) {
        return  modelMapper.map(entity, BusResponseDto.class);
    }

    public Bus toEntity(BusRequestDto dto) {
        return modelMapper.map(dto, Bus.class);
    }

}
