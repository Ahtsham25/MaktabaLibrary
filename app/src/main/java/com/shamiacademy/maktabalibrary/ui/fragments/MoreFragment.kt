package com.shamiacademy.maktabalibrary.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.ui.WebPageActivity

class MoreFragment : Fragment(R.layout.fragment_more) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.item_about).setOnClickListener {
            WebPageActivity.start(
                requireContext(),
                getString(R.string.about_app),
                "شامی اکیڈمی\n\nیہ ایپلیکیشن علماء کرام کی کتابوں کو ایک ہی جگہ، آسان اور خوبصورت انداز میں پیش کرنے کے لیے تیار کی گئی ہے۔ تمام کتابیں Internet Archive سے حاصل کی گئی ہیں اور تعلیمی و دینی مقاصد کے لیے پیش کی جا رہی ہیں۔"
            )
        }
        view.findViewById<View>(R.id.item_privacy).setOnClickListener {
            WebPageActivity.start(
                requireContext(),
                getString(R.string.privacy_policy),
                "پرائیویسی پالیسی\n\nیہ ایپ آپ کی کوئی ذاتی معلومات جمع نہیں کرتی۔ کتابیں براہ راست Internet Archive کے سرورز سے پڑھی یا ڈاؤن لوڈ کی جاتی ہیں۔ ڈاؤن لوڈ شدہ کتابیں صرف آپ کے اپنے آلے پر محفوظ ہوتی ہیں۔"
            )
        }
        view.findViewById<View>(R.id.item_disclaimer).setOnClickListener {
            WebPageActivity.start(
                requireContext(),
                getString(R.string.disclaimer),
                "ڈسکلیمر\n\nاس ایپ میں موجود تمام کتابیں Internet Archive پر پہلے سے موجود عوامی مواد سے لی گئی ہیں۔ ایپ کا مقصد صرف رسائی کو آسان بنانا ہے۔ کسی بھی مسئلے کی صورت میں براہ کرم رابطہ کریں۔"
            )
        }
        view.findViewById<View>(R.id.item_contact).setOnClickListener {
            WebPageActivity.start(
                requireContext(),
                getString(R.string.contact_us),
                "رابطہ کریں\n\nکسی بھی سوال، تجویز یا مسئلے کے لیے آپ ہم سے رابطہ کر سکتے ہیں۔"
            )
        }
        view.findViewById<View>(R.id.item_crash_logs).setOnClickListener {
            com.shamiacademy.maktabalibrary.ui.CrashLogsActivity.start(requireContext())
        }
    }
}
