package jp.kz.scout.domain.model.scout

import java.time.LocalDateTime
import jp.kz.scout.domain.common.exception.DomainException
import jp.kz.scout.domain.common.value.CandidateId
import jp.kz.scout.domain.common.value.JobOfferId
import jp.kz.scout.domain.common.value.RecruiterId
import jp.kz.scout.domain.common.value.ScoutId
import jp.kz.scout.domain.event.ScoutCreatedEvent
import jp.kz.scout.domain.event.ScoutStatusChangedEvent

/**
 * スカウト集約のルート
 *
 * リクルーターから候補者へ送信される求人スカウトを表すドメインエンティティ。 スカウトには状態遷移があり、作成→送信→既読→返信というライフサイクルをもつ。
 * スカウトの内容やステータスの整合性をこのエンティティが保証。
 */
class Scout
private constructor(
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
    // ドメインイベントを格納するリスト
    private val events = mutableListOf<Any>()

    // イベントを取得するためのメソッド
    fun getDomainEvents(): List<Any> {
        return events.toList()
    }

    // イベントをクリアするメソッド
    fun clearEvents() {
        events.clear()
    }

    /** スカウトの状態を取得する */
    fun getStatus(): ScoutStatus = status

    /** スカウトのメッセージを取得する */
    fun getMessage(): ScoutMessage = message

    /** スカウトの有効期限を取得する */
    fun getExpiresAt(): LocalDateTime = expiresAt

    /** スカウトが既読された日時を取得する */
    fun getReadAt(): LocalDateTime? = readAt

    /** スカウトが返信された日時を取得する */
    fun getRespondedAt(): LocalDateTime? = respondedAt

    /** ファクトリメソッド - 新しいスカウトを作成する */
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

            val scout =
                    Scout(
                            id = id,
                            recruiterId = recruiterId,
                            candidateId = candidateId,
                            jobOfferId = jobOfferId,
                            message = message,
                            status = ScoutStatus.DRAFT,
                            createdAt = now,
                            expiresAt = expirationPolicy.calculateExpirationDate(now)
                    )

            // スカウト作成イベントを発行
            scout.events.add(
                    ScoutCreatedEvent(
                            scoutId = id,
                            recruiterId = recruiterId,
                            candidateId = candidateId,
                            jobOfferId = jobOfferId,
                            timestamp = now
                    )
            )

            return scout
        }

        /** リポジトリから再構築するためのファクトリメソッド (このメソッドはリポジトリ実装からのみ使用される) */
        fun reconstitute(
                id: ScoutId,
                recruiterId: RecruiterId,
                candidateId: CandidateId,
                jobOfferId: JobOfferId,
                message: ScoutMessage,
                status: ScoutStatus,
                createdAt: LocalDateTime,
                expiresAt: LocalDateTime,
                readAt: LocalDateTime?,
                respondedAt: LocalDateTime?
        ): Scout {
            return Scout(
                    id = id,
                    recruiterId = recruiterId,
                    candidateId = candidateId,
                    jobOfferId = jobOfferId,
                    message = message,
                    status = status,
                    createdAt = createdAt,
                    expiresAt = expiresAt,
                    readAt = readAt,
                    respondedAt = respondedAt
            )
        }
    }

    /** スカウトを送信する DRAFT状態のスカウトのみ送信可能 */
    fun send(): Scout {
        if (status != ScoutStatus.DRAFT) {
            throw DomainException("スカウトは下書き状態でなければ送信できません。現在の状態: $status")
        }

        val oldStatus = status
        status = ScoutStatus.SENT

        // スカウト状態変更イベントを発行
        events.add(
                ScoutStatusChangedEvent(
                        scoutId = id,
                        oldStatus = oldStatus,
                        newStatus = status,
                        timestamp = LocalDateTime.now()
                )
        )

        return this
    }

    /** スカウトを既読にする SENT状態のスカウトのみ既読にできる */
    fun markAsRead(): Scout {
        if (status != ScoutStatus.SENT) {
            throw DomainException("送信済みのスカウトのみ既読にできます。現在の状態: $status")
        }

        if (isExpired()) {
            throw DomainException("有効期限切れのスカウトは既読にできません")
        }

        val oldStatus = status
        status = ScoutStatus.READ
        readAt = LocalDateTime.now()

        // スカウト状態変更イベントを発行
        events.add(
                ScoutStatusChangedEvent(
                        scoutId = id,
                        oldStatus = oldStatus,
                        newStatus = status,
                        timestamp = readAt!!
                )
        )

        return this
    }

    /** スカウトに返信する READ状態のスカウトのみ返信できる */
    fun respond(): Scout {
        if (status != ScoutStatus.READ) {
            throw DomainException("既読のスカウトのみ返信できます。現在の状態: $status")
        }

        if (isExpired()) {
            throw DomainException("有効期限切れのスカウトは返信できません")
        }

        val oldStatus = status
        status = ScoutStatus.RESPONDED
        respondedAt = LocalDateTime.now()

        // スカウト状態変更イベントを発行
        events.add(
                ScoutStatusChangedEvent(
                        scoutId = id,
                        oldStatus = oldStatus,
                        newStatus = status,
                        timestamp = respondedAt!!
                )
        )

        return this
    }

    /** スカウトを拒否する READ状態のスカウトのみ拒否できる */
    fun decline(): Scout {
        if (status != ScoutStatus.READ) {
            throw DomainException("既読のスカウトのみ拒否できます。現在の状態: $status")
        }

        if (isExpired()) {
            throw DomainException("有効期限切れのスカウトは拒否できません")
        }

        val oldStatus = status
        status = ScoutStatus.DECLINED
        respondedAt = LocalDateTime.now()

        // スカウト状態変更イベントを発行
        events.add(
                ScoutStatusChangedEvent(
                        scoutId = id,
                        oldStatus = oldStatus,
                        newStatus = status,
                        timestamp = respondedAt!!
                )
        )

        return this
    }

    /** スカウトの有効期限が切れているかどうかを確認する */
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }

    /** スカウトメッセージを更新する DRAFT状態のスカウトのみ編集可能 */
    fun updateMessage(newMessage: ScoutMessage): Scout {
        if (status != ScoutStatus.DRAFT) {
            throw DomainException("スカウトメッセージは下書き状態でのみ編集できます。現在の状態: $status")
        }

        message = newMessage
        return this
    }

    /** スカウトの有効期限を延長する SENT状態のスカウトのみ延長可能 */
    fun extendExpiration(newExpirationPolicy: ScoutExpiration): Scout {
        if (status != ScoutStatus.SENT) {
            throw DomainException("送信済みのスカウトのみ有効期限を延長できます。現在の状態: $status")
        }

        if (!isExpired()) {
            val now = LocalDateTime.now()
            val newExpiresAt = newExpirationPolicy.calculateExpirationDate(now)

            // 現在の有効期限よりも新しい有効期限の方が後の場合のみ更新
            if (newExpiresAt.isAfter(expiresAt)) {
                expiresAt = newExpiresAt
            }
        } else {
            throw DomainException("有効期限切れのスカウトは期限を延長できません")
        }

        return this
    }

    /** スカウトをキャンセルする DRAFT, SENT状態のスカウトのみキャンセル可能 */
    fun cancel(): Scout {
        if (status != ScoutStatus.DRAFT && status != ScoutStatus.SENT) {
            throw DomainException("下書きか送信済みのスカウトのみキャンセルできます。現在の状態: $status")
        }

        val oldStatus = status
        status = ScoutStatus.CANCELLED

        // スカウト状態変更イベントを発行
        events.add(
                ScoutStatusChangedEvent(
                        scoutId = id,
                        oldStatus = oldStatus,
                        newStatus = status,
                        timestamp = LocalDateTime.now()
                )
        )

        return this
    }

    /** スカウトを削除（論理削除）する キャンセル済みか期限切れのスカウトのみ削除可能 */
    fun delete(): Scout {
        if (status != ScoutStatus.CANCELLED && !isExpired()) {
            throw DomainException("キャンセル済みか期限切れのスカウトのみ削除できます。現在の状態: $status")
        }

        val oldStatus = status
        status = ScoutStatus.DELETED

        // スカウト状態変更イベントを発行
        events.add(
                ScoutStatusChangedEvent(
                        scoutId = id,
                        oldStatus = oldStatus,
                        newStatus = status,
                        timestamp = LocalDateTime.now()
                )
        )

        return this
    }

    /** この集約のビジネス等価性を定義 */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Scout
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
