package jp.kz.scout.domain.model.scout

import java.time.LocalDateTime

/** スカウトに含まれるメッセージ内容を表す値オブジェクト */
class ScoutMessage
private constructor(
        val subject: String,
        val body: String,
        val personalizedGreeting: String?,
        val companyIntroduction: String?,
        val positionDescription: String?,
        val benefits: String?,
        val callToAction: String,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
) {

    /**
     * 新しいスカウトメッセージを作成する
     *
     * @param subject 件名
     * @param body メッセージ本文
     * @param personalizedGreeting 個人向けの挨拶（オプション）
     * @param companyIntroduction 会社紹介（オプション）
     * @param positionDescription 職種説明（オプション）
     * @param benefits 福利厚生・メリット（オプション）
     * @param callToAction 行動喚起メッセージ
     * @return 作成されたスカウトメッセージ
     */
    fun create(
            subject: String,
            body: String,
            personalizedGreeting: String? = null,
            companyIntroduction: String? = null,
            positionDescription: String? = null,
            benefits: String? = null,
            callToAction: String = "ご興味をお持ちいただけましたら、ぜひご返信ください。"
    ): ScoutMessage {
        val now = LocalDateTime.now()

        return ScoutMessage(
                        subject = subject.trim(),
                        body = body.trim(),
                        personalizedGreeting = personalizedGreeting?.trim(),
                        companyIntroduction = companyIntroduction?.trim(),
                        positionDescription = positionDescription?.trim(),
                        benefits = benefits?.trim(),
                        callToAction = callToAction.trim(),
                        createdAt = now,
                        updatedAt = now
                )
                .also { it.validate() }
    }

    /**
     * メッセージの妥当性を検証する
     *
     * @throws Exception バリデーションエラーの場合
     */
    private fun validate() {
        // 件名の検証
        if (subject.isBlank()) {
            throw IllegalArgumentException("スカウトの件名は必須です")
        }
    }
}
