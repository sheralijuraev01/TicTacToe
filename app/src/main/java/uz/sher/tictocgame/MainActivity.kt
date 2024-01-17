package uz.sher.tictocgame


import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import uz.sher.tictocgame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var appUpdateManager: AppUpdateManager? = null

    private val REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
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

        checkForUpdates()
        showFeedbackDialog()
        appUpdateManager!!.registerListener(
            installStateUpdatedListener
        )
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

   private var installStateUpdatedListener =
        InstallStateUpdatedListener { installState: InstallState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                Toast.makeText(
                    this,
                    "Downloaded, Restart the app in 5 seconds",
                    Toast.LENGTH_SHORT
                ).show()
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ appUpdateManager!!.completeUpdate() }, 5000)
//            showCompletedUpdate();
            }
        }

    private fun showFeedbackDialog() {
        val reviewManager = ReviewManagerFactory.create(this)
        reviewManager.requestReviewFlow().addOnCompleteListener { it: Task<ReviewInfo?> ->
            if (it.isSuccessful) {
                reviewManager.launchReviewFlow(this, it.result!!)
            }
        }
    }

    private fun checkForUpdates() {
        appUpdateManager!!.appUpdateInfo.addOnSuccessListener { info: AppUpdateInfo ->
            val isUpdateAvailable =
                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            if (isUpdateAvailable && isUpdateAllowed) {
                try {
                    appUpdateManager!!.startUpdateFlowForResult(
                        info,
                        AppUpdateType.FLEXIBLE,
                        this,
                        REQUEST_CODE
                    )
                } catch (e: SendIntentException) {
                    throw RuntimeException(e)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Nimadir xato ketdi", Toast.LENGTH_SHORT).show()
                Log.e("Error", "Nimadir xato ketdi")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager!!.unregisterListener(installStateUpdatedListener)
    }

}


