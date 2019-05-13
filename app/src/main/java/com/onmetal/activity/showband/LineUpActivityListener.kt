package com.onmetal.activity.showband

import com.onmetal.web.model.FullPerson

interface LineUpActivityListener {
    fun finish(lineup: List<FullPerson>?)
}