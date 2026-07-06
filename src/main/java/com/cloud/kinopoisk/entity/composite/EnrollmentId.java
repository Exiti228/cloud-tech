package com.cloud.kinopoisk.entity.composite;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentId implements Serializable {
    private UUID userId;

    private UUID movieId;
}
