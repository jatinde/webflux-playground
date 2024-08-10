package com.github.jatinde.webflux_payground.dto;

import java.util.UUID;

public record UploadResponse(UUID confirmationId, Long productsCount) {
    
}
