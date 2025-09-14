package com.ahumadamob.fnanz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Entity
@Table(name = "pictures")
@Getter
@Setter
@NoArgsConstructor
public class Picture extends BaseEntity {

    @Column(name = "url", nullable = false)
    @NotBlank
    @URL
    private String url;

    @Column(name = "path", nullable = false)
    @NotBlank
    private String path;

    @Column(name = "file_name", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String fileName;

    @Column(name = "mime_type", nullable = false)
    @NotBlank
    @Pattern(regexp = "^[\\w.+-]+/[\\w.+-]+$", message = "Formato MIME inválido")
    private String mimeType;

    @Column(name = "size", nullable = false)
    @NotNull
    @Min(1)
    @Max(5 * 1024 * 1024)
    private Long size;

    @Column(name = "ordering")
    @Min(0)
    private Integer order;

    @Column(name = "cover", nullable = false)
    @NotNull
    private Boolean cover = false;

    @Column(name = "created_date", nullable = false)
    @NotNull
    private LocalDateTime createdDate;

    @PrePersist
    public void prePersist() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }
}
