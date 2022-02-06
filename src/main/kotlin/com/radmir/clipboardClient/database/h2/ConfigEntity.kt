package com.radmir.clipboardClient.database.h2

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "config")
class ConfigEntity(
    @Id @Column(name = "component") val component: String? = null,
    @Column(name = "value") var value: String? = null
)