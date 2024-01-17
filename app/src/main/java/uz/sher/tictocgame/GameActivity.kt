package uz.sher.tictocgame

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog


import uz.sher.tictocgame.databinding.ActivityGameBinding

import kotlin.random.Random


class GameActivity : AppCompatActivity() {
    /* CPU x, person o
    *
    * */
    lateinit var binding: ActivityGameBinding
    private var activePlayer = 0
    private val random = Random
    private val states = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1)
    private var isGameActive = true
    private var isCPU: Boolean = false
    private var isWin = false
    private var winPosition = arrayOf(-1, -1, -1)


    private lateinit var firtsplayerName: String
    private lateinit var secondPlayerName: String
    private var firstPlayerScore: Int = 0
    private var secondPlayerScore: Int = 0

    /*1-yutdi,2-durang,3-yutqazdi*/

    private var winningPositions = arrayOf(
        arrayOf(0, 1, 2),
        arrayOf(3, 4, 5),
        arrayOf(6, 7, 8),

        arrayOf(0, 3, 6),
        arrayOf(1, 4, 7),
        arrayOf(2, 5, 8),

        arrayOf(0, 4, 8),
        arrayOf(2, 4, 6),


        )

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isCPU = intent.getBooleanExtra("isCPU", false)
        firtsplayerName = intent.getStringExtra("name1").toString()
        secondPlayerName = intent.getStringExtra("name2").toString()
        binding.firstPlayer.text = this.firtsplayerName

        isWin = false
        moveChangeBackground()

        if (isCPU) {
            binding.secondPlayer.text = "CPU"
            binding.sideImage.setImageResource(R.drawable.robot)
            firstPlayerScore = 0
            secondPlayerScore = 0
            binding.scoreTable.text = "$firstPlayerScore:$secondPlayerScore"
        } else {
            binding.secondPlayer.text = this.secondPlayerName
            binding.sideImage.setImageResource(R.drawable.man_side)
            firstPlayerScore = 0
            secondPlayerScore = 0
            binding.scoreTable.text = "$firstPlayerScore:$secondPlayerScore"
        }


        binding.buttonBack.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    @SuppressLint("ResourceAsColor")
    fun onclick(view: View) {
        val clicked = view as ImageView
        val tapped = clicked.tag.toString().toInt()
        if (isGameActive) {
            if (states[tapped] == -1) {

                if (activePlayer == 0) {
                    clicked.setImageResource(R.drawable.o)
                    states[tapped] = 0
                    activePlayer = 1

                    if (isCPU) {
                        val timer = object : CountDownTimer(500, 1000) {
                            override fun onTick(millisUntilFinished: Long) {

                            }

                            override fun onFinish() {
                                withCPU()
                            }
                        }
                        timer.start()
                    }

                } else if (!isCPU) {
                    clicked.setImageResource(R.drawable.x)
                    states[tapped] = 1
                    activePlayer = 0

                }
            }

            determineCheck()

        }
        if (!isWin) {
            moveChangeBackground()
        }


    }

    private fun gameOver() {
        isGameActive = true
        isWin = false
        moveChangeBackground()
//       activePlayer=0
        for (i in states.indices) {
            states[i] = -1
        }
        for (image in 0 until binding.gridContainer.childCount) {
            (binding.gridContainer.getChildAt(image) as ImageView).setImageResource(0)
            (binding.gridContainer.getChildAt(image) as ImageView).setBackgroundResource(R.drawable.background_tictactoe)

        }
    }

    fun withCPU() {
        if (isGameActive) {
            if (activePlayer == 1) {
                activePlayer = 0

                var isFound = false
                while (!isFound) {
                    val ind = random.nextInt(9)
                    //5,2,6
                    //      val states = arrayOf(-1, -1, 0, -1, -1, 1, 1, -1, 1)
                    if (states[ind] == -1) {
                        isFound = true
                        states[ind] = 1
                        val imageCpu = binding.gridContainer.getChildAt(ind) as ImageView
                        imageCpu.setImageResource(R.drawable.x)

                        determineCheck()
                    }

                }
            }

        }
        if (!isWin) {
            moveChangeBackground()
        }
    }

    @SuppressLint("ResourceAsColor")
    fun checkWin() {
        for (winningPosition in winningPositions) {
            if (states[winningPosition[0]] != -1 &&
                states[winningPosition[0]] == states[winningPosition[1]] &&
                states[winningPosition[1]] == states[winningPosition[2]]
            ) {
                isWin = true
                for (i in 0 until 3) {
                    winPosition[i] = winningPosition[i]
                }
                if (states[winningPosition[0]] == 0)
                    isWinAnimation(0)
                else isWinAnimation(1)
                isGameActive = false
                activePlayer = 0
                val timer = object : CountDownTimer(1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}

                    override fun onFinish() {
                        if (states[winningPosition[0]] == 0) {
                            winAlertdialog(firtsplayerName)
                            firstPlayerScore++
                        } else {
                            if (isCPU) {
                                loseAlertDialog(firtsplayerName)
                                secondPlayerScore++
                            } else {
                                winAlertdialog(secondPlayerName)
                                secondPlayerScore++
                            }
                        }
                        binding.scoreTable.text = "$firstPlayerScore:$secondPlayerScore"
                    }
                }
                timer.start()
            }
        }
