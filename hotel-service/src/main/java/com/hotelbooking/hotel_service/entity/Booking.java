package com.hotelbooking.hotel_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Hotel id is required")
    private Long hotelId;

    @NotBlank(message = "Guest name cannot be blank")
    private String guestName;

    @Min(value = 1, message = "People count must be at least 1")
    private Integer peopleCount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Positive(message = "Total price must be positive")
    private Double totalPrice;

    private String status;
}