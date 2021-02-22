/*
 * Copyright (c) 2020 Hemanth Savarla.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package code.name.monkey.retromusic.activities

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import code.name.monkey.appthemehelper.ThemeStore
import code.name.monkey.appthemehelper.util.MaterialUtil
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.activities.base.AbsBaseActivity
import kotlinx.android.synthetic.main.activity_pro_version.*

class PurchaseActivity : AbsBaseActivity(){

    private var restorePurchaseAsyncTask: AsyncTask<*, *, *>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setDrawUnderStatusBar()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_version)
        setStatusbarColor(Color.TRANSPARENT)
        setLightStatusbar(false)
        setNavigationbarColor(Color.BLACK)
        setLightNavigationBar(false)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        restoreButton.isEnabled = false
        purchaseButton.isEnabled = false

        MaterialUtil.setTint(purchaseButton, true)

        restoreButton.setOnClickListener {
            if (restorePurchaseAsyncTask == null || restorePurchaseAsyncTask!!.status != AsyncTask.Status.RUNNING) {
                restorePurchase()
            }
        }
        purchaseButton.setOnClickListener {

        }
        bannerContainer.backgroundTintList =
            ColorStateList.valueOf(ThemeStore.accentColor(this))
    }

    private fun restorePurchase() {
        if (restorePurchaseAsyncTask != null) {
            restorePurchaseAsyncTask!!.cancel(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG: String = "PurchaseActivity"
    }
}
