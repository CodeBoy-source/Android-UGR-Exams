package com.example.sqlprototype.p4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


import com.example.sqlprototype.R;

public class GpsEtsiitHomeFragment extends Fragment {

    View root;
    Button btnBiblioteca, btnCafeteria, btnAula35, btnSecretaria, btnDespachoMarcelino, btnQr;
    int destinyNode = 0;
    GpsEtsiitFragment gpsEtsiitFragment = new GpsEtsiitFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_gps_etsiit_home, container, false);

        btnBiblioteca = root.findViewById(R.id.btnBiblioteca);
        btnCafeteria = root.findViewById(R.id.btnCafeteria);
        btnAula35 = root.findViewById(R.id.btnAula35);
        btnSecretaria = root.findViewById(R.id.btnSecretaria);
        btnDespachoMarcelino = root.findViewById(R.id.btnDespachoMarcelino);
        btnQr = root.findViewById(R.id.btnQr);

        for (Button btn : new Button[]{btnBiblioteca, btnCafeteria, btnAula35, btnSecretaria, btnDespachoMarcelino}) {
            btn.setOnClickListener(v -> {
                DataBaseAccess db = DataBaseAccess.getInstance(root.getContext());
                String destiny_name = btn.getText().toString().toLowerCase();
                destinyNode = db.getNodeByName(destiny_name);
                if (destinyNode == 0)
                    throw new RuntimeException("failed to get destinyNode for " + destiny_name);

                gpsEtsiitFragment.setDestinyNode(destinyNode);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, gpsEtsiitFragment).commit();
            });
        }

        return root;
    }
}