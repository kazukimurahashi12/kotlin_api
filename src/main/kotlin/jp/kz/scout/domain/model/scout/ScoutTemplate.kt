package jp.kz.scout.domain.model.scout

/**
 * スカウトのテンプレートを表すエンティティ
 *
 * 再利用可能なスカウトメッセージのテンプレートを管理し パーソナライゼーションやカスタマイズ機能を提供する
 */
class ScoutTemplate
private constructor(
        val id: TemplateId,
        val companyId: CompanyId,
        val createdBy: RecruiterId,
        private var name: String,
        private var description: String,
        private var category: TemplateCategory,
        private var subjectTemplate: String,
        private var bodyTemplate: String,
        private var personalizedGreetingTemplate: String?,
        private var companyIntroductionTemplate: String?,
        private var positionDescriptionTemplate: String?,
        private var benefitsTemplate: String?,
        private var callToActionTemplate: String,
        private var isActive: Boolean,
        private var isShared: Boolean,
        val createdAt: LocalDateTime,
        private var updatedAt: LocalDateTime,
        private var usageCount: Int = 0,
        private var successRate: Double = 0.0
) {}
