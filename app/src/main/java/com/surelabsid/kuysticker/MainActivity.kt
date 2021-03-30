package com.surelabsid.kuysticker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.gabrielbb.cutout.CutOut
import com.vlk.multimager.activities.GalleryActivity
import com.vlk.multimager.utils.Constants
import com.vlk.multimager.utils.Image
import com.vlk.multimager.utils.Params
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createSticker.setOnClickListener {
            imagePicker()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            //something wrong
            return
        }

        when (requestCode) {
            Constants.TYPE_MULTI_PICKER -> handleIntentPicker(data)
            CutOut.CUTOUT_ACTIVITY_REQUEST_CODE.toInt() -> {
                val uri = CutOut.getUri(data)

                //do something with this uri
                val imageList = arrayListOf<Image>()
                val image = Image(Random.nextLong(), uri, uri.path, false)
                imageList.add(image)

                //send to new pack activity
                val newStickerPackActivity = Intent(this, CreateNewStickerPackActivity::class.java)
                newStickerPackActivity.apply {
                    putExtra(CreateNewStickerPackActivity.INCOMING_IMAGE, imageList)
                    startActivity(this)
                }
            }
        }
    }

    // ini dipakai untuk menghandle intent/data dari file picker
    private fun handleIntentPicker(data: Intent?) {
        val imagesList: ArrayList<Image>? =
            data?.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST)

        if (imagesList != null) {
            if (imagesList.size > 1) {
                // ini pasti dia pilih lebih dari satu gambar

                val newStickerPackActivity = Intent(this, CreateNewStickerPackActivity::class.java)
                newStickerPackActivity.apply {
                    putExtra(CreateNewStickerPackActivity.INCOMING_IMAGE, imagesList)
                    startActivity(this)
                }
                Toast.makeText(this@MainActivity, "Multiple Selected", Toast.LENGTH_SHORT).show()
            } else {
                // satu gambar aja yang dipilih
                CutOut.activity().src(imagesList[0].uri).start(this)
                Toast.makeText(this@MainActivity, "Single Select", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun imagePicker() {
        val intent = Intent(this, GalleryActivity::class.java)
        val params = Params()
        params.pickerLimit = 10
        params.toolbarColor = resources.getColor(R.color.purple_700)
        params.actionButtonColor = resources.getColor(R.color.teal_200)
        params.buttonTextColor = resources.getColor(R.color.teal_200)
        intent.putExtra(Constants.KEY_PARAMS, params)
        startActivityForResult(intent, Constants.TYPE_MULTI_PICKER)
    }


}