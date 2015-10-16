package com.agu.operaciones.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

import com.agu.operaciones.utilities.ImageCache.ImageLoader;
import com.agu.operaciones.R;
import com.agu.operaciones.providers.TicketMetaData;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FotosYComentarioNoEditable.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FotosYComentarioNoEditable#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FotosYComentarioNoEditable extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    public static final String TAG = "FotosYComentariosNoEditable";

    public static ImageButton[] images = new ImageButton[5];


    // Valores por default para estos campos


    private File directory;
    private File[] files;
    private EditText descripcionEditText;

    // Url imagenes tomadas
    private String[] imagenesUrl = new String[3];

    private Bundle bundle;


    private HorizontalScrollView hSV;
    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String estadoSincronizado;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FotosYComentarioNoEditable.
     */
    // TODO: Rename and change types and number of parameters
    public static FotosYComentarioNoEditable newInstance(String param1, String param2, String[] param3, String param4) {
        FotosYComentarioNoEditable fragment = new FotosYComentarioNoEditable();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putStringArray(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4,param4);
        fragment.setArguments(args);
        return fragment;
    }

    public FotosYComentarioNoEditable() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            imagenesUrl = getArguments().getStringArray(ARG_PARAM3);
            estadoSincronizado = getArguments().getString(ARG_PARAM4);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View V = inflater.inflate(R.layout.fragment_fotos_ycomentario_no_editable, container, false);
        //Evalua el estado de la vista
        initUI(V);
        validateFiles(mParam1);
        return V;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionFyCNoeditable(uri);
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
        public void onFragmentInteractionFyCNoeditable(Uri uri);
    }

    private void initUI(View v) {
        //Se realiza la instancia de las imagenes
        images[0] = (ImageButton) v.findViewById(R.id.img1);
        images[1] = (ImageButton) v.findViewById(R.id.img2);
        images[2] = (ImageButton) v.findViewById(R.id.img3);


        //instancia al scrollView que contendrá los elementos de la interfaz
        hSV = (HorizontalScrollView) v.findViewById(R.id.horizontalScrollView);
        descripcionEditText = (EditText) v.findViewById(R.id.descTF);
        descripcionEditText.setKeyListener(null);
        descripcionEditText.setEnabled(false);
        descripcionEditText.setFocusable(false);
        if(mParam2.equals("null")){
            descripcionEditText.setText("Sin texto en el comentario");
        }else{
            descripcionEditText.setText(mParam2);
        }
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
        }else{
            if (estadoSincronizado.equals(TicketMetaData.CONST_SINCRONIZADO_SI)) {
                //Se toma la imagen por default cuando no se tiene una imagen y se coloca en la vista
                int loader = R.drawable.fotodefault;
                ImageLoader imgLoader = new ImageLoader(context);

                imgLoader.DisplayImage(imagenesUrl[0], loader, images[0]);
                imgLoader.DisplayImage(imagenesUrl[1], loader, images[1]);
                imgLoader.DisplayImage(imagenesUrl[2], loader, images[2]);
                System.out.println("esta es la url de la imagen enviada: "+ imagenesUrl[0]);
            }
        }
    }

}
