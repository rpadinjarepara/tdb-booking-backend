package com.tdb.booking.repository;

import com.tdb.booking.entity.DarshanSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SlotRepository
        extends JpaRepository<DarshanSlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM DarshanSlot s WHERE s.id = :id")
    Optional<DarshanSlot> findByIdForUpdate(
            @Param("id") Long id);
}