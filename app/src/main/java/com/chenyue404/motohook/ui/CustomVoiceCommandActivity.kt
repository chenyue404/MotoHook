package com.chenyue404.motohook.ui

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import com.chenyue404.motohook.BuildConfig
import com.chenyue404.motohook.R
import org.json.JSONArray
import org.json.JSONException

/**
 * Created by chenyue on 2022/10/16 0016.
 */
class CustomVoiceCommandActivity : Activity() {

    companion object {
        const val KEY_VOICE_COMMANDS = "KEY_VOICE_COMMANDS"
    }

    private val rvList: RecyclerView by lazy { findViewById(R.id.rvList) }
    private val btAdd: Button by lazy { findViewById(R.id.btAdd) }
    private val btSave: Button by lazy { findViewById(R.id.btSave) }

    private val pref: SharedPreferences? by lazy {
        getSharedPreferences(
            "${BuildConfig.APPLICATION_ID}_preferences", Context.MODE_WORLD_READABLE
        )
    }
    private val dataList: MutableList<String> = mutableListOf()
    private val adapter: ListAdapter by lazy { ListAdapter() }
    private val editDialog by lazy { EditDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_voice_command)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        rvList.adapter = adapter
        readRules()
        adapter.dataList.run {
            clear()
            addAll(dataList)
        }

        btAdd.setOnClickListener {
            editDialog.show()
            editDialog.positiveClickListener = {
                adapter.dataList.add(it)
                adapter.notifyItemInserted(adapter.itemCount - 1)
            }
        }
        btSave.setOnClickListener {
            hideKeyboard()
            saveRules()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun readRules() {
        pref?.getString(
            KEY_VOICE_COMMANDS, null
        )?.let {
            try {
                JSONArray(it)
            } catch (e: JSONException) {
                null
            }
        }?.let {
            for (i in 0 until it.length()) {
                val rule = it.getString(i)
                dataList.add(rule)
            }
        }
    }

    private fun saveRules() {
        dataList.run {
            clear()
            addAll(adapter.dataList)
        }
        val jsonArray = JSONArray()
        dataList.forEach {
            jsonArray.put(it)
        }

        pref?.edit {
            putString(KEY_VOICE_COMMANDS, jsonArray.toString())
        }
    }

    private class ListAdapter : RecyclerView.Adapter<ListAdapter.VH>() {
        var dataList: MutableList<String> = mutableListOf()

        private class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tv0: TextView = view.findViewById(R.id.tv0)
            val ibDelete: ImageButton = view.findViewById(R.id.ibDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_custom_command, parent, false)
        )

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = dataList[position]
            holder.tv0.text = item
            holder.tv0.setOnClickListener {
                EditDialog(it.context).run {
                    positiveClickListener = { newStr ->
                        dataList[position] = newStr
                        notifyItemChanged(position)
                    }
                    show(item)
                }
            }
            holder.ibDelete.setOnClickListener {
                dataList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount - position)
            }
        }

        override fun getItemCount() = dataList.size

        private fun log(str: String) {
            Log.d("CustomVoiceCommandActivity", str)
        }
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            window.decorView.windowToken,
            0
        )
    }

    private fun log(str: String) {
        Log.d("CustomVoiceCommandActivity", str)
    }

    private class EditDialog(private val mContext: Context) {

        var positiveClickListener: ((text: String) -> Unit)? = null
        var negativeClickListener: (() -> Unit)? = null

        private val editText by lazy {
            EditText(mContext).apply {
                hint = "支持正则"
            }
        }
        private val dialog: AlertDialog by lazy {
            AlertDialog.Builder(mContext, R.style.dialogTheme)
                .setTitle("自定义指令")
                .setView(editText)
                .setPositiveButton(mContext.getString(android.R.string.ok)) { _, _ ->
                    val str = editText.text.toString()
                    positiveClickListener?.invoke(str)
                }
                .setNegativeButton(mContext.getString(android.R.string.cancel)) { _, _ ->
                    negativeClickListener?.invoke()
                }
                .setOnCancelListener {
                    positiveClickListener = null
                    negativeClickListener = null
                }
                .create()
                .apply {
                    setCanceledOnTouchOutside(false)
                }
        }

        fun show(str: String = "") {
            dialog.show()
            editText.setText(str)
        }

        fun cancel() {
            dialog.cancel()
        }
    }
}