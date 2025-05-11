package jp.kz.scout.domain.model.scout

import java.time.LocalDateTime

/**
 * スカウト集約のルート
 *
 * リクルーターから候補者へ送信される求人スカウトを表すドメインエンティティ
 */
class Scout private constructor(
    val id: ScoutId,
    val recruiterId: RecruiterId,
    val candidateId: CandidateId,
    val jobOfferId: JobOfferId,
    private var message: ScoutMessage,
    private var status: ScoutStatus,
    val createdAt: LocalDateTime,
    private var expiresAt: LocalDateTime,
    private var readAt: LocalDateTime? = null,
    private var respondedAt: LocalDateTime? = null
) {
    // スカウトの状態を取得
    fun getStatus(): ScoutStatus = status

    // スカウトのメッセージを取得
    fun getMessage(): ScoutMessage = message

    // スカウトの有効期限を取得
    fun getExpiresAt(): LocalDateTime = expiresAt

    // スカウトが既読された日時を取得
    fun getReadAt(): LocalDateTime? = readAt

    // スカウトが返信された日時を取得
    fun getRespondedAt(): LocalDateTime? = respondedAt

    // ファクトリメソッド(新しいスカウトを作成)
    companion object {
        fun create(
            recruiterId: RecruiterId,
            candidateId: CandidateId,
            jobOfferId: JobOfferId,
            message: ScoutMessage,
            expirationPolicy: ScoutExpiration
        ): Scout {
            val now = LocalDateTime.now()
            val id = ScoutId.generate()

            val scout = Scout(
                id = id,
                recruiterId = recruiterId,
                candidateId = candidateId,
                jobOfferId = jobOfferId,
                message = message,
                status = ScoutStatus.DRAFT,
                createdAt = now,
                expiresAt = expirationPolicy.calculateExpirationDate(now)
            )

            // スカウト作成イベント発行
            scout.events.add(ScoutCreatedEvent(
                scoutId = id,
                recruiterId = recruiterId,
                candidateId = candidateId,
                jobOfferId = jobOfferId,
                timestamp = now
            ))

            return scout
        }
    }
}