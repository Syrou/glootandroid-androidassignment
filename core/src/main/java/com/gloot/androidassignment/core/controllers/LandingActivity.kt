package com.gloot.androidassignment.core.controllers

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import com.gloot.androidassignment.core.R
import com.gloot.androidassignment.core.adapters.PlayerAdapter
import com.gloot.androidassignment.network.models.Player

import kotlinx.android.synthetic.main.activity_landing.*
import kotlinx.android.synthetic.main.content_landing.*
import android.content.DialogInterface
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.gloot.androidassignment.core.actions.PlayerActions
import com.gloot.androidassignment.core.extensions.bind
import com.gloot.androidassignment.core.states.PlayerListState
import com.gloot.androidassignment.core.store
import org.rekotlin.StoreSubscriber
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.TextView


class LandingActivity : AppCompatActivity(), StoreSubscriber<PlayerListState?> {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val errorView by bind<RelativeLayout>(R.id.loading_error_view)
    private val loadingView by bind<RelativeLayout>(R.id.loading_view)
    private val whale by bind<LottieAnimationView>(R.id.loading_error_animation)
    private val noPlayersView by bind<TextView>(R.id.no_players_view)
    private val addPlayerButton by bind<FloatingActionButton>(R.id.add_player_button)
    override fun newState(state: PlayerListState?) {
        //Ensure mainThread
        this@LandingActivity.runOnUiThread(java.lang.Runnable {
            state?.apply {
                players?.takeIf { it.isEmpty() }?.apply { noPlayersView.visibility = View.VISIBLE }?:run{ noPlayersView.visibility = View.GONE}
                loading.takeIf{ it }?.apply{ loadingView.visibility = View.VISIBLE }?:run{ loadingView.visibility = View.GONE }
                failed.takeIf{ it }?.apply{

                        errorView.visibility = View.VISIBLE
                        errorReason.let {
                            if(it.isNotEmpty()){
                                Toast.makeText(this@LandingActivity, errorReason, Toast.LENGTH_SHORT).show()
                            }
                        }

                }?:run {
                    errorView.visibility = View.GONE
                    players?.let {
                        recyclerView.adapter = PlayerAdapter(it)
                        animateFabToPresentableState()
                    }

                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        setSupportActionBar(toolbar)

        viewManager = LinearLayoutManager(this)
        recyclerView = player_recycler_view.apply {
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager
            store.dispatch(PlayerActions.LoadPlayers())
        }

        addPlayerButton.setOnClickListener { view ->
            createAddDialogAndShow(view)
        }

        whale.setOnClickListener { _ ->
            store.dispatch(PlayerActions.LoadPlayers())
        }
    }

    private fun animateFabToPresentableState(){
        val addPlayerButtonAnimation = ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        addPlayerButtonAnimation.duration = 250
        addPlayerButtonAnimation.interpolator = AccelerateInterpolator()
        addPlayerButtonAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                addPlayerButton.visibility = View.VISIBLE
            }
            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {

                val expand = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                expand.duration = 195
                expand.interpolator = DecelerateInterpolator()
                addPlayerButton.startAnimation(expand)
            }
        })
        addPlayerButton.startAnimation(addPlayerButtonAnimation)
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

    private fun createAddDialogAndShow(view:View){
        val builder = AlertDialog.Builder(this)
        val inflater:LayoutInflater = this.layoutInflater
        val dialogRootView: View = inflater.inflate(R.layout.dialog_add_player, null)
        val playerNameInput = dialogRootView.findViewById<EditText>(R.id.player_name_input)

        builder.setView(dialogRootView)
                // Add action buttons
                .setPositiveButton(R.string.add, DialogInterface.OnClickListener { _, _ ->
                    addPlayer(playerNameInput.text.toString(), view)
                })
                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        builder.create().show()
    }

    private fun addPlayer(name:String, anchor:View){
        val player = Player(name = name)
        store.dispatch(PlayerActions.AddPlayer(player, this))
    }
}
