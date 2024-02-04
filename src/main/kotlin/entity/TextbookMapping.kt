package entity

import com.fasterxml.jackson.annotation.JsonProperty

/**
@author Yu-Jing
@create 2024-01-05-10:45 AM
 */
data class TextbookMapping(
    @JsonProperty("_id")
    var id: String? = null,
    var subject: String? = null,
    var year: String? = null,
    var volume: String? = null,
    var type: String? = null,
    var target: TargetData? = null,
    var enabled: Boolean? = null
){
    data class TargetData(
        var year: String? = null,
        var version: String? = null,
        var volume: String? = null,
        var subject: String? = null
    )
}
