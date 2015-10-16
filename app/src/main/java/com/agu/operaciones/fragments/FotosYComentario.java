package com.agu.operaciones.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

import com.agu.operaciones.R;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FotosYComentario.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FotosYComentario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FotosYComentario extends Fragment {

    private static final int TICKET_LOADER = 1;
    public static final String TAG = "FotosYComentarios";
    public static final String IMGVAL = "imagenes";
    public static final String COMENTVAL = "comentario";

    public static ImageButton[] images = new ImageButton[5];


    // Valores por default para estos campos


    private File directory;
    private File[] files;
    private EditText descripcionEditText;

    private Bundle bundle;


    private HorizontalScrollView hSV;
    private Context context;





    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    public static FotosYComentario newInstance(String path, String text) {
        FotosYComentario fragment = new FotosYComentario();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, path);
        args.putString(ARG_PARAM2,text);
        fragment.setArguments(args);
        return fragment;
    }

    public FotosYComentario() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        bundle = new Bundle();
        bundle.putBoolean(IMGVAL,false);
        bundle.putString(COMENTVAL,"");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_fotos_ycomentario, container, false);
        //Evalua el estado de la vista
        initUI(V);
        validateFiles(mParam1);
        return V;
    }


    public void onButtonPressed(Bundle bundle) {
        if (mListener != null) {
            mListener.onFragmentInteraction(bundle);
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
    @Override
    public void onStart() {
        super.onStart();
        //mListener.onFragmentInteraction(bundle);
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

        public void onFragmentInteraction(Bundle bundle);
    }

    private void validateFiles(String path) {

        //Archivo creado para almacenar las imágenes el cual tendra la ruta pasada por parametro
        directory = new File(path);
        //Mediante listFiles se obtinene los archivos que contiene la carpeta
        files = directory.listFiles();

        //validamos la cantidad de archivos que contiene la carpeta
        if (files.length > 0) {
            int totalI;
            for (totalI = 0; totalI < files.length; totalI++) {
                ImageButton currentImageButton = (ImageButton) hSV.findViewWithTag(files[totalI].getName());
                currentImageButton.setImageBitmap(BitmapFactory.decodeFile(files[totalI].getPath()));
            }

            // validamos el numero de imágenes minimo para poder avanzar a la siguiente activity
            if (totalI > 0) {
                bundle.putBoolean(IMGVAL,true);
                bundle.putString(COMENTVAL,descripcionEditText.getText().toString().trim() );
                mListener.onFragmentInteraction(bundle);
            }

        }
    }
    public int picturesTaked(){
        return files.length;
    }

    private void initUI(View v) {
        //Se realiza la instancia de las imagenes
        images[0] = (ImageButton) v.findViewById(R.id.img1);
        images[1] = (ImageButton) v.findViewById(R.id.img2);
        images[2] = (ImageButton) v.findViewById(R.id.img3);


        //instancia al scrollView que contendrá los elementos de la interfaz
        hSV = (HorizontalScrollView) v.findViewById(R.id.horizontalScrollView);
        descripcionEditText = (EditText) v.findViewById(R.id.descTF);
        if(mParam2.equals("null")){
            descripcionEditText.setText("");
        }else{
            descripcionEditText.setText(mParam2);
        }


        //Listener que se ejecuta al recibir un cambio en la caja de descripcion que interactua con el usuario
        descripcionEditText.addTextChangedListener(new TextWatcher() {

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

                bundle.putString(COMENTVAL,s.toString().trim() );
                mListener.onFragmentInteraction(bundle);

            }
        });

    }

}
