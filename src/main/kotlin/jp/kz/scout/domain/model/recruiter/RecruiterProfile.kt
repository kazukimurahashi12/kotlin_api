package jp.kz.scout.domain.model.recruiter

import java.time.LocalDateTime

/**
 * リクルーターのプロフィール情報を表す値オブジェクト
 *
 * リクルーターの個人情報、連絡先、所属部署などの プロフィール関連の情報をカプセル化する
 */
class RecruiterProfile
private constructor(
        val name: String,
        val email: Email,
        val department: String?,
        val jobTitle: String?,
        val phoneNumber: PhoneNumber?,
        val profileImageUrl: String?,
        val introduction: String?,
        val specialties: Set<String>,
        val languages: Set<Language>,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
) {}
