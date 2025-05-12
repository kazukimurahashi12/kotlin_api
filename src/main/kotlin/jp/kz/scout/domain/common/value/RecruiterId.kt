package jp.kz.scout.domain.common.value

import java.util.UUID

/** リクルーターを識別するための値オブジェクト */
class RecruiterId private constructor(private val value: String) {

    companion object {
        /**
         * 新しいリクルーターIDを生成する
         *
         * @return 新しく生成されたリクルーターID
         */
        fun generate(): RecruiterId {
            return RecruiterId(UUID.randomUUID().toString())
        }

        /**
         * 既存の文字列からリクルーターIDを作成する 主にリポジトリからの再構築時に使用される
         *
         * @param id ID文字列
         * @return 指定された文字列から作成されたリクルーターID
         * @throws IllegalArgumentException 文字列が有効なUUID形式でない場合
         */
        fun from(id: String): RecruiterId {
            // 入力検証：UUIDの妥当性をチェック
            try {
                UUID.fromString(id)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("リクルーターIDは有効なUUID形式である必要があります: $id", e)
            }
            return RecruiterId(id)
        }
    }

    /**
     * このリクルーターIDの文字列表現を取得する
     *
     * @return ID文字列
     */
    fun value(): String {
        return value
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
     * このリクルーターIDと他のオブジェクトの等価性を判定する
     *
     * @param other 比較対象のオブジェクト
     * @return 同じリクルーターIDである場合はtrue、それ以外はfalse
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecruiterId
        return value == other.value
    }

    /**
     * このリクルーターIDのハッシュコードを計算する
     *
     * @return ハッシュコード
     */
    override fun hashCode(): Int {
        return value.hashCode()
    }
}
