package com.cloud.kinopoisk.entity.composite;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
public class EnrollmentId implements Serializable {
    private UUID userId;
    private UUID movieId;
}
