package com.gloot.androidassignment.core.actions

import android.content.Context
import com.gloot.androidassignment.network.models.Player
import org.rekotlin.Action

interface PlayerActions : Action{
    class LoadPlayers() : PlayerActions
    class InitializePlayerList(val players: List<Player>) : PlayerActions
    class AddPlayer(val player:Player, val context:Context) : PlayerActions
    class RemovePlayer(val player:Player, val context: Context) : PlayerActions
    class UpdatePlayer(val player:Player, val context:Context) : PlayerActions
    class ErrorMessage(val errorReason:String) : PlayerActions
    class ActiveSelectedPlayer(val player: Player?) : PlayerActions
    class GetSelectedPlayer(val selectedPlayer: Player) : PlayerActions
}