package com.example.pizzumburgum.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {

    private Long id;
    private BigDecimal total;
    private List<CarritoItemDTO> items;
}
