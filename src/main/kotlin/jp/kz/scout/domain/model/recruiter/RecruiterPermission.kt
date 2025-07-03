package jp.kz.scout.domain.model.recruiter

/**
 * リクルーターの権限設定を表す値オブジェクト
 *
 * リクルーターが実行可能な操作や、アクセス可能なリソースに関する 権限情報をカプセル化する
 */
class RecruiterPermission
private constructor(
        val permissions: Set<PermissionType>,
        val role: Role,
        val restrictions: Set<Restriction>,
        val grantedAt: LocalDateTime,
        val expiresAt: LocalDateTime?,
        val grantedBy: String? // 権限を付与した管理者等のID
) {}
