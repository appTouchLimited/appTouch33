package uk.co.apptouch.apptouch30;


import android.os.Bundle;
      //  import android.support.v4.app.Fragment;
        import android.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

public class FragmentItemOne extends Fragment {
    public static FragmentItemOne newInstance() {
        FragmentItemOne fragment = new FragmentItemOne();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_one, container, false);
    }
}
