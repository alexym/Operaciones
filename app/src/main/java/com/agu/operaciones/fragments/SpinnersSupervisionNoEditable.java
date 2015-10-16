package com.agu.operaciones.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agu.operaciones.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpinnersSupervisionNoEditable.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpinnersSupervisionNoEditable#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpinnersSupervisionNoEditable extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private Context context;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    // TODO: Rename and change types of parameters
    private String prioridad;
    private String procede;
    private String atendido;
    private String etapa;

    private TextView prioridad_tv,procede_tv,prioridadTitulo_tv,procedeTitulo_tv;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpinnersSupervisionNoEditable.
     */
    // TODO: Rename and change types and number of parameters
    public static SpinnersSupervisionNoEditable newInstance(String param1, String param2, String param3,String param4) {
        SpinnersSupervisionNoEditable fragment = new SpinnersSupervisionNoEditable();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    public SpinnersSupervisionNoEditable() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            prioridad   = getArguments().getString(ARG_PARAM1);
            procede     = getArguments().getString(ARG_PARAM2);
            atendido    = getArguments().getString(ARG_PARAM3);
            etapa       = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_spinners_supervision_no_editable, container, false);
        //Evalua el estado de la vista
        initUI(V);

        return V;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionSpinnerNoEdit(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteractionSpinnerNoEdit(Uri uri);
    }
    public void initUI(View v){
        prioridad_tv    = (TextView) v.findViewById(R.id.prioridad_TextView_noedit);
        procede_tv      = (TextView) v.findViewById(R.id.procedeAtendido_TextView);


        if(atendido.equals("")){
            prioridad_tv.setText(prioridad);
            procede_tv.setText(procede);
        }else{
            prioridadTitulo_tv = (TextView) v.findViewById(R.id.tituloPrioridad_textView_no_edit);
            procedeTitulo_tv = (TextView) v.findViewById(R.id.tituloProcedeAtendido_TextView_no_edit);
            procede_tv.setText(atendido);
            procedeTitulo_tv.setText("Atendido");
            prioridadTitulo_tv.setVisibility(TextView.INVISIBLE);

        }



    }

}
