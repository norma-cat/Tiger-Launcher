package org.elnix.dragonlauncher.data

class BackupTypeException(
    val key: String,
    val expected: String,
    val actual: String?,
    val value: Any?
) : Exception(
    "Backup error for key='$key': expected=$expected but got=$actual (value=$value)"
)