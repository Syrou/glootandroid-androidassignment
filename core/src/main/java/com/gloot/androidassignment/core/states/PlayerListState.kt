package com.gloot.androidassignment.core.states

import com.gloot.androidassignment.network.models.Player
import org.rekotlin.StateType

data class PlayerListState(
        var players: List<Player>? = null,
        var loading:Boolean = true,
        var failed:Boolean = false,
        var errorReason:String = "",
        var activePlayer:Player? = null
) : StateType