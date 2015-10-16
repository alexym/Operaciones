package com.agu.operaciones.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.agu.operaciones.R;
import com.agu.operaciones.providers.TicketMetaData.TicketTable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpinnersSupervisionEditable.OnFragmentInteractionListenerSpinnerSE} interface
 * to handle interaction events.
 * Use the {@link SpinnersSupervisionEditable#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpinnersSupervisionEditable extends Fragment {
    public static final String TAG = "SpinnersSupvervisionEditable";

    private Bundle bundle;
    private Context context;

    private Spinner statusOperacion_spinner, unidadesMedida_spinner;
    private TextView materiales_edittext, cantidad_edittext;

    private String statusOperacion = "";
    private String materiales = "";
    private String cantidad = "";
    private String unidadesMedida = "";


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";


    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;

    private OnFragmentInteractionListenerSpinnerSE mListener;


    public static SpinnersSupervisionEditable newInstance(String param1, String param2, String param3, String param4) {
        SpinnersSupervisionEditable fragment = new SpinnersSupervisionEditable();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    public SpinnersSupervisionEditable() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
        }
        bundle = new Bundle();
//        bundle.putString(TicketTable.KEY_Procede,procede);
//        bundle.putString(TicketTable.KEY_Prioridad,prioridad);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_spinners_supervision_editable, container, false);
        //Evalua el estado de la vista
        initUI(V);

        return V;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Bundle bundle) {
        if (mListener != null) {
            mListener.onFragmentInteractionSSE(bundle);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        try {
            mListener = (OnFragmentInteractionListenerSpinnerSE) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListenerSpinnerSE {
        // TODO: Update argument type and name
        public void onFragmentInteractionSSE(Bundle bundle);
    }

    public void initUI(View v) {

        statusOperacion_spinner = (Spinner) v.findViewById(R.id.estadoOp_spinner);
        materiales_edittext = (TextView) v.findViewById(R.id.materiales_edittext);
        unidadesMedida_spinner = (Spinner) v.findViewById(R.id.unidad_medida_spinner);
        cantidad_edittext = (TextView) v.findViewById(R.id.cantidad_edittext);


        /********* Estado operacion ********/
        ArrayAdapter adapterProcede = ArrayAdapter.createFromResource(context, R.array.estadoOperacion, android.R.layout.simple_spinner_item);
        adapterProcede.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusOperacion_spinner.setAdapter(adapterProcede);

        int procedePosition = adapterProcede.getPosition(mParam1);
        statusOperacion_spinner.setSelection(procedePosition);

        statusOperacion_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(
                    AdapterView<?> parentView,
                    View selectedView, int position, long id) {
                statusOperacion = parentView.getItemAtPosition(position).toString();
                bundle.putString(TicketTable.KEY_EstatusOp, statusOperacion);
                mListener.onFragmentInteractionSSE(bundle);

            }

            @Override
            public void onNothingSelected(
                    AdapterView<?> arg0) {

            }

        });

        /********* Unidades Medida ********/
        ArrayAdapter adapterPrioridad = ArrayAdapter.createFromResource(context, R.array.unidadMedida, android.R.layout.simple_spinner_item);
        adapterPrioridad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unidadesMedida_spinner.setAdapter(adapterPrioridad);
        // ponemos el valor de la base de datos previamente seleccionado
        int prioridadPosition = adapterPrioridad.getPosition(mParam4);
        unidadesMedida_spinner.setSelection(prioridadPosition);
        //Listener del spinner prioridad
        unidadesMedida_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(
                    AdapterView<?> parentView, View selectedView, int position, long id) {

                unidadesMedida = parentView.getItemAtPosition(position).toString();
                bundle.putString(TicketTable.KEY_UDeMedida, unidadesMedida);
                mListener.onFragmentInteractionSSE(bundle);
            }

            @Override
            public void onNothingSelected(
                    AdapterView<?> arg0) {

            }

        });
        /********* Materiales ********/
        if(mParam2 == null ||mParam2.equals("null")){
            materiales_edittext.setText("");
        }else{
            materiales_edittext.setText(mParam2);
            bundle.putString(TicketTable.KEY_Materiales, mParam2);
            mListener.onFragmentInteractionSSE(bundle);
        }

        materiales_edittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                bundle.putString(TicketTable.KEY_Materiales, s.toString().trim());
                mListener.onFragmentInteractionSSE(bundle);

            }
        });
        /********* Cantidad ********/

        if(mParam3 == null || mParam3.equals("null")){
            cantidad_edittext.setText("");
        }else{
            cantidad_edittext.setText(mParam3);
            bundle.putString(TicketTable.KEY_Cantidad, mParam3);
            mListener.onFragmentInteractionSSE(bundle);
        }
        cantidad_edittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                bundle.putString(TicketTable.KEY_Cantidad, s.toString().trim());
                mListener.onFragmentInteractionSSE(bundle);

            }
        });

    }

}

