package com.gloot.androidassignment.core.middlewares

import android.app.Activity
import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.widget.Toast
import com.gloot.androidassignment.core.R
import com.gloot.androidassignment.core.actions.PlayerActions
import com.gloot.androidassignment.core.states.CoreState
import com.gloot.androidassignment.network.PlayerApi
import com.gloot.androidassignment.network.models.Player
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.rekotlin.DispatchFunction
import org.rekotlin.Middleware
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

internal val networkMiddleWare: Middleware<CoreState> = { dispatch, state ->
    { next ->
        { action ->
            when (action) {
                is PlayerActions.LoadPlayers -> {
                    loadPlayersFromApi(dispatch)
                }
                is PlayerActions.AddPlayer -> {
                    addPlayer(action.player, action.context,dispatch)
                }
                is PlayerActions.RemovePlayer -> {
                    removePlayer(action.player, action.context, dispatch)
                }
                is PlayerActions.UpdatePlayer -> {
                    updatePlayer(action.player, action.context, dispatch)
                }
                is PlayerActions.GetSelectedPlayer -> {
                    getSpecificPlayer(action.selectedPlayer, dispatch)
                }
            }
            next(action)
        }
    }
}

private fun getSpecificPlayer(player: Player, dispatch: DispatchFunction){
    var selectedPlayer:Player? = null
    PlayerApi.instance
            .getPlayer(player.id)
            .flatMap { fetchedPlayer ->
                selectedPlayer = fetchedPlayer
                PlayerApi.instance.getPlayers() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ players:List<Player> ->
                dispatch(PlayerActions.ActiveSelectedPlayer(selectedPlayer))
                dispatch(PlayerActions.InitializePlayerList(players))
            }, { onError -> dispatch(PlayerActions.ErrorMessage(onError.localizedMessage))})
}

private fun updatePlayer(player:Player, context:Context, dispatch: DispatchFunction){
    var updatedPlayer:Player? = null
    PlayerApi.instance
            .putPlayer(player.id, player)
            .flatMap { newUpdatedPlayer ->
                updatedPlayer = newUpdatedPlayer
                PlayerApi.instance.getPlayers()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                    { players: List<Player> ->
                        Timer().schedule(timerTask {
                            Snackbar
                                    .make((context as Activity).findViewById<FloatingActionButton>(R.id.fab_edit), context.getString(R.string.player_has_been_updated, updatedPlayer?.name), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null)
                                    .show()
                        }, 300)
                        dispatch(PlayerActions.ActiveSelectedPlayer(updatedPlayer))
                        dispatch(PlayerActions.InitializePlayerList(players))
                    },
                    {onError ->
                        val localizedError = context.getString(R.string.failure_update_player)
                        Toast.makeText(context,localizedError, Toast.LENGTH_SHORT ).show() })
}

private fun addPlayer(player:Player, context:Context, dispatch: DispatchFunction){
    var addedPlayer:Player? = null
    PlayerApi.instance
            .addPlayer(player)
            .flatMap { player ->
                addedPlayer = player
                PlayerApi.instance.getPlayers()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                    { players: List<Player> ->
                        Timer().schedule(timerTask {
                            Snackbar
                                    .make((context as Activity).findViewById<FloatingActionButton>(R.id.add_player_button), context.getString(R.string.player_has_been_added, addedPlayer?.name), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null)
                                    .show()
                        }, 300)
                        dispatch(PlayerActions.InitializePlayerList(players))
                    },
                    {onError -> dispatch(PlayerActions.ErrorMessage(onError.localizedMessage)) })
}

private fun removePlayer(player:Player, context: Context, dispatch: DispatchFunction){
    var removedPlayer:Player? = null
    PlayerApi.instance
            .deletePlayer(player.id)
            .flatMap { player ->
                removedPlayer = player
                PlayerApi.instance.getPlayers() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                    { players:List<Player> ->
                        (context as Activity).finish()
                        dispatch(PlayerActions.InitializePlayerList(players))
                    },
                    {onError ->
                        val localizedError = context.getString(R.string.failure_remove_player)
                        Toast.makeText(context,localizedError, Toast.LENGTH_SHORT ).show() })
}

private fun loadPlayersFromApi(dispatch: DispatchFunction){
    PlayerApi.instance.getPlayers()
            .delay(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                    {players -> dispatch(PlayerActions.InitializePlayerList(players)) },
                    {onError -> dispatch(PlayerActions.ErrorMessage(onError.localizedMessage)) })
}