package com.gloot.androidassignment.core.reducers

import com.gloot.androidassignment.core.actions.PlayerActions
import com.gloot.androidassignment.core.states.PlayerListState
import org.rekotlin.Action

fun playerListReducer(action: Action, movieListState: PlayerListState?): PlayerListState {
    var state = movieListState ?: PlayerListState()
    when (action) {
        is PlayerActions.LoadPlayers -> {
            state = state.copy(loading = true, failed = false)
        }
        is PlayerActions.InitializePlayerList -> {
            state = state.copy(players = action.players, failed = false, loading = false, errorReason = "")
        }
        is PlayerActions.ErrorMessage -> {
            state = state.copy(failed = true, loading = false, errorReason = action.errorReason)
        }
        is PlayerActions.ActiveSelectedPlayer -> {
            state = state.copy(activePlayer = action.player)
        }
    }
    return state
}