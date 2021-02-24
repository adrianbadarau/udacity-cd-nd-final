package com.adrianbadarau.klist.front_end.service.dto

/**
 * A DTO representing a password change required data - current and new password.
 */
data class PasswordChangeDTO(var currentPassword: String? = null, var newPassword: String? = null)
