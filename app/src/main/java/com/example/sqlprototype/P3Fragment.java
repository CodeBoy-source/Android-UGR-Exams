package com.example.sqlprototype;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class P3Fragment extends Fragment {

    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_dialog_flow, container, false);

        WebView webview = root.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        String html =
                "<iframe\n" +
                        "    allow=\"microphone;\"\n" +
                        "    width=\"100%\"\n" +
                        "    height=\"100%\"\n" +
                        "    src=\"https://console.dialogflow.com/api-client/demo/embedded/250ff287-26e6-4975-8bf9-d260c64754d1\">\n" +
                        "</iframe>";
        webview.loadData(html, "text/html", null);

        return root;
    }
}