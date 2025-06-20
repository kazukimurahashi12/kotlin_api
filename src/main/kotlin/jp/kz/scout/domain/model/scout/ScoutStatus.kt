package jp.kz.scout.domain.model.scout

/**
 * スカウトの状態を表す値オブジェクト
 *
 * スカウトのライフサイクル各段階を表現
 * 状態遷移のルールとビジネスロジックをカプセル化
 */
enum class ScoutStatus(
    val code: String,
    val displayName: String,
    val Description: String
) {
    /**
     * 下書き状態
     * スカウトが作成されたが、まだ送信されていない状態
     */
    DRAFT("DRAFT", "下書き", "作成済みだが未送信のスカウト"),

    /**
     * 送信済み状態
     * スカウトが候補者に送信された状態
     */
    SENT("SENT", "送信済み", "候補者に送信されたスカウト"),

    /**
     * 既読状態
     * 候補者がスカウトを確認した状態
     */
    READ("READ", "既読", "候補者が確認済みのスカウト"),

    /**
     * 返信済み状態
     * 候補者が興味を示して返信した状態
     */
    RESPONDED("RESPONDED", "返信済み", "候補者が興味を示して返信したスカウト"),

    /**
     * 辞退状態
     * 候補者がスカウトを辞退した状態
     */
    DECLINED("DECLINED", "辞退", "候補者が辞退したスカウト"),

    /**
     * キャンセル状態
     * リクルーターがスカウトをキャンセルした状態
     */
    CANCELLED("CANCELLED", "キャンセル", "リクルーターがキャンセルしたスカウト"),

    /**
     * 期限切れ状態
     * スカウトの有効期限が切れた状態
     */
    EXPIRED("EXPIRED", "期限切れ", "有効期限が切れたスカウト"),

    /**
     * 削除状態（論理削除）
     * スカウトが削除された状態
     */
    DELETED("DELETED", "削除済み", "削除されたスカウト");

    companion object {

        fun getActiveStatuses(): List<ScoutStatus> {
            return listOf(DRAFT, SENT, READ)
        }
    }

    fun isActive(): Boolean {
        return this in getActiveStatuses()
    }

}