package com.gloot.androidassignment.core.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.gloot.androidassignment.core.R
import com.gloot.androidassignment.core.SELECTED_PLAYER
import com.gloot.androidassignment.core.actions.PlayerActions
import com.gloot.androidassignment.core.controllers.PlayerDetailActivity
import com.gloot.androidassignment.core.store
import com.gloot.androidassignment.network.models.Player

class PlayerAdapter(private val players: List<Player>) :
        RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    class ViewHolder(val playerItem: CardView) : RecyclerView.ViewHolder(playerItem)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PlayerAdapter.ViewHolder {
        val playerItemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.player_item, parent, false) as CardView

        return ViewHolder(playerItemView)
    }
    @SuppressLint("StringFormatInvalid")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.playerItem.context
        holder.playerItem.setOnClickListener {
            store.dispatch(PlayerActions.ActiveSelectedPlayer(players[position]))
            val intent = Intent(context, PlayerDetailActivity::class.java).apply {
                putExtra(SELECTED_PLAYER, players[position])
            }
            context.startActivity(intent)
        }
        val playerName = holder.playerItem.findViewById<TextView>(R.id.player_name)
        var localizedPlayerNameText = context.resources.getString(R.string.player_name, players[position].name)
        playerName.text = localizedPlayerNameText
    }
    override fun getItemCount() = players.size

}