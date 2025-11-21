package com.ocp.sdk

import org.json.JSONObject

data class Pipeline(
    val pipelineId: String,
    val stages: Map<Stage, List<String>>,
    val meta: Map<String, Any>
) {
    companion object {
        fun fromJson(jsonString: String): Pipeline {
            val json = JSONObject(jsonString)
            val pipelineId = json.getString("pipelineId")
            
            val stagesJson = json.getJSONObject("stages")
            val stages = mutableMapOf<Stage, List<String>>()
            
            Stage.values().forEach { stage ->
                if (stagesJson.has(stage.name)) {
                    val pluginsArray = stagesJson.getJSONArray(stage.name)
                    val pluginList = mutableListOf<String>()
                    for (i in 0 until pluginsArray.length()) {
                        pluginList.add(pluginsArray.getString(i))
                    }
                    stages[stage] = pluginList
                }
            }

            val metaJson = json.optJSONObject("meta")
            val meta = mutableMapOf<String, Any>()
            metaJson?.keys()?.forEach { key ->
                meta[key] = metaJson.get(key)
            }

            return Pipeline(pipelineId, stages, meta)
        }
    }
}

enum class Stage {
    PREVIEW,
    RECORD,
    POST
}
