package jp.kz.scout.domain.model.recruiter

import java.time.LocalDateTime

/**
 * リクルーター集約のルート
 *
 * 求人スカウトを送信する企業担当者を表すドメインエンティティ。 プロフィール、権限、送信枠の整合性を保証し、 スカウト送信に関するビジネスルールを管理する。
 */
class Recruiter
private constructor(
        val id: RecruiterId,
        val companyId: CompanyId,
        private var profile: RecruiterProfile,
        private var permission: RecruiterPermission,
        private var quota: RecruiterQuota,
        private var status: RecruiterStatus,
        val registeredAt: LocalDateTime,
        private var lastActiveAt: LocalDateTime,
        private var updatedAt: LocalDateTime
) {}
