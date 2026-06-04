package com.lahoa.lahoa_be.dto.error;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MaterialStockInfoDTO {

    String warehouseName;
    Integer onHand;
    Integer reserved;
}
