package com.kopnyaev.fschool.repository;

import com.kopnyaev.fschool.model.Apprentice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprenticeRepository extends JpaRepository<Apprentice, Long> {
}
