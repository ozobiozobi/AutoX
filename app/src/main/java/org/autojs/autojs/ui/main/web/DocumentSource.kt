package org.autojs.autojs.ui.main.web

enum class DocumentSource(
    val sourceName: String,
    val uri: String,
    val isLocal: Boolean = false,
    val openUri: String? = null
) {
    DOC_V2_LOCAL("本地文档v2", "docs/v2", true,"https://autox-doc.vercel.app/")
}