//        binding.scoreTable.text="$firtsplayerScore:$secondPlayerScore"

    }

    fun winAlertdialog(winnerName: String) {
        val viewWin = layoutInflater.inflate(R.layout.win_finish_game_condition, null)
        val builder = AlertDialog.Builder(this)
        val button = viewWin.findViewById<ImageView>(R.id.win_restart)
        val winner = viewWin.findViewById<TextView>(R.id.winner_name)
        winner.text = winnerName
        builder.setView(viewWin)
        val dialog: AlertDialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.show()
        button.setOnClickListener {
            gameOver()
            dialog.dismiss()
        }
    }

    fun loseAlertDialog(loserName: String) {
        val viewLose = layoutInflater.inflate(R.layout.lose_finish_game_condition, null)
        val builder = AlertDialog.Builder(this)
        val button = viewLose.findViewById<ImageView>(R.id.lose_restart)
        val loser = viewLose.findViewById<TextView>(R.id.loser_name)
        loser.text = loserName
        builder.setView(viewLose)
        val dialog: AlertDialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.show()
        button.setOnClickListener {
            gameOver()
            dialog.dismiss()
        }
    }

    private fun drawAlertDialog() {
        val viewDraw = layoutInflater.inflate(R.layout.draw_finish_game_condition, null)
        val builder = AlertDialog.Builder(this)
        val button = viewDraw.findViewById<ImageView>(R.id.draw_restart)
        val name = viewDraw.findViewById<TextView>(R.id.winner_name)
        name.text = ""
        builder.setView(viewDraw)
        val dialog: AlertDialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        button.setOnClickListener {
            gameOver()
            dialog.dismiss()
        }

    }

    private fun isFillStatus(): Boolean {
        var isFound: Boolean = true
        for (i in states.indices) {
            if (states[i] == -1) {
                isFound = false
                break
            }

        }
        return isFound
    }

    private fun determineCheck() {
        checkWin()
        if (!isWin) {
            isDraw()
        }
    }

    private fun isDraw() {
        if (isFillStatus()) {
            activePlayer = 0
            isGameActive = false
            drawAlertDialog()
        }
    }

    private fun isWinAnimation(index: Int) {

        if (isWin) {
            for (i in 0 until 3) {
                val imageWin = binding.gridContainer.getChildAt(winPosition[i]) as ImageView
                imageWin.setBackgroundResource(R.drawable.winbackground)
                if (index == 0)
                    imageWin.setImageResource(R.drawable.o_blue)
                else if (index == 1) imageWin.setImageResource(R.drawable.x_blue)
            }
        }
    }


    private fun moveChangeBackground() {
        if (activePlayer == 0) {
            binding.firtPlayerContainer.setBackgroundResource(R.drawable.backgroundgameinfo_move)
            binding.secondPlayerContainer.setBackgroundResource(R.drawable.backgroundgameinfo)
            binding.lotti1.visibility=View.VISIBLE
            binding.lotti2.visibility=View.INVISIBLE
        } else if (activePlayer == 1) {
            binding.firtPlayerContainer.setBackgroundResource(R.drawable.backgroundgameinfo)
            binding.secondPlayerContainer.setBackgroundResource(R.drawable.backgroundgameinfo_move)
            binding.lotti1.visibility=View.INVISIBLE
            binding.lotti2.visibility=View.VISIBLE

        }
    }
}


