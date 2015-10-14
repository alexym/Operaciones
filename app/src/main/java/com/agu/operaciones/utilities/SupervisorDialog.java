package com.agu.operaciones.utilities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.agu.operaciones.R;
import com.agu.operaciones.SplashActivity;

/**
 * Created by Cloudco on 14/10/15.
 */
public class SupervisorDialog extends DialogFragment {

    ImageButton cerrar;
    TextView supervisorTV,idTelSupTV,horaTV,cuadrillaTV,appVersionTV;
    String sup,idTel,horaIni,cuadrilla;

    //Constructors

    @SuppressLint("ValidFragment")
    public SupervisorDialog(String _nombre, String _idTelefono,String _horaInicio,String _cuadrilla){
        sup = _nombre;
        idTel = _idTelefono;
        horaIni = _horaInicio;
        cuadrilla = _cuadrilla;
    }
    public SupervisorDialog(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.supervisor_dialog);
        cerrar = (ImageButton)dialog.getWindow().findViewById(R.id.cerrarSupDialogButton);
        supervisorTV = (TextView)dialog.getWindow().findViewById(R.id.supervisorTextV);
        supervisorTV.setText(sup);
        idTelSupTV = (TextView)dialog.getWindow().findViewById(R.id.TelSupTextV);
        idTelSupTV.setText("ID Tel. "+idTel);
        horaTV = (TextView)dialog.getWindow().findViewById(R.id.horaInicioTextV);
        horaTV.setText(horaIni);
        cuadrillaTV = (TextView)dialog.getWindow().findViewById(R.id.cuadrillaTextV);
        cuadrillaTV.setText("Cuadrilla: "+cuadrilla);
        appVersionTV = (TextView)dialog.getWindow().findViewById(R.id.versionApp);
        appVersionTV.setText("Versión : " + SplashActivity.SOFTWARE_VERSION);
        cerrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;


    }


}