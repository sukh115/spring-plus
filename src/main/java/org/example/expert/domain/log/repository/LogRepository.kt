package org.example.expert.domain.log.repository

import org.example.expert.domain.log.entity.Log
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LogRepository : JpaRepository<Log, Long>
