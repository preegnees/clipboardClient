package com.radmir.clipboardClient.database.h2

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfigDAO: JpaRepository<ConfigEntity?, String?>{}