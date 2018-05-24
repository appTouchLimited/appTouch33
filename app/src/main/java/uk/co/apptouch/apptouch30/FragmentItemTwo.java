package uk.co.apptouch.apptouch30;



import android.app.Fragment;
import android.os.Bundle;
//import android.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



public class FragmentItemTwo extends Fragment {

    public static FragmentItemTwo newInstance() {
        FragmentItemTwo fragment = new FragmentItemTwo();
        return fragment;
    }

    View myView;
    String textString, imageString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView=inflater.inflate(R.layout.fragment_item_two, container, false);

        TextView txtViewOne = myView.findViewById(R.id.txtViewFragTwo);
       // ImageView imageViewOne = myView.findViewById(R.id.imageViewFragTwo);
        Bundle b1 = getArguments();
        textString = b1.getString("remoteconfigText");
        imageString = b1.getString("remoteconfigImage");
        txtViewOne.setText(textString);


        new DownloadImageTask((ImageView) myView.findViewById(R.id.imageViewFragTwo))
                .execute(imageString);


        return myView;

    }
}
