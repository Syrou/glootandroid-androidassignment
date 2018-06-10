package com.gloot.androidassignment.core.controllers

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.gloot.androidassignment.core.R
import com.gloot.androidassignment.core.SELECTED_PLAYER
import com.gloot.androidassignment.core.actions.PlayerActions
import com.gloot.androidassignment.core.extensions.bind
import com.gloot.androidassignment.core.states.PlayerListState
import com.gloot.androidassignment.core.store
import com.gloot.androidassignment.network.models.Player
import org.rekotlin.StoreSubscriber

class PlayerDetailActivity : AppCompatActivity(), StoreSubscriber<PlayerListState?> {
    private val playerId by bind<TextView>(R.id.player_id)
    private val playerName by bind<TextView>(R.id.player_name)
    private val editButton by bind<FloatingActionButton>(R.id.fab_edit)
    private val removeButton by bind<FloatingActionButton>(R.id.fab_delete)

    override fun newState(state: PlayerListState?) {
        this@PlayerDetailActivity.runOnUiThread(java.lang.Runnable {
            state?.apply {
                activePlayer?.let {
                    val formattedId:String = getString(R.string.player_id_format, it.id)
                    val formattedName:String = getString(R.string.player_name_format, it.name)
                    playerId.text = formattedId
                    playerName.text = formattedName
                }

                removeButton.setOnClickListener {
                    activePlayer?.let { store.dispatch(PlayerActions.RemovePlayer(it, this@PlayerDetailActivity))  }
                }

                editButton.setOnClickListener {
                    val view:View = it;
                    activePlayer?.let { createEditDialogAndShow(view, it)}
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_detail)
        /*This process will only make a call towards the API to see if any changes has been made
        to the player and in that case display the updated data. Will use reference data as start*/
        val intentPlayer:Player = intent.getSerializableExtra(SELECTED_PLAYER) as Player
        store.dispatch(PlayerActions.GetSelectedPlayer(intentPlayer))
    }

    override fun onStart() {
        super.onStart()
        store.subscribe(this) {
            it.select {
                it.playerListState
            }.skipRepeats()
        }
    }

    override fun onStop() {
        super.onStop()
        store.unsubscribe(this)
    }

    private fun createEditDialogAndShow(view: View, player: Player){
        val builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = this.layoutInflater
        val dialogRootView: View = inflater.inflate(R.layout.dialog_add_player, null)
        val playerNameInput = dialogRootView.findViewById<EditText>(R.id.player_name_input)
        player.let { playerNameInput.setText(it.name, TextView.BufferType.EDITABLE) }

        builder.setView(dialogRootView)
                // Add action buttons
                .setPositiveButton(R.string.update, DialogInterface.OnClickListener { _, _ ->
                    val updatedPlayer = Player(id = player.id, name = playerNameInput.text.toString())
                    store.dispatch(PlayerActions.UpdatePlayer(player = updatedPlayer, context = this))
                })
                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        builder.create().show()
    }

}
