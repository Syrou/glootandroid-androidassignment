package com.gloot.androidassignment.core.reducers

import com.gloot.androidassignment.core.states.CoreState
import org.rekotlin.Action

fun coreReducer(action: Action, appState: CoreState?): CoreState =
        CoreState(
                playerListState = playerListReducer(action, appState?.playerListState)
        )