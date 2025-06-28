package jp.kz.scout.domain.model.scout

import java.time.temporal.ChronoUnit

/**
 * スカウト有効期限ポリシーを表すドメインオブジェクト
 *
 * スカウト有効期限の計算ロジックと有効期限に関するビジネスルールをカプセル化する。 異なる職種や状況に応じて柔軟に期限設定を可能にする。
 */
class ScoutExpiration
private constructor(
        val type: ExpirationType,
        val durationValue: Long,
        val durationUnit: ChronoUnit,
        val extendable: Boolean,
        val maxExtensions: Int,
        val warningThresholdHours: Long
) {

    /** 有効期限のタイプを定義 */
    enum class ExpirationType(val displayName: String, val description: String) {
        STANDARD("標準", "一般的なスカウトの標準期限"),
        URGENT("緊急", "急募案件用の短期期限"),
        EXECUTIVE("役員", "役員・管理職向けの長期期限"),
        INTERNSHIP("インターン", "インターンシップ用の期限"),
        FREELANCE("フリーランス", "フリーランス案件用の期限"),
        CUSTOM("カスタム", "個別設定された期限")
    }
}
