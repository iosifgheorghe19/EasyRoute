package dam.project.easyroute;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View thisView =  inflater.inflate(R.layout.fragment_login, container, false);
        Button b = (Button) thisView.findViewById(R.id.login_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText usrText = (EditText)thisView.findViewById(R.id.usernameText);
                EditText pwdText = (EditText)thisView.findViewById(R.id.pwdText);
                Boolean isCorrectCombination = false;
                UserDB db = new UserDB(getActivity());
                Cursor userCursor = db.getCursorUsers(new String[]{UserDB.COL_USERNAME, UserDB.COL_USERPASSWORD}, null, null);
                String userName = usrText.getText().toString();
                String passwd = pwdText.getText().toString();
                while (userCursor.moveToNext())
                {
                    if (userName.equals(userCursor.getString(0)))
                    {
                        if (passwd.equals(userCursor.getString(1)))
                        {
                            isCorrectCombination = true;
                            if (mListener != null)
                                mListener.onFragmentInteraction("login");
                        }
                    }
                }
                if (!isCorrectCombination)
                    new AlertDialog.Builder(getActivity()).setMessage(R.string.login_failed_msg).setPositiveButton(R.string.ok_lbl, null).create().show();
            }
        });
        Button b2 = (Button) thisView.findViewById(R.id.register_button);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFragmentInteraction("register");
            }
        });
        return thisView;
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String path);
    }
}
