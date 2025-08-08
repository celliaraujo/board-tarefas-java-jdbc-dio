package br.com.ca.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BlockEntity {
    private Long id;
    private OffsetDateTime blokedAt;
    private String blockReason;
    private OffsetDateTime unblokedAt;
    private String unblockReason;
}
