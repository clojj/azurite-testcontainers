package com.lectra.jdbc

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LegoModelRepository : CrudRepository<LegoModel, Long>
