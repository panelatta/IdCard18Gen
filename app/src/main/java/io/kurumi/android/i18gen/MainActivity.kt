package io.kurumi.android.i18gen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import cn.hutool.core.util.IdcardUtil

class MainActivity : Activity() {

    var last: String
        get() {
            return getSharedPreferences("i18gen", Context.MODE_PRIVATE).getString("last", "")!!
        }
        @SuppressLint("ApplySharedPref")
        set(value) {
            getSharedPreferences("i18gen", Context.MODE_PRIVATE).edit().apply {
                putString("last", value)
                commit()
            }
        }

    val id17 by lazy {
        findViewById<EditText>(R.id.id17)
    }

    val gen by lazy {
        findViewById<Button>(R.id.gen)
    }

    val group by lazy {
        findViewById<Button>(R.id.group)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        @Suppress("DEPRECATION")
        titleColor = 0xffffff
        id17.setText(last)
        id17.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                last = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        })
        group.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=538982726&card_type=group&source=qrcode"))
                startActivity(intent)
            } catch (ex: Exception) {
                Toast.makeText(this, "没有安装QQ/TIM", Toast.LENGTH_SHORT).show()
            }
        }
        gen.setOnClickListener {
            val idtext = id17.text?.toString()
            if (idtext?.length != 17) {
                id17.error = "请输入前17位身份证"
                return@setOnClickListener
            }
            val id18 = IdcardUtil::class.java
                    .getDeclaredMethod("getCheckCode18", String::class.java)
                    .apply {
                        isAccessible = true
                    }
                    .invoke(null, idtext)
                    .toString()
            val id = "$idtext$id18"
            @Suppress("DEPRECATION")
            AlertDialog.Builder(this)
                    .setTitle("生成完成")
                    .setMessage("""
                        |性别 : ${if (IdcardUtil.getGenderByIdCard(id) == 0) "女" else "男"}
                        |生日 : ${IdcardUtil.getBirth(id)}
                        |地址 : ${IdcardUtil.getProvinceByIdCard(id)}
                        |第十八位为 : $id18
                        |身份证号为 : $id
                    """.trimMargin())
                    .setNegativeButton("完成") { _, _ ->
                    }
                    .setPositiveButton("复制身份证号") { _, _ ->
                        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                                .primaryClip = ClipData.newPlainText(null, id)
                        Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show()
                    }
                    .show()
        }


    }

}
