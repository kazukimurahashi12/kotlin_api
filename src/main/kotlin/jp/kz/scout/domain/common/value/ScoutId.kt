package jp.kz.scout.domain.common.value

import java.util.UUID

/** スカウトを識別するための値オブジェクト */
class ScoutId private constructor(private val value: String) {

    companion object {
        // 識別子のプレフィックス
        private const val PREFIX = "SCT-"

        /**
         * 新しいスカウトIDを生成
         *
         * @return 新しく生成されたスカウトID
         */
        fun generate(): ScoutId {
            val uuid = UUID.randomUUID().toString()
            return ScoutId("$PREFIX$uuid")
        }

        /**
         * 既存の文字列からスカウトIDを作成 主にリポジトリからの再構築時に使用される
         *
         * @param id ID文字列
         * @return 指定された文字列から作成されたスカウトID
         * @throws IllegalArgumentException 文字列が有効な形式でない場合
         */
        fun from(id: String): ScoutId {
            validate(id)
            return ScoutId(id)
        }

        /**
         * 外部システムから受け取ったIDを、スカウトIDの形式に変換して作成 レガシーシステムとの連携時などに使用
         *
         * @param externalId 外部システムのID
         * @return 変換されたスカウトID
         */
        fun fromExternal(externalId: String): ScoutId {
            // 外部IDを内部形式に変換（必要に応じて）
            return if (externalId.startsWith(PREFIX)) {
                from(externalId)
            } else {
                ScoutId("$PREFIX$externalId")
            }
        }

        /**
         * 複数のスカウトIDをカンマ区切りの文字列から解析 バッチ処理などで使用
         *
         * @param idsString カンマ区切りのID文字列
         * @return スカウトIDのリスト
         */
        fun parseMultiple(idsString: String): List<ScoutId> {
            return idsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }.map {
                from(it)
            }
        }

        /**
         * ID文字列のバリデーション
         *
         * @param id 検証するID文字列
         * @throws IllegalArgumentException 無効な形式の場合
         */
        private fun validate(id: String) {
            if (id.isBlank()) {
                throw IllegalArgumentException("スカウトIDは空文字列にできません")
            }

            if (id.startsWith(PREFIX)) {
                // プレフィックス付きの場合はUUID部分を検証
                val uuidPart = id.substring(PREFIX.length)
                try {
                    UUID.fromString(uuidPart)
                } catch (e: IllegalArgumentException) {
                    throw IllegalArgumentException("スカウトIDの形式が不正です: $id", e)
                }
            } else {
                // プレフィックスなしの場合は全体をUUIDとして検証
                try {
                    UUID.fromString(id)
                } catch (e: IllegalArgumentException) {
                    // レガシーIDの場合は形式を限定しない
                    if (!id.matches(Regex("^[a-zA-Z0-9-_]+$"))) {
                        throw IllegalArgumentException("スカウトIDに無効な文字が含まれています: $id")
                    }
                }
            }
        }
    }

    /**
     * このスカウトIDの文字列表現を取得する
     *
     * @return ID文字列
     */
    fun value(): String {
        return value
    }

    /**
     * プレフィックスを除去したUUID部分のみを取得 外部システムとの連携時に使用
     *
     * @return プレフィックスを除去したUUID文字列
     */
    fun withoutPrefix(): String {
        return if (value.startsWith(PREFIX)) {
            value.substring(PREFIX.length)
        } else {
            value
        }
    }

    /**
     * このスカウトIDが特定のプレフィックスを持つかどうかを確認
     *
     * @return プレフィックスを持つ場合はtrue
     */
    fun hasPrefix(): Boolean {
        return value.startsWith(PREFIX)
    }

    /**
     * このオブジェクトの文字列表現を返す
     *
     * @return ID文字列
     */
    override fun toString(): String {
        return value
    }

    /**
     * このスカウトIDと他のオブジェクトの等価性を判定
     *
     * @param other 比較対象のオブジェクト
     * @return 同じスカウトIDである場合はtrue、それ以外はfalse
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScoutId
        return value == other.value
    }

    /**
     * このスカウトIDのハッシュコードを計算
     *
     * @return ハッシュコード
     */
    override fun hashCode(): Int {
        return value.hashCode()
    }

    /** 他のスカウトIDと比較 ソート処理などで使用 */
    operator fun compareTo(other: ScoutId): Int {
        return value.compareTo(other.value)
    }
}
