package com.gloot.androidassignment.core.states

import org.rekotlin.StateType

data class CoreState(
        var playerListState: PlayerListState? = null
) : StateType