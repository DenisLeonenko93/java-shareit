package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable page);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable page);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable page);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable page);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable page);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable page);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long bookerId, Pageable page);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(Long itemId, BookingStatus status, LocalDateTime start);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, BookingStatus status, LocalDateTime start);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime end);
}
