package au.edu.jcu.cp3406.outdoorready.model

data class ReadinessSummary(
    val headline: String,
    val reason: String,
    val advice: List<String>,
    val severity: SummarySeverity,
)

enum class SummarySeverity {
    Low,
    Moderate,
    High,
}

