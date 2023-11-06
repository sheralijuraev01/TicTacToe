package uz.sher.tictocgame

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog


import uz.sher.tictocgame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val view = layoutInflater.inflate(R.layout.create_items, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val dialog: AlertDialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val secondPlayerInfo = view.findViewById<LinearLayout>(R.id.secondPlayer)
        val button_start = view.findViewById<Button>(R.id.startGame)
        val playerName1 = view.findViewById<EditText>(R.id.firtPlayerName)
        val playerName2 = view.findViewById<EditText>(R.id.secondPlayerName)
        val playerNameInfo = view.findViewById<TextView>(R.id.firtPlayerNameInfo)
        var nameOne: String
        var nameTwo: String

        binding.personButton.setOnClickListener {



            secondPlayerInfo.visibility = View.VISIBLE
            dialog.show()
            playerNameInfo.text = "Enter first player name"
            button_start.setOnClickListener {


                nameOne = playerName1.text.toString()
                nameTwo = playerName2.text.toString()
                if (nameOne.isNotEmpty() && nameTwo.isNotEmpty()) {
                    val intent = Intent(this, GameActivity::class.java)

                    intent.putExtra("isCPU", false)
                    intent.putExtra("name1", nameOne)
                    intent.putExtra("name2", nameTwo)
                    startActivity(intent)
                    dialog.dismiss()
                    finish()
                } else Toast.makeText(this, "Enter players name!", Toast.LENGTH_SHORT).show()

            }

        }

        binding.cpuButton.setOnClickListener {
            secondPlayerInfo.visibility = View.GONE
            dialog.show()
            playerNameInfo.text = "Enter player name"
            button_start.setOnClickListener {
                nameOne = playerName1.text.toString()
                if (nameOne.isNotEmpty()) {
                    val intent = Intent(this, GameActivity::class.java)
                    intent.putExtra("isCPU", true)
                    intent.putExtra("name1", nameOne)
                    startActivity(intent)
                    dialog.dismiss()
                    finish()
                } else Toast.makeText(this, "Enter player name!", Toast.LENGTH_SHORT).show()
            }
        }




        binding.appInfo.setOnClickListener {
            val viewInfo = layoutInflater.inflate(R.layout.app_info, null)
            val builderInfo = AlertDialog.Builder(this)
            builderInfo.setView(viewInfo)
            val dialogInfo: AlertDialog = builderInfo.create()
            dialogInfo.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialogInfo.show()
        }

        binding.exit.setOnClickListener {
            finish()
        }
    }



}


